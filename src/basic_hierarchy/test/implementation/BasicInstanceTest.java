package basic_hierarchy.test.implementation;

import static org.junit.Assert.*;

import org.junit.Test;

import basic_hierarchy.implementation.BasicInstance;

public class BasicInstanceTest {

	private static final String GEN_0 = "gen.0";
	BasicInstance basIn;
	
	public BasicInstanceTest() {
		basIn = new BasicInstance("inst", GEN_0, new double[]{0.0, 0.5}, null);
	}

	@Test
	public void testGetInstanceName() {
		assertEquals("inst", basIn.getInstanceName());
	}

	@Test
	public void testSetInstanceName() {
		assertEquals("inst", basIn.getInstanceName());
		basIn.setInstanceName("newInst");
		assertEquals("newInst", basIn.getInstanceName());
	}

	@Test
	public void testGetData() {
		assertArrayEquals(new double[]{0.0, 0.5}, basIn.getData(), 0.0);
	}

	@Test
	public void testSetData() {
		assertArrayEquals(new double[]{0.0, 0.5}, basIn.getData(), 0.0);
		basIn.setData(new double[]{1.0, 1.5});
		assertArrayEquals(new double[]{1.0, 1.5}, basIn.getData(), 0.0);
	}

	@Test
	public void testGetNodeId() {
		assertEquals(GEN_0, basIn.getNodeId());
	}

	@Test
	public void testSetNodeId() {
		assertEquals(GEN_0, basIn.getNodeId());
		basIn.setNodeId("noweId");
		assertEquals("noweId", basIn.getNodeId());
	}

	@Test
	public void testGetTrueClass() {
		assertEquals(null, basIn.getTrueClass());
	}

	@Test
	public void testSetTrueClass() {
		basIn.setTrueClass("trueClass");
		assertEquals("trueClass", basIn.getTrueClass());
	}

}
