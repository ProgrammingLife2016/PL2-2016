package nl.tudelft.pl2016gr2.model;

import static nl.tudelft.pl2016gr2.util.TestingUtilities.mockNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import nl.tudelft.pl2016gr2.model.graph.HashGraph;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Tests the {@link HashGraph} class.
 *
 * @author Wouter Smit
 */
public class HashGraphTest {

  private Map<Integer, GraphNode> nodes = new HashMap<>();
  private Collection<Integer> genomes = new ArrayList<>();

  /**
   * Sets up the graph structure with mocked nodes.
   */
  @Before
  public void setUp() {
    for (int i = 0; i < 5; i++) {
      nodes.put(i, mockNode(i, false));
    }
    int rootId = 5;
    nodes.put(rootId, mockNode(rootId, true));

    for (int i = 0; i < 5; i++) {
      genomes.add(i);
    }
  }

  @Test
  public void testHashGraphConstructorForNodesAndGenomes() {
    HashGraph graph = new HashGraph(nodes, genomes);

    nodes.forEach((id, node) -> assertTrue(graph.contains(node)));
    assertTrue(graph.getGenomes().containsAll(genomes));
  }

  @Test
  public void testHashGraphConstructorIncludingRootNodes() {
    Collection<GraphNode> rootNodes = new ArrayList<>(
        Arrays.asList(mockNode(5, true), mockNode(10, true)));
    HashGraph graph = new HashGraph(nodes, rootNodes, genomes);

    nodes.forEach((id, node) -> assertTrue(graph.contains(node)));
    assertTrue(graph.getGenomes().containsAll(genomes));
    assertTrue(graph.getRootNodes().containsAll(rootNodes));
  }

  @Test
  public void testToString() throws Exception {
    HashGraph graph = new HashGraph();
    assertEquals("", graph.toString());
    graph.add(mockNode(0, false));
    assertNotNull(graph.toString());
  }

  @Test
  public void testRemove() throws Exception {
    HashGraph graph = new HashGraph(nodes, genomes);
    ArrayList<GraphNode> outEdges = new ArrayList<>();
    outEdges.add(nodes.get(4));
    nodes.get(3).setOutEdges(outEdges);
    graph.remove(nodes.get(3), true, true);
    assertEquals(null, graph.getNode(nodes.get(3).getId()));
  }

  @Test
  public void testGetOrderedGraph() {
    HashMap<Integer, GraphNode> nodes = new HashMap<>();
    GraphNode node1 = new SequenceNode(1);
    node1.setLevel(10);
    GraphNode node2 = new SequenceNode(2);
    node2.setLevel(9);
    
    nodes.put(1, node1);
    nodes.put(2, node2);
    
    HashGraph graph = new HashGraph(nodes, new ArrayList<>());
    ArrayList<GraphNode> orderedGraph = graph.getOrderedGraph();
    assertEquals(2, orderedGraph.size());
    assertEquals(2, orderedGraph.get(0).getId());
    assertEquals(1, orderedGraph.get(1).getId());
  }

}
