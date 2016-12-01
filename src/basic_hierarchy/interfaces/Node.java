package basic_hierarchy.interfaces;

import java.util.LinkedList;


/**
 * A {@link Node} is a collection of {@link Instance}s which were assigned to
 * one {@link Node} by some method or generator.
 */
public interface Node
{
	/**
	 * @return the id of this node.
	 */
	public String getId();

	/**
	 * @return the parent node of this node, or null if this is the root node.
	 */
	public Node getParent();

	/**
	 * @return the parent node's id, or null if this is the root node.
	 */
	public String getParentId();

	/**
	 * @return list of child nodes of this node.
	 */
	public LinkedList<Node> getChildren();

	/**
	 * @return list of instances which belong to this particular node.
	 */
	public LinkedList<Instance> getNodeInstances();

	/**
	 * @return list of instances which belong to this node or its child nodes.
	 */
	public LinkedList<Instance> getSubtreeInstances();

	/**
	 * @return node representation e.g. its centroid or medoid.
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

	/**
	 * Prints the String representation of this Node to the console
	 */
	public void printSubtree();
}
