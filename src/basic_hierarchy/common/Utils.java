package basic_hierarchy.common;

import java.util.Stack;

import basic_hierarchy.interfaces.Group;


public class Utils
{
	/*
	 * Applicable only to positive numbers
	 */
	public static boolean isPositiveNumeric( String str )
	{
		for ( char c : str.toCharArray() ) {
			if ( !Character.isDigit( c ) ) return false;
		}
		return true;
	}

	public int getNumberOfSubtreeGroups( Group subtreeRoot )
	{
		Stack<Group> stack = new Stack<Group>();
		stack.push( subtreeRoot );
		int groupCount = 0;

		while ( !stack.empty() ) {
			Group g = stack.pop();
			++groupCount;
			for ( Group child : g.getChildren() ) {
				stack.push( child );
			}
		}

		return groupCount;
	}
}
