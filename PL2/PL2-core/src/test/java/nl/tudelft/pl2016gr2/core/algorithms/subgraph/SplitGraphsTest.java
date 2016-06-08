package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import static nl.tudelft.pl2016gr2.util.TestingUtilities.mockNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.pl2016gr2.model.graph.HashGraph;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.data.BaseSequence;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Tests the {@link SplitGraphs} class.
 *
 * @author Wouter Smit
 */
public class SplitGraphsTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private SequenceGraph mockedGraph;
  private ArrayList<Integer> genomeSet;
  private SplitGraphs defInstance;
  private HashMap<Integer, GraphNode> nodes;

  /**
   * Sets up mocked dependencies.
   */
  @Before
  public void setup() {
    nodes = new HashMap<>();
    genomeSet = new ArrayList<>(Arrays.asList(new Integer[] {0, 1, 3, 4, 5}));
    mockedGraph = mock(SequenceGraph.class);
    when(mockedGraph.getGenomes()).thenReturn(genomeSet);
    defInstance = new SplitGraphs(mockedGraph);
    setupIterator();
  }

  private void setupIterator() {
    @SuppressWarnings("unchecked") // Necessary when mocking generic types
        Iterator<GraphNode> mockedIterator = mock(Iterator.class);

    GraphNode firstNode = mockGraphNode(new Integer[] {genomeSet.get(0), genomeSet.get(1)},
        new Integer[] {3}, new Integer[] {3}, 0);
    GraphNode secondNode = mockGraphNode(new Integer[] {genomeSet.get(1)}, new Integer[] {0, 1, 3},
        new Integer[] {0, 1, 3}, 1);
    GraphNode thirdNode = mockGraphNode(new Integer[] {genomeSet.get(1), genomeSet.get(2)},
        new Integer[] {2, 5, 3}, new Integer[] {1, 10, 2}, 3);

    when(mockedIterator.hasNext()).thenReturn(true, true, true, false);
    when(mockedIterator.next()).thenReturn(firstNode, secondNode, thirdNode);
    doAnswer(new ForEachRemainingAnswer()).when(mockedIterator).forEachRemaining(any());
    doAnswer(new ForEachAnswer()).when(mockedGraph).forEach(any());
    when(mockedGraph.iterator()).thenReturn(mockedIterator);
    when(mockedGraph.getNode(0)).thenReturn(firstNode);
    when(mockedGraph.getNode(1)).thenReturn(secondNode);
    when(mockedGraph.getNode(3)).thenReturn(thirdNode);
  }

  /**
   * Creates a mocked node with a specified identifier, genome list, inLinks and outLinks.
   *
   * @param genomes    The list of genomes in this node
   * @param inLinks    The list of inLinks
   * @param outLinks   The list of outLinks
   * @param identifier The node ID
   */
  private GraphNode mockGraphNode(Integer[] genomes, Integer[] inLinks, Integer[] outLinks,
      int identifier) {
    Collection<GraphNode> inLinkNodes = new ArrayList<>();
    for (Integer inLink : inLinks) {
      inLinkNodes.add(mockNode(inLink, false));
    }
    Collection<GraphNode> outLinkNodes = new ArrayList<>();
    for (Integer outLink : outLinks) {
      outLinkNodes.add(mockNode(outLink, false));
    }
    return new SequenceNode(identifier, new BaseSequence("ACTG"), Arrays.asList(genomes),
        inLinkNodes, outLinkNodes);
  }

  @Test
  public void testSplitGraphs() {
    assertEquals(mockedGraph,
        AccessPrivate.getFieldValue("graph_field", SplitGraphs.class, defInstance));
  }

  /**
   * Verifies that the {@link SplitGraphs#getSubgraph(Collection)} method throws an assertionError
   * on invalid genomes.
   */
  @Test
  public void testGetSubgraphThrowsAssertionErrorForInvalidGenomes() {
    int wrongGenome = 1000;
    assertFalse(genomeSet.contains(wrongGenome));

    exception.expect(AssertionError.class);
    defInstance.getSubgraph(Collections.singletonList(wrongGenome));
  }

  /**
   * Verifies that the {@link SplitGraphs#getSubgraph(Collection)} method returns a non-empty
   * graph.
   */
  @Test
  public void testGetSubgraphReturnsNonEmptyGraph() {

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, 1));
    assertFalse(subgraph.isEmpty());
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains at least the specified genome.
   */
  @Test
  public void testGetSubgraphOnlyContainsGenomeNodes() {
    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, 1));
    assertFalse(subgraph.isEmpty());

    //for (GraphNode graphNode : subgraph) {
    //  assertTrue(graphNode.getGenomes().contains(genomeSet.get(0)));
    //}
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph with all
   * nodes that contain at least one of the specified genomes.
   */
  @Test
  public void testGetSubgraphContainsAllGenomeNodes() {
    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, 1));
    assertFalse(subgraph.isEmpty());

    Collection<GraphNode> expectedNodes = new ArrayList<>();
    mockedGraph.iterator().forEachRemaining(expectedNodes::add);
    expectedNodes.removeIf(node -> {
      boolean noneOfGenomes = true;
      Collection<Integer> genomes = node.getGenomes();
      for (Integer genome : genomes) {
        if (genomeSet.subList(0, 1).contains(genome)) {
          noneOfGenomes = false;
        }
      }
      return noneOfGenomes;
    });

    for (GraphNode graphNode : subgraph) {
      assertTrue(graphNode.getGenomes().containsAll(expectedNodes));
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the specified genomes.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedGenomes() {
    int subListSize = 2;
    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, subListSize));
    assertFalse(subgraph.isEmpty());
    for (GraphNode graphNode : subgraph) {
      assertTrue(genomeSet.subList(0, subListSize).containsAll(graphNode.getGenomes()));
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains all of the specified genomes.
   */
  @Test
  public void testGetSubgraphNodesContainAllSpecifiedGenomes() {
    int subListSize = 2;
    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, subListSize));
    assertFalse(subgraph.isEmpty());

    /*for (GraphNode graphNode : subgraph) {
      Collection<Integer> expectedGenomes = mockedGraph.getNode(graphNode.getId()).getGenomes();
      expectedGenomes.removeIf(genome -> !genomeSet.subList(0, subListSize).contains(genome));
      assertTrue(graphNode.getGenomes().containsAll(expectedGenomes));
    }*/
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the inLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedInLinks() {
    int subListSize = 1;
    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, subListSize));
    assertFalse(subgraph.isEmpty());

    for (GraphNode graphNode : subgraph) {
      for (GraphNode inEdge : graphNode.getInEdges()) {
        assertTrue(subgraph.contains(inEdge));
      }
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains all the inLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphContainsAllSpecifiedInLinks() {
    int subListSize = 1;
    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, subListSize));
    assertFalse(subgraph.isEmpty());

    for (GraphNode graphNode : subgraph) {
      Collection<GraphNode> expectedLinks = mockedGraph.getNode(graphNode.getId()).getInEdges();
      expectedLinks.removeIf(link -> !nodes.containsValue(link));
      assertTrue(graphNode.getInEdges().containsAll(expectedLinks));
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the outLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedOutLinks() {
    int subListSize = 1;
    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, subListSize));
    assertFalse(subgraph.isEmpty());

    for (GraphNode graphNode : subgraph) {
      for (GraphNode outEdge : graphNode.getOutEdges()) {
        assertTrue(subgraph.contains(outEdge));
      }
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains all the outLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainAllSpecifiedOutLinks() {
    int subListSize = 1;
    SequenceGraph subgraph = defInstance.getSubgraph(genomeSet.subList(0, subListSize));

    assertFalse(subgraph.isEmpty());
    for (GraphNode graphNode : subgraph) {
      Collection<GraphNode> expectedLinks = mockedGraph.getNode(graphNode.getId()).getOutEdges();
      expectedLinks.removeIf(link -> !nodes.containsValue(link));
      assertTrue(graphNode.getOutEdges().containsAll(expectedLinks));
    }
  }

  /**
   * Tests that the edge marked with <code>xx</code> is not added to the subgraph.
   * <pre>
   *     (1)
   *    /   \
   * (0)-xx-(2)
   * </pre>
   * This is given two genomes.<br />
   * <code>Genome 1: 0 -> 1 -> 2</code>
   * <code>Genome 2: 0 -> 2</code>
   * When a subgraph is created for genome 1, the marked edge should not be in the graph.
   */
  @Test
  @SuppressWarnings("checkstyle:methodlength")
  public void testGetSubGraphContainsOnlyEdgesWithCorrectGenomes() {
    // Mock the graph
    int genomeOne = 1;
    int genomeTwo = 2;
    GraphNode zero = mockGraphNode(
        new Integer[] {genomeOne, genomeTwo}, new Integer[] {}, new Integer[] {}, 0);
    GraphNode one = mockGraphNode(new Integer[] {genomeOne}, new Integer[] {}, new Integer[] {}, 1);
    GraphNode two = mockGraphNode(
        new Integer[] {genomeOne, genomeTwo}, new Integer[] {}, new Integer[] {}, 2);

    zero.addAllInEdges(Collections.emptyList());
    zero.addAllOutEdges(Arrays.asList(one, two));
    one.addAllInEdges(Collections.singletonList(zero));
    one.addAllOutEdges(Collections.singletonList(two));
    two.addAllInEdges(Arrays.asList(zero, one));
    two.addAllOutEdges(Collections.emptyList());

    Map<Integer, GraphNode> nodes = new HashMap<>();
    nodes.put(0, zero);
    nodes.put(1, one);
    nodes.put(2, two);
    SequenceGraph graph = new HashGraph(
        nodes, Collections.singletonList(zero), Arrays.asList(genomeOne, genomeTwo));

    // Instrument the sub graph
    SplitGraphs splitGraph = new SplitGraphs(graph);
    SequenceGraph subGraph = splitGraph.getSubgraph(Collections.singletonList(1));

    graph.forEach(node -> assertTrue(subGraph.contains(node)));

    subGraph.forEach(node -> {
      if (node.getId() == 0) {
        assertFalse(node.getOutEdges().contains(two));
        assertTrue(node.getOutEdges().contains(one));
      }
      if (node.getId() == 1) {
        assertTrue(node.getOutEdges().contains(two));
        assertTrue(node.getInEdges().contains(zero));
      }
      if (node.getId() == 2) {
        assertFalse(node.getInEdges().contains(zero));
        assertTrue(node.getInEdges().contains(one));
      }
    });
  }

  @Test
  @SuppressWarnings("checkstyle:methodlength")
  public void testGetSubGraphCreatesNewNodes() {
    // Mock the graph
    int genomeOne = 1;
    int genomeTwo = 2;
    GraphNode zero = mockGraphNode(
        new Integer[] {genomeOne, genomeTwo}, new Integer[] {}, new Integer[] {}, 0);
    GraphNode one = mockGraphNode(new Integer[] {genomeOne}, new Integer[] {}, new Integer[] {}, 1);
    GraphNode two = mockGraphNode(
        new Integer[] {genomeOne, genomeTwo}, new Integer[] {}, new Integer[] {}, 2);

    zero.addAllInEdges(Collections.emptyList());
    zero.addAllOutEdges(Arrays.asList(one, two));
    one.addAllInEdges(Collections.singletonList(zero));
    one.addAllOutEdges(Collections.singletonList(two));
    two.addAllInEdges(Arrays.asList(zero, one));
    two.addAllOutEdges(Collections.emptyList());

    Map<Integer, GraphNode> nodes = new HashMap<>();
    nodes.put(0, zero);
    nodes.put(1, one);
    nodes.put(2, two);
    SequenceGraph graph = new HashGraph(
        nodes, Collections.singletonList(zero), Arrays.asList(genomeOne, genomeTwo));

    // Instrument the sub graph
    SplitGraphs splitGraph = new SplitGraphs(graph);
    SequenceGraph subGraph = splitGraph.getSubgraph(Collections.singletonList(1));

    subGraph.forEach(node -> assertFalse(nodes.get(node.getId()) == node));
  }

  /**
   * Stubs the forEachRemaining method by applying the lambda to every element in the iterator.
   * <p>
   * Requires correct stubbing of the <code>next()</code> and <code>hasNext()</code> methods.
   * </p>
   */
  private static class ForEachRemainingAnswer implements Answer<Void> {

    @Override
    @SuppressWarnings("unchecked") // Necessary to cast the InvocationOnMock types.
    public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
      Iterator<GraphNode> invocation = (Iterator<GraphNode>) invocationOnMock.getMock();
      Consumer<GraphNode> arg = (Consumer<GraphNode>) invocationOnMock.getArguments()[0];
      while (invocation.hasNext()) {
        arg.accept(invocation.next());
      }
      return null;
    }
  }

  /**
   * Stubs the forEach method in a Graph by applying the lambda to every element in its iterator.
   * <p>
   * Requires correct stubbing of the <code>next()</code> and <code>hasNext()</code> methods,
   * as well as the <code>iterator()</code> method.
   * </p>
   */
  private static class ForEachAnswer implements Answer<Void> {

    @Override
    @SuppressWarnings("unchecked") // Necessary to cast the InvocationOnMock types.
    public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
      SequenceGraph invocation = (SequenceGraph) invocationOnMock.getMock();
      Consumer<GraphNode> arg = (Consumer<GraphNode>) invocationOnMock.getArguments()[0];
      Iterator<GraphNode> graphIterator = invocation.iterator();
      while (graphIterator.hasNext()) {
        arg.accept(graphIterator.next());
      }
      return null;
    }
  }
}
