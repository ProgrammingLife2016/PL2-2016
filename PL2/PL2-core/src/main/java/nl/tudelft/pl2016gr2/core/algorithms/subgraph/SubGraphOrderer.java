//package nl.tudelft.pl2016gr2.core.algorithms.subgraph;
//
//import nl.tudelft.pl2016gr2.model.GraphNode;
//import nl.tudelft.pl2016gr2.model.SequenceGraph;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
///**
// * Thread which can be used to order a subgraph and mark the overlapping nodes.
// */
//public class SubGraphOrderer extends Thread {
//
//  private final SequenceGraph mainGraphOrder;
//  private final SequenceGraph subGraph;
//  private final boolean markOverlap;
//  private OverlapThread overlapThread;
//  private ArrayList<GraphNode> nodeOrder;
//
//  /**
//   * Create an instance of this class. Orders the graph, but doesn't mark overlapping nodes.
//   *
//   * @param mainGraphOrder a map containing the order of the main graph.
//   * @param subGraph       the subgraph.
//   */
//  public SubGraphOrderer(SequenceGraph mainGraphOrder, SequenceGraph subGraph) {
//    this.mainGraphOrder = mainGraphOrder;
//    this.subGraph = subGraph;
//    markOverlap = false;
//  }
//
//  /**
//   * Create an instance of this class. Orders the graph and marks the overlapping nodes.
//   *
//   * @param mainGraphOrder a map containing the order of the main graph.
//   * @param subGraph       the subgraph.
//   * @param overlapThread  the thread which is checking which nodes are overlapping.
//   */
//  public SubGraphOrderer(SequenceGraph mainGraphOrder,
//      SequenceGraph subGraph, OverlapThread overlapThread) {
//    this.mainGraphOrder = mainGraphOrder;
//    this.subGraph = subGraph;
//    this.overlapThread = overlapThread;
//    markOverlap = true;
//  }
//
//  /**
//   * Wait till the thread completes its execution and get the ordered list of nodes.
//   *
//   * @return the list of ordered nodes.
//   */
//  public ArrayList<GraphNode> getNodeOrder() {
//    try {
//      this.join();
//    } catch (InterruptedException ex) {
//      Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
//    }
//    return nodeOrder;
//  }
//
//  /**
//   * Order the nodes of the subgraph.
//   *
//   * @param mainGraphOrder a map containing the order of the main graph.
//   * @param subGraph       the subgraph.
//   * @return the ordered subgraph.
//   */
//  private ArrayList<GraphNode> orderNodes() {
//    ArrayList<GraphNode> subGraphOrder = new ArrayList<>();
//    for (GraphNode graphNode : subGraph) {
//      GraphNode node = graphNode;
//      node.setLevel(mainGraphOrder.getNode(node.getId()).getLevel());
//      subGraphOrder.add(node);
//    }
//    return subGraphOrder;
//  }
//
//  /**
//   * Mark all of the overlapping nodes in the subgraph.
//   *
//   * @param overlappingNodes a map containing all of the overlapping nodes.
//   * @param nodeOrder        the ordered list of nodes of the subgraph.
//   */
//  private static void markOverlap(HashSet<GraphNode> overlappingNodes,
//      ArrayList<GraphNode> nodeOrder) {
//    nodeOrder.forEach(graphNode -> {
//      graphNode.setOverlapping(overlappingNodes.contains(graphNode));
//    });
//  }
//
//  /**
//   * Calculate the subgraph order and mark the overlapping nodes.
//   */
//  @Override
//  public void run() {
//    nodeOrder = orderNodes();
//    Collections.sort(nodeOrder, (GraphNode node1, GraphNode node2)
//        -> node1.getLevel() - node2.getLevel());
//    if (markOverlap) {
//      markOverlap(overlapThread.getOverlappedNodes(), nodeOrder);
//    }
//  }
//}
