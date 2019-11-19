package basic_hierarchy.test.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import basic_hierarchy.common.Utils;

public class UtilsTest {

	@Test
	public void testIsPositiveNumeric() {
		assertEquals(true, Utils.isPositiveNumeric("1"));
		assertEquals(false, Utils.isPositiveNumeric("-41"));
		assertEquals(false, Utils.isPositiveNumeric("test"));
	}

	@Test(expected = RuntimeException.class)
	public void testCheckInterruptStatus() {
		Utils.checkInterruptStatus();
	}

}
