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
        nodes.add( new BasicNode( "gen.0.0.10", null, false ) );
        nodes.add( new BasicNode( "gen.0.0.11.3", null, false ) );
        nodes.add( new BasicNode( "gen.0.0.11.5", null, false ) );
    }

    @Test
    public void assertBranchSeparatorSingleChar()
    {
        // HierarchyBuilder makes optimizations under the assumption that the branch
        // separator for node IDs is a single character.
        Assert.assertEquals( 1, Constants.HIERARCHY_BRANCH_SEPARATOR.length() );
    }

    @Test
    public void fixDepthGaps() throws Exception
    {
        // Creates:
        // - gen.0.0
        // - gen.0.0.11
        List<BasicNode> artificial = HierarchyBuilder.fixDepthGaps( root, nodes, false );

        // Assert that no unexpected nodes have been created
        Assert.assertEquals( 2, artificial.size() );

        BasicNode artificial0 = artificial.get( 0 ); // gen.0.0
        BasicNode artificial1 = artificial.get( 1 ); // gen.0.0.11

        BasicNode leaf0 = nodes.get( 1 ); // gen.0.0.10
        BasicNode leaf1 = nodes.get( 2 ); // gen.0.0.11.3
        BasicNode leaf2 = nodes.get( 3 ); // gen.0.0.11.5

        // Assert child -> parent relations
        Assert.assertEquals( root, artificial0.getParent() );
        Assert.assertEquals( artificial0, artificial1.getParent() );
        Assert.assertEquals( artificial0, leaf0.getParent() );
        Assert.assertEquals( artificial1, leaf1.getParent() );
        Assert.assertEquals( artificial1, leaf2.getParent() );

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
        // - gen.0.0.11
        List<BasicNode> artificialDepth = HierarchyBuilder.fixDepthGaps( root, nodes, false );

        // Creates:
        // - gen.0.0.[0-9]
        // - gen.0.0.11.0
        // - gen.0.0.11.1
        // - gen.0.0.11.2
        // - gen.0.0.11.4
        List<BasicNode> artificialBreadth = HierarchyBuilder.fixBreadthGaps( root, false );

        BasicNode artificial1 = artificialDepth.get( 1 ); // gen.0.0.11

        Assert.assertEquals( 14, artificialBreadth.size() );

        // Compiling under Java 7, can't use lambdas...
        List<BasicNode> gen11 = new ArrayList<BasicNode>();
        for ( BasicNode n : artificialBreadth ) {
            if ( n.getId().startsWith( "gen.0.0.0.11." ) )
                gen11.add( n );
        }

        Assert.assertEquals( 4, gen11.size() );

        Assert.assertTrue( artificial1.getChildren().containsAll( gen11 ) );

        for ( BasicNode leaf : gen11 ) {
            Assert.assertEquals( artificial1, leaf.getParent() );
        }
    }
}
