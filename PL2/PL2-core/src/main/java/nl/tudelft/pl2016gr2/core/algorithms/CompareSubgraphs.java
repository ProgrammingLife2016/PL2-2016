package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.GraphNodeOrder;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
   * @param topGraph    the top graph.
   * @param bottomGraph the bottom graph.
   * @return a pair containing as left value the graph node order of the top graph and as left value
   *         the node graph order of the bottom graph.
   */
  public static Pair<ArrayList<GraphNodeOrder>, ArrayList<GraphNodeOrder>> compareGraphs(
      OriginalGraph topGraph, OriginalGraph bottomGraph) {
    // todo: decolapse bubble from the graphs (possibly remember bubbles here for easy recolapsing)
    OverlapThread overlapThread = new OverlapThread(topGraph, bottomGraph);
    GraphOrdererThread topGraphOrderer = new GraphOrdererThread(topGraph, overlapThread);
    GraphOrdererThread bottomGraphOrderer = new GraphOrdererThread(bottomGraph, overlapThread);
    overlapThread.start();
    topGraphOrderer.start();
    bottomGraphOrderer.start();

    ArrayList<GraphNodeOrder> orderedTopGraph = topGraphOrderer.getOrderedGraph();
    ArrayList<GraphNodeOrder> orderedBottomGraph = bottomGraphOrderer.getOrderedGraph();

    alignOverlappingNodes(orderedTopGraph, orderedBottomGraph);
    // todo: recolapse bubble from the graphs
    // todo: check if nodes are really overlapping (taking into account the bubbles)
    return new Pair<>(orderedTopGraph, orderedBottomGraph);
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
  private static ArrayList<GraphNodeOrder> calculateGraphOrder(OriginalGraph graph) {
    ArrayList<GraphNodeOrder> nodeOrder = new ArrayList<>();
    HashMap<Integer, Integer> reachedCount = new HashMap<>();
    Set<Integer> currentLevel = new HashSet<>();
    currentLevel.add(graph.getRoot().getId());

    for (int level = 0; !currentLevel.isEmpty(); level++) {
      Set<Integer> nextLevel = new HashSet<>();
      ArrayList<ArrayList<Integer>> addedOutLinks = new ArrayList<>();
      for (Integer nodeId : currentLevel) {
        AbstractNode node = graph.getNode(nodeId);
        int count = reachedCount.getOrDefault(nodeId, 0);
        if (node.getInlinks().size() == count) {
          nodeOrder.add(new GraphNodeOrder(node, level));
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
   * Get all of the nodes which are in both the top graph and the bottom graph.
   *
   * @param topGraph    the top graph.
   * @param bottomGraph the bottom graph.
   * @return the overlapping nodes.
   */
  private static HashMap<Integer, AbstractNode> calculateOverlappedNodes(OriginalGraph topGraph,
      OriginalGraph bottomGraph) {
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
   * Align the shared graph nodes, so when the top and bottom graph are drawn separately the
   * overlapping nodes are drawn at the same x coordinate.
   *
   * @param topOrder    the order of the nodes in the top graph.
   * @param bottomOrder the order of the nodes in the bottom graph.
   */
  private static void alignOverlappingNodes(ArrayList<GraphNodeOrder> topOrder,
      ArrayList<GraphNodeOrder> bottomOrder) {
    int curTopLevel = 0;
    int startIndexOfCurTopLevel = 0;
    int curBottomLevel = 0;
    int startIndexOfPrevBottomLevel = 0;
    for (int i = 0; i < topOrder.size(); i++) {
      GraphNodeOrder topNode = topOrder.get(i);
      int topNodeId = topNode.getNode().getId();
      if (topNode.getLevel() > curTopLevel) {
        curTopLevel = topNode.getLevel();
        startIndexOfCurTopLevel = i;
      }
      if (topNode.isOverlapping()) {
        Pair<GraphNodeOrder, Pair<Integer, Integer>> match = findMatchInBottomGraph(bottomOrder,
            curBottomLevel, startIndexOfPrevBottomLevel, topNodeId);
        GraphNodeOrder bottomNode = match.left;
        startIndexOfPrevBottomLevel = match.right.left;
        curBottomLevel = match.right.right;
        alignOverlappingNode(topOrder, bottomOrder, curTopLevel, startIndexOfCurTopLevel,
            curBottomLevel, startIndexOfPrevBottomLevel, topNode, bottomNode);
      }
    }
  }

  /**
   * Find the matching node in the bottom graph.
   *
   * @param bottomOrder                the order of the nodes of the top graph.
   * @param curBottomLevel             the current leve in the graph of the bottom graph.
   * @param startIndexOfCurBottomLevel the starting index of the current bottom level in the bottom
   *                                   order list.
   * @param topNodeId                  the node which the found node should match to.
   * @return a pair containing the new bottom order and a pair containing the new starting index of
   *         the previous bottem level in the bottom order list and the new bottom level.
   */
  private static Pair<GraphNodeOrder, Pair<Integer, Integer>> findMatchInBottomGraph(
      ArrayList<GraphNodeOrder> bottomOrder, int curBottomLevel, int startIndexOfCurBottomLevel,
      int topNodeId) {
    int newLevel = curBottomLevel;
    int newStartIndex = startIndexOfCurBottomLevel;
    GraphNodeOrder bottomNode = null;
    int startIndexOfPrevBottomLevel = 0;
    for (int i = newStartIndex; i < bottomOrder.size(); i++) {
      bottomNode = bottomOrder.get(i);
      if (bottomNode.getLevel() > newLevel) {
        newLevel = bottomNode.getLevel();
        startIndexOfPrevBottomLevel = newStartIndex;
        newStartIndex = i;
      }
      if (bottomNode.getNode().getId() == topNodeId) {
        break;
      }
    }
    return new Pair<>(bottomNode, new Pair<>(startIndexOfPrevBottomLevel, newLevel));
  }

  /**
   * Move the nodes which don't align correctly to the found overlapping nodes, so they are
   * correctly aligned.
   *
   * @param topOrder                    the order of the nodes of the top graph.
   * @param bottomOrder                 the order of the nodes of the top graph.
   * @param curTopLevel                 the current level in the graph of the top graph.
   * @param startIndexOfCurTopLevel     the starting index of the current level of the top graph.
   * @param curBottomLevel              the current leve in the graph of the bottom graph.
   * @param startIndexOfPrevBottomLevel the starting index of the previous level of the bottom
   *                                    graph.
   * @param topNode                     the overlapping node of the top graph.
   * @param bottomNode                  the overlapping node of the bottom graph.
   */
  private static void alignOverlappingNode(
      ArrayList<GraphNodeOrder> topOrder, ArrayList<GraphNodeOrder> bottomOrder, int curTopLevel,
      int startIndexOfCurTopLevel, int curBottomLevel, int startIndexOfPrevBottomLevel,
      GraphNodeOrder topNode, GraphNodeOrder bottomNode) {
    int startIndexOfCurBottomLevel = startIndexOfPrevBottomLevel;
    while (bottomOrder.get(startIndexOfCurBottomLevel).getLevel() < curBottomLevel) {
      ++startIndexOfCurBottomLevel;
    }
    if (curTopLevel > curBottomLevel) {
      int offset = curTopLevel - curBottomLevel;
      for (int j = startIndexOfCurBottomLevel; j < bottomOrder.size(); j++) {
        bottomOrder.get(j).addPositionOffset(offset);
      }
    } else if (curTopLevel < curBottomLevel) {
      int offset = curBottomLevel - curTopLevel;
      for (int j = startIndexOfCurTopLevel; j < topOrder.size(); j++) {
        topOrder.get(j).addPositionOffset(offset);
      }
    }
    topNode.setUnmovable();
    bottomNode.setUnmovable();
  }

  /**
   * Thread which can be used to calculate the overlapping nodes of two graph.
   */
  private static class OverlapThread extends Thread {

    private HashMap<Integer, AbstractNode> overlappedNodes;
    private final OriginalGraph firstGraph;
    private final OriginalGraph secondGraph;

    /**
     * Construct a overlap thread, which finds the overlapping nodes of the given graphs.
     *
     * @param firstGraph  the first graph.
     * @param secondGraph the second graph.
     */
    private OverlapThread(OriginalGraph firstGraph, OriginalGraph secondGraph) {
      this.firstGraph = firstGraph;
      this.secondGraph = secondGraph;
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
     * Calculate the overlapping graph nodes.
     */
    @Override
    public void run() {
      overlappedNodes = calculateOverlappedNodes(firstGraph, secondGraph);
    }
  }

  /**
   * Thread which can be used to order a graph.
   */
  private static class GraphOrdererThread extends Thread {

    private ArrayList<GraphNodeOrder> orderedGraph;
    private final OriginalGraph graph;
    private final OverlapThread overlapThread;

    /**
     * Construct a graph orderer thread.
     *
     * @param graph         the graph to order.
     * @param overlapThread the overlap thread which is calculating the overlapping graph nodes.
     */
    private GraphOrdererThread(OriginalGraph graph, OverlapThread overlapThread) {
      this.graph = graph;
      this.overlapThread = overlapThread;
    }

    /**
     * Wait till the thread completes its execution and get the ordered graph.
     *
     * @return the ordered graph.
     */
    public ArrayList<GraphNodeOrder> getOrderedGraph() {
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
      try {
        overlapThread.join();
        orderedGraph.stream().forEach(graphNode -> {
          graphNode.setOverlapping(overlapThread.overlappedNodes.containsKey(
              graphNode.getNode().getId()));
        });
      } catch (InterruptedException ex) {
        Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
