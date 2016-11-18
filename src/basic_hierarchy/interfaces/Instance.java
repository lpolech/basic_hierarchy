package basic_hierarchy.interfaces;

/**
 * An instance is a singular entity that can be placed in the broader context of a {@link Hierarchy}.
 */
public interface Instance
{
	/**
	 * @return the name of this particular instance
	 */
	public String getInstanceName();

	/**
	 * @return data columns associated with this instance
	 */
	public double[] getData();

	/**
	 * @return id of the true class (group) to which this instance has been assigned ('ground truth').
	 *         Null if not set.
	 */
	public String getTrueClass();

	/**
	 * @return id of the group to which this instance has been assigned ('assigned class').
	 */
	public String getAssignedClass();
}
