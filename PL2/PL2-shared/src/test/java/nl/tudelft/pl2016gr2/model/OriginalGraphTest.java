package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class OriginalGraphTest extends GraphTest {
  
  private OriginalGraph curGra;

  @Override
  public GraphInterface getInstance() {
    OriginalGraph gra = new OriginalGraph();
    ArrayList<String> ar = new ArrayList<String>();
    ar.add("aba");
    Node n1 = new Node(2, 8, ar, 5);
    Node n2 = new Node(5, 8, null, 9);
    gra.addNode(n1);
    gra.addNode(n2);
    gra.setGenoms(ar);
    this.curGra = gra;
    return gra;
  }
  
  @Test
  public void testGetNodes() {
    assertEquals(curGra.getNodes().size(), 2);
  }
  
  @Test
  public void testGetGenoms() {
    assertEquals(curGra.getGenoms().size(), 1);
  }
  
  @Test
  public void testToString() {
    String exp = "id: 2 sequencelength: 8 inlinks: [] outlinks: [] snips: 5, id: 2, "
        + "flow: 0.0\nid: 5 sequencelength: 8 inlinks: [] outlinks: [] snips: 9, id: 5, "
        + "flow: 0.0\n";
    assertEquals(curGra.toString(), exp);
  }
  
  @Test
  public void getTargetTest() {
    assertEquals(curGra.getTargets(2).size(), 0);
  }
}
