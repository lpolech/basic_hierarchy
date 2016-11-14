package basic_hierarchy.test;

import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.reader.GeneratedARFFReader;
import basic_hierarchy.reader.GeneratedCSVReader;

public class ReadTest {

	public static void main(String[] args) {
//		GeneratedARFFReader arffReader = new GeneratedARFFReader();
//		Hierarchy H = arffReader.load("balancedTree5000hierVis.arff", false, true, false);

		GeneratedCSVReader arffReader = new GeneratedCSVReader();
		Hierarchy H = arffReader.load("balancedTree5000.csv", false, false, false, true);
		
		H.printTree();
	}

}
