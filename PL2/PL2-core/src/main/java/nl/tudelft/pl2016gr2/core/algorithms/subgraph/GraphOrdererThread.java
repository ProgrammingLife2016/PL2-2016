package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.SequenceGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread which can be used to order a graph.
 *
 * @author Faris
 */
public class GraphOrdererThread extends Thread {

//  private ArrayList<GraphNode> orderedGraph;
  private final SequenceGraph graph;

  /**
   * Construct a graph orderer thread.
   *
   * @param graph the graph to order.
   */
  public GraphOrdererThread(SequenceGraph graph) {
    this.graph = graph;
  }

  /**
   * Calculate the order of the nodes of the graph, so there are no backward edges. The given array
   * list contains the nodes in order from left to right. Nodes which are at the same horizontal
   * position have sequential positions in the array list and have the same value for their level
   * field.
   */
  private void calculateGraphOrder() {
    HashMap<GraphNode, Integer> reachedCount = new HashMap<>();
    Set<GraphNode> currentLevel = new HashSet<>();
    currentLevel.addAll(graph.getRootNodes());

    while (!currentLevel.isEmpty()) {
      Set<GraphNode> nextLevel = new HashSet<>();
      ArrayList<ArrayList<GraphNode>> addedOutLinks = new ArrayList<>();
      for (GraphNode node : currentLevel) {
        int count = reachedCount.getOrDefault(node, 0);
        if (node.getInEdges().size() == count) {

          int maxInLevel = 0;
          for (GraphNode inEdge : node.getInEdges()) {
            if (inEdge.getLevel() > maxInLevel) {
              maxInLevel = inEdge.getLevel();
            }
          }
          node.setLevel(maxInLevel + node.size());
          nextLevel.addAll(node.getOutEdges());
          addedOutLinks.add(new ArrayList<>(node.getOutEdges()));
        }
      }
      updateReachedCount(reachedCount, addedOutLinks);
      currentLevel = nextLevel;
    }
  }

  /**
   * Update the reached count according to the out edges which have been iterated over.
   *
   * @param reachedCount  the reached count map.
   * @param addedOutLinks the outlinks which have been iterated over.
   */
  private static void updateReachedCount(HashMap<GraphNode, Integer> reachedCount,
      ArrayList<ArrayList<GraphNode>> addedOutLinks) {
    for (ArrayList<GraphNode> outEdges : addedOutLinks) {
      for (GraphNode outEdge : outEdges) {
        reachedCount.put(outEdge, reachedCount.getOrDefault(outEdge, 0) + 1);
      }
    }
  }

  /**
   * Wait till the thread completes its execution and get the ordered map of nodes.
   *
   * @return a hashmap containing an id, node order mapping.
   */
  public SequenceGraph getGraph() {
    try {
      this.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
    }
    return graph;
  }

  /**
   * Calculate the graph order.
   */
  @Override
  public void run() {
    calculateGraphOrder();
  }
}
