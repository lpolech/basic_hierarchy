package basic_hierarchy.interfaces;

public interface DataReader {
	public Hierarchy load(String filePath, boolean withClassAttribute, boolean fillBreathGaps);
}
