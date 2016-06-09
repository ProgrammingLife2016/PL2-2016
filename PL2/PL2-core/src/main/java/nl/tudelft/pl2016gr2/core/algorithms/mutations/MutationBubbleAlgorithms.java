package nl.tudelft.pl2016gr2.core.algorithms.mutations;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.StraightSequenceBubble;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This class creates straight sequence bubbles, indel bubbles and point mutation bubbles in the
 * given graph to group together the mutations and make the graph smaller..
 *
 * @author Cas
 */
public class MutationBubbleAlgorithms {

  private static int bubbleCount = 0;

  private MutationBubbleAlgorithms() {
  }

  /**
   * Make bubbles in the given array list of nodes.
   *
   * @param orderedNodes the ordered list of nodes (by x coordinate) of the graph.
   * @return the new array list which contains the newly created bubbles and other nodes of the
   *         graph.
   */
  public static ArrayList<GraphNode> makeBubbels(ArrayList<GraphNode> orderedNodes) {
    return initStraightInDelPoint(orderedNodes);
  }

  private static ArrayList<GraphNode> initStraightInDelPoint(ArrayList<GraphNode> orderedGraph) {
    bubbleCount = Integer.MIN_VALUE;
    ArrayList<GraphNode> orderedNodes = filterPointMutation(orderedGraph);
    orderedNodes = filterIndel(orderedNodes);
    orderedNodes.sort((GraphNode first, GraphNode second) -> first.getLevel() - second.getLevel());
    orderedNodes = filterStraightSequence(orderedNodes);

    orderedNodes.sort((GraphNode first, GraphNode second) -> first.getLevel() - second.getLevel());
    return orderedNodes;
  }

  /**
   * Filter the point mutations out of the graph by creating bubbles of them.
   *
   * @param orderedNodes the ordered list of nodes.
   * @return the list of nodes containing bubbles.
   */
  private static ArrayList<GraphNode> filterPointMutation(List<GraphNode> orderedNodes) {
    HashSet<GraphNode> nestedNodes = new HashSet<>();
    ArrayList<GraphNode> bubbledNodes = new ArrayList<>();
    for (GraphNode node : orderedNodes) {
      if (nestedNodes.contains(node)) {
        continue;
      }
      bubbledNodes.add(node);
      PointMutationBubble mutationBubble = createPointMutationBubble(node.getOutEdges());
      if (mutationBubble != null) {
        bubbledNodes.add(mutationBubble);
        nestedNodes.addAll(mutationBubble.getChildren());
      }
    }
    return bubbledNodes;
  }

  /**
   * Creates a point mutation bubble iff the given list contains 2 nodes which form a point
   * mutation, otherwise returns null.
   *
   * @param node the collection of nodes.
   * @return a point mutation bubble or null if the nodes didn't form a point mutation.
   */
  private static PointMutationBubble createPointMutationBubble(Collection<GraphNode> outEdges) {
    if (outEdges.size() == 2) {
      Iterator<GraphNode> it = outEdges.iterator();
      GraphNode firstChild = it.next();
      GraphNode secondChild = it.next();
      if (firstChild.size() == 1 && secondChild.size() == 1
          && firstChild.getInEdges().size() == 1 && secondChild.getInEdges().size() == 1
          && firstChild.getOutEdges().size() == 1 && secondChild.getOutEdges().size() == 1
          && firstChild.getOutEdges().containsAll(secondChild.getOutEdges())) {
        return createPointMutationBubble(firstChild, secondChild);
      }
    }
    return null;
  }

  /**
   * Create a point mutation bubble.
   *
   * @param firstChild  the first child node in the bubble.
   * @param secondChild the second child node in the bubble.
   * @return the point mutation bubble.
   */
  private static PointMutationBubble createPointMutationBubble(GraphNode firstChild,
      GraphNode secondChild) {
    ArrayList<GraphNode> nestedNodes = new ArrayList<>();
    nestedNodes.add(firstChild);
    nestedNodes.add(secondChild);

    PointMutationBubble bubble = new PointMutationBubble(bubbleCount++, firstChild.getInEdges(),
        firstChild.getOutEdges(), nestedNodes, VerticalAligner.POINT_MUTATION_ALIGNER);

    GraphNode startNode = firstChild.getInEdges().iterator().next();
    startNode.removeOutEdge(firstChild);
    startNode.removeOutEdge(secondChild);
    startNode.addOutEdge(bubble);

    GraphNode endNode = firstChild.getOutEdges().iterator().next();
    endNode.removeInEdge(firstChild);
    endNode.removeInEdge(secondChild);
    endNode.addInEdge(bubble);

    return bubble;
  }

  /**
   * Filter the insertions/deletions out of the graph by creating bubbles of them.
   *
   * @param orderedNodes the ordered list of nodes.
   * @return the list of nodes containing bubbles.
   */
  private static ArrayList<GraphNode> filterIndel(List<GraphNode> orderedNodes) {
    HashSet<GraphNode> nestedNodes = new HashSet<>();
    ArrayList<GraphNode> bubbledNodes = new ArrayList<>();
    for (GraphNode node : orderedNodes) {
      if (nestedNodes.contains(node)) {
        continue;
      }
      bubbledNodes.add(node);
      IndelBubble mutationBubble = createIndelBubble(node);
      if (mutationBubble != null) {
        bubbledNodes.add(mutationBubble);
        nestedNodes.addAll(mutationBubble.getChildren());
      }
    }
    return bubbledNodes;
  }

  /**
   * Creates an indel bubble iff the children of the given parent contain for an indel bubble.
   *
   * @param parent the parent node.
   * @return a point mutation bubble or null if the nodes didn't form a point mutation.
   */
  private static IndelBubble createIndelBubble(GraphNode parent) {
    Collection<GraphNode> outEdges = parent.getOutEdges();
    if (outEdges.size() == 2) {
      Iterator<GraphNode> it = outEdges.iterator();
      GraphNode startNode = it.next();
      GraphNode endNode = it.next();
      if (startNode.getLevel() > endNode.getLevel()) {
        GraphNode temp = startNode;
        startNode = endNode;
        endNode = temp;
      }
      ArrayList<GraphNode> nestedNodes = new ArrayList<>();
      GraphNode nodeIter = startNode;
      while (nodeIter.getOutEdges().size() == 1 && nodeIter.getInEdges().size() == 1
          && nodeIter.getLevel() < endNode.getLevel()) {
        nestedNodes.add(nodeIter);
        nodeIter = nodeIter.getOutEdges().iterator().next();
      }
      if (nodeIter == endNode) {
        return createIndelBubble(nestedNodes, parent, endNode);
      }
    }
    return null;
  }

  /**
   * Create an indel bubble.
   *
   * @param nestedNodes the nested nodes of the bubble.
   * @param startNode   the inedge node of the bubble.
   * @param endNode     the outedge node of the bubble.
   * @return the point mutation bubble.
   */
  private static IndelBubble createIndelBubble(List<GraphNode> nestedNodes, GraphNode startNode,
      GraphNode endNode) {
    ArrayList<GraphNode> inEdges = new ArrayList<>();
    inEdges.add(startNode);
    ArrayList<GraphNode> outEdges = new ArrayList<>();
    outEdges.add(endNode);

    IndelBubble bubble = new IndelBubble(bubbleCount++, inEdges, outEdges, nestedNodes,
        VerticalAligner.INDEL_ALIGNER);

    startNode.removeOutEdge(nestedNodes.get(0));
    startNode.removeOutEdge(endNode);
    startNode.addOutEdge(bubble);

    endNode.removeInEdge(startNode);
    endNode.removeInEdge(nestedNodes.get(nestedNodes.size() - 1));
    endNode.addInEdge(bubble);

    return bubble;
  }

  /**
   * Filters all straight sequences.
   * <p>
   * A straight sequence is defined as a sequence of nodes that have no branches. Both the first and
   * the last node may have multiple in/out edges. In addition, all nodes in the straight sequence
   * must have the same overlap type. E.g. they must be either in both graphs or only in the
   * current. The last requirement prevents bubbling of inter-graph mutations
   * </p>
   *
   * @param orderedNodes The nodes in the unfiltered graph
   * @return The nodes in the filtered graph
   */
  private static ArrayList<GraphNode> filterStraightSequence(List<GraphNode> orderedNodes) {
    ArrayList<GraphNode> newNodes = new ArrayList<>(orderedNodes.size());
    HashSet<GraphNode> visited = new HashSet<>(orderedNodes.size());
    orderedNodes.forEach(node -> {
      // Node has not been seen earlier
      if (!visited.contains(node)) {
        // It has now
        visited.add(node);

        // Iterate as far as possible and create a bubble if that length > 1.
        // Add the result (which is either a single node, or the created bubble
        newNodes.addAll(detectStraightSequence(node, visited));
      }
    });
    return newNodes;
  }

  /**
   * Determines whether the node is a straight sequence.
   * <p>
   * Tries to form a straight sequence starting at <code>startNode</code>. If the immediate child
   * does not qualify for a straight sequence, the <code>startNode</code> itself is returned. If it
   * does, a Bubble is created, which is then returned.
   * </p>
   *
   * @param startNode The node to start at (potential first node in bubble)
   * @param visited   The visited list to avoid iterating over nodes multiple times
   * @return The <code>startNode</code> if no sequence was made, or the Bubble for the sequence
   */
  private static Collection<GraphNode> detectStraightSequence(GraphNode startNode, 
      Set<GraphNode> visited) {
    boolean overlap = startNode.getGuiData().overlapping;
    List<GraphNode> nestedNodes = new ArrayList<>();
    nestedNodes.add(startNode);
    GraphNode current = startNode;
    while (current.getOutEdges().size() == 1) {
      GraphNode child = current.getOutEdges().iterator().next();
      if (child.getGuiData().overlapping == overlap && child.getInEdges().size() == 1) {
        visited.add(child);
        nestedNodes.add(child);
        current = child;
      } else {
        break;
      }
    }
    if (nestedNodes.size() < 3) {
      return nestedNodes;
    }
    nestedNodes.remove(startNode);
    nestedNodes.remove(current);
    StraightSequenceBubble bubble = new StraightSequenceBubble(bubbleCount++, 
        Collections.singletonList(startNode), Collections.singletonList(current), nestedNodes,
        VerticalAligner.STRAIGHT_SEQUENCE_ALIGNER);
    return getNewNodes(startNode, current, bubble);
  }
  
  private static Collection<GraphNode> getNewNodes(GraphNode start, GraphNode end, 
      GraphNode bubble) {
    start.setOutEdges(Collections.singletonList(bubble));
    end.setInEdges(Collections.singletonList(bubble));
     
    List<GraphNode> newNodes = new ArrayList<>();
    newNodes.add(start);
    newNodes.add(bubble);
    newNodes.add(end);
    return newNodes;
  }
}