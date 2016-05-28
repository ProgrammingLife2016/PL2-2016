package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;

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

  /**
   * Align two subgraphs, so their overlapping nodes are in the same level (graph depth).
   *
   * @param mainGraphOrder the main graph node order.
   * @param topGraph       the top graph.
   * @param bottomGraph    the bottom graph.
   * @return a pair containing as left value the graph node order of the top graph and as left value
   *         the node graph order of the bottom graph.
   */
  public static Pair<ArrayList<GraphNode>, ArrayList<GraphNode>> compareGraphs(
      SequenceGraph mainGraphOrder, SequenceGraph topGraph,
      SequenceGraph bottomGraph) {
    // todo: decolapse bubble from the graphs (possibly remember bubbles here for easy recolapsing)
    OverlapThread overlapThread = new OverlapThread(topGraph, bottomGraph);
    SubGraphOrderer topGraphOrderer = new SubGraphOrderer(mainGraphOrder, topGraph, overlapThread);
    SubGraphOrderer bottomGraphOrderer = new SubGraphOrderer(mainGraphOrder, bottomGraph,
        overlapThread);
    overlapThread.start();
    topGraphOrderer.start();
    bottomGraphOrderer.start();

    ArrayList<GraphNode> orderedTopGraph = topGraphOrderer.getNodeOrder();
    ArrayList<GraphNode> orderedBottomGraph = bottomGraphOrderer.getNodeOrder();

    //alignOverlappingNodes(orderedTopGraph, orderedBottomGraph);
    removeEmptyLevels(orderedTopGraph, orderedBottomGraph);
    // todo: recolapse bubble from the graphs
    // todo: check if nodes are really overlapping (taking into account the bubbles)
    return new Pair<>(orderedTopGraph, orderedBottomGraph);
  }

  /**
   * Remove the empty levels of a single graph.
   *
   * @param graphOrder the order of the graph.
   */
  public static void removeEmptyLevels(ArrayList<GraphNode> graphOrder) {
    boolean[] levelIsNotEmpty = new boolean[graphOrder.get(graphOrder.size() - 1).getLevel() + 1];
    for (GraphNode nodePosition : graphOrder) {
      levelIsNotEmpty[nodePosition.getLevel()] = true;
    }
    fillEmptyLevels(graphOrder, levelIsNotEmpty);
  }

  /**
   * Remove any level (graph depth level) which contains no nodes.
   *
   * @param topOrder    the order of the top graph.
   * @param bottomOrder the order of the bottom graph.
   */
  private static void removeEmptyLevels(ArrayList<GraphNode> topOrder,
      ArrayList<GraphNode> bottomOrder) {
    int highestTopLevel = topOrder.get(topOrder.size() - 1).getLevel();
    int highestBottomLevel = bottomOrder.get(bottomOrder.size() - 1).getLevel();
    int highestLevel;
    if (highestTopLevel > highestBottomLevel) {
      highestLevel = highestTopLevel;
    } else {
      highestLevel = highestBottomLevel;
    }
    boolean[] levelIsNotEmpty = new boolean[highestLevel + 1];
    for (GraphNode topNode : topOrder) {
      levelIsNotEmpty[topNode.getLevel()] = true;
    }
    for (GraphNode bottomNode : bottomOrder) {
      levelIsNotEmpty[bottomNode.getLevel()] = true;
    }
    fillEmptyLevels(topOrder, levelIsNotEmpty);
    fillEmptyLevels(bottomOrder, levelIsNotEmpty);
  }

  /**
   * Fill all of the empty graph levels by moving everything after it to the left.
   *
   * @param nodeOrder       the order of the nodes in the graph.
   * @param levelIsNotEmpty an arraylist containing all of the graph levels which containg no nodes.
   */
  private static void fillEmptyLevels(ArrayList<GraphNode> nodeOrder,
      boolean[] levelIsNotEmpty) {
    int curLevel = 0;
    int emptyLevels = 0;
    for (GraphNode node : nodeOrder) {
      if (node.getLevel() > curLevel) {
        for (int i = curLevel; i < node.getLevel(); i++) {
          if (!levelIsNotEmpty[i]) {
            ++emptyLevels;
          }
        }
        curLevel = node.getLevel();
      }
      node.addPositionOffset(-emptyLevels);
    }
  }
}
