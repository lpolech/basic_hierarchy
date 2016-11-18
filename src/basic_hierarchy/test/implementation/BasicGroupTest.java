package basic_hierarchy.test.implementation;

import static org.junit.Assert.assertArrayEquals;

import java.util.LinkedList;

import org.junit.Before;

import basic_hierarchy.common.Constants;
import basic_hierarchy.implementation.BasicGroup;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.interfaces.Group;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.test.TestCommon;


public class BasicGroupTest
{
	private BasicGroup node;
	private BasicGroup child;


	@Before
	public void setUp()
	{
		String nodeId = Constants.ROOT_ID;
		String firstChildNodeId = TestCommon.getIDOfChildCluster( nodeId, 0 );

		LinkedList<Instance> instances = new LinkedList<>();
		instances.add( new BasicInstance( "first", nodeId, new double[] { 0.0, 0.5 }, null ) );
		instances.add( new BasicInstance( "second", nodeId, new double[] { 1.5, 2.0 }, null ) );
		instances.add( new BasicInstance( "third", nodeId, new double[] { 0.0, 0.5 }, null ) );

		LinkedList<Instance> childInstances = new LinkedList<>();
		childInstances.add( new BasicInstance( "first", firstChildNodeId, new double[] { 0.3, -0.5 }, null ) );
		childInstances.add( new BasicInstance( "second", firstChildNodeId, new double[] { 2.7, -2.0 }, null ) );
		childInstances.add( new BasicInstance( "third", firstChildNodeId, new double[] { -4.0, 0.0 }, null ) );
		childInstances.add( new BasicInstance( "fourth", firstChildNodeId, new double[] { 0.0, -1.5 }, null ) );

		node = new BasicGroup( Constants.ROOT_ID, null, new LinkedList<Group>(), instances, false );
		child = new BasicGroup( firstChildNodeId, node, new LinkedList<Group>(), childInstances, false );
		node.addChild( child );
	}

	@org.junit.Test
	public void calculateSimpleCentroid() throws Exception
	{
		assertArrayEquals(
			new double[] { 0.5, 1.0 }, node.getGroupRepresentation().getData(),
			TestCommon.DOUBLE_COMPARISION_DELTA
		);
		assertArrayEquals(
			new double[] { -0.25, -1.0 }, child.getGroupRepresentation().getData(),
			TestCommon.DOUBLE_COMPARISION_DELTA
		);
	}

	@org.junit.Test
	public void calculateCentroidWithSubtree() throws Exception
	{
		node.recalculateCentroid( true );

		assertArrayEquals(
			new double[] { 0.071428571, -0.142857143 }, node.getGroupRepresentation().getData(),
			TestCommon.DOUBLE_COMPARISION_DELTA
		);
		assertArrayEquals(
			new double[] { -0.25, -1.0 }, child.getGroupRepresentation().getData(),
			TestCommon.DOUBLE_COMPARISION_DELTA
		);
	}
}
