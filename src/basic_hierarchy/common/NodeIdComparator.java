package basic_hierarchy.common;

import java.util.Comparator;

import basic_hierarchy.interfaces.Node;


/**
 * Compares two groups.
 * <p>
 * Implementation compares the two groups' IDs using {@linkplain StringIdComparator}.
 * </p>
 */
public class NodeIdComparator implements Comparator<Node>
{
	private StringIdComparator idComparator = new StringIdComparator();


	/**
	 * Note: this comparator imposes orderings that are <b>inconsistent with {@code equals}</b>.
	 * 
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare( Node o1, Node o2 )
	{
		String o1Id = o1.getId();
		String o2Id = o2.getId();

		return idComparator.compare( o1Id, o2Id );
	}
}
