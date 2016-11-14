package basic_hierarchy.test;

import basic_hierarchy.common.Constants;
import basic_hierarchy.implementation.BasicHierarchy;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;

import java.util.HashMap;
import java.util.LinkedList;

public class TestCommon {
    public static final double DOUBLE_COMPARISION_DELTA = 1e-9;

    public static String getIDOfChildCluster(String parentId, int childNumber)
    {
        return parentId + Constants.HIERARCHY_BRANCH_SEPARATOR + childNumber;
    }

    public static Hierarchy getTwoGroupsHierarchy() {
        String rootId = basic_hierarchy.common.Constants.ROOT_ID;
        String childId = TestCommon.getIDOfChildCluster(rootId, 0);
        LinkedList<Instance> firstClusterInstances = new LinkedList<>();
        firstClusterInstances.add(new BasicInstance("11", rootId, new double[]{1.0, 2.0}, rootId));
        firstClusterInstances.add(new BasicInstance("12", rootId, new double[]{3.0, 4.0}, rootId));
        BasicNode firstCluster = new BasicNode(rootId, null, new LinkedList<Node>(), firstClusterInstances, false);

        LinkedList<Instance> secondClusterInstances = new LinkedList<Instance>();
        secondClusterInstances.add(new BasicInstance("21", childId, new double[]{1.5, 2.5}, childId));
        secondClusterInstances.add(new BasicInstance("22", childId, new double[]{3.5, 4.5}, rootId));
        BasicNode secondCluster = new BasicNode(childId, firstCluster, new LinkedList<Node>(), secondClusterInstances, false);

        firstCluster.addChild(secondCluster);

        LinkedList<Node> groups = new LinkedList<>();
        groups.add(firstCluster);
        groups.add(secondCluster);
        HashMap<String, Integer> eachClassWithCount = new HashMap<>();
        eachClassWithCount.put(rootId, 3);
        eachClassWithCount.put(childId, 1);

        return new BasicHierarchy(firstCluster, groups, eachClassWithCount, 4);
    }

    public static Hierarchy getFourGroupsHierarchy() {
        String rootId = basic_hierarchy.common.Constants.ROOT_ID;
        String rootFirstChildId = getIDOfChildCluster(rootId, 0);
        String rootSecondChildId = getIDOfChildCluster(rootId, 1);
        String rootFirstChildFirstChildId = getIDOfChildCluster(rootFirstChildId, 0);

        LinkedList<Instance> rootClusterInstances = new LinkedList<>();
        rootClusterInstances.add(new BasicInstance("11", rootId, new double[]{1.0, 2.0}, rootId));
        rootClusterInstances.add(new BasicInstance("12", rootId, new double[]{3.0, 4.0}, rootId));
        BasicNode rootCluster = new BasicNode(rootId, null, new LinkedList<Node>(), rootClusterInstances, false);

        LinkedList<Instance> rootFirstChildInstances = new LinkedList<Instance>();
        rootFirstChildInstances.add(new BasicInstance("21", rootFirstChildId, new double[]{1.5, 2.5}, rootFirstChildId));
        rootFirstChildInstances.add(new BasicInstance("22", rootFirstChildId, new double[]{3.5, 4.5}, rootFirstChildFirstChildId));
        BasicNode rootFirstChildCluster = new BasicNode(rootFirstChildId, rootCluster, new LinkedList<Node>(), rootFirstChildInstances, false);

        LinkedList<Instance> rootSecondChildInstances = new LinkedList<Instance>();
        rootSecondChildInstances.add(new BasicInstance("31", rootSecondChildId, new double[]{-1.5, 2.5}, rootFirstChildId));
        rootSecondChildInstances.add(new BasicInstance("32", rootSecondChildId, new double[]{3.5, -4.5}, rootId));
        rootSecondChildInstances.add(new BasicInstance("33", rootSecondChildId, new double[]{-3.5, -4.5}, rootSecondChildId));
        BasicNode rootSecondChildCluster = new BasicNode(rootSecondChildId, rootCluster, new LinkedList<Node>(), rootSecondChildInstances, false);

        LinkedList<Instance> rootFirstChildFirstChildInstances = new LinkedList<Instance>();
        rootFirstChildFirstChildInstances.add(new BasicInstance("41", rootFirstChildFirstChildId, new double[]{0.5, 0.5}, rootFirstChildFirstChildId));
        rootFirstChildFirstChildInstances.add(new BasicInstance("42", rootFirstChildFirstChildId, new double[]{-0.5, -0.5}, rootFirstChildFirstChildId));
        rootFirstChildFirstChildInstances.add(new BasicInstance("43", rootFirstChildFirstChildId, new double[]{3.5, -0.5}, rootSecondChildId));
        rootFirstChildFirstChildInstances.add(new BasicInstance("41", rootFirstChildFirstChildId, new double[]{0.25, 0.75}, rootId));
        BasicNode rootFirstChildFirstChildCluster = new BasicNode(rootFirstChildFirstChildId, rootFirstChildCluster, new LinkedList<Node>(), rootFirstChildFirstChildInstances, false);

        rootCluster.addChild(rootFirstChildCluster);
        rootCluster.addChild(rootSecondChildCluster);
        rootFirstChildCluster.addChild(rootFirstChildFirstChildCluster);

        LinkedList<Node> groups = new LinkedList<>();
        groups.add(rootCluster);
        groups.add(rootFirstChildCluster);
        groups.add(rootSecondChildCluster);
        groups.add(rootFirstChildFirstChildCluster);
        HashMap<String, Integer> eachClassWithCount = new HashMap<>();
        eachClassWithCount.put(rootId, 4);
        eachClassWithCount.put(rootFirstChildId, 2);
        eachClassWithCount.put(rootSecondChildId, 2);
        eachClassWithCount.put(rootFirstChildFirstChildId, 3);

        return new BasicHierarchy(rootCluster, groups, eachClassWithCount, 11);
    }
}
