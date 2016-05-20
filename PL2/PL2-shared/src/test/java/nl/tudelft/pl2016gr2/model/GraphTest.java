package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public abstract class GraphTest {

  private GraphInterface graph;

  @Before
  public void setup() {
    graph = getInstance();
  }

  /**
   * Child classes should implement this method in order the get an instance of a concrete
   * implementation.
   *
   * @return an implementation of the GraphInterface.
   */
  public abstract GraphInterface getInstance();

  @Test
  public void testSize() {
    assertEquals(2, graph.getSize());
  }

  @Test
  public void testGetNode() {
    assertEquals(8, graph.getNode(5).getSequenceLength());
  }

  @Test
  public void getRootTest() {
    assertEquals((int) graph.getRootNodes().get(0), 2);
  }

}
