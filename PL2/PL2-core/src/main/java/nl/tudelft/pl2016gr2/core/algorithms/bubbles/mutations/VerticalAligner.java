package nl.tudelft.pl2016gr2.core.algorithms.bubbles.mutations;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.IVerticalAligner;

/**
 * This class is used to vertically align the nodes which are nested in a bubble.
 *
 * @author Faris
 */
public class VerticalAligner {

  public static final IVerticalAligner INDEL_ALIGNER = (orderedNodes, inEdges) -> {
    for (GraphNode node : orderedNodes) {
      node.getGuiData().relativeYPos = 0.25;
      node.getGuiData().maxHeight = 0.45;
    }
  };

  public static final IVerticalAligner STRAIGHT_SEQUENCE_ALIGNER = (orderedNodes, inEdges) -> {
    for (GraphNode node : orderedNodes) {
      node.getGuiData().relativeYPos = 0.5;
      node.getGuiData().maxHeight = 1.0;
    }
  };

  public static final IVerticalAligner POINT_MUTATION_ALIGNER = (orderedNodes, inEdges) -> {
    double maxHeight = 1.0 / orderedNodes.size();
    double curYPos = 0;
    for (GraphNode node : orderedNodes) {
      node.getGuiData().relativeYPos = curYPos + maxHeight / 2.0;
      node.getGuiData().maxHeight = maxHeight * 0.9;
      curYPos += maxHeight;
    }
  };

  private VerticalAligner() {
  }
}
