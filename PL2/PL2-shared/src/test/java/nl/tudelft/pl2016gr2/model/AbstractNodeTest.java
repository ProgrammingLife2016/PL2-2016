package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import nl.tudelft.pl2016gr2.model.graph.nodes.AbstractGraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Tests the {@link AbstractGraphNode} class.
 *
 * @author Wouter Smit
 */
public class AbstractNodeTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private AbstractGraphNode instance;

  /**
   * Sets up the abstract spied class.
   */
  @Before
  public void setUp() {
    instance = mock(AbstractGraphNode.class);
    Mockito.when(instance.getId()).thenCallRealMethod();
    Mockito.when(instance.toString()).thenCallRealMethod();
    Mockito.when(instance.getGenomesOverEdge(any())).thenCallRealMethod();
  }

  @Test
  public void testConstructor() {
    AbstractGraphNode extendedNode = new SequenceNode(5);
    assertEquals(5, extendedNode.getId());
  }

  @Test
  public void getId() {
    AccessPrivate.setFieldValue("id_field", AbstractGraphNode.class, instance, 5);
    assertEquals(5, instance.getId());
  }

  @Test
  public void hasChildren() {
    assertFalse(instance.hasChildren());
  }

  @Test
  public void getGenomesOverEdge() {
    GraphNode otherNode = mock(GraphNode.class);

    Mockito.when(otherNode.getId()).thenReturn(2);
    AccessPrivate.setFieldValue("id_field", AbstractGraphNode.class, instance, 5);

    Mockito.when(instance.getGenomes()).thenReturn(Arrays.asList(1, 5));
    Mockito.when(otherNode.getGenomes()).thenReturn(Arrays.asList(2, 1));

    Mockito.when(otherNode.getInEdges()).thenReturn(Collections.singletonList(instance));
    Mockito.when(instance.getOutEdges()).thenReturn(Collections.singletonList(otherNode));

    Collection<Integer> genomesOverEdge = instance.getGenomesOverEdge(otherNode);
    assertTrue(genomesOverEdge.contains(1));
    assertFalse(genomesOverEdge.contains(2));
    assertFalse(genomesOverEdge.contains(5));
    assertEquals(1, instance.getGenomesOverEdge(otherNode).size());
  }

  @Test
  public void getGenomesOverEdgeDoesNotReturnLongerPaths() {
    GraphNode otherNode = mock(GraphNode.class);
    GraphNode inBetweenNode = mock(GraphNode.class);

    Mockito.when(otherNode.getId()).thenReturn(2);
    Mockito.when(inBetweenNode.getId()).thenReturn(1);
    AccessPrivate.setFieldValue("id_field", AbstractGraphNode.class, instance, 5);

    Mockito.when(instance.getGenomes()).thenReturn(Arrays.asList(1, 2, 3));
    Mockito.when(inBetweenNode.getGenomes()).thenReturn(Arrays.asList(1, 3));
    Mockito.when(otherNode.getGenomes()).thenReturn(Arrays.asList(1, 2, 3));

    Mockito.when(otherNode.getInEdges()).thenReturn(Arrays.asList(instance, inBetweenNode));
    Mockito.when(inBetweenNode.getInEdges()).thenReturn(Collections.singletonList(instance));
    Mockito.when(inBetweenNode.getOutEdges()).thenReturn(Collections.singletonList(otherNode));
    Mockito.when(instance.getOutEdges()).thenReturn(Arrays.asList(inBetweenNode, otherNode));

    Collection<Integer> genomesOverEdge = instance.getGenomesOverEdge(otherNode);

    assertTrue(!genomesOverEdge.contains(1));
    assertTrue(genomesOverEdge.contains(2));
    assertEquals(1, genomesOverEdge.size());
  }

  @Test
  public void getGenomesOverEdgeDoesNotReturnLongerPathsBackwards() {
    GraphNode otherNode = mock(GraphNode.class);
    GraphNode startNode = mock(GraphNode.class);

    Mockito.when(otherNode.getId()).thenReturn(2);
    Mockito.when(startNode.getId()).thenReturn(1);
    AccessPrivate.setFieldValue("id_field", AbstractGraphNode.class, instance, 5);

    Mockito.when(instance.getGenomes()).thenReturn(Arrays.asList(1, 3));
    Mockito.when(startNode.getGenomes()).thenReturn(Arrays.asList(1, 2, 3));
    Mockito.when(otherNode.getGenomes()).thenReturn(Arrays.asList(1, 2, 3));

    Mockito.when(otherNode.getInEdges()).thenReturn(Arrays.asList(instance, startNode));
    Mockito.when(instance.getInEdges()).thenReturn(Collections.singletonList(instance));
    Mockito.when(instance.getOutEdges()).thenReturn(Collections.singletonList(otherNode));
    Mockito.when(startNode.getOutEdges()).thenReturn(Arrays.asList(startNode, otherNode));

    Collection<Integer> genomesOverEdge = instance.getGenomesOverEdge(otherNode);

    assertTrue(!genomesOverEdge.contains(2));
    assertTrue(genomesOverEdge.containsAll(Arrays.asList(1, 3)));
    assertEquals(2, genomesOverEdge.size());
  }

  @Test
  public void getGenomesOverEdgeThrowsAssertionWhenNotSuccessor() {
    GraphNode otherNode = mock(GraphNode.class);

    Mockito.when(otherNode.getId()).thenReturn(2);
    AccessPrivate.setFieldValue("id_field", AbstractGraphNode.class, instance, 5);

    Mockito.when(otherNode.getInEdges()).thenReturn(Collections.emptyList());
    Mockito.when(instance.getOutEdges()).thenReturn(Collections.emptyList());

    exception.expect(AssertionError.class);
    instance.getGenomesOverEdge(otherNode);
  }

  @Test
  public void testToString() {
    AccessPrivate.setFieldValue("id_field", AbstractGraphNode.class, instance, 5);
    assertEquals("id: 5", instance.toString());
  }

}
