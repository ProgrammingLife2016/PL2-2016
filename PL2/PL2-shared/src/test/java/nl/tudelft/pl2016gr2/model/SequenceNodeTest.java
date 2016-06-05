package nl.tudelft.pl2016gr2.model;

import static nl.tudelft.pl2016gr2.util.TestingUtilities.mockNode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Tests the {@link SequenceNode} class.
 *
 * @author Wouter Smit
 */
public class SequenceNodeTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private SequenceNode instance;
  private BaseSequence mockedSequence;
  private String sequence;

  /**
   * Sets up the mocked dependencies.
   */
  @Before
  public void setUp() {
    instance = new SequenceNode(5);

    sequence = "ACTG";
    mockedSequence = mock(BaseSequence.class);
    Mockito.when(mockedSequence.getBaseSequence()).thenReturn(sequence);
  }

  @Test
  public void testConstructorWithBaseSequence() {
    SequenceNode node = new SequenceNode(5, mockedSequence);
    assertEquals(sequence, node.getSequence());
  }

  @Test
  public void testConstructorWithGenomes() {
    Collection<Integer> genomes = Arrays.asList(0, 1);
    SequenceNode node = new SequenceNode(5, mockedSequence, genomes);
    assertTrue(node.getGenomes().containsAll(genomes));
  }

  @Test
  public void testConstructorWithInEdges() {
    Collection<GraphNode> inEdges = Arrays.asList(mockNode(10, false), mockNode(1, false));
    Collection<GraphNode> outEdges = Arrays.asList(mockNode(2, false), mockNode(3, true));
    SequenceNode node = new SequenceNode(
        5, mockedSequence, Collections.singletonList(1), inEdges, outEdges);
    assertTrue(node.getInEdges().containsAll(inEdges));
    outEdges.forEach(edge -> assertFalse(node.getInEdges().contains(edge)));
  }

  @Test
  public void testConstructorWithOutEdgeC() {
    Collection<GraphNode> inEdges = Arrays.asList(mockNode(10, false), mockNode(1, false));
    Collection<GraphNode> outEdges = Arrays.asList(mockNode(2, false), mockNode(3, true));
    SequenceNode node = new SequenceNode(
        5, mockedSequence, Collections.singletonList(1), inEdges, outEdges);
    assertTrue(node.getOutEdges().containsAll(outEdges));
    inEdges.forEach(edge -> assertFalse(node.getOutEdges().contains(edge)));
  }

  @Test
  public void setAndGetSequence() {
    assertNull(instance.getSequence());
    instance.setSequence(mockedSequence);
    assertEquals(sequence, instance.getSequence());
  }

  @Test
  public void getInEdgesEmptyWhenNone() {
    assertTrue(instance.getInEdges().isEmpty());
  }

  @Test
  public void getInEdges() {
    GraphNode mockedNode = mockNode(5, false);
    instance.addInEdge(mockedNode);

    assertTrue(instance.getInEdges().contains(mockedNode));
  }

  @Test
  public void addInEdgeAddsNewElement() {
    GraphNode mockedNode = mockNode(50, false);
    assertFalse(instance.getInEdges().contains(mockedNode));

    instance.addInEdge(mockedNode);

    assertTrue(instance.getInEdges().contains(mockedNode));
  }

  @Test
  public void addInEdgeDoesNotOverrideOldElements() {
    GraphNode mockedNode = mockNode(5, false);
    instance.addInEdge(mockedNode);
    instance.addInEdge(mockNode(10, false));

    assertTrue(instance.getInEdges().contains(mockedNode));
  }

  @Test
  public void addAllInEdgesAddsAllEdges() {
    GraphNode nodeOne = mockNode(1, false);
    GraphNode nodeTwo = mockNode(2, false);
    assertFalse(instance.getInEdges().contains(nodeOne));
    assertFalse(instance.getInEdges().contains(nodeTwo));

    instance.addAllInEdges(Arrays.asList(nodeOne, nodeTwo));

    assertTrue(instance.getInEdges().contains(nodeOne));
    assertTrue(instance.getInEdges().contains(nodeTwo));
  }

  @Test
  public void addAllInEdgesLeavesOldEdges() {
    GraphNode oldNode = mockNode(10, false);
    GraphNode node = new SequenceNode(5, mock(BaseSequence.class), Collections.emptyList(),
        Collections.singletonList(oldNode), Collections.emptyList());

    assertTrue(node.getInEdges().contains(oldNode));
    GraphNode newNode = mockNode(2, false);

    node.addAllInEdges(Collections.singletonList(newNode));

    assertTrue(node.getInEdges().contains(oldNode));
  }

  @Test
  public void addAllInEdgesAddsOnlyEdges() {
    final int edgeCount = instance.getInEdges().size();

    GraphNode nodeOne = mockNode(1, false);
    GraphNode nodeTwo = mockNode(2, false);

    instance.addAllInEdges(Arrays.asList(nodeOne, nodeTwo));

    assertEquals(edgeCount + 2, instance.getInEdges().size());
  }

  @Test
  public void removeInEdgeRemovesElement() {
    GraphNode edge = mockNode(50, false);
    instance.addInEdge(edge);
    assertTrue(instance.getInEdges().contains(edge));

    instance.removeInEdge(edge);
    assertFalse(instance.getInEdges().contains(edge));
  }

  @Test
  public void removeInEdgeLeavesOtherElements() {
    GraphNode mockedNode = mockNode(5, false);
    instance.addInEdge(mockedNode);

    instance.addInEdge(mockNode(10, true));

    assertTrue(instance.getInEdges().contains(mockedNode));
  }

  @Test
  public void getOutEdgesEmptyWhenNone() {
    assertTrue(instance.getOutEdges().isEmpty());
  }

  @Test
  public void getOutEdges() {
    GraphNode mockedNode = mockNode(5, false);
    instance.addOutEdge(mockedNode);

    assertTrue(instance.getOutEdges().contains(mockedNode));
  }

  @Test
  public void addOutEdgeAddsNewElement() {
    GraphNode mockedNode = mockNode(10, false);
    GraphNode newNode = mockNode(5, false);
    assertFalse(instance.getOutEdges().contains(mockedNode));

    instance.addOutEdge(newNode);

    assertTrue(instance.getOutEdges().contains(newNode));
  }

  @Test
  public void addOutEdgeDoesNotOverrideOldElements() {
    GraphNode mockedNode = mockNode(10, false);
    instance.addOutEdge(mockedNode);
    instance.addOutEdge(mockNode(12, true));

    assertTrue(instance.getOutEdges().contains(mockedNode));
  }

  @Test
  public void addAllOutEdgesAddsAllEdges() {
    GraphNode nodeOne = mockNode(1, false);
    GraphNode nodeTwo = mockNode(2, false);
    assertFalse(instance.getOutEdges().contains(nodeOne));
    assertFalse(instance.getOutEdges().contains(nodeTwo));

    instance.addAllOutEdges(Arrays.asList(nodeOne, nodeTwo));

    assertTrue(instance.getOutEdges().contains(nodeOne));
    assertTrue(instance.getOutEdges().contains(nodeTwo));
  }

  @Test
  public void addAllOutEdgesLeavesOldEdges() {
    GraphNode oldNode = mockNode(10, false);
    GraphNode node = new SequenceNode(5, mock(BaseSequence.class), Collections.emptyList(),
        Collections.emptyList(), Collections.singletonList(oldNode));

    assertTrue(node.getOutEdges().contains(oldNode));
    GraphNode newNode = mockNode(2, false);

    node.addAllOutEdges(Collections.singletonList(newNode));

    assertTrue(node.getOutEdges().contains(oldNode));
  }

  @Test
  public void addAllOutEdgesAddsOnlyEdges() {
    final int edgeCount = instance.getOutEdges().size();

    GraphNode nodeOne = mockNode(1, false);
    GraphNode nodeTwo = mockNode(2, false);

    instance.addAllOutEdges(Arrays.asList(nodeOne, nodeTwo));

    assertEquals(edgeCount + 2, instance.getOutEdges().size());
  }

  @Test
  public void removeOutEdgeRemovesElement() {
    GraphNode edge = mockNode(5, false);
    instance.addOutEdge(edge);
    assertTrue(instance.getOutEdges().contains(edge));

    instance.removeOutEdge(edge);
    assertFalse(instance.getOutEdges().contains(edge));
  }

  @Test
  public void removeOutEdgeLeavesOtherElements() {
    GraphNode mockedNode = mockNode(5, false);
    GraphNode removeNode = mockNode(10, true);
    instance.addAllOutEdges(Arrays.asList(mockedNode, removeNode));

    instance.removeOutEdge(removeNode);

    assertTrue(instance.getOutEdges().contains(mockedNode));
  }

  @Test
  public void getGenomesEmptyWhenNone() {
    assertTrue(instance.getGenomes().isEmpty());
  }

  @Test
  public void getGenomes() {
    final int testGenome = 0;
    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void addGenomeLeavesOldUnchanged() {
    final int oldGenome = 0;
    instance.addGenome(oldGenome);
    assertTrue(instance.getGenomes().contains(oldGenome));
    instance.addGenome(1);
    assertTrue(instance.getGenomes().contains(oldGenome));
  }

  @Test
  public void addGenomeAddsNewElement() {
    final int testGenome = 0;
    assertFalse(instance.getGenomes().contains(testGenome));
    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void addDuplicateGenomeThrowsAssertion() {
    final int testGenome = 1;
    instance.addGenome(testGenome);
    exception.expect(AssertionError.class);
    instance.addGenome(testGenome);
  }

  @Test
  public void addAllGenomesAddsAllGenomes() {
    final int genomeOne = 50;
    final int genomeTwo = 60;
    assertFalse(instance.getGenomes().contains(genomeOne));
    assertFalse(instance.getGenomes().contains(genomeTwo));

    instance.addAllGenomes(Arrays.asList(genomeOne, genomeTwo));

    assertTrue(instance.getGenomes().contains(genomeOne));
    assertTrue(instance.getGenomes().contains(genomeTwo));
  }

  @Test
  public void addAllGenomesAddsLeavesOldGenomes() {
    final int oldGenome = 50;
    final int newGenome = 60;
    GraphNode node = new SequenceNode(5, mock(BaseSequence.class),
        Collections.singletonList(oldGenome), Collections.emptyList(), Collections.emptyList());
    assertTrue(node.getGenomes().contains(oldGenome));

    node.addAllGenomes(Collections.singletonList(newGenome));

    assertTrue(node.getGenomes().contains(oldGenome));
  }

  @Test
  public void addAllGenomesAddsOnlyGenomes() {
    final int genomeCount = instance.getGenomes().size();

    instance.addAllGenomes(Arrays.asList(50, 60));

    assertEquals(genomeCount + 2, instance.getGenomes().size());
  }

  @Test
  public void removeGenomeRemovesElement() {
    final int testGenome = 1;
    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));

    instance.removeGenome(testGenome);

    assertFalse(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void removeGenomeDoesNotRemoveByIndex() {
    final int outOfBounds = instance.getGenomes().size() + 50;
    instance.addGenome(outOfBounds);
    assertTrue(instance.getGenomes().contains(outOfBounds));

    instance.removeGenome(outOfBounds);

    assertFalse(instance.getGenomes().contains(outOfBounds));
  }

  @Test
  public void removeGenomeLeavesOtherElements() {
    final int otherElement = 0;
    final int removedElement = 1;
    instance.addGenome(otherElement);
    instance.addGenome(removedElement);
    assertTrue(instance.getGenomes().contains(otherElement));

    instance.removeGenome(removedElement);

    assertTrue(instance.getGenomes().contains(otherElement));
  }

  @Test
  public void removeNonExistentGenomeThrowsAssertion() {
    exception.expect(AssertionError.class);
    instance.removeGenome(1100);
  }

  @Test
  public void trimToSizeTrimsGenomeMemory() {
    for (int i = 0; i < 1000; i++) {
      instance.addGenome(i);
    }
    for (int i = 0; i < 1000; i++) {
      instance.removeGenome(i);
    }
    System.gc();
    Runtime runtime = Runtime.getRuntime();
    long oldMemory = runtime.totalMemory() - runtime.freeMemory();

    instance.trimToSize();
    System.gc();

    long newMemory = runtime.totalMemory() - runtime.freeMemory();
    assertTrue(newMemory < oldMemory);
  }

  @Test
  public void copy() {
    Class old = instance.getClass();
    assertEquals(old, instance.copy().getClass());
  }

  @Test
  public void accept() {
    NodeVisitor mockedVisitor = mock(NodeVisitor.class);
    instance.accept(mockedVisitor);
    verify(mockedVisitor).visit(instance);
  }

  @Test
  public void isRootTrueWhenRoot() {
    assertTrue(instance.isRoot());
  }

  @Test
  public void isRootFalseWhenNotRoot() {
    instance.addInEdge(mockNode(20, false));
    assertFalse(instance.isRoot());
  }

}