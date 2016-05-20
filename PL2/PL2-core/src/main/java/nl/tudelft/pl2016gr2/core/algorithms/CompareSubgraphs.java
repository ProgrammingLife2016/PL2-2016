package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
  public static Pair<ArrayList<NodePosition>, ArrayList<NodePosition>> compareGraphs(
      HashMap<Integer, NodePosition> mainGraphOrder, OriginalGraph topGraph,
      OriginalGraph bottomGraph) {
    // todo: decolapse bubble from the graphs (possibly remember bubbles here for easy recolapsing)
    OverlapThread overlapThread = new OverlapThread(topGraph, bottomGraph);
    SubGraphOrderer topGraphOrderer = new SubGraphOrderer(mainGraphOrder, topGraph, overlapThread);
    SubGraphOrderer bottomGraphOrderer = new SubGraphOrderer(mainGraphOrder, bottomGraph,
        overlapThread);
    overlapThread.start();
    topGraphOrderer.start();
    bottomGraphOrderer.start();

    ArrayList<NodePosition> orderedTopGraph = topGraphOrderer.getNodeOrder();
    ArrayList<NodePosition> orderedBottomGraph = bottomGraphOrderer.getNodeOrder();

    //alignOverlappingNodes(orderedTopGraph, orderedBottomGraph);
    removeEmptyLevels(orderedTopGraph, orderedBottomGraph);
    // todo: recolapse bubble from the graphs
    // todo: check if nodes are really overlapping (taking into account the bubbles)
    return new Pair<>(orderedTopGraph, orderedBottomGraph);
  }

  /**
   * Remove any level (graph depth level) which contains no nodes.
   *
   * @param topOrder    the order of the top graph.
   * @param bottomOrder the order of the bottom graph.
   */
  private static void removeEmptyLevels(ArrayList<NodePosition> topOrder,
      ArrayList<NodePosition> bottomOrder) {
    int highestTopLevel = topOrder.get(topOrder.size() - 1).getLevel();
    int highestBottomLevel = bottomOrder.get(bottomOrder.size() - 1).getLevel();
    int highestLevel;
    if (highestTopLevel > highestBottomLevel) {
      highestLevel = highestTopLevel;
    } else {
      highestLevel = highestBottomLevel;
    }
    boolean[] levelIsNotEmpty = new boolean[highestLevel + 1];
    for (NodePosition topNode : topOrder) {
      levelIsNotEmpty[topNode.getLevel()] = true;
    }
    for (NodePosition bottomNode : bottomOrder) {
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
  private static void fillEmptyLevels(ArrayList<NodePosition> nodeOrder,
      boolean[] levelIsNotEmpty) {
    int curLevel = 0;
    int emptyLevels = 0;
    for (NodePosition node : nodeOrder) {
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

  /**
   * Thread which can be used to order a subgraph and mark the overlapping nodes.
   */
  private static class SubGraphOrderer extends Thread {

    private final HashMap<Integer, NodePosition> mainGraphOrder;
    private final OriginalGraph subGraph;
    private final OverlapThread overlapThread;
    private ArrayList<NodePosition> nodeOrder;

    /**
     * Create an instance of this class.
     *
     * @param mainGraphOrder a map containing the order of the main graph.
     * @param subGraph       the subgraph.
     * @param overlapThread  the thread which is checking which nodes are overlapping.
     */
    private SubGraphOrderer(HashMap<Integer, NodePosition> mainGraphOrder,
        OriginalGraph subGraph, OverlapThread overlapThread) {
      this.mainGraphOrder = mainGraphOrder;
      this.subGraph = subGraph;
      this.overlapThread = overlapThread;
    }

    /**
     * Wait till the thread completes its execution and get the ordered list of nodes.
     *
     * @return the list of ordered nodes.
     */
    public ArrayList<NodePosition> getNodeOrder() {
      try {
        this.join();
      } catch (InterruptedException ex) {
        Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
      }
      return nodeOrder;
    }

    /**
     * Order the nodes of the subgraph.
     *
     * @param mainGraphOrder a map containing the order of the main graph.
     * @param subGraph       the subgraph.
     * @return the ordered subgraph.
     */
    private static ArrayList<NodePosition> orderNodes(
        HashMap<Integer, NodePosition> mainGraphOrder, OriginalGraph subGraph) {
      ArrayList<NodePosition> subGraphOrder = new ArrayList<>();
      subGraph.getNodes().forEach((Integer id, Node node) -> {
        int level = mainGraphOrder.get(id).getLevel();
        subGraphOrder.add(new NodePosition(node, level));
      });
      return subGraphOrder;
    }

    /**
     * Mark all of the overlapping nodes in the subgraph.
     *
     * @param overlappingNodes a map containing all of the overlapping nodes.
     * @param nodeOrder        the ordered list of nodes of the subgraph.
     */
    private static void markOverlap(HashMap<Integer, AbstractNode> overlappingNodes,
        ArrayList<NodePosition> nodeOrder) {
      nodeOrder.forEach(graphNode -> {
        graphNode.setOverlapping(overlappingNodes.containsKey(
            graphNode.getNode().getId()));
      });
    }

    /**
     * Calculate the subgraph order and mark the overlapping nodes.
     */
    @Override
    public void run() {
      nodeOrder = orderNodes(mainGraphOrder, subGraph);
      Collections.sort(nodeOrder);
      try {
        overlapThread.join();
        markOverlap(overlapThread.overlappedNodes, nodeOrder);
      } catch (InterruptedException ex) {
        Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  /**
   * Thread which can be used to calculate the overlapping nodes of two graph.
   */
  private static class OverlapThread extends Thread {

    private HashMap<Integer, AbstractNode> overlappedNodes;
    private final OriginalGraph topGraph;
    private final OriginalGraph bottomGraph;

    /**
     * Construct a overlap thread, which finds the overlapping nodes of the given graphs.
     *
     * @param firstGraph  the first graph.
     * @param secondGraph the second graph.
     */
    private OverlapThread(OriginalGraph firstGraph, OriginalGraph secondGraph) {
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
}
