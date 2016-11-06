package basic_hierarchy.test;

import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.reader.GeneratedARFFReader;
import basic_hierarchy.reader.GeneratedCSVReader;


public class ReadTest
{
	public static void main( String[] args )
	{
		// GeneratedARFFReader arffReader = new GeneratedARFFReader();
		// Hierarchy H = arffReader.load("balancedTree5000hierVis.arff", false, true, false);

		GeneratedCSVReader arffReader = new GeneratedCSVReader();
		Hierarchy hierarchy = arffReader.load( "Edge_tool_n03265032.sbow.csv", true, true, false );

		System.out.println( hierarchy.toString() );
	}
}
