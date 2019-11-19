package basic_hierarchy.common;

public class Utils {
	/*
	 * Applicable only to positive numbers
	 */
	public static boolean isPositiveNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	/**
	 * Checks whether the current thread has been interrupted. If it was, clears the
	 * interrupt flag and throws an exception.
	 */
	public static void checkInterruptStatus() {
		if (Thread.interrupted())
			throw new RuntimeInterruptedException();
	}

	@SuppressWarnings("serial")
	public static class RuntimeInterruptedException extends RuntimeException {
	}
}
