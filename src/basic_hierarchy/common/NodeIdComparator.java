package basic_hierarchy.common;

import java.util.Comparator;

import basic_hierarchy.interfaces.Node;


/**
 * Compares two nodes by comparing their ids.
 */
public class NodeIdComparator implements Comparator<Node>
{
    private Comparator<String> idComparator = new AlphanumComparator();


    /**
     * Note: this comparator imposes orderings that are <b>inconsistent with {@code equals}</b>.
     * 
     * @see Comparator#compare(Object, Object)
     */
    @Override
    public int compare( Node o1, Node o2 )
    {
        return idComparator.compare( o1.getId(), o2.getId() );
    }
}
