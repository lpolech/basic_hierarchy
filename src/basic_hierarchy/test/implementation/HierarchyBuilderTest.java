package basic_hierarchy.test.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import basic_hierarchy.common.AlphanumComparator;
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
    public void idOrdering()
    {
        // Test that the sorting algorithm works as we expect it to.
        String[] expected = {
            "gen.0.0.1",
            "gen.0.0.1.0",
            "gen.0.0.2",
            "gen.0.0.10",
            "gen.0.1",
            "gen.0.2",
            "gen.0.10.0"
        };

        String[] sorted = Arrays.copyOf( expected, expected.length );
        Arrays.sort( sorted, new AlphanumComparator() );

        Assert.assertArrayEquals( expected, sorted );
    }

    @Test
    public void fixDepthGaps() throws Exception
    {
        // Test that fixDepthGaps algorithm works correctly.

        // Creates:
        // - gen.0.0
        // - gen.0.0.11
        List<BasicNode> artificial = new HierarchyBuilder().fixDepthGaps( root, nodes, false );

        // Assert that no unexpected nodes have been created
        Assert.assertEquals( 2, artificial.size() );

        BasicNode artificial0 = findNodeWithId( artificial, "gen.0.0" );
        BasicNode artificial1 = findNodeWithId( artificial, "gen.0.0.11" );

        BasicNode leaf0 = findNodeWithId( nodes, "gen.0.0.10" );
        BasicNode leaf1 = findNodeWithId( nodes, "gen.0.0.11.3" );
        BasicNode leaf2 = findNodeWithId( nodes, "gen.0.0.11.5" );

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
        // Test that fixBreadthGaps algorithm works correctly.

        HierarchyBuilder hb = new HierarchyBuilder();

        // Creates:
        // - gen.0.0
        // - gen.0.0.11
        List<BasicNode> artificialDepth = hb.fixDepthGaps( root, nodes, false );

        // Creates:
        // - gen.0.0.[0-9]
        // - gen.0.0.11.0
        // - gen.0.0.11.1
        // - gen.0.0.11.2
        // - gen.0.0.11.4
        List<BasicNode> artificialBreadth = hb.fixBreadthGaps( root, false );

        BasicNode artificial1 = findNodeWithId( artificialDepth, "gen.0.0.11" );

        Assert.assertEquals( 14, artificialBreadth.size() );

        // Compiling under Java 7, can't use lambdas...
        List<BasicNode> gen11 = new ArrayList<BasicNode>();
        for ( BasicNode n : artificialBreadth ) {
            if ( n.getId().startsWith( "gen.0.0.11." ) )
                gen11.add( n );
        }

        Assert.assertEquals( 4, gen11.size() );

        Assert.assertTrue( artificial1.getChildren().containsAll( gen11 ) );

        for ( BasicNode leaf : gen11 ) {
            Assert.assertEquals( artificial1, leaf.getParent() );
        }
    }

    private BasicNode findNodeWithId( Collection<BasicNode> c, String id )
    {
        for ( BasicNode n : c ) {
            if ( n.getId().equals( id ) )
                return n;
        }

        return null;
    }
}
