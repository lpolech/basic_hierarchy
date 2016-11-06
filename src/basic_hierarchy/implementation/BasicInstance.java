package basic_hierarchy.implementation;

import basic_hierarchy.interfaces.Instance;


public class BasicInstance implements Instance
{
	private String instanceName;
	private double[] data;
	private String groupId;
	private String trueClass;


	public BasicInstance( String instanceName, String groupId, double[] data, String trueClass )
	{
		this.instanceName = instanceName;
		this.groupId = groupId;
		this.data = data;
		this.trueClass = trueClass;
	}

	@Override
	public String getGroupId()
	{
		return groupId;
	}

	@Override
	public double[] getData()
	{
		return data;
	}

	@Override
	public String getTrueClass()
	{
		return trueClass;
	}

	@Override
	public String getInstanceName()
	{
		return instanceName;
	}

	@Override
	public void setGroupId( String groupId )
	{
		this.groupId = groupId;
	}
}
