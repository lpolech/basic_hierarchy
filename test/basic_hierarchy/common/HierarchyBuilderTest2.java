package basic_hierarchy.common;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.function.IntConsumer;

import org.junit.Test;

import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;
import javafx.util.Pair;

public class HierarchyBuilderTest2 {

	private static final String GEN_0_1 = "gen.0.1";
	private static final String GEN_0_0 = "gen.0.0";
	private static final String GEN_0 = "gen.0";
	private static final String FIRST = "first";
	HierarchyBuilder hier;

	public HierarchyBuilderTest2() {
		hier = new HierarchyBuilder();
	}

	@Test
	public void testGetProgress() {
		assertEquals(0, hier.getProgress());
	}

	@Test
	public void testGetStatusMessage() {
		assertEquals("", hier.getStatusMessage());
	}

	@Test
	public void testBuildCompleteHierarchy() {
		Pair<LinkedList<BasicNode>, BasicNode> pair = crateNodes();
		LinkedList<BasicNode> nodes = pair.getKey();

		List<? extends Node> newNodes = hier.buildCompleteHierarchy(null, nodes, true, false);
		assertEquals(3, newNodes.size());
	}

	@Test
	public void testSortAllChildrenNode() {
		BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(),
				false);
		BasicNode child = new BasicNode(GEN_0_1, null, new LinkedList<Node>(), new LinkedList<Instance>(), false);
		BasicNode child2 = new BasicNode(GEN_0_0, null, new LinkedList<Node>(), new LinkedList<Instance>(), false);
		node.addChild(child);
		node.addChild(child2);

		assertEquals(child, node.getChildren().get(0));
		HierarchyBuilder.sortAllChildren(node);
		assertEquals(child2, node.getChildren().get(0));

	}

	@Test
	public void testSortAllChildrenNodeComparatorOfNode() {
		BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(),
				false);
		BasicNode child = new BasicNode(GEN_0_1, null, new LinkedList<Node>(), new LinkedList<Instance>(), false);
		BasicNode child2 = new BasicNode(GEN_0_0, null, new LinkedList<Node>(), new LinkedList<Instance>(), false);
		node.addChild(child);
		node.addChild(child2);

		assertEquals(child, node.getChildren().get(0));
		HierarchyBuilder.sortAllChildren(node, new NodeIdComparator());
		assertEquals(child2, node.getChildren().get(0));
	}

	@Test
	public void testRecalculateCentroids() {
		Pair<LinkedList<BasicNode>, BasicNode> pair = crateNodes();
		BasicNode node = pair.getValue();
		LinkedList<BasicNode> nodes = pair.getKey();

		String firstChildNodeId = HierarchyUtils.getIDOfChildCluster(Constants.ROOT_ID, 0);
		node.setRepresentation(new BasicInstance(FIRST, firstChildNodeId, new double[] { 0.3, -0.5 }, null));

		assertArrayEquals(new double[] { 0.3, -0.5 }, node.getNodeRepresentation().getData(), 0.0);

		IntConsumer progressReporter = e -> {
		};
		HierarchyBuilder.recalculateCentroids(nodes, false, progressReporter);
		assertArrayEquals(new double[] { 0.5, 1.0 }, node.getNodeRepresentation().getData(), 0.0);
	}

	@Test
	public void testCreateParentChildRelations() {
		BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(),
				false);
		BasicNode child = new BasicNode(GEN_0_0, null, new LinkedList<Node>(), new LinkedList<Instance>(), false);

		LinkedList<BasicNode> nodes = new LinkedList<>();
		nodes.add(node);
		nodes.add(child);
		IntConsumer progressReporter = e -> {
		};

		assertNotEquals(node, child.getParent());
		HierarchyBuilder.createParentChildRelations(nodes, progressReporter);
		assertEquals(node, child.getParent());
	}

	@Test
	public void testGetNodeHeight() {
		BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(),
				false);
		assertEquals(1, HierarchyBuilder.getNodeHeight(node));
	}

	@Test
	public void testGetIdHeight() {
		assertEquals(2, HierarchyBuilder.getIdHeight(GEN_0_1));
	}

	@Test
	public void testGetNodeIdSegments() {
		BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(),
				false);
		assertArrayEquals(new String[] { "0" }, HierarchyBuilder.getNodeIdSegments(node));
	}

	@Test
	public void testAreNodesParentAndChild() {
		BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(),
				false);
		BasicNode child = new BasicNode(GEN_0_1, node, new LinkedList<Node>(), new LinkedList<Instance>(), false);
		assertEquals(true, HierarchyBuilder.areNodesParentAndChild(node, child));
	}

	@Test
	public void testAreNodesAncestorAndDescendant() {
		BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<Instance>(),
				false);
		BasicNode child = new BasicNode(GEN_0_1, node, new LinkedList<Node>(), new LinkedList<Instance>(), false);
		assertEquals(true, HierarchyBuilder.areNodesAncestorAndDescendant(node, child));
	}

	@Test
	public void testAreIdsParentAndChild() {
		assertEquals(false, HierarchyBuilder.areIdsParentAndChild(GEN_0, GEN_0));
		assertEquals(true, HierarchyBuilder.areIdsParentAndChild(GEN_0, GEN_0_0));
		assertEquals(false, HierarchyBuilder.areIdsParentAndChild(GEN_0, "gen.0.0.0"));
	}

	@Test
	public void testAreIdsAncestorAndDescendant() {
		assertEquals(false, HierarchyBuilder.areIdsAncestorAndDescendant(GEN_0, GEN_0));
		assertEquals(true, HierarchyBuilder.areIdsAncestorAndDescendant(GEN_0, GEN_0_1));
		assertEquals(false, HierarchyBuilder.areIdsAncestorAndDescendant(GEN_0, "gen0.1b1"));
		assertEquals(true, HierarchyBuilder.areIdsAncestorAndDescendant("gen", "gen.1.b1"));
		assertEquals(false, HierarchyBuilder.areIdsAncestorAndDescendant("gen", "gen1.b1"));
	}

	private Pair<LinkedList<BasicNode>, BasicNode> crateNodes() {
		String nodeId = Constants.ROOT_ID;

		LinkedList<Instance> instances = new LinkedList<>();
		instances.add(new BasicInstance(FIRST, nodeId, new double[] { 0.0, 0.5 }, null));
		instances.add(new BasicInstance("second", nodeId, new double[] { 1.5, 2.0 }, null));
		instances.add(new BasicInstance("third", nodeId, new double[] { 0.0, 0.5 }, null));

		LinkedList<Instance> childInstances = new LinkedList<>();
		BasicNode rootNode = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), instances, false);
		BasicNode child = new BasicNode(GEN_0_0, null, new LinkedList<Node>(), childInstances, false);

		LinkedList<BasicNode> nodes = new LinkedList<>();
		nodes.add(rootNode);
		nodes.add(child);

		return new Pair<>(nodes, rootNode);
	}

}
