package basic_hierarchy.implementation;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import basic_hierarchy.TestCommon;
import basic_hierarchy.common.Constants;
import basic_hierarchy.common.HierarchyUtils;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;

public class BasicNodeTest {
	private static final String THIRD = "third";
	private static final String SECOND = "second";
	private static final String FIRST = "first";
	private static final String NOWEID = "noweid";
	private static final String GEN_0_2 = "gen.0.2";
	private BasicNode node;
	private BasicNode child;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@Before
	public void setUpStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@After
	public void restoreStreams() {
		System.setOut(System.out);
		System.setErr(System.err);
	}

	@Before
	public void setUp() {
		String nodeId = Constants.ROOT_ID;
		String firstChildNodeId = HierarchyUtils.getIDOfChildCluster(nodeId, 0);

		LinkedList<Instance> instances = new LinkedList<>();
		instances.add(new BasicInstance(FIRST, nodeId, new double[] { 0.0, 0.5 }, null));
		instances.add(new BasicInstance(SECOND, nodeId, new double[] { 1.5, 2.0 }, null));
		instances.add(new BasicInstance(THIRD, nodeId, new double[] { 0.0, 0.5 }, null));

		LinkedList<Instance> childInstances = new LinkedList<>();
		childInstances.add(new BasicInstance(FIRST, firstChildNodeId, new double[] { 0.3, -0.5 }, null));
		childInstances.add(new BasicInstance(SECOND, firstChildNodeId, new double[] { 2.7, -2.0 }, null));
		childInstances.add(new BasicInstance(THIRD, firstChildNodeId, new double[] { -4.0, 0.0 }, null));
		childInstances.add(new BasicInstance("fourth", firstChildNodeId, new double[] { 0.0, -1.5 }, null));

		node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), instances, false);
		child = new BasicNode(firstChildNodeId, node, new LinkedList<Node>(), childInstances, false);
		node.addChild(child);
	}

	@org.junit.Test
	public void calculateSimpleCentroid() {
		assertArrayEquals(new double[] { 0.5, 1.0 }, node.getNodeRepresentation().getData(),
				TestCommon.DOUBLE_COMPARISION_DELTA);
		assertArrayEquals(new double[] { -0.25, -1.0 }, child.getNodeRepresentation().getData(),
				TestCommon.DOUBLE_COMPARISION_DELTA);
	}

	@org.junit.Test
	public void calculateCentroidWithSubtree() {
		node.recalculateCentroid(true);

		assertArrayEquals(new double[] { 0.071428571, -0.142857143 }, node.getNodeRepresentation().getData(),
				TestCommon.DOUBLE_COMPARISION_DELTA);
		assertArrayEquals(new double[] { -0.25, -1.0 }, child.getNodeRepresentation().getData(),
				TestCommon.DOUBLE_COMPARISION_DELTA);
	}

	@Test
	public void testBasicNodeStringNodeBoolean() {
		BasicNode newNode = new BasicNode("nowe", node, true);
		assertArrayEquals(new double[] {}, newNode.getNodeRepresentation().getData(), 0.0);
		assertEquals("nowe", newNode.getId());
		assertEquals(0, newNode.getChildren().size());
		assertEquals(0, newNode.getNodeInstances().size());
	}

	@Test
	public void testBasicNodeStringNodeInstance() {
		BasicNode newNode = new BasicNode("nowe", node,
				(new BasicInstance("fourth", "gen.0.0", new double[] { 0.0, -1.5 }, null)));
		assertEquals("nowe", newNode.getId());
		assertEquals(0, newNode.getChildren().size());
		assertEquals(0, newNode.getNodeInstances().size());
	}

	@Test
	public void testSetParent() {
		assertEquals(null, node.getParent());
		node.setParent(child);
		assertEquals(child, node.getParent());
	}

	@Test
	public void testSetParentId() {
		child.setParentId(NOWEID);
		assertEquals(NOWEID, node.getId());
	}

	@Test
	public void testSetId() {
		node.setId(NOWEID);
		assertEquals(NOWEID, node.getId());
	}

	@Test
	public void testSetChildren() {
		assertNotEquals(null, node.getChildren());
		node.setChildren(null);
		assertEquals(null, node.getChildren());

		LinkedList<Node> children = new LinkedList<>();
		children.add(child);
		node.setChildren(children);
		assertEquals(children, node.getChildren());
	}

	@Test
	public void testAddChild() {
		assertEquals(1, node.getChildren().size());
		LinkedList<Instance> thirdInstances = new LinkedList<>();
		thirdInstances.add(new BasicInstance(FIRST, GEN_0_2, new double[] { 0.3, -0.5 }, null));
		BasicNode thirdNode = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), thirdInstances, false);
		node.addChild(thirdNode);
		assertEquals(2, node.getChildren().size());
	}

	@Test
	public void testAddInstance() {
		assertEquals(3, node.getNodeInstances().size());
		node.addInstance(new BasicInstance(FIRST, GEN_0_2, new double[] { 0.3, -0.5 }, null));
		assertEquals(4, node.getNodeInstances().size());
	}

	@Test
	public void testSetInstances() {
		LinkedList<Instance> thirdInstances = new LinkedList<>();
		thirdInstances.add(new BasicInstance(FIRST, GEN_0_2, new double[] { 0.3, -0.5 }, null));
		assertEquals(3, node.getNodeInstances().size());
		node.setInstances(thirdInstances);
		assertEquals(1, node.getNodeInstances().size());
		assertEquals(thirdInstances, node.getNodeInstances());
	}

	@Test
	public void testSetRepresentation() {
		assertArrayEquals(new double[] { 0.5, 1 }, node.getNodeRepresentation().getData(), 0.0);
		node.setRepresentation(new BasicInstance(FIRST, "Id", new double[] { 0.3, -0.5 }, null));
		assertArrayEquals(new double[] { 0.3, -0.5 }, node.getNodeRepresentation().getData(), 0.0);
	}

	@Test
	public void testGetId() {
		assertEquals("gen.0", node.getId());
	}

	@Test
	public void testGetParent() {
		assertEquals(node, child.getParent());
	}

	@Test
	public void testGetParentId() {
		assertEquals("gen.0", child.getParentId());
	}

	@Test
	public void testGetChildren() {
		assertEquals(child, node.getChildren().get(0));
	}

	@Test
	public void testGetNodeInstances() {
		assertEquals(3, node.getNodeInstances().size());
		assertArrayEquals(new double[] { 0.0, 0.5 }, node.getNodeInstances().get(0).getData(), 0.0);

	}

	@Test
	public void testGetSubtreeInstances() {
		assertEquals(3, node.getNodeInstances().size());
		assertEquals(7, node.getSubtreeInstances().size());
		assertArrayEquals(new double[] { 0.0, 0.5 }, node.getSubtreeInstances().get(0).getData(), 0.0);
		assertArrayEquals(new double[] { 0.0, -1.5 }, node.getSubtreeInstances().get(6).getData(), 0.0);
	}

	@Test
	public void testGetNodeRepresentation() {
		assertArrayEquals(new double[] { 0.5, 1 }, node.getNodeRepresentation().getData(), 0.0);
		assertArrayEquals(new double[] { -0.25, -1 }, child.getNodeRepresentation().getData(), 0.0);
	}

	@Test
	public void testToString() {
		LinkedList<Instance> thirdInstances = new LinkedList<>();
		thirdInstances.add(new BasicInstance(FIRST, GEN_0_2, new double[] { 0.3, -0.5 }, null));
		BasicNode thirdNode = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), thirdInstances, false);
		node.addChild(thirdNode);
		assertEquals("L--gen.0(3)|--gen.0.0(4)L--gen.0(1)", node.toString().replaceAll("\\s", ""));
	}

	@Test
	public void testPrintSubtree() {
		LinkedList<Instance> thirdInstances = new LinkedList<>();
		thirdInstances.add(new BasicInstance(FIRST, GEN_0_2, new double[] { 0.3, -0.5 }, null));
		BasicNode thirdNode = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), thirdInstances, false);
		node.addChild(thirdNode);
		node.printSubtree();
		assertEquals("L--gen.0(3)|--gen.0.0(4)L--gen.0(1)", outContent.toString().replaceAll("\\s", ""));
	}

}