package basic_hierarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Node;
import basic_hierarchy.interfaces.Hierarchy;


/**
 * This class exposes methods allowing to build and fix gaps in {@link Hierarchy} objects.
 */
public class HierarchyBuilder
{
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

		buildHierarchy( nodes );
		nodes = fixDepthGaps( root, nodes, useSubtree );

		if ( fixBreadthGaps ) {
			nodes = fixBreadthGaps( root, nodes, useSubtree );
		}

		for ( BasicNode n : nodes ) {
			n.recalculateCentroid( useSubtree );
		}

		Collections.sort( nodes, new NodeIdComparator() );

		return nodes;
	}

	/**
	 * Builds a hierarchy of nodes, based solely on the nodes present in the specified collection.
	 * <p>
	 * If a node's ID implies it has a parent, but that parent is not present in the collection, then
	 * that node will not have its parent set.
	 * </p>
	 * <p>
	 * This means that this method DOES NOT GUARANTEE that the hierarchy it creates will be contiguous.
	 * To fix this, follow-up this method with {@link #fixDepthGaps(BasicNode, List)} and/or
	 * {@link #fixBreadthGaps(BasicNode, List)}
	 * </p>
	 * 
	 * @param nodes
	 *            collection of all nodes to build the hierarchy from
	 */
	private static void buildHierarchy( List<BasicNode> nodes )
	{
		for ( int i = 0; i < nodes.size(); ++i ) {
			BasicNode parent = nodes.get( i );
			String[] parentBranchIds = getNodeBranchIds( parent );

			for ( int j = 0; j < nodes.size(); ++j ) {
				if ( i == j ) {
					// Can't become a parent unto itself.
					continue;
				}

				BasicNode child = nodes.get( j );
				String[] childBranchIds = getNodeBranchIds( child );

				if ( areNodesDirectlyRelated( parentBranchIds, childBranchIds ) ) {
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
	 *            whether the cntroid calculation should also include child nodes' instances.
	 * @return the complete 'fixed' collection of nodes, filled with artificial nodes
	 */
	private static List<BasicNode> fixDepthGaps( BasicNode root, List<BasicNode> nodes, boolean useSubtree )
	{
		List<BasicNode> artificialNodes = new ArrayList<BasicNode>();

		StringBuilder buf = new StringBuilder();

		for ( int i = 0; i < nodes.size(); ++i ) {
			BasicNode node = nodes.get( i );

			if ( node == root ) {
				// Don't consider the root node.
				continue;
			}

			if ( node.getParent() == null ) {
				String[] nodeBranchIds = getNodeBranchIds( node );

				BasicNode nearestParent = null;
				int nearestParentHeight = -1;

				// Try to find nearest parent in 'real' nodes
				BasicNode candidate = findNearestAncestor( nodes, nodeBranchIds, -1, i );
				if ( candidate != null ) {
					nearestParent = candidate;
					nearestParentHeight = getNodeBranchIds( candidate ).length;
				}

				// Try to find nearest parent in artificial nodes
				candidate = findNearestAncestor( nodes, nodeBranchIds, nearestParentHeight );
				if ( candidate != null ) {
					nearestParent = candidate;
					nearestParentHeight = getNodeBranchIds( candidate ).length;
				}

				if ( nearestParent != null ) {
					BasicNode newParent = nearestParent;

					for ( int j = nearestParentHeight; j < nodeBranchIds.length - 1; ++j ) {
						buf.setLength( 0 );

						String newId = buf
							.append( newParent.getId() )
							.append( Constants.HIERARCHY_BRANCH_SEPARATOR )
							.append( nodeBranchIds[j] )
							.toString();

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
					newParent.addChild( node );
					node.setParent( newParent );
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

		List<BasicNode> allNodes = new ArrayList<BasicNode>( artificialNodes );
		allNodes.addAll( nodes );

		return allNodes;
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
	 * @param nodes
	 *            the original collection of nodes
	 * @param useSubtree
	 *            whether the cntroid calculation should also include child nodes' instances.
	 * @return the complete 'fixed' collection of nodes, filled with artificial nodes
	 */
	private static List<BasicNode> fixBreadthGaps( BasicNode root, List<BasicNode> nodes, boolean useSubtree )
	{
		List<BasicNode> artificialNodes = new ArrayList<BasicNode>();

		Comparator<Node> nodeComparator = new NodeIdComparator();
		StringBuilder buf = new StringBuilder();

		Queue<BasicNode> pendingNodes = new LinkedList<BasicNode>();
		pendingNodes.add( root );

		while ( !pendingNodes.isEmpty() ) {
			BasicNode current = pendingNodes.remove();

			List<Node> children = current.getChildren();
			List<Node> newChildren = new LinkedList<Node>();

			for ( int i = 0; i < children.size(); ++i ) {
				Node child = children.get( i );

				if ( child == null ) {
					// If i-th child doesn't exist, then there's a gap. Fix it.
					buf.setLength( 0 );
					buf.append( current.getId() ).append( Constants.HIERARCHY_BRANCH_SEPARATOR ).append( i );

					BasicNode newNode = new BasicNode( buf.toString(), current, useSubtree );
					newNode.setParent( current );

					newChildren.add( newNode );
					artificialNodes.add( newNode );
				}
				else {
					if ( areNodesRelated( current, child ) ) {
						pendingNodes.add( (BasicNode)child );
					}
					else {
						throw new RuntimeException(
							String.format(
								"Fatal error while filling breadth gaps! '%s' IS NOT an ancestor of '%s', " +
									"but '%s' IS a child of '%s'!",
								current.getId(), child.getId(), child.getId(), current.getId()
							)
						);
					}
				}
			}

			for ( Node n : newChildren ) {
				current.addChild( n );
			}
			Collections.sort( children, nodeComparator );
		}

		List<BasicNode> allNodes = new ArrayList<BasicNode>( artificialNodes );
		allNodes.addAll( nodes );

		return allNodes;
	}

	/**
	 * {@link #findNearestAncestor(List, String[], int, int)}
	 */
	private static BasicNode findNearestAncestor( List<BasicNode> nodes, String[] childBranchIds, int nearestHeight )
	{
		return findNearestAncestor( nodes, childBranchIds, nearestHeight, -1 );
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
	private static BasicNode findNearestAncestor( List<BasicNode> nodes, String[] childBranchIds, int nearestHeight, int maxIndex )
	{
		if ( maxIndex < 0 ) {
			maxIndex = nodes.size();
		}

		BasicNode result = null;

		for ( int i = 0; i < maxIndex; ++i ) {
			BasicNode parent = nodes.get( i );
			String[] parentBranchIds = getNodeBranchIds( parent );

			if ( parentBranchIds.length > nearestHeight ) {
				if ( areNodesRelated( parentBranchIds, childBranchIds ) ) {
					result = parent;
					nearestHeight = parentBranchIds.length;
				}
			}
		}

		return result;
	}

	/**
	 * Convenience method to split a node's IDs into segments for easier processing.
	 */
	private static String[] getNodeBranchIds( Node g )
	{
		String[] result = g.getId().split( Constants.HIERARCHY_BRANCH_SEPARATOR_REGEX );
		// Ignore the first index ('gen')
		return Arrays.copyOfRange( result, 1, result.length );
	}

	/**
	 * {@link #areNodesDirectlyRelated(String[], String[])}
	 */
	private static boolean areNodesDirectlyRelated( Node parent, Node child )
	{
		return areNodesDirectlyRelated( getNodeBranchIds( parent ), getNodeBranchIds( child ) );
	}

	/**
	 * Checks whether the two nodes are directly related (parent-child).
	 * This method returns false if both ids point to the same node.
	 * 
	 * @param parentIds
	 *            ID segments of the node acting as parent
	 * @param childIds
	 *            ID segments of the node as child
	 * @return whether the two nodes are in fact in a direct parent-child relationship.
	 */
	private static boolean areNodesDirectlyRelated( String[] parentIds, String[] childIds )
	{
		// Check that the child is exactly one level 'deeper' than the parent.

		if ( parentIds.length + 1 == childIds.length ) {
			// Compare the node IDs to verify that they are related.
			return areNodesRelated( parentIds, childIds );
		}
		else {
			return false;
		}
	}

	/**
	 * {@link #areNodesRelated(String[], String[])}
	 */
	private static boolean areNodesRelated( Node parent, Node child )
	{
		return areNodesRelated( getNodeBranchIds( parent ), getNodeBranchIds( child ) );
	}

	/**
	 * Checks whether the two nodes are indirectly related (ancestor-descendant).
	 * This method returns false if both ids point to the same node.
	 * 
	 * @param parentIds
	 *            ID segments of the node acting as parent
	 * @param childIds
	 *            ID segments of the node as child
	 * @return whether the two nodes are in fact in a ancestor-descendant relationship.
	 */
	private static boolean areNodesRelated( String[] parentIds, String[] childIds )
	{
		if ( parentIds.length < childIds.length ) {
			for ( int i = 0; i < parentIds.length; ++i ) {
				if ( !parentIds[i].equals( childIds[i] ) ) {
					return false;
				}
			}

			return true;
		}
		else {
			return false;
		}
	}
}
