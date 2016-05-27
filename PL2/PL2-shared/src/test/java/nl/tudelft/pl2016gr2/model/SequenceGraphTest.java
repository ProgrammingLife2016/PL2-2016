package nl.tudelft.pl2016gr2.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Tests the {@link SequenceGraph} interface class.
 *
 * @author Wouter Smit
 */
@RunWith(Parameterized.class)
public class SequenceGraphTest {

  private SequenceGraphFactory factory;
  private SequenceGraph instance;

  /**
   * Test constructor to inject the parameterized instance of the interface.
   *
   * @param factory The factory that will produce new instances on demand
   */
  public SequenceGraphTest(SequenceGraphFactory factory) {
    this.factory = factory;
  }

  /**
   * Defines the classes that implement this interface.
   * <p>
   * These classes will be consumed by the test constructor.
   * </p>
   *
   * @return The parameter collection
   */
  @Parameterized.Parameters
  public static Collection<Object[]> instancesToTest() {
    return Arrays.asList(
        new Object[] {(SequenceGraphFactory) HashGraph::new},
        // Added a second time to prevent 1 element bug that makes asList return (non-array) objects
        // Can be removed when at least one more implementation is added to the parameters
        new Object[] {(SequenceGraphFactory) HashGraph::new});
  }

  /**
   * Factory interface which produces a new instance of type <code>SequenceGraph</code>.
   */
  private interface SequenceGraphFactory {
    SequenceGraph getInstance();
  }

  @Before
  public void setUp() {
    instance = factory.getInstance();
  }

  protected static GraphNode mockNode(int id, boolean isRoot) {
    GraphNode mockedNode = mock(GraphNode.class);
    Mockito.when(mockedNode.getId()).thenReturn(id);
    Mockito.when(mockedNode.isRoot()).thenReturn(isRoot);
    if (isRoot) {
      Mockito.when(mockedNode.getInEdges()).thenReturn(new ArrayList<>());
    } else {
      Mockito.when(mockedNode.getInEdges()).thenReturn(new ArrayList<>(Arrays.asList(10, 5, 25)));
    }
    return mockedNode;
  }

  @Test
  public void getRootNodesShouldReturnNoElementsWhenNoRoots() {
    assertEquals(0, instance.getRootNodes().size());
  }

  @Test
  public void getRootNodesShouldReturnRootWhenAddedNormally() {
    assertEquals(0, instance.getRootNodes().size());

    // Mock a 'root node'
    GraphNode mockedNode = mockNode(10, true);

    instance.add(mockedNode);

    Collection<Integer> newRootNodes = instance.getRootNodes();
    assertEquals(1, newRootNodes.size());
    assertTrue(newRootNodes.contains(mockedNode.getId()));
  }

  @Test
  public void addAsRootNodeShouldAddToRootNodes() {
    final int rootId = 25;

    // Mock a non-root node
    GraphNode mockedNode = mockNode(rootId, false);

    instance.add(mockedNode);
    assertFalse(instance.getRootNodes().contains(rootId));

    // Change mock to be a root node
    Mockito.when(mockedNode.getId()).thenReturn(10);
    Mockito.when(mockedNode.isRoot()).thenReturn(true);
    Mockito.when(mockedNode.getInEdges()).thenReturn(new ArrayList<>());

    instance.addAsRootNode(rootId);

    assertTrue(instance.getRootNodes().contains(rootId));
  }

  @Test
  public void getGenomesShouldReturnNoElementsWhenNoGenomes() {
    assertEquals(0, instance.getGenomes().size());
  }

  @Test
  public void getGenomesShouldReturnGenomeWhenAddedNormally() {
    String testGenome = "testGenome";
    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void removeGenome() {
    String testGenome = "testGraphGenome";

    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));

    instance.removeGenome(testGenome);
    assertFalse(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void sizeWhenEmpty() {
    assertFalse(instance.iterator().hasNext());
    assertEquals(0, instance.size());
  }

  @Test
  public void sizeWhenNonEmpty() {
    int expectedSize = 5;
    for (int i = 0; i < 5; i++) {
      instance.add(mockNode(i, false));
    }
    assertEquals(expectedSize, instance.size());
  }

  @Test
  public void isEmpty() {
    assertEquals(0, instance.size());
    assertTrue(instance.isEmpty());
  }

  @Test
  public void containsShouldBeFalseWhenNotInGraph() {
    Assert.assertFalse(instance.contains(25));
  }

  @Test
  public void containsShouldBeTrueWhenInGraph() {
    int nodeId = 25;
    GraphNode mockedNode = mockNode(nodeId, false);
    instance.add(mockedNode);

    Assert.assertTrue(instance.contains(nodeId));
  }

  @Test
  public void getNodeNullWhenNotFound() {
    assertNull(instance.getNode(25));
  }

  @Test
  public void getNodeFindsNodeWhenAdded() {
    int nodeId = 10;
    GraphNode mockedNode = mockNode(nodeId, false);
    instance.add(mockedNode);

    assertEquals(mockedNode, instance.getNode(nodeId));
  }

  @Test
  public void addSetsRootNode() {
    int rootId = 10;
    GraphNode mockedRoot = mockNode(rootId, true);
    assertFalse(instance.getRootNodes().contains(rootId));

    instance.add(mockedRoot);

    assertTrue(instance.getRootNodes().contains(rootId));
  }

  @Test
  public void removeWhenNotPresentShouldReturnNull() {
    assertNull(instance.remove(25));
  }

  @Test
  public void testRemove() {
    int nodeId = 50;
    GraphNode mockedNode = mockNode(nodeId, false);
    instance.add(mockedNode);
    assertTrue(instance.contains(nodeId));

    instance.remove(nodeId);

    assertFalse(instance.contains(nodeId));
  }

  @Test
  public void iteratorShouldIterateOnlyOverExistingElements() {
    assertTrue(instance.isEmpty());
    for (int i = 0; i < 5; i++) {
      instance.add(mockNode(i, false));
    }

    for (GraphNode anInstance : instance) {
      int nextId = anInstance.getId();
      assertTrue(nextId >= 0 && nextId < 5);
    }
  }

  @Test
  public void iteratorShouldIterateOverAllElements() {
    assertTrue(instance.isEmpty());
    for (int i = 0; i < 5; i++) {
      instance.add(mockNode(i, false));
    }

    Iterator<GraphNode> iterator = instance.iterator();
    boolean[] iterated = new boolean[] {false, false, false, false, false};
    while (iterator.hasNext()) {
      int nextId = iterator.next().getId();
      iterated[nextId] = true;
    }
    for (int i = 0; i < 5; i++) {
      assertTrue(iterated[i]);
    }
  }
}
