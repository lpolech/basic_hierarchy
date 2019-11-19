package basic_hierarchy.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.reader.GeneratedCSVReader;

public class ReadTest {

	GeneratedCSVReader CSVReader;

	public ReadTest() {
		CSVReader = new GeneratedCSVReader();
	}

	@Test
	public void testGeneratedCSVReader() {
		Hierarchy H = null;
		try {
			H = CSVReader.load("balancedTree5000.csv", false, false, false, false, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(5000, H.getOverallNumberOfInstances());
		assertEquals(2, H.getRoot().getNodeRepresentation().getData().length);
	}

	@Test
	public void testGetProgress() {
		assertEquals(0, CSVReader.getProgress());
	}

	@Test
	public void testGetStatusMessage() {
		assertEquals("", CSVReader.getStatusMessage());
	}
}
