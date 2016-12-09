package basic_hierarchy.implementation;

import java.util.*;

import basic_hierarchy.common.Constants;
import basic_hierarchy.common.StringIdComparator;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;
import basic_hierarchy.test.TestCommon;


public class BasicHierarchy implements Hierarchy
{
	private Node root;
	private Node[] groups;
	private String[] classes;
    private int[] classCounts;
	private int overallNumberOfInstances;
    private String[] dataNames;

    /**
     * Creates a new hierarchy object.
     *
     * @param root
     *            the root node of the hierarchy. Must not be null.
     * @param nodes
     *            list of all nodes in the hierarchy
     * @param dataNames
     *            array of names, for data columns in instances
     * @param eachClassWithCount
     *            map of classes (node identifiers) to the number of children nodes in that class
     * @param overallNumberOfInstances
     *            total number of instances in the hierarchy
     */
    public BasicHierarchy(
            Node root, List<? extends Node> nodes,
            String[] dataNames,
            Map<String, Integer> eachClassWithCount,
            int overallNumberOfInstances )
    {
        this(root, nodes, dataNames, overallNumberOfInstances);

        classes = new String[eachClassWithCount.size()];
        classCounts = new int[eachClassWithCount.size()];

        LinkedList<String> sortedKeys = new LinkedList<String>( eachClassWithCount.keySet() );
        Collections.sort( sortedKeys, new StringIdComparator() );
        for ( int index = 0; index < sortedKeys.size(); ++index ) {
            String key = sortedKeys.get( index );
            classes[index] = key;
            classCounts[index] = eachClassWithCount.get( key );
        }
    }

    /**
     * Creates a new hierarchy object.
     *
     * @param root
     *            the root node of the hierarchy. Must not be null.
     * @param nodes
     *            list of all nodes in the hierarchy
     * @param dataNames
     *            array of names, for data columns in instances
     * @param eachClassWithCount
     *            map of classes (node identifiers) to the number of children nodes in that class
     */
    public BasicHierarchy(
            Node root, List<? extends Node> nodes,
            String[] dataNames,
            Map<String, Integer> eachClassWithCount )
    {
        this( root, nodes, dataNames, eachClassWithCount, 0 );

        for ( Node g : nodes ) {
            this.overallNumberOfInstances += g.getNodeInstances().size();
        }
    }

    public BasicHierarchy(
            Node root, List<? extends Node> nodes,
            Map<String, Integer> eachClassWithCount,
            int overallNumberOfInstances )
    {
        this( root, nodes, null, eachClassWithCount, overallNumberOfInstances );
    }

	public BasicHierarchy(Node root, List<? extends Node>  nodes, HashMap<String, Integer> eachClassWithCount,
			int overallNumberOfInstances)
	{
        this(root, nodes, overallNumberOfInstances);
        classes = new String[eachClassWithCount.size()];
        classCounts = new int[eachClassWithCount.size()];

        LinkedList<String> sortedKeys = new LinkedList<String>( eachClassWithCount.keySet() );
        Collections.sort( sortedKeys, new StringIdComparator() );

        for ( int index = 0; index < sortedKeys.size(); ++index ) {
            String key = sortedKeys.get( index );
            classes[index] = key;
            classCounts[index] = eachClassWithCount.get( key );
        }
    }

    public BasicHierarchy(Node root, List<? extends Node>  nodes, String[] dataNames, String[] classes, int[] classesCount,
                          int overallNumberOfInstances)
    {
        this(root, nodes, dataNames, overallNumberOfInstances);
        this.classes = classes;
        this.classCounts = classesCount;
    }

    private BasicHierarchy(Node root, List<? extends Node>  nodes, int overallNumberOfInstances) {
        if ( root == null ) {
            throw new IllegalArgumentException( "Root node must not be null." );
        }
        this.root = root;
        this.groups = nodes.toArray(new BasicNode[nodes.size()]);
        this.overallNumberOfInstances = overallNumberOfInstances;
        this.dataNames = null;
    }

    private BasicHierarchy(Node root, List<? extends Node>  nodes, String[] dataNames, int overallNumberOfInstances) {
        if ( root == null ) {
            throw new IllegalArgumentException( "Root node must not be null." );
        }
        this.root = root;
        this.groups = nodes.toArray(new BasicNode[nodes.size()]);
        this.dataNames = dataNames;
        this.overallNumberOfInstances = overallNumberOfInstances;
    }

	@Override
	public Node getRoot()
	{
		return root;
	}

	@Override
	public int getNumberOfGroups()
	{
		return groups.length;
	}

	@Override
	public int getNumberOfClasses()
	{
		return classes.length;
	}

	@Override
	public Node[] getGroups()
	{
		return groups;
	}

	@Override
	public String[] getClasses()
	{
		return classes;
	}

    @Override
	public int[] getClassesCount() {
        return classCounts;
    }

	@Override
	public int getParticularClassCount(String className, boolean withInstancesInheritance) {
		int index = Arrays.binarySearch( classes, className, new StringIdComparator() );

		if ( index < 0 ) {
			// Not found.
			return index;
		}
		else {
			// REFACTOR below code could be faster, by moving the computations into the constructor in a smart way
			// e.g. by using the partial results (from other classes) to compute results for other classes
			if ( withInstancesInheritance ) {
				String prefix = className + Constants.HIERARCHY_BRANCH_SEPARATOR;
				int result = classCounts[index];
				for ( int i = index; i < classCounts.length; ++i ) {
					if ( className.length() < classes[i].length() && classes[i].startsWith( prefix ) ) {
						result += classCounts[i];
					}
				}

				return result;
			}
			else {
				return classCounts[index];
			}
		}
	}

	public int getOverallNumberOfInstances() {
		return overallNumberOfInstances;
	}

	@Override
	public Hierarchy getFlatClusteringWithCommonEmptyRoot() {
        Node artificialRoot = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(), false);

        LinkedList<Node> groups = new LinkedList<>();
        groups.add(artificialRoot);
        int nodesCounter = 0;
        for (Node n : this.getGroups()) {
            if (!n.getNodeInstances().isEmpty()) {
                Node nodeToAdd = new BasicNode(TestCommon.getIDOfChildCluster(Constants.ROOT_ID, nodesCounter++),
                        artificialRoot, new LinkedList<Node>(), new LinkedList<Instance>(), false);

                for (Instance inst : n.getNodeInstances()) {
                    nodeToAdd.addInstance(new BasicInstance(inst.getInstanceName(), nodeToAdd.getId(),
                            inst.getData().clone(), inst.getTrueClass()));
                }

                groups.add(nodeToAdd);
                artificialRoot.addChild(nodeToAdd);
            }
        }

        return new BasicHierarchy(artificialRoot, groups, this.dataNames, this.classes, this.classCounts, this.overallNumberOfInstances);
    }

	@Override
	public String[] getDataNames()
	{
		return dataNames;
	}

	@Override
	public String toString()
	{
		if ( root != null ) {
			return root.toString();
		}

		throw new RuntimeException( "Implementation error: this hierarchy has no root node." );
	}

	public void printTree()
	{
		root.printSubtree();
	}
}
