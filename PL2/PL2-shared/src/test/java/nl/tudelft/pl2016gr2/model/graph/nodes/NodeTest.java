package nl.tudelft.pl2016gr2.model.graph.nodes;

//package nl.tudelft.pl2016gr2.model;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//
//
//public class NodeTest {
//
//  private Node node;
//  private String bases = "actcaggcagatcattcgctagacat";
//
//  /**
//   * Creates a new clean node before every test.
//   */
//  @Before
//  public void setup() {
//    String str1 = "ref1.fasta";
//    String str2 = "ref2.fasta";
//    ArrayList<String> ar = new ArrayList<>();
//    ar.add(str1);
//    ar.add(str2);
//    node = new Node(3, 6, new ArrayList<>(), 5);
//    node.setGenomes(ar);
//  }
//
//  @Test
//  public void alignmentTest() {
//    node.setAlignment(9);
//    assertEquals(9, node.getAlignment());
//  }
//
//  @Test
//  public void flowTest() {
//    node.setFlow(5.0);
//    node.addFlow(6.0);
//    node.addFlow(-4.0);
//    assertEquals(7.0, node.getFlow(), 0.001);
//  }
//
//  @Test
//  public void toStringTest() {
//    assertEquals(node.toString(),
//        "id: 3 sequencelength: 6 inlinks: [] outlinks: [] snips: 5, id: 3, flow: 0.0");
//  }
//
//  @Test
//  public void testEqualsOne() {
//    assertTrue(node.equals(new Node(3, 6, new ArrayList<>(), 5)));
//  }
//
//  @Test
//  public void testEqualsTwo() {
//    assertFalse(node.equals(new Node(2, 7, new ArrayList<>(), 5)));
//  }
//
//  @Test
//  public void testEqualsThree() {
//    assertFalse(node.equals(new Object()));
//  }
//
//  @Test
//  public void testHashCodeOne() {
//    assertEquals(node.hashCode(), new Node(3, 6, new ArrayList<>(), 5).hashCode());
//  }
//
//  @Test
//  public void testGetGenomesOverEdgeOne() {
//
//    node.addOutlink(2);
//
//    assertEquals(0, node.getGenomesOverEdge(new Node(2, 0, new ArrayList<>(), 0)));
//  }
//
//  @Test
//  public void testGetGenomesOverEdgeTwo() {
//    ArrayList<String> ar = new ArrayList<>();
//    ar.add("ref1.fasta");
//
//    Node otherNode = new Node(2, 0, new ArrayList<>(), 0);
//    otherNode.setGenomes(ar);
//
//    node.addOutlink(2);
//
//    assertEquals(1, node.getGenomesOverEdge(otherNode));
//  }
//
//  @Test
//  public void testGetGenomesOverEdgeThree() {
//    ArrayList<String> ar = new ArrayList<>();
//    ar.add("ref1.fasta");
//    ar.add("ref2.fasta");
//
//    Node otherNode = new Node(2, 0, new ArrayList<>(), 0);
//    otherNode.setGenomes(ar);
//
//    node.addOutlink(2);
//
//    assertEquals(2, node.getGenomesOverEdge(otherNode));
//  }
//
//  @Test
//  public void getSnipsTest() {
//    assertEquals(5, node.getSnips());
//  }
//
//  @Test
//  public void basesTest() {
//    node.setBases(bases);
//    assertEquals(bases, node.getBases());
//  }
//
//  @Test
//  public void genomeTest() {
//    Iterator<String> it = node.getGenomes().iterator();
//    it.next();
//    assertEquals("ref2.fasta", it.next());
//  }
//
//}
