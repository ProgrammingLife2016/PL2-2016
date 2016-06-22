package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.graph.HashGraph;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.data.BaseSequence;
import nl.tudelft.pl2016gr2.model.graph.nodes.Node;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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
    BaseSequence bases = new BaseSequence("A");
    nodea = new SequenceNode(0, bases);
    nodeb = new SequenceNode(1, bases);
    nodec = new SequenceNode(2, bases);
    noded = new SequenceNode(3, bases);
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
    GraphOrdererThread orderer = new GraphOrdererThread(graph, new LinkedList<>());
    orderer.start();
    SequenceGraph orderedGraph = orderer.getGraph();
    assertEquals(4, orderedGraph.size());
  }
}
