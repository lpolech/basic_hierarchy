package basic_hierarchy.test;

import java.io.IOException;

import basic_hierarchy.interfaces.DataReader;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.reader.GeneratedCSVReader;


public class ReadTest
{
	public static void main( String[] args )
	{
		// GeneratedARFFReader arffReader = new GeneratedARFFReader();
		// Hierarchy H = arffReader.load("balancedTree5000hierVis.arff", false, true, false);

		GeneratedCSVReader reader = new GeneratedCSVReader();
		Hierarchy hierarchy = loadSilent( reader, "Edge_tool_n03265032.sbow.csv", true, true, false, false, true );

		System.out.println( hierarchy.toString() );
	}

	private static Hierarchy loadSilent(
		DataReader reader,
		String path,
		boolean instanceNameAttr,
		boolean trueClassAttr,
		boolean dataNamesRow,
		boolean fixBreadthGaps,
		boolean useSubtree )
	{
		try {
			return reader.load( path, instanceNameAttr, trueClassAttr, dataNamesRow, fixBreadthGaps, useSubtree );
		}
		catch ( IOException e ) {
			System.err.println( "Error while reading file: " );
			e.printStackTrace();
			System.exit( 1 );
		}

		return null;
	}
}
