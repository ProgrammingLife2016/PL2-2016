package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread which can be used to calculate the overlapping nodes of two graph.
 */
public class OverlapThread extends Thread {

  private HashSet<GraphNode> overlappedNodes;
  private final SequenceGraph topGraph;
  private final SequenceGraph bottomGraph;

  /**
   * Construct a overlap thread, which finds the overlapping nodes of the given graphs.
   *
   * @param firstGraph  the first graph.
   * @param secondGraph the second graph.
   */
  public OverlapThread(SequenceGraph firstGraph, SequenceGraph secondGraph) {
    this.topGraph = firstGraph;
    this.bottomGraph = secondGraph;
  }

  /**
   * Wait till the thread completes its execution and get the overlapping nodes of the graphs.
   *
   * @return the overlapping nodes of the graphs.
   */
  public HashSet<GraphNode> getOverlappedNodes() {
    try {
      this.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
    }
    return overlappedNodes;
  }

  /**
   * Get all of the nodes which are in both the top graph and the bottom graph.
   *
   * @param topGraph    the top graph.
   * @param bottomGraph the bottom graph.
   * @return the overlapping nodes.
   */
  private HashSet<GraphNode> calculateOverlappedNodes() {
    final HashSet<GraphNode> overlap = new HashSet<>();
    for (GraphNode graphNode : topGraph) {
      if (bottomGraph.contains(graphNode)) {
        overlap.add(graphNode);
      }
    }
    return overlap;
  }

  /**
   * Calculate the overlapping graph nodes.
   */
  @Override
  public void run() {
    overlappedNodes = calculateOverlappedNodes();
  }
}
