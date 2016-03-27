package basic_hierarchy.implementation;

import basic_hierarchy.interfaces.Instance;

public class BasicInstance implements Instance {
	private double[] data;
	private String nodeId;
	private String trueClass;
	
	public BasicInstance(String nodeId, double[] data, String trueClass)
	{
		this.nodeId = nodeId;				
		this.data = data;
		this.trueClass = trueClass;
	}
	@Override
	public String getNodeId() {
		return nodeId;
	}

	@Override
	public double[] getData() {
		return data;
	}
	@Override
	public String getTrueClass() {
		return trueClass;
	}
	@Override
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
		
	}
}
