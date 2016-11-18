package basic_hierarchy.test;

import java.util.HashMap;
import java.util.LinkedList;

import basic_hierarchy.common.Constants;
import basic_hierarchy.implementation.BasicGroup;
import basic_hierarchy.implementation.BasicHierarchy;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.interfaces.Group;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;


public class TestCommon
{
	public static final double DOUBLE_COMPARISION_DELTA = 1e-9;


	public static String getIDOfChildCluster( String parentId, int childNumber )
	{
		return parentId + Constants.HIERARCHY_BRANCH_SEPARATOR + childNumber;
	}

	public static Hierarchy getTwoGroupsHierarchy()
	{
		String rootId = Constants.ROOT_ID;
		String childId = TestCommon.getIDOfChildCluster( rootId, 0 );

		LinkedList<Instance> firstClusterInstances = new LinkedList<>();
		firstClusterInstances.add( new BasicInstance( "11", rootId, new double[] { 1.0, 2.0 }, rootId ) );
		firstClusterInstances.add( new BasicInstance( "12", rootId, new double[] { 3.0, 4.0 }, rootId ) );
		BasicGroup firstCluster = new BasicGroup( rootId, null, new LinkedList<Group>(), firstClusterInstances, false );

		LinkedList<Instance> secondClusterInstances = new LinkedList<Instance>();
		secondClusterInstances.add( new BasicInstance( "21", childId, new double[] { 1.5, 2.5 }, childId ) );
		secondClusterInstances.add( new BasicInstance( "22", childId, new double[] { 3.5, 4.5 }, rootId ) );
		BasicGroup secondCluster = new BasicGroup( childId, firstCluster, new LinkedList<Group>(), secondClusterInstances, false );

		firstCluster.addChild( secondCluster );

		LinkedList<Group> groups = new LinkedList<>();
		groups.add( firstCluster );
		groups.add( secondCluster );
		HashMap<String, Integer> eachClassWithCount = new HashMap<>();
		eachClassWithCount.put( rootId, 3 );
		eachClassWithCount.put( childId, 1 );

		String[] dataNames = { "x", "y" };
		return new BasicHierarchy( firstCluster, groups, dataNames, eachClassWithCount );
	}

	public static Hierarchy getFourGroupsHierarchy()
	{
		String rootId = Constants.ROOT_ID;
		String rootFirstChildId = getIDOfChildCluster( rootId, 0 );
		String rootSecondChildId = getIDOfChildCluster( rootId, 1 );
		String rootFirstChildFirstChildId = getIDOfChildCluster( rootFirstChildId, 0 );

		LinkedList<Instance> rootClusterInstances = new LinkedList<>();
		rootClusterInstances.add( new BasicInstance( "11", rootId, new double[] { 1.0, 2.0 }, rootId ) );
		rootClusterInstances.add( new BasicInstance( "12", rootId, new double[] { 3.0, 4.0 }, rootId ) );
		BasicGroup rootCluster = new BasicGroup(
			rootId, null, new LinkedList<Group>(), rootClusterInstances, false
		);

		LinkedList<Instance> rootFirstChildInstances = new LinkedList<Instance>();
		rootFirstChildInstances.add( new BasicInstance( "21", rootFirstChildId, new double[] { 1.5, 2.5 }, rootFirstChildId ) );
		rootFirstChildInstances.add( new BasicInstance( "22", rootFirstChildId, new double[] { 3.5, 4.5 }, rootFirstChildFirstChildId ) );
		BasicGroup rootFirstChildCluster = new BasicGroup(
			rootFirstChildId, rootCluster, new LinkedList<Group>(), rootFirstChildInstances, false
		);

		LinkedList<Instance> rootSecondChildInstances = new LinkedList<Instance>();
		rootSecondChildInstances.add( new BasicInstance( "31", rootSecondChildId, new double[] { -1.5, 2.5 }, rootFirstChildId ) );
		rootSecondChildInstances.add( new BasicInstance( "32", rootSecondChildId, new double[] { 3.5, -4.5 }, rootId ) );
		rootSecondChildInstances.add( new BasicInstance( "33", rootSecondChildId, new double[] { -3.5, -4.5 }, rootSecondChildId ) );
		BasicGroup rootSecondChildCluster = new BasicGroup(
			rootSecondChildId, rootCluster, new LinkedList<Group>(), rootSecondChildInstances, false
		);

		LinkedList<Instance> rootFirstChildFirstChildInstances = new LinkedList<Instance>();
		rootFirstChildFirstChildInstances.add(
			new BasicInstance( "41", rootFirstChildFirstChildId, new double[] { 0.5, 0.5 }, rootFirstChildFirstChildId )
		);
		rootFirstChildFirstChildInstances.add(
			new BasicInstance( "42", rootFirstChildFirstChildId, new double[] { -0.5, -0.5 }, rootFirstChildFirstChildId )
		);
		rootFirstChildFirstChildInstances.add(
			new BasicInstance( "43", rootFirstChildFirstChildId, new double[] { 3.5, -0.5 }, rootSecondChildId )
		);
		rootFirstChildFirstChildInstances.add(
			new BasicInstance( "44", rootFirstChildFirstChildId, new double[] { 0.25, 0.75 }, rootId )
		);
		BasicGroup rootFirstChildFirstChildCluster = new BasicGroup(
			rootFirstChildFirstChildId, rootFirstChildCluster, new LinkedList<Group>(), rootFirstChildFirstChildInstances, false
		);

		rootCluster.addChild( rootFirstChildCluster );
		rootCluster.addChild( rootSecondChildCluster );
		rootFirstChildCluster.addChild( rootFirstChildFirstChildCluster );

		LinkedList<Group> groups = new LinkedList<>();
		groups.add( rootCluster );
		groups.add( rootFirstChildCluster );
		groups.add( rootSecondChildCluster );
		groups.add( rootFirstChildFirstChildCluster );
		HashMap<String, Integer> eachClassWithCount = new HashMap<>();
		eachClassWithCount.put( rootId, 4 );
		eachClassWithCount.put( rootFirstChildId, 2 );
		eachClassWithCount.put( rootSecondChildId, 2 );
		eachClassWithCount.put( rootFirstChildFirstChildId, 3 );

		String[] dataNames = { "x", "y" };
		return new BasicHierarchy( rootCluster, groups, dataNames, eachClassWithCount );
	}
}
