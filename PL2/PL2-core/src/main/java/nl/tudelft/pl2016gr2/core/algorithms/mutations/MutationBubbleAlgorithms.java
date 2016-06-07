//package nl.tudelft.pl2016gr2.core.algorithms.mutations;
//
//import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
//import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
///**
// * @author Cas
// */
//public class MutationBubbleAlgorithms {
//
//  private static int bubbleCount = 0;
//  private static HashMap<GraphNode, Bubble> bubbled = new HashMap<>();
//
//  /**
//   * Make bubbles in the given array list of nodes.
//   *
//   * @param orderedNodes the ordered list of nodes (by x coordinate) of the graph.
//   * @return the new array list which contains the newly created bubbles and other nodes of the
//   * graph.
//   */
//  public static ArrayList<GraphNode> makeBubbels(ArrayList<GraphNode> orderedNodes) {
//    return initStraightInDelPoint(orderedNodes);
//  }
//
//  private static ArrayList<GraphNode> initStraightInDelPoint(ArrayList<GraphNode> orderedGraph) {
//    bubbleCount = Integer.MIN_VALUE;
//    return filterStraightSequence(orderedGraph);
//  }
//
//  /**
//   * Filters all straight sequences.
//   * <p>
//   * A straight sequence is defined as a sequence of nodes that have no branches.
//   * Both the first and the last node may have multiple in/out edges.
//   * In addition, all nodes in the straight sequence must have the same overlap type.
//   * E.g. they must be either in both graphs or only in the current.
//   * The last requirement prevents bubbling of inter-graph mutations
//   * </p>
//   *
//   * @param orderedNodes The nodes in the unfiltered graph
//   * @return The nodes in the filtered graph
//   */
//  private static ArrayList<GraphNode> filterStraightSequence(Collection<GraphNode> orderedNodes) {
//    ArrayList<GraphNode> newNodes = new ArrayList<>(orderedNodes.size());
//    HashSet<GraphNode> visited = new HashSet<>(orderedNodes.size());
//    orderedNodes.forEach(node -> {
//      // Node has not been seen earlier
//      if (!visited.contains(node)) {
//        // It has now
//        visited.add(node);
//
//        // Iterate as far as possible and create a bubble if that length > 1.
//        // Add the result (which is either a single node, or the created bubble
//        newNodes.add(detectStraightSequence(node, visited));
//      }
//    });
//    return newNodes;
//  }
//
//  /**
//   * Determines whether the node is a straight sequence.
//   * <p>
//   * Tries to form a straight sequence starting at <code>startNode</code>.
//   * If the immediate child does not qualify for a straight sequence, the <code>startNode</code>
//   * itself is returned.
//   * If it does, a Bubble is created, which is then returned.
//   * </p>
//   *
//   * @param startNode The node to start at (potential first node in bubble)
//   * @param visited   The visited list to avoid iterating over nodes multiple times
//   * @return The <code>startNode</code> if no sequence was made, or the Bubble for the sequence
//   */
//  private static GraphNode detectStraightSequence(GraphNode startNode, Set<GraphNode> visited) {
//    boolean overlap = startNode.isOverlapping();
//    ArrayList<GraphNode> nestedNodes = new ArrayList<>();
//    nestedNodes.add(startNode);
//
//    GraphNode current = startNode;
//    // Loop through all subsequent straight nodes
//    while (current.getOutEdges().size() == 1) {
//      // 'Loop' through the only child
//      Iterator<GraphNode> iterator = current.getOutEdges().iterator();
//      GraphNode child = iterator.next();
//      assert !iterator.hasNext();
//      // Child is part of the straight sequence of the same overlap type (true or false)
//      if (child.isOverlapping() == overlap && child.getInEdges().size() == 1) {
//        assert !visited.contains(child);
//        // Add to visited to avoid redundant looping in main loop
//        visited.add(child);
//        // Add node to bubble
//        nestedNodes.add(child);
//        // Check next node
//        current = child;
//      } else {
//        break;
//      }
//    }
//    // If only the startNode has been added, we don't actually have a straight sequence bubble
//    if (nestedNodes.size() == 1) {
//      return startNode;
//    }
//    // Else, we can return the new bubble
//    StraightSequenceBubble bubble = new StraightSequenceBubble(
//        bubbleCount++, startNode.getInEdges(), current.getOutEdges(), nestedNodes);
//
//    addEdges(bubble, startNode, current);
//    return bubble;
//  }
//
//  /**
//   * Iterates over all nodes and removes any edges that lead to nodes that are no long in the
//   * collection.
//   *
//   * @param bubble The node collection
//   */
//  private static void addEdges(Bubble bubble, GraphNode startNode, GraphNode endNode) {
//    assert startNode != endNode;
//    startNode.getInEdges().forEach(inEdge -> {
//      inEdge.addOutEdge(bubble);
//      inEdge.removeOutEdge(startNode);
//    });
//    endNode.getOutEdges().forEach(outEdge -> {
//      outEdge.addInEdge(bubble);
//      outEdge.removeInEdge(endNode);
//    });
//  }
//}