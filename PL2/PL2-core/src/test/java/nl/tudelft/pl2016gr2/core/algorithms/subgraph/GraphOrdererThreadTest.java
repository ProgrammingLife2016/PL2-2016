package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
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
    ArrayList<Integer> links = new ArrayList<>();
    nodea = new Node(0, 1, new ArrayList<>(), 0);
    links.add(1);
    nodea.setOutlinks(links);
    nodeb = new Node(1, 1, new ArrayList<>(), 0);
    links = new ArrayList<>();
    links.add(0);
    nodeb.setInlinks(links);
    links = new ArrayList<>();
    links.add(2);
    links.add(3);
    nodeb.setOutlinks(links);
    nodec = new Node(2, 1, new ArrayList<>(), 0);
    noded = new Node(3, 1, new ArrayList<>(), 0);
    links = new ArrayList<>();
    links.add(1);
    nodec.setInlinks(links);
    noded.setInlinks(links);
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
    OriginalGraph graph = new OriginalGraph(nodes, rootNodes, new ArrayList<>());
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
