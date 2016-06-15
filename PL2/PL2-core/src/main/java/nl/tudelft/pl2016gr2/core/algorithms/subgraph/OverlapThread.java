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
      putAllChildren(node, nodeMap);
    }
    for (GraphNode node : largestOrderedGraph) {
      overlapAllChildren(node, nodeMap);
    }
  }

  /**
   * Put all children of the given node in the nodemap.
   *
   * @param node    the node.
   * @param nodeMap the nodemap.
   */
  private void putAllChildren(GraphNode node, HashMap<GraphNode, GraphNode> nodeMap) {
    if (node.hasChildren()) {
      for (GraphNode child : node.getChildren()) {
        putAllChildren(child, nodeMap);
      }
    } else {
      nodeMap.put(node, node);
    }
  }

  /**
   * Set the overlap value of all children based on if the node is presentin the nodeMap.
   *
   * @param node    the node.
   * @param nodeMap the nodemap.
   */
  private void overlapAllChildren(GraphNode node, HashMap<GraphNode, GraphNode> nodeMap) {
    if (node.hasChildren()) {
      for (GraphNode child : node.getChildren()) {
        overlapAllChildren(child, nodeMap);
      }
    } else {
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
