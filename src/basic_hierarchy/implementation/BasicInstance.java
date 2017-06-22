package basic_hierarchy.implementation;

import basic_hierarchy.interfaces.Instance;


public class BasicInstance implements Instance
{
	private String instanceName;
	private double[] data;
	private String nodeId;
	private String trueClass;


	public BasicInstance( String instanceName, String nodeId, double[] data )
	{
		this.instanceName = instanceName;
		this.nodeId = nodeId;
		this.data = data;
	}

	public BasicInstance( String instanceName, String nodeId, double[] data, String trueClass )
	{
		this( instanceName, nodeId, data );
		this.trueClass = trueClass;
	}

	@Override
	public String getInstanceName()
	{
		return instanceName;
	}

    @Override
    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    @Override
	public double[] getData()
	{
		return data;
	}

    @Override
    public void setData(double[] data) {
        this.data = data;
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

    @Override
    public void setTrueClass(String trueClass) {

    }
}
