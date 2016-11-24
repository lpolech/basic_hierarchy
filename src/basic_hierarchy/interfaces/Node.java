package basic_hierarchy.interfaces;

import java.util.LinkedList;


/**
 * A group is a collection of {@link Instance}s which belong to the same class.
 */
public interface Node
{
	/**
	 * @return the id of this group.
	 */
	public String getId();

	/**
	 * @return the parent group of this group, or null if this is the root group.
	 */
	public Node getParent();

	/**
	 * @return the parent group's id, or null if this is the root group.
	 */
	public String getParentId();

	/**
	 * @return the child groups of this group.
	 */
	public LinkedList<Node> getChildren();

	/**
	 * Returns instances which belong to this particular group.
	 */
	public LinkedList<Instance> getNodeInstances();

	/**
	 * Returns instances which belong to this group or its subgroups.
	 */
	public LinkedList<Instance> getSubtreeInstances();

	/**
	 * Returns a node centroid or medoid.
	 */
	public Instance getNodeRepresentation();

	public void printSubtree();
}
