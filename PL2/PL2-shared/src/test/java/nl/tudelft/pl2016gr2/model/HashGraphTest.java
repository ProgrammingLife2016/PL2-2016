package nl.tudelft.pl2016gr2.model;

import static nl.tudelft.pl2016gr2.model.SequenceGraphTest.mockNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Tests the {@link HashGraph} class.
 *
 * @author Wouter Smit
 */
public class HashGraphTest {
  private Map<Integer, GraphNode> nodes = new HashMap<>();
  private Collection<String> genomes = new ArrayList<>();

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
      genomes.add("genome" + i);
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

}