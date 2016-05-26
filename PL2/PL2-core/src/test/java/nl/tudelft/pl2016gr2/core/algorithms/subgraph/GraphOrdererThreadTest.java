package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import static org.junit.Assert.assertEquals;
import nl.tudelft.pl2016gr2.model.HashGraph;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.model.StringSequenceNode;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test of class {@link GraphOrdererThread}.
 *
 * @author Faris
 */
public class GraphOrdererThreadTest {

  private Node nodea;
  private Node nodeb;
  private Node nodec;
  private Node noded;

  /**
   * Initialize the nodes of the graph.
   */
  @Before
  public void initializeNodes() {
    nodea = new StringSequenceNode(0);
    nodea.addOutEdge(1);
    nodeb = new StringSequenceNode(1);
    nodeb.addInEdge(0);
    nodeb.addOutEdge(2);
    nodeb.addOutEdge(3);
    nodec = new StringSequenceNode(2);
    noded = new StringSequenceNode(3);
    nodec.addInEdge(1);
    noded.addInEdge(1);
  }

  /**
   * Test of getOrderedGraph method, of class GraphOrdererThread.
   */
  @Test
  public void testGetOrderedGraph() {
    HashMap<Integer, Node> nodes = new HashMap<>();
    nodes.put(0, nodea);
    nodes.put(1, nodeb);
    nodes.put(2, nodec);
    nodes.put(3, noded);

    ArrayList<Integer> rootNodes = new ArrayList<>();
    rootNodes.add(0);
    SequenceGraph graph = new HashGraph(nodes, rootNodes, new ArrayList<>());
    GraphOrdererThread orderer = new GraphOrdererThread(graph);
    orderer.start();
    HashMap<Integer, NodePosition> orderedGraph = orderer.getOrderedGraph();
    assertEquals(4, orderedGraph.size());
    assertEquals(0, orderedGraph.get(0).getLevel());
    assertEquals(1, orderedGraph.get(1).getLevel());
    assertEquals(2, orderedGraph.get(2).getLevel());
    assertEquals(2, orderedGraph.get(3).getLevel());
  }

}
