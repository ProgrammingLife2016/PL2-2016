package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.model.StringSequenceNode;
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
import java.util.HashMap;
import java.util.Iterator;
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
  private ArrayList<String> genomeSet;
  private SplitGraphs defInstance;
  private Collection<Integer> nodeIDs;

  /**
   * Sets up mocked dependencies.
   */
  @Before
  public void setup() {
    genomeSet = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      genomeSet.add("testGen0" + i);
    }
    mockedGraph = mock(SequenceGraph.class);
    when(mockedGraph.getGenomes()).thenReturn(genomeSet);
    defInstance = new SplitGraphs(mockedGraph);
    setupIterator();
  }

  private void setupIterator() {
    nodeIDs = new ArrayList<>();
    nodeIDs.add(0);
    nodeIDs.add(1);
    nodeIDs.add(3);

    @SuppressWarnings("unchecked") // Necessary when mocking generic types
    Iterator<GraphNode> mockedIterator = mock(Iterator.class);

    Node firstNode = createNode(new String[]{genomeSet.get(0), genomeSet.get(1)}, new Integer[]{3},
        new Integer[]{3}, 0);
    Node secondNode = createNode(new String[]{genomeSet.get(1)}, new Integer[]{0, 1, 3},
        new Integer[]{0, 1, 3}, 1);
    Node thirdNode = createNode(new String[]{genomeSet.get(1), genomeSet.get(2)},
        new Integer[]{2, 5, 3}, new Integer[]{1, 10, 2}, 3);

    when(mockedIterator.hasNext()).thenReturn(true, true, true, false);
    when(mockedIterator.next()).thenReturn(firstNode, secondNode, thirdNode);
    doAnswer(new ForEachAnswer()).when(mockedIterator).forEachRemaining(any());
    when(mockedGraph.iterator()).thenReturn(mockedIterator);
    when(mockedGraph.getNode(0)).thenReturn(firstNode);
    when(mockedGraph.getNode(1)).thenReturn(secondNode);
    when(mockedGraph.getNode(3)).thenReturn(thirdNode);
    //whem(firstNode.copy())
  }

  /**
   * Creates a mocked node with a specified identifier, genome list, inLinks and outLinks.
   *
   * @param genomes    The list of genomes in this node
   * @param inLinks    The list of inLinks
   * @param outLinks   The list of outLinks
   * @param identifier The node ID
   */
  private Node createNode(String[] genomes, Integer[] inLinks, Integer[] outLinks, int identifier) {
    Node node = new StringSequenceNode(identifier, "ACTG", Arrays.asList(genomes), Arrays.asList(
        inLinks), Arrays.asList(outLinks));
    return node;
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
    String wrongGenome = "wrong";
    assertFalse(genomeSet.contains(wrongGenome));
    ArrayList<String> wrongSet = new ArrayList<>();
    wrongSet.add(wrongGenome);

    exception.expect(AssertionError.class);
    defInstance.getSubgraph(wrongSet);
  }

  /**
   * Verifies that the {@link SplitGraphs#getSubgraph(Collection)} method returns a non-empty graph.
   */
  @Test
  public void testGetSubgraphReturnsNonEmptyGraph() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(0)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains at least the specified genome.
   */
  @Test
  public void testGetSubgraphOnlyContainsGenomeNodes() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(0)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());

    for (GraphNode graphNode : subgraph) {
      assertTrue(graphNode.getGenomes().contains(genomeSub.get(0)));
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph with all
   * nodes that contain at least one of the specified genomes.
   */
  @Test
  public void testGetSubgraphContainsAllGenomeNodes() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(0)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());

    Collection<GraphNode> expectedNodes = new ArrayList<>();
    mockedGraph.iterator().forEachRemaining(expectedNodes::add);
    expectedNodes.removeIf(node -> {
      boolean noneOfGenomes = true;
      Collection<String> genomes = node.getGenomes();
      for (String genome : genomes) {
        if (genomeSub.contains(genome)) {
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
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(0), genomeSet.get(2)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());
    for (GraphNode graphNode : subgraph) {
      assertTrue(genomeSub.containsAll(graphNode.getGenomes()));
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains all of the specified genomes.
   */
  @Test
  public void testGetSubgraphNodesContainAllSpecifiedGenomes() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(0), genomeSet.get(2)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());

    for (GraphNode graphNode : subgraph) {
      Collection<String> expectedGenomes = mockedGraph.getNode(graphNode.getId()).getGenomes();
      expectedGenomes.removeIf(genome -> !genomeSub.contains(genome));
      assertTrue(graphNode.getGenomes().containsAll(expectedGenomes));
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the inLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedInLinks() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(1)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());

    for (GraphNode graphNode : subgraph) {
      for (Integer inEdge : graphNode.getInEdges()) {
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
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(1)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());
    for (GraphNode graphNode : subgraph) {
      Collection<Integer> expectedLinks = mockedGraph.getNode(graphNode.getId()).getInEdges();
      expectedLinks.removeIf(link -> !nodeIDs.contains(link));
      assertTrue(graphNode.getInEdges().containsAll(expectedLinks));
    }
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the outLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedOutLinks() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(1)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());

    for (GraphNode graphNode : subgraph) {
      for (Integer outEdge : graphNode.getOutEdges()) {
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
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[]{genomeSet.get(1)}));

    SequenceGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.isEmpty());
    for (GraphNode graphNode : subgraph) {
      Collection<Integer> expectedLinks = mockedGraph.getNode(graphNode.getId()).getOutEdges();
      expectedLinks.removeIf(link -> !nodeIDs.contains(link));
      assertTrue(graphNode.getOutEdges().containsAll(expectedLinks));
    }
  }

  /**
   * Stubs the forEachRemaining method by applying the lambda to every element in the iterator.
   * <p>
   * Requires correct stubbing of the <code>next()</code> and <code>hasNext()</code> methods.
   * </p>
   */
  private static class ForEachAnswer implements Answer<Void> {

    @Override
    @SuppressWarnings("unchecked") // Necessary to cast the InvocationOnMock types.
    public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
      Iterator<Node> invocation = (Iterator<Node>) invocationOnMock.getMock();
      Consumer<Node> arg = (Consumer<Node>) invocationOnMock.getArguments()[0];
      while (invocation.hasNext()) {
        arg.accept(invocation.next());
      }
      return null;
    }
  }
}
