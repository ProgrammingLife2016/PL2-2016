package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;

public class OriginalGraphTest extends GraphTest {
  
  private OriginalGraph curGraph;

  @Override
  public GraphInterface getInstance() {
    OriginalGraph graph = new OriginalGraph();
    ArrayList<String> ar = new ArrayList<>();
    ar.add("aba");
    Node n1 = new Node(2, 8, ar, 5);
    Node n2 = new Node(5, 8, new ArrayList<>(), 9);
    graph.addNode(n1);
    graph.addNode(n2);
    graph.setGenoms(ar);
    this.curGraph = graph;
    return graph;
  }
  
  @Test
  public void testGetNodes() {
    assertEquals(2, curGraph.getNodes().size());
  }
  
  @Test
  public void testGetGenoms() {
    assertEquals(1, curGraph.getGenoms().size());
  }
  
  @Test
  public void testToString() {
    String exp = "id: 2 sequencelength: 8 inlinks: [] outlinks: [] snips: 5, id: 2, "
        + "flow: 0.0\nid: 5 sequencelength: 8 inlinks: [] outlinks: [] snips: 9, id: 5, "
        + "flow: 0.0\n";
    assertEquals(exp, curGraph.toString());
  }
  
  @Test
  public void getTargetTest() {
    assertEquals(0, curGraph.getTargets(2).size());
  }
}
