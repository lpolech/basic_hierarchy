package basic_hierarchy.implementation;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basic_hierarchy.interfaces.Instance;


public class BasicInstance implements Instance
{
	private String instanceName;
	private String[] dataNames;
	private double[] data;
	private String assignedClass;
	private String trueClass;


	public BasicInstance( String instanceName, String assignedClass, String[] dataNames, double[] data )
	{
		this.instanceName = instanceName;
		this.assignedClass = assignedClass;
		this.dataNames = dataNames;
		this.data = data;
	}

	public BasicInstance( String instanceName, String assignedClass, String[] dataNames, double[] data, String trueClass )
	{
		this( instanceName, assignedClass, dataNames, data );
		this.trueClass = trueClass;
	}

	@Override
	public String getInstanceName()
	{
		return instanceName;
	}

	@Override
	public String[] getDataNames()
	{
		return dataNames;
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

	public List<Map.Entry<String, Double>> getNamesAndData()
	{
		if ( dataNames.length != data.length ) {
			throw new RuntimeException( "Error: data names array and data array do not have the same length!" );
		}

		List<Map.Entry<String, Double>> result = new ArrayList<>( data.length );

		for ( int i = 0; i < data.length; ++i ) {
			result.add(
				new AbstractMap.SimpleImmutableEntry<String, Double>( dataNames[i], data[i] )
			);
		}

		return result;
	}
}
