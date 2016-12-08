package basic_hierarchy.common;

import java.util.LinkedList;
import java.util.Stack;

import basic_hierarchy.implementation.BasicHierarchy;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;

public class Utils {
	/*
	 * Applicable only to positive numbers
	 */
	public static boolean isPositiveNumeric(String str)
	{
	    for (char c : str.toCharArray())
	    {
	        if (!Character.isDigit(c)) return false;
	    }
	    return true;
	}
	
	public static int getNumberOfSubtreeGroups(Node subtreeRoot) {
		Stack<Node> s = new Stack<Node>();
		s.push(subtreeRoot);
		int numberOfGroups = 0;
		while(!s.empty())
		{
			Node n = s.pop();
			numberOfGroups++;
			for(Node child: n.getChildren())
			{
				s.push(child);
			}
		}
		return numberOfGroups;
	}

	public static Hierarchy getOneClusterHierarchy(Hierarchy h) {
        LinkedList<Instance> instances = new LinkedList<>();
        for(Instance i: h.getRoot().getSubtreeInstances()) {
            instances.add(new BasicInstance(i.getInstanceName(), Constants.ROOT_ID, i.getData().clone(), i.getTrueClass()));
        }

        Node root = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), instances, false);

        LinkedList<Node> groups = new LinkedList<>();
        groups.add(root);

        return new BasicHierarchy(root, groups, h.getClasses(), h.getClassesCount(), h.getOverallNumberOfInstances());
    }
}
