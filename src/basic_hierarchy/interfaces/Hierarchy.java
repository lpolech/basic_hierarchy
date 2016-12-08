package basic_hierarchy.interfaces;

public interface Hierarchy {
	public Node getRoot();
	public Node[] getGroups();
	public int getNumberOfGroups();
	public String[] getClasses();
	public int getNumberOfClasses();
    public int[] getClassesCount();
    public int getParticularClassCount(String className, boolean withClassHierarchy);
	public int getOverallNumberOfInstances();
	public void printTree();//@FIXME this could be a default implementation in java 8
    public Hierarchy getFlatClusteringWithCommonEmptyRoot(); //@FIXME we can make it a static method with default
    //implementation but only in java 8
}
