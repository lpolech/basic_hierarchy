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

	/**
	 * Sets the parent node of this node.
	 * 
	 * @param parent
	 *            the new parent node
	 */
	public void setParent( Node parent );

	/**
	 * Sets the children nodes of this node.
	 * 
	 * @param children
	 *            list of children nodes
	 */
	public void setChildren( LinkedList<Node> children );

	/**
	 * Adds a child node to this node.
	 * 
	 * @param child
	 *            the node to add
	 */
	public void addChild( Node child );

	/**
	 * Adds a new instance to this node.
	 * 
	 * @param instance
	 *            the instance to add
	 */
	public void addInstance( Instance instance );

	/**
	 * Sets the instance list of this node.
	 * 
	 * @param instances
	 *            list of instances
	 */
	public void setInstances( LinkedList<Instance> instances );

	/**
	 * Sets the representation instance of this node.
	 * 
	 * @param representation
	 *            representation instance
	 */
	public void setRepresentation( Instance representation );

	public void printSubtree();
}
