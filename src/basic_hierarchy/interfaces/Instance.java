package basic_hierarchy.interfaces;

/**
 * An instance is a singular entity (a single observation/data point) that can be placed in a {@link Node},
 * which in turn can be placed in a {@link Hierarchy}.
 */
public interface Instance
{
	/**
	 * @return the name of this particular instance.
	 */
	public String getInstanceName();

	/**
	 * @return data row (feature values) associated with this instance.
	 */
	public double[] getData();

	/**
	 * @return id of the true class this instance comes from ('ground truth').
	 *         Null if not set.
	 */
	public String getTrueClass();

	/**
	 * @return id of the class to which this instance has been assigned ('assigned class').
	 */
	public String getNodeId();

	/**
	 * Sets the assigned class of this instance.
	 * 
	 * @param id
	 *            id of the assigned class
	 */
	public void setNodeId( String id );
}
