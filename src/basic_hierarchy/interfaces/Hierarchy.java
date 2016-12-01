package basic_hierarchy.interfaces;

/**
 * A hierarchy is a collection of {@link Node}s, all of which share one common ancestor (the root node).
 */
public interface Hierarchy
{
	/**
	 * @return the root node of this hierarchy.
	 */
	public Node getRoot();

	/**
	 * @return array of all groups of instances in this hierarchy.
	 */
	public Node[] getGroups();

	/**
	 * @return the number of groups in this hierarchy. Equivalent to {@code getGroups().length}.
	 */
	public int getNumberOfGroups();

	/**
	 * @return array of classes present in this hierarchy.
	 */
	public String[] getClasses();

	/**
	 * @return the number of ground-truth classes. Equivalent to {@code getClasses().length}.
	 */
	public int getNumberOfClasses();

	/**
	 * @return the total number of instances in this hierarchy, ie. sum of instances in all groups.
	 */
	public int getNumberOfInstances();

	/**
	 * Returns the number of instances that are assigned to {@code className} class within the whole hierarchy.
	 * 
	 * @param className
	 *            class name to look for.
	 * @param withNodeInheritance
	 *            if true, the result will also include child classes of the specified class.
	 * @return the number of instances that are assigned to the specified class in this hierarchy.
	 */
	public int getClassCount( String className, boolean withNodeInheritance );

	/**
	 * Prints the String representation of this Hierarchy to the console
	 */
	public void printTree();

	/**
	 * @return names for each data column in this hierarchy
	 */
	public String[] getDataNames();
}
