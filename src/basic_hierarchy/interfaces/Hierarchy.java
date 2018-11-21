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
	public int getOverallNumberOfInstances();

    /**
     * Returns a table witch counts of every class from hierarchy. The order of the classes are the same as in
     * {@code getClasses()} array.
     *
      * @return table with size of every class.
     */
    public int[] getClassesCount();

	/**
	 * Returns the number of instances that are assigned to {@code className} class within the whole hierarchy.
	 * 
	 * @param className
	 *            class name to look for.
	 * @param withNodeInheritance
	 *            if true, the result will also include child classes of the specified class.
	 * @return the number of instances that are assigned to the specified class in this hierarchy.
	 */
	public int getParticularClassCount( String className, boolean withNodeInheritance );

    /**
     * Return a hierarchy that simulates flat clustering. It has a empty, artificial node and all the nodes from
     *
     * {@code this} hierarchy are direct child of this root.
     *
     * @return hierarchy simulating a flat clustering.
     */
    public Hierarchy getFlatClusteringWithCommonEmptyRoot(); //@FIXME we can make it a static method with default
    //implementation but only in java 8

	/**
	 * Prints the String representation of this Hierarchy to the console
	 */
	public void printTree();//@FIXME this could be a default implementation in java 8

	/**
	 * @return names for each data column in this hierarchy
	 */
	public String[] getDataNames();
	
	/**
	 * Deletes data names array
	 */
	public void deleteDataNames();
}
