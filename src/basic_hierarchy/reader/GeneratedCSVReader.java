package basic_hierarchy.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basic_hierarchy.common.AlphanumComparator;
import basic_hierarchy.common.Constants;
import basic_hierarchy.common.HierarchyBuilder;
import basic_hierarchy.common.Utils;
import basic_hierarchy.implementation.BasicHierarchy;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.DataReader;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Node;


public class GeneratedCSVReader implements DataReader
{
    private static final String REGEX_NODE_ID = "gen(" + Constants.HIERARCHY_BRANCH_SEPARATOR_REGEX + "\\d+)+";

    private HierarchyBuilder hb = null;

    private volatile int progress = 0;
    private volatile String statusMsg = "";


    public GeneratedCSVReader()
    {
    }

    /**
     * @return value representing progress of reading the file, values [0, 100], or
     *         negative for indeterminate operation.
     */
    public int getProgress()
    {
        return hb == null ? progress : hb.getProgress();
    }

    /**
     * @return message describing the currently performed operation.
     */
    public String getStatusMessage()
    {
        return hb == null ? statusMsg : hb.getStatusMessage();
    }

    /**
     * This method assumes that data are generated using Michał Spytkowski's data generator, using TSSB method.
     * For more information about the generator, see https://arxiv.org/abs/1606.05681
     * <p>
     * The first node listed is always the root node. It is also the only node without a parent.
     * Nodes are given in depth-first order.
     * </p>
     * <p>
     * Files are loaded assuming UTF-8 encoding.
     * </p>
     * 
     * @throws IOException
     *             if an IO error occurred while reading the file
     * @throws NumberFormatException
     *             if one of the instance features was not a parsable {@code double}
     *             (indicating error in input file, or incorrect reader settings)
     */
    @Override
    public Hierarchy load(
        String filePath,
        boolean withInstancesNameAttribute,
        boolean withTrueClassAttribute,
        boolean withColumnHeaders,
        boolean fixBreadthGaps,
        boolean useSubtree ) throws IOException
    {
        statusMsg = "Parsing file...";
        progress = 0;

        // REFACTOR: Could create a factory class to generate nodes.
        // REFACTOR: Skip nodes' elements containing "gen" prefix and assume that every ID prefix always begins with "gen"
        File inputFile = new File( filePath );
        if ( !inputFile.exists() || inputFile.isDirectory() ) {
            throw new RuntimeException(
                String.format(
                    "Cannot access file: '%s'. Does it exist, and is it a %s-separated text file?",
                    filePath, Constants.DELIMITER
                )
            );
        }

        BasicNode root = null;
        String[] dataNames = null;
        Map<String, BasicNode> nodeMap = new TreeMap<String, BasicNode>( new AlphanumComparator() );
        HashMap<String, Integer> eachClassAndItsCount = new HashMap<String, Integer>();
        int overallNumberOfInstances = 0;

        Reader reader = new InputStreamReader( new FileInputStream( filePath ), "UTF-8" );

        final long bytesTotal = inputFile.length();
        long bytesRead = 0;

        try ( BufferedReader br = new BufferedReader( reader ) ) {
            final int optionalColumns = boolToInt( withTrueClassAttribute ) + boolToInt( withInstancesNameAttribute );
            final int minimumColumnCount = 1 + optionalColumns;
            int dataColumnCount = -1;
            int totalColumnCount = -1;

            for ( String inputLine; ( inputLine = br.readLine() ) != null; ) {
                Utils.checkInterruptStatus();

                bytesRead += inputLine.getBytes( "UTF-8" ).length;
                progress = (int)( 100 * ( (double)bytesRead / bytesTotal ) );

                String[] lineValues = inputLine.split( Constants.DELIMITER );

                if ( dataColumnCount == -1 ) {
                    // First line encountered.

                    // Make sure that the file is valid -- it needs to have a node ID column,
                    // at most 2 optional columns, and at least one data column.
                    if ( lineValues.length <= minimumColumnCount ) {
                        throw new RuntimeException(
                            String.format(
                                "Input data is not formatted correctly. Each line should contain at least a node ID columm and a value column " +
                                    "(and optionally class attribute and/or instance name).%nLine: %s",
                                inputLine
                            )
                        );
                    }
                    else {
                        // File seems to be valid -- compute column counts for all the other rows.
                        totalColumnCount = lineValues.length;
                        dataColumnCount = totalColumnCount - minimumColumnCount;
                    }

                    if ( withColumnHeaders ) {
                        dataNames = new String[dataColumnCount];
                        for ( int i = 0; i < dataColumnCount; ++i ) {
                            dataNames[i] = lineValues[minimumColumnCount + i];
                        }
                        continue;
                    }
                }

                // Assert that the row has the expected number of columns.
                if ( lineValues.length != totalColumnCount ) {
                    throw new RuntimeException(
                        String.format(
                            "Input data not formatted corectly - each line should contain a total of %s columns (this line has %s).%nLine: %s%n",
                            totalColumnCount, lineValues.length, inputLine
                        )
                    );
                }

                String assignedClassAttr = lineValues[0];
                if ( !isValidNodeId( assignedClassAttr ) ) {
                    throw new RuntimeException(
                        String.format(
                            "Assigned class is not a valid node id: '%s'%nLine:%s%n",
                            assignedClassAttr, inputLine
                        )
                    );
                }

                String trueClassAttr = null;
                if ( withTrueClassAttribute ) {
                    // If present, true class is always assumed to be in the second column.
                    trueClassAttr = lineValues[1];
                    if ( !isValidNodeId( trueClassAttr ) ) {
                        throw new RuntimeException(
                            String.format(
                                "True class is not a valid node id: '%s'%nLine: %s%n",
                                trueClassAttr, inputLine
                            )
                        );
                    }

                    eachClassAndItsCount.put( trueClassAttr, getOrDefault( eachClassAndItsCount, trueClassAttr, 0 ) + 1 );
                }

                String instanceNameAttr = null;
                if ( withInstancesNameAttribute ) {
                    // If present, instance name is assumed to be in the second column, unless
                    // true class is also present - then it is assumed to be in the third column.
                    instanceNameAttr = lineValues[1 + boolToInt( withTrueClassAttribute )];
                }

                double[] values = parseInstanceFeatures( inputLine, lineValues, dataColumnCount, minimumColumnCount );

                BasicNode node = getOrDefault( nodeMap, assignedClassAttr, null );
                if ( node == null ) {
                    // Node for this id doesn't exist yet. Create it.
                    node = new BasicNode( assignedClassAttr, null, useSubtree );
                    nodeMap.put( assignedClassAttr, node );
                }

                node.addInstance( new BasicInstance( instanceNameAttr, node.getId(), values, trueClassAttr ) );
                overallNumberOfInstances++;

                if ( root == null && assignedClassAttr.equalsIgnoreCase( Constants.ROOT_ID ) ) {
                    root = node;
                }
            }
        }

        hb = new HierarchyBuilder();
        progress = 100;

        ArrayList<BasicNode> nodes = new ArrayList<BasicNode>( nodeMap.values() );
        List<? extends Node> allNodes = hb.buildCompleteHierarchy( root, nodes, fixBreadthGaps, useSubtree );

        if ( root == null ) {
            // If root was missing from input file, then it must've been created artificially - find it.
            // List of nodes should be sorted by ID, therefore finding root should have negligible overhead.
            for ( Node node : allNodes ) {
                Utils.checkInterruptStatus();

                if ( node.getId().equalsIgnoreCase( Constants.ROOT_ID ) ) {
                    root = (BasicNode)node;
                    break;
                }
            }
        }

        return new BasicHierarchy( root, allNodes, dataNames, eachClassAndItsCount, overallNumberOfInstances );
    }

    /**
     * Converts boolean value to an integer.
     * 
     * @param b
     *            the boolean value to convert
     * @return 1 if argument is true, 0 otherwise.
     */
    private static int boolToInt( boolean b )
    {
        return b ? 1 : 0;
    }

    /**
     * {@link Map#getOrDefault(Object, Object)} is available since 1.8, but we need to support 1.7...
     * 
     * @param map
     *            the map to read from
     * @param key
     *            the key whose associated value is to be returned
     * @param defaultValue
     *            the default mapping of the key
     * @return the value to which the specified key is mapped, or {@code defaultValue} if this map contains no mapping for the key
     */
    private static <K, V> V getOrDefault( Map<K, V> map, K key, V defaultValue )
    {
        if ( map.containsKey( key ) )
            return map.get( key );
        return defaultValue;
    }

    /**
     * Checks whether the specified string is a valid node id.
     * 
     * @param string
     *            the string to test
     * @return true if the string is a valid node id, false otherwise.
     */
    private static boolean isValidNodeId( String string )
    {
        return string.matches( REGEX_NODE_ID );
    }

    /**
     * Attempts to extract instance features from the specified line.
     * 
     * @param inputLine
     *            the line to read from; used for error reporting.
     * @param lineValues
     *            the array of line values, obtained by splitting the input line over the separator char.
     * @param dataColumnCount
     *            number of data columns / instance features.
     * @param minimumColumnCount
     *            number of columns preceding instance features' columns
     * @return array of data values - instance features
     * @throws NumberFormatException
     *             if one of the data values was not a parsable {@code double}
     *             (indicating error in input file, or incorrect reader settings)
     */
    private static double[] parseInstanceFeatures( String inputLine, String[] lineValues, int dataColumnCount, int minimumColumnCount )
    {
        double[] values = new double[dataColumnCount];

        for ( int j = 0; j < dataColumnCount; ++j ) {
            try {
                // Data columns are always last.
                values[j] = Double.parseDouble( lineValues[minimumColumnCount + j] );
            }
            catch ( NumberFormatException e ) {
                throw new NumberFormatException(
                    String.format(
                        "Failed to parse '%s' as double. All instance features should be valid floating point numbers.%nLine: %s%n",
                        lineValues[minimumColumnCount + j], inputLine
                    )
                );
            }
        }

        return values;
    }
}
