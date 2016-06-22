package nl.tudelft.pl2016gr2.util;

import static org.mockito.Mockito.mock;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A static class with utility functions that reduce code duplication in tests.
 *
 * @author Wouter Smit
 */
public class TestingUtilities {
  
  /**
   * Mocks a GraphNode with the specified ID and properties of a root or non-root node.
   *
   * @param id     the ID to assign this mocked node
   * @param isRoot The behaviour of this node
   * @return A mocked node with the specified properties
   */
  public static GraphNode mockNode(int id, boolean isRoot) {
    GraphNode mockedNode = mock(GraphNode.class);
    Mockito.when(mockedNode.getId()).thenReturn(id);
    Mockito.when(mockedNode.isRoot()).thenReturn(isRoot);
    if (isRoot) {
      Mockito.when(mockedNode.getInEdges()).thenReturn(new ArrayList<>());
    } else {
      Mockito.when(mockedNode.getInEdges()).thenReturn(new ArrayList<>(
          Arrays.asList(mock(GraphNode.class), mock(GraphNode.class), mock(GraphNode.class))));
    }
    return mockedNode;
  }
}
