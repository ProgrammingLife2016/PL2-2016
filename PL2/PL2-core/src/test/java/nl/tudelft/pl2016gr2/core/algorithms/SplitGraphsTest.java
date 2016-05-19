package nl.tudelft.pl2016gr2.core.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
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
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * Tests the {@link SplitGraphs} class.
 *
 * @author Wouter Smit
 */
public class SplitGraphsTest {
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private OriginalGraph mockedGraph;
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
    mockedGraph = mock(OriginalGraph.class);
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
    Iterator<Node> mockedIterator = mock(Iterator.class);

    Node firstNode = mockNode(new String[] {genomeSet.get(0), genomeSet.get(1)}, new Integer[] {3},
        new Integer[] {3}, 0);
    Node secondNode = mockNode(new String[] {genomeSet.get(1)}, new Integer[] {0, 1, 3},
        new Integer[] {0, 1, 3}, 1);
    Node thirdNode = mockNode(new String[] {genomeSet.get(1), genomeSet.get(2)},
        new Integer[] {2, 5, 3}, new Integer[] {1, 10, 2}, 3);

    when(mockedIterator.hasNext()).thenReturn(true, true, true, false);
    when(mockedIterator.next()).thenReturn(firstNode, secondNode, thirdNode);
    doAnswer(new ForEachAnswer()).when(mockedIterator).forEachRemaining(any());
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
  private Node mockNode(String[] genomes, Integer[] inLinks, Integer[] outLinks, int identifier) {
    Node node = mock(Node.class);
    when(node.getId()).thenReturn(identifier);
    when(node.getGenomes()).thenReturn(new ArrayList<>(Arrays.asList(genomes)));
    when(node.getInlinks()).thenReturn(new ArrayList<>(Arrays.asList(inLinks)));
    when(node.getOutlinks()).thenReturn(new ArrayList<>(Arrays.asList(outLinks)));
    when(node.getBases()).thenReturn("ACTG");
    when(node.getAlignment()).thenReturn(1);
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
   * Verifies that the {@link SplitGraphs#getSubgraph(Collection)} method returns a non-empty
   * graph.
   */
  @Test
  public void testGetSubgraphReturnsNonEmptyGraph() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(0)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    assertFalse(subgraph.getNodes().isEmpty());
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains at least the specified genome.
   */
  @Test
  public void testGetSubgraphOnlyContainsGenomeNodes() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(0)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphNodes = subgraph.getNodes();
    assertFalse(graphNodes.isEmpty());

    graphNodes.forEach(
        (key, node) -> assertTrue(node.getGenomes().contains(genomeSub.get(0))));
  }

  /**
   * Asserts that  the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph with all
   * nodes that contain at least one of the specified genomes.
   */
  @Test
  public void testGetSubgraphContainsAllGenomeNodes() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(0)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphNodes = subgraph.getNodes();
    assertFalse(graphNodes.isEmpty());

    Collection<Node> expectedNodes = new ArrayList<>();
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

    graphNodes.forEach(
        (key, node) -> assertTrue(node.getGenomes().containsAll(expectedNodes))
    );
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the specified genomes.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedGenomes() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(0), genomeSet.get(2)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphNodes = subgraph.getNodes();
    assertFalse(graphNodes.isEmpty());

    graphNodes.forEach(
        (key, node) -> assertTrue(genomeSub.containsAll(node.getGenomes())));
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains all of the specified genomes.
   */
  @Test
  public void testGetSubgraphNodesContainAllSpecifiedGenomes() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(0), genomeSet.get(2)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphNodes = subgraph.getNodes();
    assertFalse(graphNodes.isEmpty());

    graphNodes.forEach(
        (key, node) -> {
          Collection<String> expectedGenomes = mockedGraph.getNode(key).getGenomes();
          expectedGenomes.removeIf(genome -> !genomeSub.contains(genome));
          assertTrue(node.getGenomes().containsAll(expectedGenomes));
        });
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the inLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedInLinks() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(1)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphNodes = subgraph.getNodes();
    assertFalse(graphNodes.isEmpty());

    graphNodes.forEach(
        (key, node) -> node.getInlinks().forEach(
            link -> assertTrue(subgraph.getNodes().containsKey(link))));
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains all the inLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphContainsAllSpecifiedInLinks() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(1)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphNodes = subgraph.getNodes();
    assertFalse(graphNodes.isEmpty());
    graphNodes.forEach(
        (key, node) -> {
          Collection<Integer> expectedLinks = mockedGraph.getNode(key).getInlinks();
          expectedLinks.removeIf(link -> !nodeIDs.contains(link));
          assertTrue(node.getInlinks().containsAll(expectedLinks));
        }
    );
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the outLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedOutLinks() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(1)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphNodes = subgraph.getNodes();
    assertFalse(graphNodes.isEmpty());

    graphNodes.forEach(
        (key, node) -> node.getOutlinks().forEach(
            link -> assertTrue(subgraph.getNodes().containsKey(link))));
  }


  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains all the outLinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainAllSpecifiedOutLinks() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(1)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphNodes = subgraph.getNodes();
    assertFalse(graphNodes.isEmpty());
    graphNodes.forEach(
        (key, node) -> {
          Collection<Integer> expectedLinks = mockedGraph.getNode(key).getOutlinks();
          expectedLinks.removeIf(link -> !nodeIDs.contains(link));
          assertTrue(node.getOutlinks().containsAll(expectedLinks));
        }
    );
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
