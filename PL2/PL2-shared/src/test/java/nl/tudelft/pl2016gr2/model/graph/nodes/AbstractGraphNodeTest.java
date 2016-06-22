package nl.tudelft.pl2016gr2.model.graph.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Tests the {@link AbstractGraphNode} class.
 *
 * @author Wouter Smit
 */
public class AbstractGraphNodeTest {

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
  public void testToString() {
    AccessPrivate.setFieldValue("id_field", AbstractGraphNode.class, instance, 5);
    assertEquals("id: 5", instance.toString());
  }

  @Test
  public void testEquals() {
    AbstractGraphNode node1 = new SequenceNode(10);
    AbstractGraphNode node2 = new SequenceNode(10);
    assertTrue(node1.equals(node2));
  }

  @Test
  public void testNotEquals() {
    AbstractGraphNode node1 = new SequenceNode(10);
    AbstractGraphNode node2 = new SequenceNode(11);
    assertFalse(node1.equals(node2));
  }

  @Test
  public void testHashcode() {
    AbstractGraphNode node = new SequenceNode(10);
    assertEquals(370, node.hashCode());
  }

  @Test
  public void testSetEdges() {
    AbstractGraphNode node = new SequenceNode(0);
    node.setInEdges(new ArrayList<>());
    node.setOutEdges(new ArrayList<>());
    assertTrue(node.getInEdges().isEmpty());
    assertTrue(node.getOutEdges().isEmpty());
  }

  @Test
  public void testGetChildren() {
    AbstractGraphNode node = new SequenceNode(0);
    assertEquals(null, node.getChildren());
  }

}
