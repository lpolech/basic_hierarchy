package basic_hierarchy.implementation;

import static basic_hierarchy.TestConst.*;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BasicInstanceTest {

	BasicInstance basIn;

	public BasicInstanceTest() {
		basIn = new BasicInstance("inst", NODE_ID_GEN_0, new double[] { 0.0, 0.5 }, null);
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
		assertArrayEquals(new double[] { 0.0, 0.5 }, basIn.getData(), 0.0);
	}

	@Test
	public void testSetData() {
		assertArrayEquals(new double[] { 0.0, 0.5 }, basIn.getData(), 0.0);
		basIn.setData(new double[] { 1.0, 1.5 });
		assertArrayEquals(new double[] { 1.0, 1.5 }, basIn.getData(), 0.0);
	}

	@Test
	public void testGetNodeId() {
		assertEquals(NODE_ID_GEN_0, basIn.getNodeId());
	}

	@Test
	public void testSetNodeId() {
		assertEquals(NODE_ID_GEN_0, basIn.getNodeId());
		basIn.setNodeId(NEW_NODE_ID);
		assertEquals(NEW_NODE_ID, basIn.getNodeId());
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
