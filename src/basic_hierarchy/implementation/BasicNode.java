package basic_hierarchy.implementation;

import java.util.LinkedList;

import basic_hierarchy.interfaces.Node;
import basic_hierarchy.interfaces.Instance;


public class BasicNode implements Node
{
	private String id;
	private Node parent;
	private LinkedList<Node> children;
	private LinkedList<Instance> instances;
	private Instance representation;


	private BasicNode( String id, Node parent, LinkedList<Node> children, LinkedList<Instance> instances )
	{
		this.id = id;
		this.parent = parent;
		this.children = children;
		this.instances = instances;
	}

	public BasicNode( String id, Node parent, LinkedList<Node> children, LinkedList<Instance> instances, boolean useSubtree )
	{
		this( id, parent, children, instances );
		recalculateCentroid( useSubtree );
	}

	public BasicNode( String id, Node parent, LinkedList<Node> children, LinkedList<Instance> instances, Instance representation )
	{
		this( id, parent, children, instances );
		this.representation = representation;
	}

	public BasicNode( String id, Node parent, boolean useSubtree )
	{
		this( id, parent, new LinkedList<Node>(), new LinkedList<Instance>(), useSubtree );
	}

	public BasicNode( String id, Node parent, Instance representation )
	{
		this( id, parent, new LinkedList<Node>(), new LinkedList<Instance>(), representation );
	}

	@Override
	public void setParent( Node parent )
	{
		this.parent = parent;
	}

	public void setId( String id )
	{
		this.id = id;
	}

	@Override
	public void setChildren( LinkedList<Node> children )
	{
		this.children = children;
	}

	@Override
	public void addChild( Node child )
	{
		this.children.add( child );
	}

	@Override
	public void addInstance( Instance instance )
	{
		this.instances.add( instance );
	}

	@Override
	public void setInstances( LinkedList<Instance> instances )
	{
		this.instances = instances;
	}

	@Override
	public void setRepresentation( Instance representation )
	{
		this.representation = representation;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public Node getParent()
	{
		return parent;
	}

	@Override
	public String getParentId()
	{
		return parent.getId();
	}

	@Override
	public LinkedList<Node> getChildren()
	{
		return children;
	}

	@Override
	public LinkedList<Instance> getNodeInstances()
	{
		return instances;
	}

	@Override
	public LinkedList<Instance> getSubtreeInstances()
	{
		LinkedList<Instance> result = new LinkedList<Instance>( instances );

		for ( Node child : children ) {
			result.addAll( child.getSubtreeInstances() );
		}

		return result;
	}

	@Override
	public Instance getNodeRepresentation()
	{
		return this.representation;
	}

	@Override
	public String toString()
	{
		return print( "", true );
	}

	public void printSubtree()
	{
		System.out.println( toString() );
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
			Node n = children.get( i );
			if ( n instanceof BasicNode ) {
				buf.append( ( (BasicNode)n ).print( childPrefix, false ) )
					.append( '\n' );
			}
		}

		// Print the last child as tail
		if ( children.size() > 0 ) {
			Node n = children.get( children.size() - 1 );
			if ( n instanceof BasicNode ) {
				buf.append( ( (BasicNode)n ).print( childPrefix, true ) )
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
		LinkedList<Instance> instances = useSubtree ? getSubtreeInstances() : getNodeInstances();

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

		Instance oldRepresentation = this.representation;
		this.representation = new BasicInstance( "centroid", "centroid", centroidCoordinates, "centroid" );
		return oldRepresentation;
	}
}
