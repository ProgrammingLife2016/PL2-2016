package nl.tudelft.pl2016gr2.model.graph.nodes;

import java.util.Collection;

/**
 * This interface can be used to vertically align a set of nodes.
 *
 * @author Faris
 */
public interface IVerticalAligner {

  /**
   * Align the ordered nodes by y coordinate.
   *
   * @param orderedNodes the ordered nodes.
   * @param inEdges      the in edges of the bubble around the nodes.
   */
  void align(Collection<GraphNode> orderedNodes, Collection<GraphNode> inEdges);
}
