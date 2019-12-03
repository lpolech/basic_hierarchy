package basic_hierarchy.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.reader.GeneratedCSVReader;

public class ReadTest {

	GeneratedCSVReader csvReader;

	public ReadTest() {
		csvReader = new GeneratedCSVReader();
	}

	@Test
	public void testGeneratedCSVReader() {
		Hierarchy h = null;
		try {
			h = csvReader.load("balancedTree5000.csv", false, false, false, false, true);
			assertEquals(5000, h.getOverallNumberOfInstances());
			assertEquals(2, h.getRoot().getNodeRepresentation().getData().length);
		} catch (IOException e) {
			org.junit.Assert.fail(e.toString());
		}
	}

	@Test
	public void testGetProgress() {
		assertEquals(0, csvReader.getProgress());
	}

	@Test
	public void testGetStatusMessage() {
		assertEquals("", csvReader.getStatusMessage());
	}
}
