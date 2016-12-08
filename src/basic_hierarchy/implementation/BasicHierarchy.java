package basic_hierarchy.implementation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import basic_hierarchy.common.Constants;
import basic_hierarchy.common.StringIdComparator;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;
import basic_hierarchy.test.TestCommon;

public class BasicHierarchy implements Hierarchy {
	private Node root;
	private BasicNode[] groups;
	private String[] classes;
    private int[] classesCount;
	private int overallNumberOfInstances;
	
	public BasicHierarchy(Node root, LinkedList<Node> groups, HashMap<String, Integer> eachClassWithCount,
			int overallNumberOfInstances)
	{
        this(root, groups, overallNumberOfInstances);

		classes = new String[eachClassWithCount.size()];
		classesCount = new int[eachClassWithCount.size()];
		LinkedList<String> sortedKeyes = new LinkedList<String>(eachClassWithCount.keySet());
		Collections.sort(sortedKeyes, new StringIdComparator());
		int arrayIndex = 0;
		for(String key: sortedKeyes)
		{
			classes[arrayIndex] = key;
			classesCount[arrayIndex] = eachClassWithCount.get(key);
			arrayIndex++;
		}
	}

    public BasicHierarchy(Node root, LinkedList<Node> groups, String[] classes, int[] classesCount, int overallNumberOfInstances)
    {
        this(root, groups, overallNumberOfInstances);
        this.classes = classes;
        this.classesCount = classesCount;
    }

    private BasicHierarchy(Node root, LinkedList<Node> groups, int overallNumberOfInstances) {
        this.root = root;
        this.groups = groups.toArray(new BasicNode[groups.size()]);
        this.overallNumberOfInstances = overallNumberOfInstances;
    }
	
	@Override
	public Node getRoot() {
		return root;
	}

	@Override
	public int getNumberOfGroups() {
		return groups.length;
	}

	@Override
	public int getNumberOfClasses() {
		return classes.length;
	}

	@Override
	public Node[] getGroups() {
		return groups;
	}

	@Override
	public String[] getClasses() {
		return classes;
	}

    @Override
	public int[] getClassesCount() {
        return classesCount;
    }

	@Override
	public int getParticularClassCount(String className, boolean withInstancesInheritance) {
		int index = Arrays.binarySearch(classes, className, new StringIdComparator());
		if(index < 0)
			return index;
		else
		{
			//REFACTOR below code could be faster, by moving the computations into the constructor in a smart way
			//e.g. by using the partial results (from other classes) to compute results for other classes
			if(withInstancesInheritance)
			{
				int returnValue = classesCount[index];
				for(int i = index; i < classesCount.length; i++)
				{
					if(className.length() < classes[i].length() && classes[i].startsWith(className+Constants.HIERARCHY_BRANCH_SEPARATOR))
					{
						returnValue += classesCount[i];
					}
				}
				return returnValue;
			}
			else
				return classesCount[index];
		}
	}

	public int getOverallNumberOfInstances() {
		return overallNumberOfInstances;
	}

	@Override
	public void printTree() {
		if(root != null)
			root.printSubtree();
	}

	@Override
	public Hierarchy getFlatClusteringWithCommonEmptyRoot() {
		Node artificialRoot = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(), false);

		LinkedList<Node> groups = new LinkedList<>();
		groups.add(artificialRoot);
		int nodesCounter = 0;
		for(Node n: this.getGroups()) {
		    if(!n.getNodeInstances().isEmpty()) {
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

		return new BasicHierarchy(artificialRoot, groups, this.classes, this.classesCount, this.overallNumberOfInstances);
	}
}
