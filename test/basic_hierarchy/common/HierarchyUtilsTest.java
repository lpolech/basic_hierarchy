package basic_hierarchy.common;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import basic_hierarchy.implementation.BasicHierarchy;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;

public class HierarchyUtilsTest {

	private static final String GEN_0_3 = "gen.0.3";
	private static final String GEN_0_1 = "gen.0.1";
	private static final String GEN_0_0 = "gen.0.0";
	private static final String GEN_0 = "gen.0";
	private static final String GEN_0_2 = "gen.0.2";
	private static final String GEN_0_1_1 = "gen.0.1.1";
	private static final String GEN_0_0_0 = "gen.0.0.0";

	/**
	 * Feature values' array is copied by reference when cloning Instances (memory
	 * optimization)
	 */
	private static final boolean FEATURE_ARRAY_OPTIMIZATION = true;

	Hierarchy alpha = null;
	Hierarchy fourGroupsHierarchy;
	private static Random random;

	@BeforeClass
	public static void init() {
		random = new SecureRandom();
	}

	@Before
	public void setup() {
		alpha = generateHierarchy(3000, 2, GEN_0, GEN_0_0, GEN_0_1, GEN_0_2, GEN_0_3, GEN_0_1_1);
		fourGroupsHierarchy = basic_hierarchy.TestCommon.getFourGroupsHierarchy();
	}

	@Test
	public void testGetOneClusterHierarchy() {
		Hierarchy h = basic_hierarchy.TestCommon.getTwoGroupsHierarchy();
		assertEquals(2, h.getNumberOfGroups());

		Hierarchy h2 = HierarchyUtils.getOneClusterHierarchy(h);
		assertEquals(1, h2.getNumberOfGroups());
	}

	@Test
	public void testGetNumberOfSubtreeGroups() {
		BasicNode node;
		BasicNode child;
		String nodeId = Constants.ROOT_ID;
		String firstChildNodeId = HierarchyUtils.getIDOfChildCluster(nodeId, 0);

		node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), new LinkedList<>(), false);
		child = new BasicNode(firstChildNodeId, node, new LinkedList<Node>(), new LinkedList<>(), false);
		node.addChild(child);
		assertEquals(2, HierarchyUtils.getNumberOfSubtreeGroups(node));

	}

	@Test
	public void testClone() {
		Hierarchy test = HierarchyUtils.clone(alpha, false, null);
		Assert.assertFalse(test == alpha);

		compareHierarchies(alpha, test);
	}

	@Test
	public void testSubHierarchy() {
		testSubHierarchy(alpha, GEN_0_2, Constants.ROOT_ID);
		testSubHierarchy(alpha, GEN_0_1_1, Constants.ROOT_ID);
	}

	@Test
	public void testMerge() {
		Hierarchy test = HierarchyUtils.subHierarchy(alpha, GEN_0_2, Constants.ROOT_ID);
		testMerge(alpha, test, GEN_0_2);

		test = HierarchyUtils.subHierarchy(alpha, GEN_0_1_1, "gen.0.8.4.3");
		testMerge(alpha, test, GEN_0_1_1);
	}

	// -------------------------------------------------------------

	public static void testSubHierarchy(Hierarchy alphaH, String srcId, String destId) {
		Hierarchy testH = HierarchyUtils.subHierarchy(alphaH, srcId, destId);

		List<Node> alphaNs = new ArrayList<>(Arrays.asList(alphaH.getGroups()));
		alphaNs.removeIf(n -> !n.getId().contains(srcId));
		List<Node> testNs = Arrays.asList(testH.getGroups());

		Assert.assertEquals(alphaNs.size(), testNs.size());

		final int _endi = alphaNs.size();
		for (int i = 0; i < _endi; ++i) {
			Node alphaN = alphaNs.get(i);
			Node testN = testNs.get(i);

			Assert.assertFalse(alphaN == testN);
			String alphaId = alphaN.getId().replace(srcId, "");
			String testId = testN.getId().replace(destId, "");
			Assert.assertEquals(alphaId, testId);

			List<Instance> alphaIs = alphaN.getNodeInstances();
			List<Instance> testIs = testN.getNodeInstances();
			Assert.assertEquals(alphaIs.size(), testIs.size());

			final int _endj = alphaIs.size();
			for (int j = 0; j < _endj; ++j) {
				Instance alphaI = alphaIs.get(j);
				Instance testI = testIs.get(j);

				alphaId = alphaI.getNodeId().replace(srcId, "");
				testId = testI.getNodeId().replace(destId, "");
				Assert.assertEquals(alphaId, testId);

				Assert.assertFalse(alphaI == testI);
				Assert.assertEquals(alphaI.getTrueClass(), testI.getTrueClass());
				Assert.assertEquals(alphaI.getInstanceName(), testI.getInstanceName());
				if (FEATURE_ARRAY_OPTIMIZATION) {
					Assert.assertEquals(alphaI.getData(), testI.getData());
				} else {
					Assert.assertFalse(alphaI.getData() == testI.getData());
					Assert.assertArrayEquals(alphaI.getData(), testI.getData(), 0);
				}
			}
		}
	}

	public static void testMerge(Hierarchy alphaH, Hierarchy testH, String mergeNodeId) {
		testH = HierarchyUtils.merge(testH, alphaH, mergeNodeId);
		compareHierarchies(alphaH, testH);
	}

	/**
	 * Compares the two hierarchies for deep equality, while also asserting that
	 * they don't contain the same objects (by reference).
	 * 
	 * @param a the first hierarchy
	 * @param b the second hierarchy
	 */
	public static void compareHierarchies(Hierarchy a, Hierarchy b) {
		Assert.assertFalse(a == b);
		Assert.assertEquals(a.getRoot().getId(), b.getRoot().getId());
		Assert.assertEquals(a.getOverallNumberOfInstances(), b.getOverallNumberOfInstances());
		Assert.assertArrayEquals(a.getClasses(), b.getClasses());
		Assert.assertArrayEquals(a.getClassesCount(), b.getClassesCount());
		Assert.assertArrayEquals(a.getDataNames(), b.getDataNames());

		List<Node> aNodes = Arrays.asList(a.getGroups());
		List<Node> bNodes = Arrays.asList(b.getGroups());
		Assert.assertEquals(aNodes.size(), bNodes.size());

		final int _endi = aNodes.size();
		for (int i = 0; i < _endi; ++i) {
			Node aN = aNodes.get(i);
			Node bN = bNodes.get(i);

			compareNodes(aN, bN);
		}
	}

	public static void compareNodes(Node a, Node b) {
		Assert.assertFalse(a == b);
		Assert.assertEquals(a.getId(), b.getId());
		if (a.getParent() != null) {
			Assert.assertEquals(a.getParent().getId(), b.getParent().getId());
		}

		// Node representations are created artificially, therefore they can't
		// reference the same feature values array object.
		compareInstances(a.getNodeRepresentation(), b.getNodeRepresentation(), false);

		List<Instance> aIs = a.getNodeInstances();
		List<Instance> bIs = b.getNodeInstances();
		Assert.assertEquals(aIs.size(), bIs.size());
		final int _endi = aIs.size();
		for (int i = 0; i < _endi; ++i) {
			compareInstances(aIs.get(i), bIs.get(i), FEATURE_ARRAY_OPTIMIZATION);
		}
	}

	/**
	 * @param a                        the first instance to compare
	 * @param b                        the second instance to compare
	 * @param featureArrayOptimization whether to assume that when instances are
	 *                                 cloned, their feature values' array is copied
	 *                                 by reference instead of deeply cloned (memory
	 *                                 optimization)
	 */
	public static void compareInstances(Instance a, Instance b, boolean featureArrayOptimization) {
		if (a == null || b == null)
			return;

		Assert.assertFalse(a == b);
		Assert.assertEquals(a.getNodeId(), b.getNodeId());
		Assert.assertEquals(a.getTrueClass(), b.getTrueClass());
		Assert.assertEquals(a.getInstanceName(), b.getInstanceName());
		if (featureArrayOptimization) {
			Assert.assertTrue(a.getData() == b.getData());
		} else {
			Assert.assertFalse(a.getData() == b.getData());
			Assert.assertArrayEquals(a.getData(), b.getData(), 0);
		}
	}

	@Test
	public void testGetClassCountMap() {
		Map<String, Integer> map = HierarchyUtils.getClassCountMap(fourGroupsHierarchy);
		assertEquals(4, (int) map.get(GEN_0));
		assertEquals(2, (int) map.get(GEN_0_0));
		assertEquals(3, (int) map.get(GEN_0_0_0));
		assertEquals(2, (int) map.get(GEN_0_1));
	}

	@Test
	public void testComputeClassCountMap() {
		Map<String, Integer> map = HierarchyUtils.computeClassCountMap(fourGroupsHierarchy.getRoot());
		assertEquals(4, (int) map.get(GEN_0));
		assertEquals(2, (int) map.get(GEN_0_0));
		assertEquals(3, (int) map.get(GEN_0_0_0));
		assertEquals(2, (int) map.get(GEN_0_1));
	}

	@Test
	public void testFlattenHierarchy() {
		assertEquals(4, fourGroupsHierarchy.getNumberOfGroups());
		Hierarchy newL = HierarchyUtils.flattenHierarchy(fourGroupsHierarchy);
		assertEquals(1, newL.getNumberOfGroups());
	}

	@Test
	public void testToCSV() {
		String s1 = "1.0;2.0\n3.0;4.0\n1.5;2.5\n3.5;4.5\n0.5;0.5\n-0.5;-0.5\n3.5;-0.5\n"
				+ "0.25;0.75\n-1.5;2.5\n3.5;-4.5\n-3.5;-4.5\n";
		assertEquals(s1, HierarchyUtils.toCSV(fourGroupsHierarchy, false, false, false, false));

		String s2 = "class;dimension_0;dimension_1\ngen.0;1.0;2.0\ngen.0;3.0;4.0\ngen.0.0;1.5;2.5\n"
				+ "gen.0.0;3.5;4.5\ngen.0.0.0;0.5;0.5\ngen.0.0.0;-0.5;-0.5\ngen.0.0.0;3.5;-0.5\n"
				+ "gen.0.0.0;0.25;0.75\ngen.0.1;-1.5;2.5\ngen.0.1;3.5;-4.5\ngen.0.1;-3.5;-4.5\n";
		assertEquals(s2, HierarchyUtils.toCSV(fourGroupsHierarchy, true, false, false, true));

		String s3 = "true_class;dimension_0;dimension_1\ngen.0;1.0;2.0\ngen.0;3.0;4.0\ngen.0.0;1.5;2.5\n"
				+ "gen.0.0.0;3.5;4.5\ngen.0.0.0;0.5;0.5\ngen.0.0.0;-0.5;-0.5\ngen.0.1;3.5;-0.5\n"
				+ "gen.0;0.25;0.75\ngen.0.0;-1.5;2.5\ngen.0;3.5;-4.5\ngen.0.1;-3.5;-4.5\n";
		assertEquals(s3, HierarchyUtils.toCSV(fourGroupsHierarchy, false, true, false, true));

		String s4 = "instance_name;dimension_0;dimension_1\n11;1.0;2.0\n12;3.0;4.0\n21;1.5;2.5\n"
				+ "22;3.5;4.5\n41;0.5;0.5\n42;-0.5;-0.5\n43;3.5;-0.5\n44;0.25;0.75\n"
				+ "31;-1.5;2.5\n32;3.5;-4.5\n33;-3.5;-4.5\n";
		assertEquals(s4, HierarchyUtils.toCSV(fourGroupsHierarchy, false, false, true, true));

		String s5 = "dimension_0;dimension_1\n1.0;2.0\n3.0;4.0\n1.5;2.5\n3.5;4.5\n0.5;0.5\n"
				+ "-0.5;-0.5\n3.5;-0.5\n0.25;0.75\n-1.5;2.5\n3.5;-4.5\n-3.5;-4.5\n";
		assertEquals(s5, HierarchyUtils.toCSV(fourGroupsHierarchy, false, false, false, true));
	}

	@Test
	public void testRemove() {
		Hierarchy newH = HierarchyUtils.remove(fourGroupsHierarchy, GEN_0_1);
		assertEquals(11, fourGroupsHierarchy.getOverallNumberOfInstances());
		assertEquals(8, newH.getOverallNumberOfInstances());
	}

	@Test
	public void testContains() {
		assertEquals(true, HierarchyUtils.contains(fourGroupsHierarchy, fourGroupsHierarchy.getRoot()));
		assertEquals(false, HierarchyUtils.contains(fourGroupsHierarchy, new BasicNode("nowe", null, false)));
	}

	@Test
	public void testGetFirstInstance() {
		assertEquals(fourGroupsHierarchy.getRoot().getNodeInstances().get(0),
				HierarchyUtils.getFirstInstance(fourGroupsHierarchy));
	}

	@Test
	public void testGetFeatureCount() {
		assertEquals(2, HierarchyUtils.getFeatureCount(fourGroupsHierarchy));
	}

	@Test
	public void testGetIDOfChildCluster() {
		assertEquals(GEN_0_1, HierarchyUtils.getIDOfChildCluster(GEN_0, 1));
	}

	@Test
	public void testGetAllNodes() {
		List<Node> l = new ArrayList<>(Arrays.asList(fourGroupsHierarchy.getGroups()));
		assertTrue(l.equals(HierarchyUtils.getAllNodes(fourGroupsHierarchy)));
	}

	@Test
	public void testDropDimensionsDoubleArrayListOfInteger() {
		double[] initDim = new double[] { 1.0, 2.0, 3.0 };
		List<Integer> dimensionNumbers = new ArrayList<>();
		dimensionNumbers.add(1);
		double[] outDim = new double[] { 2.0 };
		assertArrayEquals(outDim, HierarchyUtils.dropDimensions(initDim, dimensionNumbers), 0.0);
	}

	@Test
	public void testDropDimensionsStringArrayListOfInteger() {
		String[] initDim = new String[] { "1.0", "2.0", "3.0" };
		List<Integer> dimensionNumbers = new ArrayList<>();
		dimensionNumbers.add(1);
		String[] outDim = new String[] { "2.0" };
		assertArrayEquals(outDim, HierarchyUtils.dropDimensions(initDim, dimensionNumbers));
	}

	@Test
	public void testWrapNode() {
		Node node = fourGroupsHierarchy.getRoot().getChildren().getFirst();
		assertNotNull(HierarchyUtils.wrapNode(fourGroupsHierarchy, node, false));
	}

	@Test
	public void testRebase() {
		Stream<Node> nodes = fourGroupsHierarchy.getRoot().getChildren().stream();
		String oldId = GEN_0_0;
		List<BasicNode> destNodes = new ArrayList<>();
		HierarchyUtils.rebase(nodes, oldId, GEN_0, destNodes, true);
		assertTrue(!destNodes.isEmpty());
	}

	@Test
	public void testToMatrix() {
		double[][] expected = new double[][] { { 1, 2 }, { 3, 4 }, { 1.5, 2.5 }, { 3.5, 4.5 }, { 0.5, 0.5 },
				{ -0.5, -0.5 }, { 3.5, -0.5 }, { 0.25, 0.75 }, { -1.5, 2.5 }, { 3.5, -4.5 }, { -3.5, -4.5 } };
		double[][] output = HierarchyUtils.toMatrix(fourGroupsHierarchy);

		if (expected.length != output.length) {
			fail("expected.length (" + expected.length + ") != (" + output.length + ")output.length");
		}
		for (int i = 0; i < expected.length; i++) {
			assertArrayEquals(expected[i], output[i], 0.0);
		}

	}

	@Test
	public void testGetHierarchyNames() {
		String[] names = new String[] { "dimension 1", "dimension 2" };
		assertArrayEquals(names, HierarchyUtils.getHierarchyNames(fourGroupsHierarchy));
	}

	@Test
	public void testSave() {
		String path = "testOut.csv";
		try {
			HierarchyUtils.save(path, fourGroupsHierarchy, false, false, false, false);
			File file = new File(path);
			assertTrue(file.exists());
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
	// -------------------------------------------------------------

	public BasicHierarchy generateHierarchy(int instanceCount, int dimCount, String... ids) {

		int nodeCount = ids.length;
		int currentInstanceCount = 0;
		final int avgInstancePerNode = instanceCount / nodeCount;

		List<BasicNode> nodes = new ArrayList<>();
		for (int i = 0; i < nodeCount; ++i) {
			int nodeInstanceCount = Math.max(1, (int) (avgInstancePerNode * (random.nextGaussian() + 1)));
			if (i == nodeCount - 1 && currentInstanceCount + nodeInstanceCount < instanceCount) {
				nodeInstanceCount = instanceCount - currentInstanceCount;
			}

			currentInstanceCount += nodeInstanceCount;
			nodes.add(generateNode(ids[i], nodeInstanceCount, dimCount));
		}

		nodes.sort(new NodeIdComparator());
		BasicNode root = nodes.get(0);
		if (!root.getId().equals(Constants.ROOT_ID)) {
			root = null;
		}

		HierarchyBuilder hb = new HierarchyBuilder();
		List<? extends Node> allNodes = hb.buildCompleteHierarchy(root, nodes, false, false);

		return new BasicHierarchy(allNodes, null);
	}

	public BasicNode generateNode(String id, int instanceCount, int dimCount) {
		BasicNode node = new BasicNode(id, null, false);
		for (int i = 0; i < instanceCount; ++i) {
			node.addInstance(generateInstance(id, dimCount));
		}
		return node;
	}

	public BasicInstance generateInstance(String id, int dimCount) {
		double[] data = new double[dimCount];
		for (int i = 0; i < dimCount; i++) {
			data[i] = random.nextDouble() * 2 - 1;
		}
		return new BasicInstance(null, id, data);
	}
}
