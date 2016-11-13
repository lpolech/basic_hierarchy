package basic_hierarchy.interfaces;

import java.util.LinkedList;


/**
 * A group is a collection of {@link Instance}s which belong to the same class.
 */
public interface Group
{
	/**
	 * @return the id of this group.
	 */
	public String getId();

	/**
	 * @return the parent group of this group, or null if this is the root group.
	 */
	public Group getParent();

	/**
	 * @return the parent group's id, or null if this is the root group.
	 */
	public String getParentId();

	/**
	 * @return the child groups of this group.
	 */
	public LinkedList<Group> getChildren();

	/**
	 * Returns instances which belong to this particular group.
	 */
	public LinkedList<Instance> getInstances();

	/**
	 * Returns instances which belong to this group or its subgroups.
	 */
	public LinkedList<Instance> getSubgroupInstances();

	/**
	 * Returns a node centroid or medoid.
	 */
	public Instance getGroupRepresentation();
}
