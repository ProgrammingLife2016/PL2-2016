package nl.tudelft.pl2016gr2.model;

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
    Collection<String> genomes = Arrays.asList("gen0", "gen1");
    SequenceNode node = new SequenceNode(5, mockedSequence, genomes);
    assertTrue(node.getGenomes().containsAll(genomes));
  }

  @Test
  public void testConstructorWithInEdges() {
    Collection<Integer> inEdges = Arrays.asList(1, 3, 12);
    Collection<Integer> outEdges = Arrays.asList(2, 4, 14);
    SequenceNode node = new SequenceNode(
        5, mockedSequence, Collections.singletonList("gen"), inEdges, outEdges);
    assertTrue(node.getInEdges().containsAll(inEdges));
    outEdges.forEach(edge -> assertFalse(node.getInEdges().contains(edge)));
  }

  @Test
  public void testconstructorWithOutEdgeC() {
    Collection<Integer> inEdges = Arrays.asList(1, 3, 12);
    Collection<Integer> outEdges = Arrays.asList(2, 4, 14);
    SequenceNode node = new SequenceNode(
        5, mockedSequence, Collections.singletonList("gen"), inEdges, outEdges);
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
    instance.setInEdges(Collections.singletonList(5));

    assertTrue(instance.getInEdges().contains(5));
  }

  @Test
  public void setInEdgesOverridePreviousElements() {
    instance.setInEdges(Collections.singletonList(5));
    instance.setInEdges(Arrays.asList(1, 20, 25));
    assertFalse(instance.getInEdges().contains(5));
  }

  @Test
  public void setInEdgesAddsAllElements() {
    Collection<Integer> edgeList = Arrays.asList(1, 20, 25);
    instance.setInEdges(edgeList);
    assertTrue(instance.getInEdges().containsAll(edgeList));
  }

  @Test
  public void addInEdgeAddsNewElement() {
    assertFalse(instance.getInEdges().contains(10));

    instance.addInEdge(10);

    assertTrue(instance.getInEdges().contains(10));
  }

  @Test
  public void addInEdgeDoesNotOverrideOldElements() {
    instance.setInEdges(Collections.singletonList(5));
    instance.addInEdge(10);

    assertTrue(instance.getInEdges().contains(5));
  }

  @Test
  public void removeInEdgeRemovesElement() {
    int edge = 10;
    instance.addInEdge(edge);
    assertTrue(instance.getInEdges().contains(edge));

    instance.removeInEdge(edge);
    assertFalse(instance.getInEdges().contains(edge));
  }

  @Test
  public void removeInEdgeLeavesOtherElements() {
    instance.setInEdges(Collections.singletonList(5));

    instance.addInEdge(10);

    assertTrue(instance.getInEdges().contains(5));
  }

  @Test
  public void getOutEdgesEmptyWhenNone() {
    assertTrue(instance.getOutEdges().isEmpty());
  }

  @Test
  public void getOutEdges() {
    instance.setOutEdges(Collections.singletonList(5));

    assertTrue(instance.getOutEdges().contains(5));
  }

  @Test
  public void setOutEdgesOverridePreviousElements() {
    instance.setOutEdges(Collections.singletonList(5));
    instance.setOutEdges(Arrays.asList(1, 20, 25));
    assertFalse(instance.getOutEdges().contains(5));
  }

  @Test
  public void setOutEdgesAddsAllElements() {
    Collection<Integer> edgeList = Arrays.asList(1, 20, 25);
    instance.setOutEdges(edgeList);
    assertTrue(instance.getOutEdges().containsAll(edgeList));
  }

  @Test
  public void addOutEdgeAddsNewElement() {
    assertFalse(instance.getOutEdges().contains(10));

    instance.addOutEdge(10);

    assertTrue(instance.getOutEdges().contains(10));
  }

  @Test
  public void addOutEdgeDoesNotOverrideOldElements() {
    instance.setOutEdges(Collections.singletonList(5));
    instance.addOutEdge(10);

    assertTrue(instance.getOutEdges().contains(5));
  }

  @Test
  public void removeOutEdgeRemovesElement() {
    int edge = 10;
    instance.addOutEdge(edge);
    assertTrue(instance.getOutEdges().contains(edge));

    instance.removeOutEdge(edge);
    assertFalse(instance.getOutEdges().contains(edge));
  }

  @Test
  public void removeOutEdgeLeavesOtherElements() {
    instance.setOutEdges(Collections.singletonList(5));

    instance.addOutEdge(10);

    assertTrue(instance.getOutEdges().contains(5));
  }

  @Test
  public void getGenomesEmptyWhenNone() {
    assertTrue(instance.getGenomes().isEmpty());
  }

  @Test
  public void getGenomes() {
    final String testGenome = "testGenome";
    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void addGenomeLeavesOldUnchanged() {
    final String oldGenome = "oldGenome";
    instance.addGenome(oldGenome);
    assertTrue(instance.getGenomes().contains(oldGenome));
    instance.addGenome("newGenome");
    assertTrue(instance.getGenomes().contains(oldGenome));
  }

  @Test
  public void addGenomeAddsNewElement() {
    final String testGenome = "testGenome";
    assertFalse(instance.getGenomes().contains(testGenome));
    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void addDuplicateGenomeThrowsAssertion() {
    final String testGenome = "testGenome";
    instance.addGenome(testGenome);
    exception.expect(AssertionError.class);
    instance.addGenome(testGenome);
  }

  @Test
  public void removeGenomeRemovesElement() {
    final String testGenome = "testNodeGenome";
    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));

    instance.removeGenome(testGenome);

    assertFalse(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void removeGenomeLeavesOtherElements() {
    final String otherElement = "otherElement";
    final String removedElement = "removedElement";
    instance.addGenome(otherElement);
    instance.addGenome(removedElement);
    assertTrue(instance.getGenomes().contains(otherElement));

    instance.removeGenome(removedElement);

    assertTrue(instance.getGenomes().contains(otherElement));
  }

  @Test
  public void removeNonExistentGenomeThrowsAssertion() {
    exception.expect(AssertionError.class);
    instance.removeGenome("nonExistentGenome");
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
    instance.setInEdges(Collections.singletonList(5));
    assertFalse(instance.isRoot());
  }

}