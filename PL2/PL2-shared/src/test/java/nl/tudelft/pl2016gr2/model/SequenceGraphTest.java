package nl.tudelft.pl2016gr2.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.Assert;
import nl.tudelft.pl2016gr2.util.TestingUtilities;
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

  @Test
  public void getRootNodesShouldReturnNoElementsWhenNoRoots() {
    assertEquals(0, instance.getRootNodes().size());
  }

  @Test
  public void getRootNodesShouldReturnRootWhenAddedNormally() {
    assertEquals(0, instance.getRootNodes().size());

    // Mock a 'root node'
    GraphNode mockedNode = TestingUtilities.mockNode(10, true);

    instance.add(mockedNode);

    Collection<GraphNode> newRootNodes = instance.getRootNodes();
    assertEquals(1, newRootNodes.size());
    assertTrue(newRootNodes.contains(mockedNode));
  }

  @Test
  public void addAsRootNodeShouldAddToRootNodes() {

    // Mock a non-root node
    GraphNode mockedNode = TestingUtilities.mockNode(30, false);

    instance.add(mockedNode);
    assertFalse(instance.getRootNodes().contains(mockedNode));

    // Change mock to be a root node
    Mockito.when(mockedNode.isRoot()).thenReturn(true);
    Mockito.when(mockedNode.getInEdges()).thenReturn(new ArrayList<>());

    instance.addAsRootNode(mockedNode);

    assertTrue(instance.getRootNodes().contains(mockedNode));
  }

  @Test
  public void getGenomesShouldReturnNoElementsWhenNoGenomes() {
    assertEquals(0, instance.getGenomes().size());
  }

  @Test
  public void getGenomesShouldReturnGenomeWhenAddedNormally() {
    int testGenome = 1;
    instance.addGenome(testGenome);
    assertTrue(instance.getGenomes().contains(testGenome));
  }

  @Test
  public void removeGenome() {
    int testGenome = 1;

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
      instance.add(TestingUtilities.mockNode(i, false));
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
    Assert.assertFalse(instance.contains(TestingUtilities.mockNode(100, false)));
  }

  @Test
  public void containsShouldBeTrueWhenInGraph() {
    GraphNode mockedNode = TestingUtilities.mockNode(25, false);
    instance.add(mockedNode);

    Assert.assertTrue(instance.contains(mockedNode));
  }

  @Test
  public void getNodeNullWhenNotFound() {
    assertNull(instance.getNode(25));
  }

  @Test
  public void getNodeFindsNodeWhenAdded() {
    int nodeId = 10;
    GraphNode mockedNode = TestingUtilities.mockNode(nodeId, false);
    instance.add(mockedNode);

    assertEquals(mockedNode, instance.getNode(nodeId));
  }

  @Test
  public void addSetsRootNode() {
    GraphNode mockedRoot = TestingUtilities.mockNode(10, true);
    assertFalse(instance.getRootNodes().contains(mockedRoot));

    instance.add(mockedRoot);

    assertTrue(instance.getRootNodes().contains(mockedRoot));
  }

  @Test
  public void removeWhenNotPresentShouldReturnNull() {
    assertNull(instance.remove(TestingUtilities.mockNode(100, false)));
  }

  @Test
  public void testRemove() {
    GraphNode mockedNode = TestingUtilities.mockNode(50, false);
    instance.add(mockedNode);
    assertTrue(instance.contains(mockedNode));

    instance.remove(mockedNode);

    assertFalse(instance.contains(mockedNode));
  }

  @Test
  public void iteratorShouldIterateOnlyOverExistingElements() {
    assertTrue(instance.isEmpty());
    for (int i = 0; i < 5; i++) {
      instance.add(TestingUtilities.mockNode(i, false));
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
      instance.add(TestingUtilities.mockNode(i, false));
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
