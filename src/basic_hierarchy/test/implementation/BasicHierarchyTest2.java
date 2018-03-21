package basic_hierarchy.test.implementation;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import basic_hierarchy.common.Constants;
import basic_hierarchy.implementation.BasicHierarchy;
import basic_hierarchy.implementation.BasicInstance;
import basic_hierarchy.implementation.BasicNode;
import basic_hierarchy.interfaces.Hierarchy;
import basic_hierarchy.interfaces.Instance;
import basic_hierarchy.interfaces.Node;
import basic_hierarchy.test.TestCommon;

public class BasicHierarchyTest2 {

	Hierarchy h;
    Hierarchy flat;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
        System.setErr(System.err);
    }
    
	public BasicHierarchyTest2() 
	{
		h = TestCommon.getFourGroupsHierarchy();
        flat = h.getFlatClusteringWithCommonEmptyRoot();
	}
	
	@Test
	public void testBasicHierarchyListOfQextendsNodeStringArray() 
	{
		LinkedList<Instance> instances = new LinkedList<>();
	    instances.add(new BasicInstance("first", "gen.0", new double[]{0.0, 0.5}, null));
	    instances.add(new BasicInstance("second", "gen.0", new double[]{1.5, 2.0}, null));
	    instances.add(new BasicInstance("third", "gen.0", new double[]{0.0, 0.5}, null));
	     
	    BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), instances, false);
	     
		LinkedList<Node> nodes = new LinkedList<>();
		nodes.add( node ) ;
		nodes.add( new BasicNode("id2", null, (new BasicInstance("two", "gen.0.0", new double[]{0.0, -1.5}, "gen.0") ))) ;
		
		h= new BasicHierarchy(nodes, new String [] {});
		
		LinkedList<Node> nodes2 = new LinkedList<>();
		nodes2.add( new BasicNode("id1", null,null)) ;
		
		Hierarchy h2 = new BasicHierarchy(nodes2, null);
		
		LinkedList<Node> nodes3 = new LinkedList<>();
		nodes3.add( new BasicNode("id1", null, (new BasicInstance("one", "gen.0", new double[]{1.0, 1.5}, null) ))) ;
		nodes3.add( new BasicNode("id2", null, (new BasicInstance("two", "gen.0.0", new double[]{0.0, -1.5}, null) ))) ;
		
		Hierarchy h3= new BasicHierarchy(nodes3, new String [] {});

	}

	@Test
	public void testBasicHierarchyNodeListOfQextendsNodeStringArrayMapOfStringIntegerInt() {
		 LinkedList<Instance> instances = new LinkedList<>();
	     instances.add(new BasicInstance("first", "gen.0", new double[]{0.0, 0.5}, null));
	     instances.add(new BasicInstance("second", "gen.0", new double[]{1.5, 2.0}, null));
	     instances.add(new BasicInstance("third", "gen.0", new double[]{0.0, 0.5}, null));
	     
	     BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), instances, false);
	     
	     LinkedList<Node> nodes = new LinkedList<>();
	     nodes.add( new BasicNode("id1", null, (new BasicInstance("one", "gen.0", new double[]{1.0, 1.5}, null) ))) ;
	     nodes.add( new BasicNode("id2", null, (new BasicInstance("two", "gen.0.0", new double[]{0.0, -1.5}, null) ))) ;
	     
	     HashMap<String, Integer> map=new HashMap<String,Integer> ();
	     map.put("gen.0",1);
		 h= new BasicHierarchy(node,nodes, new String[]{},map,3 );
		
	}

	@Test
	public void testBasicHierarchyNodeListOfQextendsNodeStringArrayMapOfStringInteger() {
		LinkedList<Instance> instances = new LinkedList<>();
	     instances.add(new BasicInstance("first", "gen.0", new double[]{0.0, 0.5}, null));
	     instances.add(new BasicInstance("second", "gen.0", new double[]{1.5, 2.0}, null));
	     instances.add(new BasicInstance("third", "gen.0", new double[]{0.0, 0.5}, null));
	     
	     BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), instances, false);
	     
	     LinkedList<Node> nodes = new LinkedList<>();
	     nodes.add( new BasicNode("id1", null, (new BasicInstance("one", "gen.0", new double[]{1.0, 1.5}, null) ))) ;
	     nodes.add( new BasicNode("id2", null, (new BasicInstance("two", "gen.0.0", new double[]{0.0, -1.5}, null) ))) ;
	     
	     h= new BasicHierarchy(node, nodes, new String[] {}, new HashMap<String, Integer> () );
	}

	@Test
	public void testBasicHierarchyNodeListOfQextendsNodeMapOfStringIntegerInt() {
		
		LinkedList<Instance> instances = new LinkedList<>();
	     instances.add(new BasicInstance("first", "gen.0", new double[]{0.0, 0.5}, null));
	     instances.add(new BasicInstance("second", "gen.0", new double[]{1.5, 2.0}, null));
	     instances.add(new BasicInstance("third", "gen.0", new double[]{0.0, 0.5}, null));
	     
	     BasicNode node = new BasicNode(Constants.ROOT_ID, null, new LinkedList<Node>(), instances, false);
	     
	     LinkedList<Node> nodes = new LinkedList<>();
	     nodes.add( new BasicNode("id1", null, (new BasicInstance("one", "gen.0", new double[]{1.0, 1.5}, null) ))) ;
	     nodes.add( new BasicNode("id2", null, (new BasicInstance("two", "gen.0.0", new double[]{0.0, -1.5}, null) ))) ;
			
	     h=  new BasicHierarchy(node, nodes, new TreeMap<String, Integer> (), 1 );
	}

	@Test
	public void testGetRoot() {
		assertNotEquals(null, h.getRoot());
		assertEquals("gen.0", h.getRoot().getId());
	}

	@Test
	public void testGetNumberOfGroups() {
		assertEquals(4, h.getNumberOfGroups());
	}

	@Test
	public void testGetNumberOfClasses() {
		assertEquals(4, h.getNumberOfClasses());
	}

	@Test
	public void testGetGroups() {
		assertEquals(4,h.getGroups().length);
	}

	@Test
	public void testGetClasses() {
		assertArrayEquals(new String[] {"gen.0","gen.0.0","gen.0.0.0","gen.0.1"},  h.getClasses());
	}

	@Test
	public void testGetClassesCount() {
		assertArrayEquals(new int[] {4,2,3,2}, h.getClassesCount());
	}

	@Test
	public void testGetParticularClassCount() {
		assertEquals(11,h.getParticularClassCount("gen.0", true) );
		assertEquals(4,h.getParticularClassCount("gen.0", false) );
		assertEquals(-1,h.getParticularClassCount("gem", false) );
	}

	@Test
	public void testGetOverallNumberOfInstances() {
		assertEquals(11, h.getOverallNumberOfInstances());
	}

	@Test
	public void testGetDataNames() {
		assertArrayEquals(null, h.getDataNames());	
	}

	@Test
	public void testToString() {
		assertEquals("L--gen.0(2)|--gen.0.0(2)|L--gen.0.0.0(4)L--gen.0.1(3)", h.toString().replaceAll("\\s", ""));
	}

	@Test(expected= RuntimeException.class)
	public void testToStringThrowExc() {
		h= new BasicHierarchy(null, null, null, null);
		h.toString();
	}
	
	@Test
	public void testPrintTree() {
		h.printTree();
		assertEquals("L--gen.0(2)|--gen.0.0(2)|L--gen.0.0.0(4)L--gen.0.1(3)", outContent.toString().replaceAll("\\s", ""));
	}

}
