package basic_hierarchy.common;

import java.util.ArrayList;
import java.util.Arrays;
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
	 * Builds a complete hierarchy of groups, while also patching up holes in the original hierarchy by inserting empty groups
	 * for missing IDs.
	 * 
	 * @param root
	 *            the root group
	 * @param groups
	 *            the original collection of groups
	 * @param fixBreadthGaps
	 *            whether the hierarchy fixing algorithm should also fix gaps in breadth, not just depth.
	 * @param useSubtree
	 *            whether the centroid calculation should also include child groups' instances.
	 * @return the complete 'fixed' collection of groups, filled with artificial groups
	 */
	public static List<? extends Node> buildCompleteGroupHierarchy(
		BasicNode root, List<BasicNode> groups,
		boolean fixBreadthGaps, boolean useSubtree )
	{
		if ( root == null ) {
			// Root node was missing from input file - create it artificially.
			root = new BasicNode( Constants.ROOT_ID, null, useSubtree );
			groups.add( 0, root );
		}

		buildGroupHierarchy( groups );
		groups = fixDepthGaps( root, groups, useSubtree );

		if ( fixBreadthGaps ) {
			groups = fixBreadthGaps( root, groups, useSubtree );
		}

		for ( BasicNode g : groups ) {
			g.recalculateCentroid( useSubtree );
		}

		groups.sort( new NodeIdComparator() );

		return groups;
	}

	/**
	 * Builds a hierarchy of groups, based solely on the groups present in the specified collection.
	 * <p>
	 * If a group's ID implies it has a parent, but that parent is not present in the collection, then
	 * that group will not have its parent set.
	 * </p>
	 * <p>
	 * This means that this method DOES NOT GUARANTEE that the hierarchy it creates will be contiguous.
	 * To fix this, follow-up this method with {@link #fixDepthGaps(BasicNode, List)} and/or
	 * {@link #fixBreadthGaps(BasicNode, List)}
	 * </p>
	 * 
	 * @param groups
	 *            collection of all groups to build the hierarchy from
	 */
	private static void buildGroupHierarchy( List<BasicNode> groups )
	{
		for ( int i = 0; i < groups.size(); ++i ) {
			BasicNode parentGroup = groups.get( i );
			String[] parentBranchIds = getGroupBranchIds( parentGroup );

			for ( int j = 0; j < groups.size(); ++j ) {
				if ( i == j ) {
					// Can't become a parent unto itself.
					continue;
				}

				BasicNode childGroup = groups.get( j );
				String[] childBranchIds = getGroupBranchIds( childGroup );

				if ( areGroupsDirectlyRelated( parentBranchIds, childBranchIds ) ) {
					childGroup.setParent( parentGroup );
					parentGroup.addChild( childGroup );
				}
			}
		}
	}

	/**
	 * Fixes gaps in depth (missing ancestors) by creating empty groups where needed.
	 * <p>
	 * Such gaps appear when the source file did not list these groups (since they were empty),
	 * but their existence can be inferred from IDs of existing groups.
	 * </p>
	 * 
	 * @param root
	 *            the root group
	 * @param groups
	 *            the original collection of groups
	 * @param useSubtree
	 *            whether the cntroid calculation should also include child groups' instances.
	 * @return the complete 'fixed' collection of groups, filled with artificial groups
	 */
	private static List<BasicNode> fixDepthGaps( BasicNode root, List<BasicNode> groups, boolean useSubtree )
	{
		List<BasicNode> artificialGroups = new ArrayList<BasicNode>();

		StringBuilder buf = new StringBuilder();

		for ( int i = 0; i < groups.size(); ++i ) {
			BasicNode group = groups.get( i );

			if ( group == root ) {
				// Don't consider the root group.
				continue;
			}

			if ( group.getParent() == null ) {
				String[] groupBranchIds = getGroupBranchIds( group );

				BasicNode nearestParent = null;
				int nearestParentHeight = -1;

				// Try to find nearest parent in 'real' groups
				BasicNode candidateGroup = findNearestAncestor( groups, groupBranchIds, -1, i );
				if ( candidateGroup != null ) {
					nearestParent = candidateGroup;
					nearestParentHeight = getGroupBranchIds( candidateGroup ).length;
				}

				// Try to find nearest parent in artificial groups
				candidateGroup = findNearestAncestor( groups, groupBranchIds, nearestParentHeight );
				if ( candidateGroup != null ) {
					nearestParent = candidateGroup;
					nearestParentHeight = getGroupBranchIds( candidateGroup ).length;
				}

				if ( nearestParent != null ) {
					BasicNode newParent = nearestParent;

					for ( int j = nearestParentHeight; j < groupBranchIds.length - 1; ++j ) {
						buf.setLength( 0 );

						String newGroupId = buf
							.append( newParent.getId() )
							.append( Constants.HIERARCHY_BRANCH_SEPARATOR )
							.append( groupBranchIds[j] )
							.toString();

						// Add an empty group
						BasicNode newGroup = new BasicNode(
							newGroupId, newParent, useSubtree
						);

						// Create proper parent-child relations
						newParent.addChild( newGroup );
						newGroup.setParent( newParent );

						artificialGroups.add( newGroup );

						newParent = newGroup;
					}

					// Add missing links
					newParent.addChild( group );
					group.setParent( newParent );
				}
				else {
					throw new RuntimeException(
						String.format(
							"Could not find nearest parent for '%s'. This means that something went seriously wrong.",
							group.getId()
						)
					);
				}
			}
		}

		List<BasicNode> allGroups = new ArrayList<BasicNode>( artificialGroups );
		allGroups.addAll( groups );

		return allGroups;
	}

	/**
	 * Fixes gaps in breadth (missing siblings) by creating empty groups where needed.
	 * <p>
	 * Such gaps appear when the source file did not list these groups (since they were empty),
	 * but their existence can be inferred from IDs of existing groups.
	 * </p>
	 * 
	 * @param root
	 *            the root group
	 * @param groups
	 *            the original collection of groups
	 * @param useSubtree
	 *            whether the cntroid calculation should also include child groups' instances.
	 * @return the complete 'fixed' collection of groups, filled with artificial groups
	 */
	private static List<BasicNode> fixBreadthGaps( BasicNode root, List<BasicNode> groups, boolean useSubtree )
	{
		List<BasicNode> artificialGroups = new ArrayList<BasicNode>();

		Comparator<Node> groupComparator = new NodeIdComparator();
		StringBuilder buf = new StringBuilder();

		Queue<BasicNode> pendingGroups = new LinkedList<BasicNode>();
		pendingGroups.add( root );

		while ( !pendingGroups.isEmpty() ) {
			BasicNode currentGroup = pendingGroups.remove();

			List<Node> children = currentGroup.getChildren();
			List<Node> newChildren = new LinkedList<Node>();

			for ( int i = 0; i < children.size(); ++i ) {
				Node childGroup = children.get( i );

				if ( childGroup == null ) {
					// If i-th child doesn't exist, then there's a gap. Fix it.
					buf.setLength( 0 );
					buf.append( currentGroup.getId() ).append( Constants.HIERARCHY_BRANCH_SEPARATOR ).append( i );

					BasicNode newGroup = new BasicNode( buf.toString(), currentGroup, useSubtree );
					newGroup.setParent( currentGroup );

					newChildren.add( newGroup );
					artificialGroups.add( newGroup );
				}
				else {
					if ( areGroupsRelated( currentGroup, childGroup ) ) {
						pendingGroups.add( (BasicNode)childGroup );
					}
					else {
						throw new RuntimeException(
							String.format(
								"Fatal error while filling breadth gaps! '%s' IS NOT an ancestor of '%s', " +
									"but '%s' IS a child of '%s'!",
								currentGroup.getId(), childGroup.getId(), childGroup.getId(), currentGroup.getId()
							)
						);
					}
				}
			}

			for ( Node g : newChildren ) {
				currentGroup.addChild( g );
			}
			children.sort( groupComparator );
		}

		List<BasicNode> allGroups = new ArrayList<BasicNode>( artificialGroups );
		allGroups.addAll( groups );

		return allGroups;
	}

	/**
	 * {@link #findNearestAncestor(List, String[], int, int)}
	 */
	private static BasicNode findNearestAncestor( List<BasicNode> groups, String[] childBranchIds, int nearestHeight )
	{
		return findNearestAncestor( groups, childBranchIds, nearestHeight, -1 );
	}

	/**
	 * Attempts to find the nearest existing group that can act as an ancestor to the group specified in argument. IF no such
	 * group could be found, this method returns null.
	 * <p>
	 * This method relies on the groups' IDs being correctly formatted and allowing us to infer the parent-child relations.
	 * </p>
	 * 
	 * @param groups
	 *            list of groups to search in
	 * @param childBranchIds
	 *            ID segments of the group for which we're trying to find an ancestor
	 * @param nearestHeight
	 *            number of segments of the best candidate we have available currently (can be negative to mean 'none')
	 * @param maxIndex
	 *            max index to search to in the list of groups, for bounding purposes (can be negative to perform an unbounded search)
	 * @return the nearest group that can act as an ancestor, or null if not found
	 */
	private static BasicNode findNearestAncestor( List<BasicNode> groups, String[] childBranchIds, int nearestHeight, int maxIndex )
	{
		if ( maxIndex < 0 ) {
			maxIndex = groups.size();
		}

		BasicNode result = null;

		for ( int i = 0; i < maxIndex; ++i ) {
			BasicNode parentGroup = groups.get( i );
			String[] parentBranchIds = getGroupBranchIds( parentGroup );

			if ( parentBranchIds.length > nearestHeight ) {
				if ( areGroupsRelated( parentBranchIds, childBranchIds ) ) {
					result = parentGroup;
					nearestHeight = parentBranchIds.length;
				}
			}
		}

		return result;
	}

	/**
	 * Convenience method to split a group's IDs into segments for easier processing.
	 */
	private static String[] getGroupBranchIds( Node g )
	{
		String[] result = g.getId().split( Constants.HIERARCHY_BRANCH_SEPARATOR_REGEX );
		// Ignore the first index ('gen')
		return Arrays.copyOfRange( result, 1, result.length );
	}

	/**
	 * {@link #areGroupsDirectlyRelated(String[], String[])}
	 */
	private static boolean areGroupsDirectlyRelated( Node parent, Node child )
	{
		return areGroupsDirectlyRelated( getGroupBranchIds( parent ), getGroupBranchIds( child ) );
	}

	/**
	 * Checks whether the two groups are directly related (parent-child).
	 * This method returns false if both ids point to the same group.
	 * 
	 * @param parentIds
	 *            ID segments of the group acting as parent
	 * @param childIds
	 *            ID segments of the group as child
	 * @return whether the two groups are in fact in a direct parent-child relationship.
	 */
	private static boolean areGroupsDirectlyRelated( String[] parentIds, String[] childIds )
	{
		// Check that the child is exactly one level 'deeper' than the parent.

		if ( parentIds.length + 1 == childIds.length ) {
			// Compare the group IDs to verify that they are related.
			return areGroupsRelated( parentIds, childIds );
		}
		else {
			return false;
		}
	}

	/**
	 * {@link #areGroupsRelated(String[], String[])}
	 */
	private static boolean areGroupsRelated( Node parent, Node child )
	{
		return areGroupsRelated( getGroupBranchIds( parent ), getGroupBranchIds( child ) );
	}

	/**
	 * Checks whether the two groups are indirectly related (ancestor-descendant).
	 * This method returns false if both ids point to the same group.
	 * 
	 * @param parentIds
	 *            ID segments of the group acting as parent
	 * @param childIds
	 *            ID segments of the group as child
	 * @return whether the two groups are in fact in a ancestor-descendant relationship.
	 */
	private static boolean areGroupsRelated( String[] parentIds, String[] childIds )
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
