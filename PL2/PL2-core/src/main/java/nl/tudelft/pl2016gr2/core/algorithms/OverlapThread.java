package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread which can be used to calculate the overlapping nodes of two graph.
 */
public class OverlapThread extends Thread {

  private HashMap<Integer, AbstractNode> overlappedNodes;
  private final OriginalGraph topGraph;
  private final OriginalGraph bottomGraph;

  /**
   * Construct a overlap thread, which finds the overlapping nodes of the given graphs.
   *
   * @param firstGraph  the first graph.
   * @param secondGraph the second graph.
   */
  public OverlapThread(OriginalGraph firstGraph, OriginalGraph secondGraph) {
    this.topGraph = firstGraph;
    this.bottomGraph = secondGraph;
  }

  /**
   * Wait till the thread completes its execution and get the overlapping nodes of the graphs.
   *
   * @return the overlapping nodes of the graphs.
   */
  public HashMap<Integer, AbstractNode> getOverlappedNodes() {
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
  private HashMap<Integer, AbstractNode> calculateOverlappedNodes() {
    final HashMap<Integer, AbstractNode> overlap = new HashMap<>();
    final HashMap<Integer, Node> bottomNodes = bottomGraph.getNodes();
    topGraph.getNodes().forEach((Integer id, Node node) -> {
      if (bottomNodes.containsKey(id)) {
        overlap.put(id, node);
      }
    });
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
