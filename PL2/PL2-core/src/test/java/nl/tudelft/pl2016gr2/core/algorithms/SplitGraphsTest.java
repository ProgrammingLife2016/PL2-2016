package nl.tudelft.pl2016gr2.core.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.pl2016gr2.collectioninterfaces.GenomeIterator;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.test.utility.AccessPrivate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
    when(mockedGraph.getGenoms()).thenReturn(genomeSet);
    defInstance = new SplitGraphs(mockedGraph);
    setupIteratorGenomeOne();
    setupIteratorGenomeTwo();
  }

  /**
   * Stubs an iterator with genome 0 in the <code>genomeSet</code>.
   * This iterator returns three nodes with correct genomes and additional ones.
   */
  private void setupIteratorGenomeOne() {
    Node firstNode = mockNode(0, new String[] {genomeSet.get(0), genomeSet.get(1)},
        new Integer[] {3}, new Integer[] {3});
    Node secondNode = mockNode(1, new String[] {genomeSet.get(0)},
        new Integer[] {0, 1, 3}, new Integer[] {0, 1, 3});
    Node thirdNode = mockNode(3, new String[] {genomeSet.get(0), genomeSet.get(2)},
        new Integer[] {2, 5, 3}, new Integer[] {1, 10, 2});

    Iterator<Node> mockedIterator = mock(GenomeIterator.class);
    when(mockedIterator.hasNext()).thenReturn(true, true, false);
    when(mockedIterator.next()).thenReturn(firstNode, secondNode, thirdNode);
    when(mockedGraph.iterator(genomeSet.get(0))).thenReturn(mockedIterator);
  }

  private void setupIteratorGenomeTwo() {
    Node firstNode = mockNode(0, new String[] {genomeSet.get(0), genomeSet.get(1)},
        new Integer[] {3}, new Integer[] {3});
    Node secondNode = mockNode(1, new String[] {genomeSet.get(1)},
        new Integer[] {0, 1, 3}, new Integer[] {0, 1, 3});
    Node thirdNode = mockNode(3, new String[] {genomeSet.get(1), genomeSet.get(2)},
        new Integer[] {2, 5, 3}, new Integer[] {1, 10, 2});

    Iterator<Node> mockedIterator = mock(GenomeIterator.class);
    when(mockedIterator.hasNext()).thenReturn(true, false, false);
    when(mockedIterator.next()).thenReturn(firstNode, secondNode, thirdNode);
    when(mockedGraph.iterator(genomeSet.get(1))).thenReturn(mockedIterator);
  }

  /**
   * Creates a mocked node with a specified id, genome list, inlinks and outlinks.
   *
   * @param id       The node ID
   * @param genomes  The list of genomes in this node
   * @param inlinks  The list of inlinks
   * @param outlinks The list of outlinks
   */
  private Node mockNode(int id, String[] genomes, Integer[] inlinks, Integer[] outlinks) {
    Node node = mock(Node.class);
    when(node.getId()).thenReturn(id);
    when(node.getGenomes()).thenReturn(new ArrayList<>(Arrays.asList(genomes)));
    when(node.getInlinks()).thenReturn(new ArrayList<>(Arrays.asList(inlinks)));
    when(node.getOutlinks()).thenReturn(new ArrayList<>(Arrays.asList(outlinks)));
    return node;
  }

  @Test
  public void testSplitGraphs() {
    assertEquals(mockedGraph,
        AccessPrivate.getFieldValue("graph_field", SplitGraphs.class, defInstance));
  }

  /**
   * Verifies that the {@link SplitGraphs#getSubgraph(Collection)} method throws an exception when
   * invalid genomes are provided.
   */
  @Test
  public void testGetSubgraphThrowsExceptionForInvalidGenomes() {
    String wrongGenome = "wrong";
    assertFalse(genomeSet.contains(wrongGenome));
    ArrayList<String> wrongSet = new ArrayList<>();
    wrongSet.add(wrongGenome);

    exception.expect(NoSuchElementException.class);
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
    HashMap<Integer, Node> graphnodes = subgraph.getNodes();
    assertFalse(graphnodes.isEmpty());

    graphnodes.forEach(
        (key, node) -> assertTrue(node.getGenomes().contains(genomeSub.get(0))));
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the specified genomes.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedGenomes() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(0), genomeSet.get(1)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphnodes = subgraph.getNodes();
    assertFalse(graphnodes.isEmpty());

    graphnodes.forEach(
        (key, node) -> assertTrue(genomeSub.containsAll(node.getGenomes())));
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the inlinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedInlinks() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(0)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphnodes = subgraph.getNodes();
    assertFalse(graphnodes.isEmpty());

    graphnodes.forEach(
        (key, node) -> node.getInlinks().forEach(
            link -> assertTrue(subgraph.getNodes().containsKey(link))));
  }

  /**
   * Asserts that the {@link SplitGraphs#getSubgraph(Collection)} method returns a graph of which
   * every node contains only the outlinks that are still present in the subgraph.
   */
  @Test
  public void testGetSubgraphNodesContainOnlySpecifiedOutlinks() {
    ArrayList<String> genomeSub = new ArrayList<>(
        Arrays.asList(new String[] {genomeSet.get(0)}));

    OriginalGraph subgraph = defInstance.getSubgraph(genomeSub);
    HashMap<Integer, Node> graphnodes = subgraph.getNodes();
    assertFalse(graphnodes.isEmpty());

    graphnodes.forEach(
        (key, node) -> node.getOutlinks().forEach(
            link -> assertTrue(subgraph.getNodes().containsKey(link))));
  }
}
