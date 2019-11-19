package basic_hierarchy.test.implementation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Test;

import basic_hierarchy.common.Constants;
import basic_hierarchy.common.HierarchyUtils;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;
import basic_hierarchy.test.TestCommon;

public class BasicHierarchyTest {
	@Test
	public void generateFlatClusteringFromHierarchy() {
		Hierarchy h = TestCommon.getFourGroupsHierarchy();
		Hierarchy flat = h.getFlatClusteringWithCommonEmptyRoot();

		assertEquals(h.getGroups().length + 1, flat.getGroups().length);
		assertEquals(h.getGroups().length, flat.getRoot().getChildren().size());

		for (int i = 0; i < flat.getRoot().getChildren().size(); i++) {
			Node n = flat.getRoot().getChildren().get(i);
			assertTrue(n.getChildren().isEmpty());
			assertEquals(n.getId(), TestCommon.getIDOfChildCluster(Constants.ROOT_ID, i));
			assertEquals(flat.getRoot(), n.getParent());

//          find corresponding child in original hierarchy in compare content
			boolean foundCorrespondingNode = false;
			for (int j = 0; j < h.getGroups().length && !foundCorrespondingNode; j++) {
				Node origN = h.getGroups()[j];

				LinkedList<String> origInstNames = getCollectionOfInstanceNames(origN.getNodeInstances());
				LinkedList<String> nInstNames = getCollectionOfInstanceNames(n.getNodeInstances());

				foundCorrespondingNode = (origN.getNodeInstances().size() == n.getNodeInstances().size())
						&& origInstNames.containsAll(nInstNames);
			}
			assertTrue(foundCorrespondingNode);
		}
	}

	@Test
	public void generateFlatClusteringFromHierarchyWithEmptyNodes() {
		Hierarchy h = TestCommon.getTwoGroupsHierarchyWithEmptyNodes();
		Hierarchy flat = h.getFlatClusteringWithCommonEmptyRoot();

		assertEquals(2 + 1, flat.getGroups().length);// two groups + one artificial root
		assertEquals(2, flat.getRoot().getChildren().size());

		for (int i = 0; i < flat.getRoot().getChildren().size(); i++) {
			Node n = flat.getRoot().getChildren().get(i);
			assertTrue(n.getChildren().isEmpty());
			assertEquals(n.getId(), TestCommon.getIDOfChildCluster(Constants.ROOT_ID, i));
			assertEquals(flat.getRoot(), n.getParent());

//          find corresponding child in original hierarchy in compare content
			boolean foundCorrespondingNode = false;
			for (int j = 0; j < h.getGroups().length && !foundCorrespondingNode; j++) {
				Node origN = h.getGroups()[j];

				LinkedList<String> origInstNames = getCollectionOfInstanceNames(origN.getNodeInstances());
				LinkedList<String> nInstNames = getCollectionOfInstanceNames(n.getNodeInstances());

				foundCorrespondingNode = (origN.getNodeInstances().size() == n.getNodeInstances().size())
						&& origInstNames.containsAll(nInstNames);
			}
			assertTrue(foundCorrespondingNode);
		}
	}

	@Test
	public void generateOneClusterFromHierarchy() {
		Hierarchy h = TestCommon.getFourGroupsHierarchy();
		Hierarchy oneCluster = HierarchyUtils.getOneClusterHierarchy(h);

		assertEquals(1, oneCluster.getGroups().length);
		assertEquals(0, oneCluster.getRoot().getChildren().size());
		assertNull(oneCluster.getRoot().getParent());
		assertEquals(oneCluster.getRoot().getNodeInstances().size(), h.getRoot().getSubtreeInstances().size());

		LinkedList<String> oneClusterInstancesIDs = getCollectionOfInstanceNames(
				oneCluster.getRoot().getNodeInstances());
		LinkedList<String> origClustersInstancesIDs = getCollectionOfInstanceNames(h.getRoot().getSubtreeInstances());

		assertTrue(oneClusterInstancesIDs.containsAll(origClustersInstancesIDs));
		assertTrue(origClustersInstancesIDs.size() == oneClusterInstancesIDs.size());
	}

	private LinkedList<String> getCollectionOfInstanceNames(LinkedList<Instance> instances) {
		LinkedList<String> result = new LinkedList<>();
		for (Instance i : instances) {
			result.add(i.getInstanceName());
		}

		return result;
	}

}
