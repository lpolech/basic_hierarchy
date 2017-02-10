package basic_hierarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TreeMap;

import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Node;


/**
 * This class exposes methods allowing to build and fix gaps in {@link Hierarchy} objects.
 */
public class HierarchyBuilder
{
    private static char branchSeparator = Constants.HIERARCHY_BRANCH_SEPARATOR.charAt( 0 );

    private volatile int progress = 0;
    private volatile String statusMsg = "";


    /**
     * @return value representing progress of current operation, values [0, 100], or
     *         negative for indeterminate operation.
     */
    public int getProgress()
    {
        return progress;
    }

    /**
     * @return message describing the currently performed operation.
     */
    public String getStatusMessage()
    {
        return statusMsg;
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
    public List<? extends Node> buildCompleteHierarchy(
        BasicNode root, List<BasicNode> nodes,
        boolean fixBreadthGaps, boolean useSubtree )
    {
        statusMsg = "";
        progress = 0;

        if ( root == null ) {
            // Root node was missing from input file - create it artificially.
            root = new BasicNode( Constants.ROOT_ID, null, useSubtree );
            nodes.add( 0, root );
        }

        statusMsg = "Creating parent-child relations...";
        createParentChildRelations( nodes );

        statusMsg = "Fixing depth gaps...";
        nodes.addAll( fixDepthGaps( root, nodes, useSubtree ) );

        if ( fixBreadthGaps ) {
            statusMsg = "Fixing breadth gaps...";
            nodes.addAll( fixBreadthGaps( root, useSubtree ) );
        }

        statusMsg = "Recalculating centroids...";
        progress = 0;
        long total = nodes.size();
        long current = 0;
        for ( BasicNode n : nodes ) {
            Utils.checkInterruptStatus();

            ++current;
            progress = (int)( 100 * ( (double)current / total ) );

            n.recalculateCentroid( useSubtree );
        }

        statusMsg = "Sorting...";
        progress = 0;
        Collections.sort( nodes, new NodeIdComparator() );
        progress = 100;

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
    private void createParentChildRelations( List<BasicNode> nodes )
    {
        progress = 0;

        // Reset all previous relations first
        for ( BasicNode node : nodes ) {
            node.setChildren( new LinkedList<Node>() );
            node.setParent( null );
        }

        long total = nodes.size();
        for ( int i = 0; i < total; ++i ) {
            Utils.checkInterruptStatus();

            progress = (int)( 100 * ( (double)i / total ) );
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
    public List<BasicNode> fixDepthGaps( BasicNode root, List<BasicNode> nodes, boolean useSubtree )
    {
        progress = 0;
        List<BasicNode> artificialNodes = new ArrayList<BasicNode>();

        // TreeMap allows us to quickly find the nearest ancestor by sequentially checking lower keys,
        // and returning the one that is an ancestor to the one we're testing against.
        // Since the map is automatically sorted in an ascending order, that key is also guaranteed to be nearest.
        // (unless I've missed a crucial edge-case)
        TreeMap<String, BasicNode> treeMap = new TreeMap<>( new AlphanumComparator() );
        for ( BasicNode node : nodes ) {
            treeMap.put( node.getId(), node );
        }

        long total = nodes.size();
        for ( int i = 0; i < total; ++i ) {
            Utils.checkInterruptStatus();

            progress = (int)( 100 * ( (double)i / total ) );
            BasicNode node = nodes.get( i );

            if ( node == root ) {
                // Don't consider the root node.
                continue;
            }

            if ( node.getParent() == null ) {
                BasicNode nearestAncestor = findNearestAncestor( treeMap, node.getId() );

                if ( nearestAncestor != null ) {
                    List<BasicNode> intermediaries = fixDepthGapsBetween( nearestAncestor, node, useSubtree );
                    artificialNodes.addAll( intermediaries );

                    // Update the tree map with newly created nodes
                    for ( BasicNode intermediary : intermediaries ) {
                        treeMap.put( intermediary.getId(), intermediary );
                    }
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
    public List<BasicNode> fixDepthGapsBetween( BasicNode ancestor, BasicNode descendant, boolean useSubtree )
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
    public List<BasicNode> fixBreadthGaps( BasicNode root, boolean useSubtree )
    {
        progress = -1;
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
    public List<BasicNode> fixBreadthGapsInNode( BasicNode node, boolean useSubtree )
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
     * Attempts to find the nearest existing node that can act as an ancestor to the node specified in argument. IF no such
     * node could be found, this method returns null.
     * <p>
     * This method relies on the nodes' IDs being correctly formatted and allowing us to infer the parent-child relations.
     * </p>
     * 
     * @param treeMap
     *            tree map containing all nodes thus far (both real and artificial ones)
     * @param childId
     *            id of the child node we're trying to find an ancestor for
     * @return the nearest node that can act as an ancestor, or null if not found
     */
    private BasicNode findNearestAncestor( TreeMap<String, BasicNode> treeMap, String childId )
    {
        String prevKey = childId;

        int size = treeMap.size();
        for ( int i = 0; i < size; ++i ) {
            String candidateKey = treeMap.lowerKey( prevKey );

            if ( areIdsAncestorAndDescendant( candidateKey, childId ) ) {
                return treeMap.get( candidateKey );
            }

            prevKey = candidateKey;
        }

        return null;
    }

    /**
     * @param n
     *            the node to compute the node height for
     * 
     * @return height of the node (how deep it is within the tree). 1 means root node.
     */
    public static int getNodeHeight( Node n )
    {
        return getIdHeight( n.getId() );
    }

    /**
     * @param id
     *            the id to compute the node height for
     * 
     * @return height of the node (how deep it is within the tree). 1 means root node.
     */
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
     * {@link #areIdsParentAndChild(String, String)}
     */
    public static boolean areNodesParentAndChild( Node parent, Node child )
    {
        return areIdsParentAndChild(
            parent.getId(),
            child.getId()
        );
    }

    /**
     * {@link #areIdsAncestorAndDescendant(String, String)}
     */
    public static boolean areNodesAncestorAndDescendant( Node ancestor, Node descendant )
    {
        return areIdsAncestorAndDescendant(
            ancestor.getId(),
            descendant.getId()
        );
    }

    /**
     * Checks whether the two IDs represent nodes that are directly related (parent-child).
     * This method returns false if both IDs point to the same node.
     * 
     * @param parentId
     *            ID of the node acting as parent
     * @param childId
     *            ID of the node acting as child
     * @return whether the two nodes are in a direct parent-child relationship.
     */
    public static boolean areIdsParentAndChild( String parentId, String childId )
    {
        return areIdsAncestorAndDescendant( parentId, childId ) &&
            childId.substring( parentId.length() + 1 ).indexOf( Constants.HIERARCHY_BRANCH_SEPARATOR ) == -1;
    }

    /**
     * Checks whether the two IDs represent nodes that are indirectly related (ancestor-descendant).
     * This method returns false if both IDs point to the same node.
     * 
     * @param ancestorIds
     *            ID of the node acting as ancestor
     * @param descendantIds
     *            ID of the node acting as descendant
     * @return whether the two nodes are in a ancestor-descendant relationship.
     */
    public static boolean areIdsAncestorAndDescendant( String ancestorId, String descendantId )
    {
        return !ancestorId.equals( descendantId ) && descendantId.startsWith( ancestorId ) &&
            descendantId.charAt( ancestorId.length() ) == branchSeparator;
    }
}
