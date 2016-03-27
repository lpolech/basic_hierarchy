package basic_hierarchy.test;

import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.reader.GeneratedARFFReader;

public class ReadTest {

	public static void main(String[] args) {
		GeneratedARFFReader arffReader = new GeneratedARFFReader();
		Hierarchy H = arffReader.load("balancedTree5000hierVis.arff", true, false);
		H.printTree();

	}

}
