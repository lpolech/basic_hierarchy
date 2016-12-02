package basic_hierarchy.test.implementation;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import basic_hierarchy.common.Constants;
import basic_hierarchy.common.HierarchyBuilder;
import basic_hierarchy.implementation.BasicNode;


public class HierarchyBuilderTest
{
	List<BasicNode> nodes;
	BasicNode root;


	@Before
	public void setup()
	{
		nodes = new ArrayList<>();

		root = new BasicNode( Constants.ROOT_ID, null, false );

		nodes.add( root );
		nodes.add( new BasicNode( root.getId() + ".0.1", null, false ) );
		nodes.add( new BasicNode( root.getId() + ".0.0.3", null, false ) );
		nodes.add( new BasicNode( root.getId() + ".0.0.5", null, false ) );
	}

	@Test
	public void fixDepthGaps() throws Exception
	{
		// Creates:
		// - gen.0.0
		// - gen.0.0.0
		List<BasicNode> artificial = HierarchyBuilder.fixDepthGaps( root, nodes, false );

		// Assert that no unexpected nodes have been created
		Assert.assertEquals( artificial.size(), 2 );

		BasicNode artificial0 = artificial.get( 0 ); // gen.0.0
		BasicNode artificial1 = artificial.get( 1 ); // gen.0.0.0

		BasicNode leaf0 = nodes.get( 1 ); // gen.0.0.1
		BasicNode leaf1 = nodes.get( 2 ); // gen.0.0.0.3
		BasicNode leaf2 = nodes.get( 3 ); // gen.0.0.0.5

		// Assert child -> parent relations
		Assert.assertEquals( artificial0.getParent(), root );
		Assert.assertEquals( artificial1.getParent(), artificial0 );
		Assert.assertEquals( leaf0.getParent(), artificial0 );
		Assert.assertEquals( leaf1.getParent(), artificial1 );
		Assert.assertEquals( leaf2.getParent(), artificial1 );

		// Assert parent -> child relations
		Assert.assertTrue( root.getChildren().contains( artificial0 ) );
		Assert.assertTrue( artificial0.getChildren().contains( artificial1 ) );
		Assert.assertTrue( artificial0.getChildren().contains( leaf0 ) );
		Assert.assertTrue( artificial1.getChildren().contains( leaf1 ) );
		Assert.assertTrue( artificial1.getChildren().contains( leaf2 ) );
	}

	@Test
	public void fixBreadthGaps() throws Exception
	{
		// Creates:
		// - gen.0.0
		// - gen.0.0.0
		List<BasicNode> artificialDepth = HierarchyBuilder.fixDepthGaps( root, nodes, false );

		// Creates:
		// - gen.0.0.0.0
		// - gen.0.0.0.1
		// - gen.0.0.0.2
		// - gen.0.0.0.4
		List<BasicNode> artificialBreadth = HierarchyBuilder.fixBreadthGaps( root, false );

		BasicNode artificial1 = artificialDepth.get( 1 ); // gen.0.0.0

		Assert.assertEquals( artificialBreadth.size(), 4 );
		Assert.assertTrue( artificial1.getChildren().containsAll( artificialBreadth ) );

		for ( BasicNode leaf : artificialBreadth ) {
			Assert.assertEquals( leaf.getParent(), artificial1 );
		}
	}
}
