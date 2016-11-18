package basic_hierarchy.implementation;

import basic_hierarchy.interfaces.Instance;


public class BasicInstance implements Instance
{
	private String instanceName;
	private double[] data;
	private String assignedClass;
	private String trueClass;


	public BasicInstance( String instanceName, String assignedClass, double[] data )
	{
		this.instanceName = instanceName;
		this.assignedClass = assignedClass;
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
	public String getAssignedClass()
	{
		return assignedClass;
	}

	@Override
	public String getTrueClass()
	{
		return trueClass;
	}

	public void setAssignedClass( String assignedClass )
	{
		this.assignedClass = assignedClass;
	}
}
