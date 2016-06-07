package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

    assertTrue(instance.getGenomesOverEdge(otherNode).contains(1));
    assertEquals(1, instance.getGenomesOverEdge(otherNode).size());
  }

  @Test
  public void testToString() {
    AccessPrivate.setFieldValue("id_field", AbstractGraphNode.class, instance, 5);
    assertEquals("id: 5", instance.toString());
  }

}
