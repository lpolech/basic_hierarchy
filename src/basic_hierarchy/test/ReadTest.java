package basic_hierarchy.test;

import java.io.IOException;

import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.reader.GeneratedCSVReader;

public class ReadTest {

	public static void main(String[] args) {
//		GeneratedARFFReader arffReader = new GeneratedARFFReader();
//		Hierarchy H = arffReader.load("balancedTree5000hierVis.arff", false, true, false);

		GeneratedCSVReader arffReader = new GeneratedCSVReader();
		Hierarchy H = null;
		try {
			H = arffReader.load("balancedTree5000.csv", false, false, false, false, true);
		}
		catch ( IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		H.printTree();
	}

}
