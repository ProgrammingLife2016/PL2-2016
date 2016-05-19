package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

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

  private HashMap<Integer, NodePosition> orderedGraph;
  private final OriginalGraph graph;

  /**
   * Construct a graph orderer thread.
   *
   * @param graph the graph to order.
   */
  public GraphOrdererThread(OriginalGraph graph) {
    this.graph = graph;
  }

  /**
   * Calculate the order of the nodes of the graph, so there are no backward edges. The given array
   * list contains the nodes in order from left to right. Nodes which are at the same horizontal
   * position have sequential positions in the array list and have the same value for their level
   * field.
   *
   * @param graph the graph.
   * @return the node order.
   */
  private static HashMap<Integer, NodePosition> calculateGraphOrder(OriginalGraph graph) {
    HashMap<Integer, NodePosition> nodeOrder = new HashMap<>();
    HashMap<Integer, Integer> reachedCount = new HashMap<>();
    Set<Integer> currentLevel = new HashSet<>();
    currentLevel.addAll(graph.getRootNodes());

    for (int level = 0; !currentLevel.isEmpty(); level++) {
      Set<Integer> nextLevel = new HashSet<>();
      ArrayList<ArrayList<Integer>> addedOutLinks = new ArrayList<>();
      for (Integer nodeId : currentLevel) {
        AbstractNode node = graph.getNode(nodeId);
        int count = reachedCount.getOrDefault(nodeId, 0);
        if (node.getInlinks().size() == count) {
          nodeOrder.put(nodeId, new NodePosition(node, level));
          nextLevel.addAll(node.getOutlinks());
          addedOutLinks.add(node.getOutlinks());
        }
      }
      updateReachedCount(reachedCount, addedOutLinks);
      currentLevel = nextLevel;
    }
    return nodeOrder;
  }

  /**
   * Update the reached count according to the outlinks which have been iterated over.
   *
   * @param reachedCount  the reached count map.
   * @param addedOutLinks the outlinks which have been iterated over.
   */
  private static void updateReachedCount(HashMap<Integer, Integer> reachedCount,
      ArrayList<ArrayList<Integer>> addedOutLinks) {
    for (ArrayList<Integer> outlinks : addedOutLinks) {
      for (Integer outlink : outlinks) {
        reachedCount.put(outlink, reachedCount.getOrDefault(outlink, 0) + 1);
      }
    }
  }

  /**
   * Wait till the thread completes its execution and get the ordered map of nodes.
   *
   * @return a hashmap containing an id, node order mapping.
   */
  public HashMap<Integer, NodePosition> getOrderedGraph() {
    try {
      this.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
    }
    return orderedGraph;
  }

  /**
   * Calculate the graph order.
   */
  @Override
  public void run() {
    orderedGraph = calculateGraphOrder(graph);
  }
}
