package basic_hierarchy.implementation;

import basic_hierarchy.interfaces.Instance;


public class BasicInstance implements Instance
{
	private String instanceName;
	private double[] data;
	private String nodeId;
	private String trueClass;


	public BasicInstance( String instanceName, String assignedClass, double[] data )
	{
		this.instanceName = instanceName;
		this.nodeId = assignedClass;
		this.data = data;
	}

	public BasicInstance( String instanceName, String assignedClass, double[] data, String trueClass )
	{
		this( instanceName, assignedClass, data );
		this.trueClass = trueClass;
	}

	@Override
	public String getInstanceName()
	{
		return instanceName;
	}

	@Override
	public double[] getData()
	{
		return data;
	}

	@Override
	public String getNodeId()
	{
		return nodeId;
	}

	@Override
	public void setNodeId( String assignedClass )
	{
		this.nodeId = assignedClass;
	}

	@Override
	public String getTrueClass()
	{
		return trueClass;
	}
}
