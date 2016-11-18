package basic_hierarchy.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import basic_hierarchy.common.Constants;
import basic_hierarchy.common.HierarchyBuilder;
import basic_hierarchy.implementation.BasicGroup;
import basic_hierarchy.implementation.BasicHierarchy;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.interfaces.DataReader;
import basic_hierarchy.interfaces.Group;
import basic_hierarchy.interfaces.Hierarchy;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;


public class GeneratedARFFReader implements DataReader
{
	@Override
	public Hierarchy load(
		String filePath,
		boolean withInstancesNameAttribute,
		boolean withClassAttribute,
		boolean withDataNames,
		boolean fillBreadthGaps,
		boolean useSubtree )
	{
		File inputFile = new File( filePath );
		if ( !inputFile.exists() && inputFile.isDirectory() ) {
			System.err.println(
				"Cannot access to file: " + filePath + ". Does it exist and is it a "
					+ "weka ARFF file?\n"
			);
			System.exit( 1 );
		}

		// TODO: Implement fetching of data column names.
		String[] dataNames = null;

		DataSource source = null;
		Instances data = null;
		try {
			source = new DataSource( inputFile.getAbsolutePath() );
			data = source.getDataSet();
		}
		catch ( Exception e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit( 1 );
		}

		data.setClassIndex( Constants.INDEX_OF_ASSIGN_CLASS_IN_WEKA_INSTANCE );
		int assignClassIndex = data.classIndex();

		System.out.println(
			"Assign class attribute name is: " + data.classAttribute().name()
				+ "\nNumber of classes: " + data.numClasses()
		);

		BasicGroup root = null;
		ArrayList<BasicGroup> groups = new ArrayList<BasicGroup>();
		HashMap<String, Integer> eachClassAndItsCount = new HashMap<String, Integer>();

		int numberOfDimensions = data.numAttributes() - 1; // minus assign class attribute
		if ( withClassAttribute ) {
			numberOfDimensions -= 1;
		}

		if ( withInstancesNameAttribute ) {
			numberOfDimensions -= 1;
		}

		for ( int i = 0; i < data.numInstances(); i++ ) {
			weka.core.Instance instance = data.instance( i );

			String classAttr = null;
			if ( withClassAttribute ) {
				classAttr = instance.stringValue( Constants.INDEX_OF_GROUND_TRUTH_IN_WEKA_INSTANCE );
				if ( eachClassAndItsCount.containsKey( classAttr ) ) {
					eachClassAndItsCount.put( classAttr, eachClassAndItsCount.get( classAttr ) + 1 );
				}
				else {
					eachClassAndItsCount.put( classAttr, 1 );
				}
			}

			String instanceNameAttr = null;
			if ( withInstancesNameAttribute ) {
				instanceNameAttr = instance.stringValue( Constants.INDEX_OF_GROUND_TRUTH_IN_WEKA_INSTANCE + ( withClassAttribute ? 1 : 0 ) );
			}

			// assuming that node's instances are grouped in input file
			// REFACTOR: below could the binary-search be utilised with sorting by ID-comparator

			String assignClass = instance.stringValue( assignClassIndex );

			double[] instanceData = new double[numberOfDimensions];
			int instDataIndex = 0;
			for ( int j = 0; j < instance.numAttributes(); ++j ) {
				if ( j == Constants.INDEX_OF_ASSIGN_CLASS_IN_WEKA_INSTANCE )
					continue;

				if ( withClassAttribute && j == Constants.INDEX_OF_GROUND_TRUTH_IN_WEKA_INSTANCE )
					continue;

				if ( withClassAttribute && withInstancesNameAttribute && j == Constants.INDEX_OF_GROUND_TRUTH_IN_WEKA_INSTANCE + 1 )
					continue;

				if ( !withClassAttribute && withInstancesNameAttribute && j == Constants.INDEX_OF_GROUND_TRUTH_IN_WEKA_INSTANCE )
					continue;

				instanceData[instDataIndex] = instance.value( j );
				++instDataIndex;
			}

			boolean nodeExist = false;
			int nodeIndex = -1;
			for ( int nodeIndexIter = 0; nodeIndexIter < groups.size() && !nodeExist; nodeIndexIter++ ) {
				if ( groups.get( nodeIndexIter ).getId().equalsIgnoreCase( assignClass ) ) {
					nodeExist = true;
					nodeIndex = nodeIndexIter;
				}
			}
			if ( nodeExist ) {
				groups.get( nodeIndex ).addInstance(
					new BasicInstance( instanceNameAttr, groups.get( nodeIndex ).getId(), instanceData, classAttr )
				);
			}
			else {
				BasicGroup newGroup = new BasicGroup( assignClass, null, useSubtree );
				newGroup.addInstance( new BasicInstance( instanceNameAttr, newGroup.getId(), instanceData, classAttr ) );
				groups.add( newGroup );
			}
		}

		List<? extends Group> allGroups = HierarchyBuilder.buildCompleteGroupHierarchy( root, groups, fillBreadthGaps, useSubtree );

		if ( root == null ) {
			// If root was missing from input file, then it must've been created artificially - find it.
			// List of groups should be sorted by ID, therefore finding root should have negligible overhead.
			for ( Group group : allGroups ) {
				if ( group.getId().equalsIgnoreCase( Constants.ROOT_ID ) ) {
					root = (BasicGroup)group;
					break;
				}
			}
		}

		return new BasicHierarchy( root, allGroups, dataNames, eachClassAndItsCount );
	}

}
