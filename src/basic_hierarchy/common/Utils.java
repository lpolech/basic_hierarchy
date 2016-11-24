package basic_hierarchy.common;

import java.util.Stack;

import basic_hierarchy.interfaces.Node;


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

	public int getNumberOfSubtreeGroups( Node subtreeRoot )
	{
		Stack<Node> stack = new Stack<Node>();
		stack.push( subtreeRoot );
		int groupCount = 0;

		while ( !stack.empty() ) {
			Node g = stack.pop();
			++groupCount;
			for ( Node child : g.getChildren() ) {
				stack.push( child );
			}
		}

		return groupCount;
	}
}
