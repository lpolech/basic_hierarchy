package basic_hierarchy.test.common;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import basic_hierarchy.common.Constants;
import basic_hierarchy.common.Utils;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;
import basic_hierarchy.test.TestCommon;

public class UtilsTest {

	@Test
	public void testIsPositiveNumeric() {
		assertEquals(true, Utils.isPositiveNumeric("1"));
		assertEquals(false, Utils.isPositiveNumeric("-41"));
		assertEquals(false, Utils.isPositiveNumeric("test"));
	}

	@Test
	public void testGetNumberOfSubtreeGroups() {
		BasicNode node;
	    BasicNode child;
		 String nodeId = Constants.ROOT_ID;
	     String firstChildNodeId = TestCommon.getIDOfChildCluster(nodeId, 0);

	     node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<>(), false);
	     child = new BasicNode(firstChildNodeId, node, new LinkedList<Node>(), new LinkedList<>(), false);
	     node.addChild(child);
	     assertEquals(2, Utils.getNumberOfSubtreeGroups(node));
	     
	}

	@Test
	public void testGetOneClusterHierarchy() {
		Hierarchy h = TestCommon.getTwoGroupsHierarchy();
		assertEquals(2, h.getNumberOfGroups());
		
		Hierarchy h2 = Utils.getOneClusterHierarchy( h );
		assertEquals(1, h2.getNumberOfGroups());
	}

	@Test (expected = RuntimeException.class)
	public void testCheckInterruptStatus() {
		Utils.checkInterruptStatus();
	}

}
