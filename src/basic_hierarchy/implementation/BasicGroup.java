package basic_hierarchy.implementation;

import java.util.LinkedList;

import basic_hierarchy.interfaces.Group;
import basic_hierarchy.interfaces.Instance;


public class BasicGroup implements Group
{
	private String id;
	private Group parent;
	private LinkedList<Group> children;
	private LinkedList<Instance> instances;
	private Instance representation;


	private BasicGroup( String id, Group parent, LinkedList<Group> children, LinkedList<Instance> instances )
	{
		this.id = id;
		this.parent = parent;
		this.children = children;
		this.instances = instances;
	}

	public BasicGroup( String id, Group parent, LinkedList<Group> children, LinkedList<Instance> instances, boolean useSubtree )
	{
		this( id, parent, children, instances );
		this.representation = recalculateCentroid( useSubtree );
	}

	public BasicGroup( String id, Group parent, LinkedList<Group> children, LinkedList<Instance> instances, Instance representation )
	{
		this( id, parent, children, instances );
		this.representation = representation;
	}

	public BasicGroup( String id, Group parent, boolean useSubtree )
	{
		this( id, parent, new LinkedList<Group>(), new LinkedList<Instance>(), useSubtree );
	}

	public BasicGroup( String id, Group parent, Instance representation )
	{
		this( id, parent, new LinkedList<Group>(), new LinkedList<Instance>(), representation );
	}

	public void setParent( Group parent )
	{
		this.parent = parent;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	public void setChildren( LinkedList<Group> children )
	{
		this.children = children;
	}

	public void addChild( Group child )
	{
		this.children.add( child );
	}

	public void addInstance( Instance instance )
	{
		this.instances.add( instance );
	}

	public void setInstances( LinkedList<Instance> instances )
	{
		this.instances = instances;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public Group getParent()
	{
		return parent;
	}

	@Override
	public String getParentId()
	{
		return parent.getId();
	}

	@Override
	public LinkedList<Group> getChildren()
	{
		return children;
	}

	@Override
	public LinkedList<Instance> getInstances()
	{
		return instances;
	}

	@Override
	public LinkedList<Instance> getSubgroupInstances()
	{
		LinkedList<Instance> result = new LinkedList<Instance>( instances );

		for ( Group child : children ) {
			result.addAll( child.getSubgroupInstances() );
		}

		return result;
	}

	@Override
	public Instance getGroupRepresentation()
	{
		return this.representation;
	}

	@Override
	public String toString()
	{
		return print( "", true );
	}

	private String print( String prefix, boolean isTail )
	{
		StringBuilder buf = new StringBuilder();

		buf.append( prefix )
			.append( isTail ? "L-- " : "|-- " )
			.append( id )
			.append( '(' )
			.append( instances.size() )
			.append( ')' )
			.append( '\n' );

		String childPrefix = prefix + ( isTail ? "    " : "|   " );

		// Print all children except last
		for ( int i = 0; i < children.size() - 1; ++i ) {
			Group n = children.get( i );
			if ( n instanceof BasicGroup ) {
				buf.append( ( (BasicGroup)n ).print( childPrefix, false ) )
					.append( '\n' );
			}
		}

		// Print the last child as tail
		if ( children.size() > 0 ) {
			Group n = children.get( children.size() - 1 );
			if ( n instanceof BasicGroup ) {
				buf.append( ( (BasicGroup)n ).print( childPrefix, true ) )
					.append( '\n' );
			}
		}

		return buf.toString();
	}

	/**
	 * Recalculates the centroid for this group, and updates this group's representation.
	 * 
	 * @param useSubtree
	 *            whether the calculation should also include child groups' instances.
	 * @return the calculated centroid
	 */
	public Instance recalculateCentroid( boolean useSubtree )
	{
		LinkedList<Instance> instances = useSubtree ? getSubgroupInstances() : getInstances();

		double[] centroidCoordinates = new double[instances.isEmpty() ? 0 : instances.getFirst().getData().length];
		for ( Instance inst : instances ) {
			double[] instanceData = inst.getData();
			for ( int i = 0; i < centroidCoordinates.length; i++ ) {
				centroidCoordinates[i] += instanceData[i];
			}
		}

		for ( int i = 0; i < centroidCoordinates.length; i++ ) {
			centroidCoordinates[i] /= instances.size();
		}

		this.representation = new BasicInstance( "centroid", "centroid", centroidCoordinates, "centroid" );
		return this.representation;
	}
}
