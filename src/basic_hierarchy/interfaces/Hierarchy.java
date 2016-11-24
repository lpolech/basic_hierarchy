package basic_hierarchy.interfaces;

/**
 * A hierarchy is a collection of {@link Node}s, all of which share one common ancestor (the root group).
 */
public interface Hierarchy
{
	/**
	 * Returns the root node of this hierarchy.
	 */
	public Node getRoot();

	/**
	 * Returns an array of groups of instances.
	 */
	public Node[] getGroups();

	/**
	 * Returns the number of groups in this hierarchy. Equivalent to {@code getGroups().length}.
	 */
	public int getNumberOfGroups();

	/**
	 * Returns an array of classes present in this hierarchy.
	 */
	public String[] getClasses();

	/**
	 * Returns the number of ground-truth classes. Equivalent to {@code getClasses().length}.
	 */
	public int getNumberOfClasses();

	/**
	 * Returns the total number of instances in this hierarchy, ie. sum of instances in all groups.
	 */
	public int getNumberOfInstances();

	/**
	 * Returns the number of groups within the specified class.
	 * 
	 * @param className
	 *            Class name to look for.
	 * @param withInstanceInheritance
	 *            if true, the result will also include child classes of the specified class. (?)
	 */
	public int getClassCount( String className, boolean withInstanceInheritance );

	public void printTree();

	/**
	 * @return names for each data column in this hierarchy
	 */
	public String[] getDataNames();
}
