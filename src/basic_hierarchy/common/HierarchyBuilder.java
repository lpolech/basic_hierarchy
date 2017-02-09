package basic_hierarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Node;


/**
 * This class exposes methods allowing to build and fix gaps in {@link Hierarchy} objects.
 */
public class HierarchyBuilder
{
    private static char branchSeparator = Constants.HIERARCHY_BRANCH_SEPARATOR.charAt( 0 );


    private HierarchyBuilder()
    {
        // Static class -- disallow instantiation.
        throw new RuntimeException( "Attempted to instantiate a static class: " + getClass().getName() );
    }

    /**
     * Builds a complete hierarchy of nodes, while also patching up holes in the original hierarchy by inserting empty nodes
     * for missing IDs.
     * 
     * @param root
     *            the root node
     * @param nodes
     *            the original collection of nodes
     * @param fixBreadthGaps
     *            whether the hierarchy fixing algorithm should also fix gaps in breadth, not just depth.
     * @param useSubtree
     *            whether the centroid calculation should also include child nodes' instances.
     *            When set to {@code true}, all objects from subnodes are regarded as also belonging to their supernodes.
     * @return the complete 'fixed' collection of nodes, filled with artificial nodes
     */
    public static List<? extends Node> buildCompleteHierarchy(
        BasicNode root, List<BasicNode> nodes,
        boolean fixBreadthGaps, boolean useSubtree )
    {
        if ( root == null ) {
            // Root node was missing from input file - create it artificially.
            root = new BasicNode( Constants.ROOT_ID, null, useSubtree );
            nodes.add( 0, root );
        }

        createParentChildRelations( nodes );

        nodes.addAll( fixDepthGaps( root, nodes, useSubtree ) );

        if ( fixBreadthGaps ) {
            nodes.addAll( fixBreadthGaps( root, useSubtree ) );
        }

        for ( BasicNode n : nodes ) {
            Utils.checkInterruptStatus();

            n.recalculateCentroid( useSubtree );
        }

        Collections.sort( nodes, new NodeIdComparator() );

        return nodes;
    }

    /**
     * Updates all nodes in the specified collection so that their actual parent-child relations match
     * up with their IDs.
     * <p>
     * If a node's ID implies it has a parent, but that parent is not present in the collection, then
     * that node will not have its parent set.
     * </p>
     * <p>
     * This means that this method DOES NOT GUARANTEE that the hierarchy it creates will be contiguous.
     * To fix this, follow-up this method with {@link #fixDepthGaps(BasicNode, List, boolean)} and/or
     * {@link #fixBreadthGaps(BasicNode, boolean)}
     * </p>
     * 
     * @param nodes
     *            collection of all nodes to build the hierarchy from
     */
    private static void createParentChildRelations( List<BasicNode> nodes )
    {
        // Reset all previous relations first
        for ( BasicNode node : nodes ) {
            node.setChildren( new LinkedList<Node>() );
            node.setParent( null );
        }

        for ( int i = 0; i < nodes.size(); ++i ) {
            Utils.checkInterruptStatus();

            BasicNode parent = nodes.get( i );

            for ( int j = 0; j < nodes.size(); ++j ) {
                if ( i == j ) {
                    // Can't become a parent unto itself.
                    continue;
                }

                BasicNode child = nodes.get( j );

                if ( areIdsParentAndChild( parent.getId(), child.getId() ) ) {
                    child.setParent( parent );
                    parent.addChild( child );
                }
            }
        }
    }

    /**
     * Fixes gaps in depth (missing ancestors) by creating empty nodes where needed.
     * <p>
     * Such gaps appear when the source file did not list these nodes (since they were empty),
     * but their existence can be inferred from IDs of existing nodes.
     * </p>
     * 
     * @param root
     *            the root node
     * @param nodes
     *            the original collection of nodes
     * @param useSubtree
     *            whether the centroid calculation should also include child nodes' instances
     * @return collection of artificial nodes created as a result of this method
     */
    public static List<BasicNode> fixDepthGaps( BasicNode root, List<BasicNode> nodes, boolean useSubtree )
    {
        List<BasicNode> artificialNodes = new ArrayList<BasicNode>();

        for ( int i = 0; i < nodes.size(); ++i ) {
            Utils.checkInterruptStatus();

            BasicNode node = nodes.get( i );

            if ( node == root ) {
                // Don't consider the root node.
                continue;
            }

            if ( node.getParent() == null ) {
                BasicNode nearestAncestor = null;
                int candidateHeight = -1;

                // Try to find nearest parent in 'real' nodes
                BasicNode candidate = findNearestAncestor( nodes, node.getId(), -1, i );
                if ( candidate != null ) {
                    nearestAncestor = candidate;
                    candidateHeight = getNodeHeight( candidate );
                }

                // Try to find nearest parent in artificial nodes
                candidate = findNearestAncestor( artificialNodes, node.getId(), candidateHeight );
                if ( candidate != null ) {
                    nearestAncestor = candidate;
                }

                if ( nearestAncestor != null ) {
                    artificialNodes.addAll( fixDepthGapsBetween( nearestAncestor, node, useSubtree ) );
                }
                else {
                    throw new RuntimeException(
                        String.format(
                            "Could not find nearest parent for '%s'. This means that something went seriously wrong.",
                            node.getId()
                        )
                    );
                }
            }
        }

        return artificialNodes;
    }

    /**
     * Fixes depth gaps between the specified ancestor and descendant nodes only.
     * 
     * @param ancestor
     *            the nearest ancestor node that already exists within the hierarchy
     * @param descendant
     *            the descendant node we want to create parents for
     * @param useSubtree
     *            whether the centroid calculation should also include child nodes' instances
     * @return collection of artificial nodes created as a result of this method
     */
    public static List<BasicNode> fixDepthGapsBetween( BasicNode ancestor, BasicNode descendant, boolean useSubtree )
    {
        List<BasicNode> artificialNodes = new ArrayList<BasicNode>();

        int descendantHeight = getNodeHeight( descendant );
        int ancestorHeight = getNodeHeight( ancestor );

        String rest = descendant.getId().substring( ancestor.getId().length() + 1 );

        BasicNode newParent = ancestor;
        int prevIdx = 0;
        for ( int j = ancestorHeight; j < descendantHeight - 1; ++j ) {
            // Find the range that we need to read
            int idx = rest.indexOf( Constants.HIERARCHY_BRANCH_SEPARATOR, prevIdx );
            String part = rest.substring( prevIdx, idx );
            String newId = newParent.getId() + Constants.HIERARCHY_BRANCH_SEPARATOR + part;
            // Remove the part we've parsed.
            prevIdx = idx + 1;

            // Add an empty node
            BasicNode newNode = new BasicNode(
                newId, newParent, useSubtree
            );

            // Create proper parent-child relations
            newParent.addChild( newNode );
            newNode.setParent( newParent );

            artificialNodes.add( newNode );

            newParent = newNode;
        }

        // Add missing links
        newParent.addChild( descendant );
        descendant.setParent( newParent );

        return artificialNodes;
    }

    /**
     * Fixes gaps in breadth (missing siblings) by creating empty nodes where needed.
     * <p>
     * Such gaps appear when the source file did not list these nodes (since they were empty),
     * but their existence can be inferred from IDs of existing nodes.
     * </p>
     * 
     * @param root
     *            the root node
     * @param useSubtree
     *            whether the centroid calculation should also include child nodes' instances
     * @return collection of artificial nodes created as a result of this method
     */
    public static List<BasicNode> fixBreadthGaps( BasicNode root, boolean useSubtree )
    {
        List<BasicNode> artificialNodes = new ArrayList<>();

        Queue<BasicNode> pendingNodes = new LinkedList<>();
        pendingNodes.add( root );

        while ( !pendingNodes.isEmpty() ) {
            Utils.checkInterruptStatus();
            
            BasicNode current = pendingNodes.remove();

            // Enqueue child nodes for processing ahead of time, so that we don't
            // have to differentiate between real and artificial nodes later.
            for ( Node child : current.getChildren() ) {
                pendingNodes.add( (BasicNode)child );
            }

            artificialNodes.addAll( fixBreadthGapsInNode( current, useSubtree ) );
        }

        return artificialNodes;
    }

    /**
     * Fixes gaps in breadth just in the specified node.
     * 
     * @param node
     *            the node to fix breadth gaps in
     * @param useSubtree
     *            whether the centroid calculation should also include child nodes' instances
     * @return collection of artificial nodes created as a result of this method
     */
    public static List<BasicNode> fixBreadthGapsInNode( BasicNode node, boolean useSubtree )
    {
        LinkedList<Node> children = node.getChildren();
        List<BasicNode> artificialNodes = new ArrayList<>();

        // Make sure children are sorted so that we can detect gaps.
        Collections.sort( children, new NodeIdComparator() );

        for ( int i = 0; i < children.size(); ++i ) {
            Node child = children.get( i );

            String id = child.getId();
            int idx = id.lastIndexOf( Constants.HIERARCHY_BRANCH_SEPARATOR );
            String lastSegment = id.substring( idx + 1 );

            if ( lastSegment.equals( Integer.toString( i ) ) ) {
                // Assert that the existing nodes have correct relationships.
                if ( !areNodesAncestorAndDescendant( node, child ) ) {
                    throw new RuntimeException(
                        String.format(
                            "Fatal error while filling breadth gaps! '%s' IS NOT an ancestor of '%s', " +
                                "but '%s' IS a child of '%s'!",
                            node.getId(), child.getId(), child.getId(), node.getId()
                        )
                    );
                }
            }
            else {
                // i-th node's id isn't equal to i - there's a gap. Fix it.
                String newId = node.getId() + Constants.HIERARCHY_BRANCH_SEPARATOR + i;
                BasicNode newNode = new BasicNode( newId, node, useSubtree );
                newNode.setParent( node );

                // Insert the new node at the current index.
                // Don't enqueue the new node, since it has no children anyway
                // During the next iteration of the loop, we will process the same node again,
                // so that further gaps will be detected
                children.add( i, newNode );
                artificialNodes.add( newNode );
            }
        }

        // Children were inserted at correct indices, no need to sort again.
        // Set the list of children of the current node, in case implementation of getChildren()
        // is changed to return a copy, and not the collection itself.
        node.setChildren( children );

        return artificialNodes;
    }

    /**
     * {@link #findNearestAncestor(List, String[], int, int)}
     */
    public static BasicNode findNearestAncestor( List<BasicNode> nodes, String childId, int nearestHeight )
    {
        return findNearestAncestor( nodes, childId, nearestHeight, -1 );
    }

    /**
     * Attempts to find the nearest existing node that can act as an ancestor to the node specified in argument. IF no such
     * node could be found, this method returns null.
     * <p>
     * This method relies on the nodes' IDs being correctly formatted and allowing us to infer the parent-child relations.
     * </p>
     * 
     * @param nodes
     *            list of nodes to search in
     * @param childBranchIds
     *            ID segments of the node for which we're trying to find an ancestor
     * @param nearestHeight
     *            number of segments of the best candidate we have available currently (can be negative to mean 'none')
     * @param maxIndex
     *            max index to search to in the list of nodes, for bounding purposes (can be negative to perform an unbounded search)
     * @return the nearest node that can act as an ancestor, or null if not found
     */
    public static BasicNode findNearestAncestor( List<BasicNode> nodes, String childId, int nearestHeight, int maxIndex )
    {
        if ( maxIndex < 0 ) {
            maxIndex = nodes.size();
        }

        BasicNode result = null;

        for ( int i = 0; i < maxIndex; ++i ) {
            BasicNode parent = nodes.get( i );
            String parentId = parent.getId();
            int parentHeight = getIdHeight( parentId );

            if ( parentHeight > nearestHeight ) {
                if ( areIdsAncestorAndDescendant( parentId, childId ) ) {
                    result = parent;
                    nearestHeight = parentHeight;
                }
            }
        }

        return result;
    }

    public static int getNodeHeight( Node n )
    {
        return getIdHeight( n.getId() );
    }

    public static int getIdHeight( String id )
    {
        return id.length() - id.replace( Constants.HIERARCHY_BRANCH_SEPARATOR, "" ).length();
    }

    /**
     * Convenience method to split a node's IDs into segments for easier processing.
     */
    public static String[] getNodeIdSegments( Node n )
    {
        String[] result = n.getId().split( Constants.HIERARCHY_BRANCH_SEPARATOR_REGEX );
        // Ignore the first index ('gen')
        return Arrays.copyOfRange( result, 1, result.length );
    }

    /**
     * {@link #areIdsParentAndChild(String[], String[])}
     */
    public static boolean areNodesParentAndChild( Node parent, Node child )
    {
        return areIdsParentAndChild(
            parent.getId(),
            child.getId()
        );
    }

    /**
     * {@link #areIdsAncestorAndDescendant(String[], String[])}
     */
    public static boolean areNodesAncestorAndDescendant( Node ancestor, Node descendant )
    {
        return areIdsAncestorAndDescendant(
            ancestor.getId(),
            descendant.getId()
        );
    }

    /**
     * {@link #areIdsParentAndChild(String[], String[])}
     */
    public static boolean areIdsParentAndChild( String parentId, String childId )
    {
        return areIdsAncestorAndDescendant( parentId, childId ) &&
            childId.substring( parentId.length() + 1 ).indexOf( Constants.HIERARCHY_BRANCH_SEPARATOR ) == -1;
    }

    /**
     * {@link #areIdsAncestorAndDescendant(String[], String[])}
     */
    public static boolean areIdsAncestorAndDescendant( String parentId, String childId )
    {
        return childId.startsWith( parentId ) &&
            childId.charAt( parentId.length() ) == branchSeparator;
    }

    /**
     * Checks whether the two IDs represent nodes that are directly related (parent-child).
     * This method returns false if both IDs point to the same node.
     * 
     * @param parentIds
     *            ID segments of the node acting as parent
     * @param childIds
     *            ID segments of the node acting as child
     * @return whether the two nodes are in a direct parent-child relationship.
     */
    public static boolean areIdsParentAndChild( String[] parentIds, String[] childIds )
    {
        // Check that the child is exactly one level 'deeper' than the parent, and then
        // compare the node IDs to verify that they are related.
        return ( parentIds.length + 1 == childIds.length ) &&
            areIdsAncestorAndDescendant( parentIds, childIds );
    }

    /**
     * Checks whether the two IDs represent nodes that are indirectly related (ancestor-descendant).
     * This method returns false if both IDs point to the same node.
     * 
     * @param ancestorIds
     *            ID segments of the node acting as ancestor
     * @param descendantIds
     *            ID segments of the node acting as descendant
     * @return whether the two nodes are in a ancestor-descendant relationship.
     */
    public static boolean areIdsAncestorAndDescendant( String[] ancestorIds, String[] descendantIds )
    {
        if ( ancestorIds.length < descendantIds.length ) {
            for ( int i = 0; i < ancestorIds.length; ++i ) {
                if ( !ancestorIds[i].equals( descendantIds[i] ) ) {
                    return false;
                }
            }

            return true;
        }
        else {
            // 'ancestor' ID has more segments than 'descendant', which means there's no way
            // it can be an ancestor to the other node.
            return false;
        }
    }
}
