package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;


public class NodeTest {
  
  private Node node;
  private String bases = "actcaggcagatcattcgctagacat";
  
  /**
   * Creates a new clean node before every test.
   */
  @Before
  public void setup() {
    String str1 = "ref1.fasta";
    String str2 = "ref2.fasta";
    ArrayList<String> ar = new ArrayList<>();
    ar.add(str1);
    ar.add(str2);
    node = new Node(3, 6, new ArrayList<>(), 5);
    node.setGenomes(ar);
  }

  @Test
  public void alignmentTest() {
    node.setAlignment(9);
    assertEquals(9, node.getAlignment());
  }
  
  @Test
  public void flowTest() {
    node.setFlow(5.0);
    node.addFlow(6.0);
    node.addFlow(-4.0);
    assertEquals(7.0, node.getFlow(), 0.001);
  }
  
  @Test
  public void toStringTest() {
    assertEquals(node.toString(), 
        "id: 3 sequencelength: 6 inlinks: [] outlinks: [] snips: 5, id: 3, flow: 0.0");
  }
  
  @Test
  public void getSnipsTest() {
    assertEquals(5, node.getSnips());
  }
  
  @Test
  public void basesTest() {
    node.setBases(bases);
    assertEquals(bases, node.getBases());
  }
  
  @Test
  public void genomeTest() {
    Iterator<String> it = node.getGenomes().iterator();
    it.next();
    assertEquals("ref2.fasta", it.next());
  }

}
