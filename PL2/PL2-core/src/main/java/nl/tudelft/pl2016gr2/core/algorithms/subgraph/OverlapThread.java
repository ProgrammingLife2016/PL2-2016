package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Thread which can be used to calculate the overlapping nodes of two graph.
 */
public class OverlapThread extends Thread {

  private final ArrayList<GraphNode> smallestOrderedGraph;
  private final ArrayList<GraphNode> largestOrderedGraph;

  /**
   * Construct a overlap thread, which finds the overlapping nodes of the given graphs.
   *
   * @param orderedTopGraph    the ordered top graph nodes.
   * @param orderedBottomGraph the ordered bottom graph nodes.
   */
  public OverlapThread(ArrayList<GraphNode> orderedTopGraph,
      ArrayList<GraphNode> orderedBottomGraph) {
    if (orderedTopGraph.size() < orderedBottomGraph.size()) {
      this.smallestOrderedGraph = orderedTopGraph;
      this.largestOrderedGraph = orderedBottomGraph;
    } else {
      this.smallestOrderedGraph = orderedBottomGraph;
      this.largestOrderedGraph = orderedTopGraph;
    }
  }

  /**
   * Set the overlap property of all of the graph nodes.
   */
  private void calculateOverlappedNodes() {
    HashMap<GraphNode, GraphNode> nodeMap = new HashMap<>();
    for (GraphNode node : smallestOrderedGraph) {
      nodeMap.put(node, node);
    }
    for (GraphNode node : largestOrderedGraph) {
      GraphNode mapNode = nodeMap.get(node);
      if (mapNode != null) {
        node.getGuiData().overlapping = true;
        mapNode.getGuiData().overlapping = true;
      }
    }
  }

  /**
   * Calculate the overlapping graph nodes.
   */
  @Override
  public void run() {
    calculateOverlappedNodes();
  }
}
