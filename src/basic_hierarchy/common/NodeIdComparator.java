package basic_hierarchy.common;

import java.util.Comparator;

import basic_hierarchy.interfaces.Node;


/**
 * Compares two nodes by comparing their ids.
 */
public class NodeIdComparator implements Comparator<Node>
{
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

        // Node IDs have ordering consistent with Strings' default lexicographic ordering.
        // Thus, we can just use default Java implementation, which is also 3x faster than
        // our specialized StringIdComparator
        return o1Id.compareTo( o2Id );
    }
}
