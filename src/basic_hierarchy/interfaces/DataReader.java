package basic_hierarchy.interfaces;

import java.io.IOException;

import basic_hierarchy.common.HierarchyBuilder;


public interface DataReader
{
	/**
	 * Parses the specified file and returns a {@link Hierarchy} object.
	 * 
	 * @param filePath
	 *            path to the file to read
	 * @param withInstancesNameAttribute
	 *            if true, the reader will assume that the file includes a column containing instance names
	 * @param withTrueClassAttribute
	 *            if true, the reader will assume that the file includes a column containing true class
	 * @param withColumnHeaders
	 *            if true, the reader will assume that the first row contains column headers, specifying the name for each column
	 * @param fixBreadthGaps
	 *            if true, the {@link HierarchyBuilder} will attempt to fix the raw hierarchy built from the file.
	 * @param useSubtree
	 *            whether the centroid calculation should also include child groups' instances.
	 * @return the {@link Hierarchy} object representing data in the input file
	 */
	public Hierarchy load(
		String filePath,
		boolean withInstancesNameAttribute,
		boolean withTrueClassAttribute,
		boolean withColumnHeaders,
		boolean fixBreadthGaps,
		boolean useSubtree ) throws IOException;
}
