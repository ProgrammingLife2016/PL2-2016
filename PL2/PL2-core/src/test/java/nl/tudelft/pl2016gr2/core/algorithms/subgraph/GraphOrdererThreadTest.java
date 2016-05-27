package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.HashGraph;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.model.SequenceNode;
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
    nodea = new SequenceNode(0);
    nodeb = new SequenceNode(1);
    nodec = new SequenceNode(2);
    noded = new SequenceNode(3);
    nodea.addOutEdge(nodeb);
    nodeb.addInEdge(nodea);
    nodeb.addOutEdge(nodec);
    nodeb.addOutEdge(noded);
    nodec.addInEdge(nodeb);
    noded.addInEdge(nodeb);
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

    ArrayList<Node> rootNodes = new ArrayList<>();
    rootNodes.add(nodea);
    SequenceGraph graph = new HashGraph(nodes, rootNodes, new ArrayList<>());
    GraphOrdererThread orderer = new GraphOrdererThread(graph);
    orderer.start();
    HashMap<GraphNode, NodePosition> orderedGraph = orderer.getOrderedGraph();
    assertEquals(4, orderedGraph.size());
    assertEquals(0, orderedGraph.get(nodea).getLevel());
    assertEquals(1, orderedGraph.get(nodeb).getLevel());
    assertEquals(2, orderedGraph.get(nodec).getLevel());
    assertEquals(2, orderedGraph.get(noded).getLevel());
  }

}
