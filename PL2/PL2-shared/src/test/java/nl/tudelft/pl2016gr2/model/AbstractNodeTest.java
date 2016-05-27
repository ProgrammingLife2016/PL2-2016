package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.util.Arrays;

/**
 * Tests the {@link AbstractNode} class.
 *
 * @author Wouter Smit
 */
public class AbstractNodeTest {

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  private AbstractNode instance;

  /**
   * Sets up the abstract spied class.
   */
  @Before
  public void setUp() {
    instance = mock(AbstractNode.class);
    Mockito.when(instance.getId()).thenCallRealMethod();
    Mockito.when(instance.hasChildren()).thenCallRealMethod();
    Mockito.when(instance.getChildren()).thenCallRealMethod();
    Mockito.when(instance.size()).thenCallRealMethod();
    Mockito.when(instance.toString()).thenCallRealMethod();
    Mockito.when(instance.getGenomesOverEdge(any())).thenCallRealMethod();
  }

  @Test
  public void testConstructor() {
    AbstractNode extendedNode = new SequenceNode(5);
    assertEquals(5, extendedNode.getId());
  }

  @Test
  public void getId() {
    AccessPrivate.setFieldValue("id_field", AbstractNode.class, instance, 5);
    assertEquals(5, instance.getId());
  }

  @Test
  public void hasChildren() {
    assertFalse(instance.hasChildren());
  }

  @Test
  public void getChildren() {
    assertNull(instance.getChildren());
  }

  @Test
  public void size() {
    Mockito.when(instance.getSequence()).thenReturn("ACTG");
    assertEquals(4, instance.size());
  }

  @Test
  public void getGenomesOverEdge() {
    GraphNode otherNode = mock(GraphNode.class);

    Mockito.when(otherNode.getId()).thenReturn(2);
    AccessPrivate.setFieldValue("id_field", AbstractNode.class, instance, 5);

    Mockito.when(instance.getGenomes()).thenReturn(Arrays.asList("equal", "gen0"));
    Mockito.when(otherNode.getGenomes()).thenReturn(Arrays.asList("equal", "gen1"));

    Mockito.when(otherNode.getInEdges()).thenReturn(Arrays.asList(1, 5, 8));
    Mockito.when(instance.getOutEdges()).thenReturn(Arrays.asList(2, 10, 15));

    assertTrue(instance.getGenomesOverEdge(otherNode).contains("equal"));
    assertEquals(1, instance.getGenomesOverEdge(otherNode).size());
  }

  @Test
  public void getGenomesOverEdgeThrowsAssertionWhenNotSuccessor() {
    GraphNode otherNode = mock(GraphNode.class);

    Mockito.when(otherNode.getId()).thenReturn(2);
    AccessPrivate.setFieldValue("id_field", AbstractNode.class, instance, 5);

    Mockito.when(otherNode.getInEdges()).thenReturn(Arrays.asList(1, 8));
    Mockito.when(instance.getOutEdges()).thenReturn(Arrays.asList(10, 15));

    exception.expect(AssertionError.class);
    instance.getGenomesOverEdge(otherNode);
  }

  @Test
  public void testToString() {
    AccessPrivate.setFieldValue("id_field", AbstractNode.class, instance, 5);
    assertEquals("id: 5", instance.toString());
  }

}