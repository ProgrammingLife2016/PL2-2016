package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.GraphNode;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is used to compare and align two subgraphs with each other, so all of the overlapping
 * nodes have the same level (graph depth) in both of the subgraphs and the
 * overlapping/non-overlapping nodes are marked.
 *
 * @author Faris
 */
public class CompareSubgraphs {

  /**
   * This is a class with only static methods, so let no one make an instance of it.
   */
  private CompareSubgraphs() {
  }

//  /**
//   * Align two subgraphs, so their overlapping nodes are in the same level (graph depth).
//   *
//   * @param mainGraphOrder the main graph node order.
//   * @param topGraph       the top graph.
//   * @param bottomGraph    the bottom graph.
//   * @return a pair containing as left value the graph node order of the top graph and as left value
//   *         the node graph order of the bottom graph.
//   */
//  public static Pair<ArrayList<GraphNode>, ArrayList<GraphNode>> compareGraphs(
//      SequenceGraph mainGraphOrder, SequenceGraph topGraph,
//      SequenceGraph bottomGraph) {
//    OverlapThread overlapThread = new OverlapThread(topGraph, bottomGraph);
//    SubGraphOrderer topGraphOrderer = new SubGraphOrderer(mainGraphOrder, topGraph, overlapThread);
//    SubGraphOrderer bottomGraphOrderer = new SubGraphOrderer(mainGraphOrder, bottomGraph,
//        overlapThread);
//    overlapThread.start();
//    topGraphOrderer.start();
//    bottomGraphOrderer.start();
//
//    ArrayList<GraphNode> orderedTopGraph = topGraphOrderer.getNodeOrder();
//    ArrayList<GraphNode> orderedBottomGraph = bottomGraphOrderer.getNodeOrder();
//
//    //alignOverlappingNodes(orderedTopGraph, orderedBottomGraph);
////    removeEmptyLevels(orderedTopGraph, orderedBottomGraph);
//    return new Pair<>(orderedTopGraph, orderedBottomGraph);
//  }
  public static void alignVertically(ArrayList<GraphNode> graphOrder,
      Collection<GraphNode> rootNodes) {
    double heightPerRoot = 1.0 / rootNodes.size();
    int index = 0;
    for (GraphNode rootNode : rootNodes) {
      double startY = index * heightPerRoot;
      double endY = (index + 1) * heightPerRoot;
      rootNode.setRelvativeYPosDomain(startY, endY);
      rootNode.setRelativeYPos((startY + endY) / 2.0);
      index++;
    }
    for (GraphNode node : graphOrder) {
      if (rootNodes.contains(node)) {
        continue;
      }
      double width = node.size();
      double endX = node.getLevel();
      double startX = endX - width;

      double startY = node.getRelvativeStartYPos();
      double endY = node.getRelvativeEndYPos();

    }
  }
}
