package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.BubblePosition;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.Position;
import nl.tudelft.pl2016gr2.model.SemanticBubble;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
   * @param mainGraphOrder
   *          the main graph node order.
   * @param topGraph
   *          the top graph.
   * @param bottomGraph
   *          the bottom graph.
   * @return a pair containing as left value the graph node order of the top graph and as right
   *         value the node graph order of the bottom graph.
   */
  public static Pair<ArrayList<Position>, ArrayList<Position>> compareGraphs(
      HashMap<Integer, Position> mainGraphOrder, SequenceGraph topGraph,
      SequenceGraph bottomGraph) {
    // todo: decolapse bubble from the graphs (possibly remember bubbles here for easy recolapsing)
    OverlapThread overlapThread = new OverlapThread(topGraph, bottomGraph);
    SubGraphOrderer topGraphOrderer = new SubGraphOrderer(mainGraphOrder, topGraph, overlapThread);
    SubGraphOrderer bottomGraphOrderer = new SubGraphOrderer(mainGraphOrder, bottomGraph,
        overlapThread);
    overlapThread.start();
    topGraphOrderer.start();
    bottomGraphOrderer.start();

    ArrayList<Position> orderedTopGraph = topGraphOrderer.getNodeOrder();
    ArrayList<Position> orderedBottomGraph = bottomGraphOrderer.getNodeOrder();

    // alignOverlappingNodes(orderedTopGraph, orderedBottomGraph);
    removeEmptyLevels(orderedTopGraph, orderedBottomGraph);

    ArrayList<Position> topOrderBubbled = initStraightInDelPoint(orderedTopGraph, topGraph);
    ArrayList<Position> bottomOrderBubbled = initStraightInDelPoint(orderedBottomGraph, bottomGraph);

    // todo: recolapse bubble from the graphs
    // todo: check if nodes are really overlapping (taking into account the bubbles)
    return new Pair<>(topOrderBubbled, bottomOrderBubbled);
  }

  private static ArrayList<Position> initStraightInDelPoint(ArrayList<Position> orderedGraph,
      SequenceGraph graph) {
    HashMap<Integer, Integer> location = new HashMap<Integer, Integer>();
    for (int i = 0; i < orderedGraph.size(); i++) {
      NodePosition nodeOrder = (NodePosition) orderedGraph.get(i);
      GraphNode node = nodeOrder.getNode();
      location.put(node.getId(), i);
    }
    return findStraightInDelPoint(orderedGraph, graph, location);
  }

  @SuppressWarnings("checkstyle:methodlength")
  private static ArrayList<Position> findStraightInDelPoint(ArrayList<Position> orderedGraph, SequenceGraph graph,
      HashMap<Integer, Integer> location) {
    ArrayList<Position> newOrder = new ArrayList<Position>();
    Set<GraphNode> visited = new HashSet<GraphNode>();
    for (int i = 0; i < orderedGraph.size(); i++) {
      NodePosition order = (NodePosition) orderedGraph.get(i);
      GraphNode node = order.getNode();
      if (!visited.contains(node)) {
        int oldLevel = order.getLevel();
        int newLevel = order.getLevel();
        boolean oldOverlap = order.isOverlapping();
        boolean newOverlap = order.isOverlapping();
        BubblePosition bubblePosition = null;
        while (oldOverlap == newOverlap && node.getOutEdges().size() == 1
            && node.getInEdges().size() <= 1 && newLevel <= oldLevel + 1
            && !visited.contains(node)) {
          // De visited hierboven kan wrs weg omdat het niet voor kan komen
          if (bubblePosition == null) {
            //je wilt aan de semantic bubble waarschijnlijk een andere id meegeven
            bubblePosition = new BubblePosition(new SemanticBubble(node.getId(), node), oldLevel);
            ArrayList<Integer> nodeInEdges = (ArrayList<Integer>) node.getInEdges();
            if (nodeInEdges.size() >  0) {
              GraphNode graphNode = graph.getNode(nodeInEdges.get(0));
              graphNode.setInBubble(true);
              bubblePosition.getBubble().addNode(graphNode);
            }
            //Hier nog eerste node aan toevoegen en in/out edges goed zetten
            bubblePosition.setOverlapping(oldOverlap);
            
          } else {
            bubblePosition.getBubble().addNode(node);
          }
          visited.add(node);
          //node.setInBubble(true);
          node = graph.getNode(((ArrayList<Integer>)node.getOutEdges()).get(0));
          order = (NodePosition) orderedGraph.get(location.get(node.getId()));
          oldLevel = newLevel;
          newLevel = order.getLevel();
          oldOverlap = newOverlap;
          newOverlap = order.isOverlapping();
        }
        if (bubblePosition != null) {
          if (bubblePosition.size() == 1) {
            // Een bubble van size 1 is een in/del of point mutation,
            // die kunnen we hier dus gelijk filteren.
            checkInDel(bubblePosition, graph);
            checkPoint(bubblePosition, graph);
            //bubblePosition.getBubble().getNodes().get(0).setInBubble(false);
          } else {
            if (oldOverlap == newOverlap && node.getInEdges().size() <= 1 && newLevel <= oldLevel + 1
                && !visited.contains(node)) {
              ArrayList<Integer> nodeOutEdges = (ArrayList<Integer>) node.getOutEdges();
              GraphNode graphNode = graph.getNode(nodeOutEdges.get(0));
              bubblePosition.getBubble().addNode(graphNode);
              bubblePosition.getBubble().setOutEdges(graphNode.getOutEdges());
              bubblePosition.getBubble().setTag("Straight");
            }
          }
          newOrder.add(bubblePosition);
        } else {
          //node.setInBubble(false);
          bubblePosition = new BubblePosition(new SemanticBubble(node.getId(), node), oldLevel);
          bubblePosition.setOverlapping(oldOverlap);
          newOrder.add(bubblePosition);
        }
      }
    }
    return newOrder;
  }

  private static void checkInDel(BubblePosition bubble, SequenceGraph graph) {
    GraphNode nodeInBubble = bubble.getBubble().getNodes().get(0);
    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0) {
      return;
    }
    GraphNode parent = graph.getNode(((ArrayList<Integer>) nodeInBubble.getInEdges()).get(0));
    GraphNode child = graph.getNode(((ArrayList<Integer>) nodeInBubble.getOutEdges()).get(0));
    ArrayList<Integer> parentOutlinks = (ArrayList<Integer>) parent.getOutEdges();
    boolean isInDel = false;
    for (int i = 0; i < parentOutlinks.size(); i++) {
      GraphNode node = graph.getNode(parentOutlinks.get(i));
      if (node == child) {
        isInDel = true;
        break;
      }
    }
    if (isInDel) {
      bubble.getBubble().setTag("InDel");
    }
  }
  
  @SuppressWarnings("checkstyle:methodlength")
  private static void checkPoint(BubblePosition bubble, SequenceGraph graph) {
    GraphNode nodeInBubble = bubble.getBubble().getNodes().get(0);
    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0 || nodeInBubble.size() > 1) {
      return;
    }
    GraphNode parent = graph.getNode(((ArrayList<Integer>) nodeInBubble.getInEdges()).get(0));
    GraphNode child = graph.getNode(((ArrayList<Integer>) nodeInBubble.getOutEdges()).get(0));
    ArrayList<Integer> parentOutlinks = (ArrayList<Integer>) parent.getOutEdges();
    boolean isPoint = true;
    for (int i = 0; i < parentOutlinks.size(); i++) {
      GraphNode node = graph.getNode(parentOutlinks.get(i));
      if (node != nodeInBubble) {
        if (node.getOutEdges().size() == 1) {
          ArrayList<Integer> nodeOutEdges = (ArrayList<Integer>) node.getOutEdges();
          if (graph.getNode(nodeOutEdges.get(0)) == child && node.size() == 1) {
            //Only for completeness, this assignment does actually do nothing.
            isPoint = true;
          } else {
            isPoint = false;
            break;
          }
        } else {
          isPoint = false;
          break;
        }
      }
    }
    if (parentOutlinks.size() == 1) {
      isPoint = false;
    }
    if (isPoint) {
      bubble.getBubble().setTag("PointMutation");
    }
  }

  /**
   * Remove any level (graph depth level) which contains no nodes.
   *
   * @param orderedTopGraph    the order of the top graph.
   * @param orderedBottomGraph the order of the bottom graph.
   */
  private static void removeEmptyLevels(ArrayList<Position> orderedTopGraph,
      ArrayList<Position> orderedBottomGraph) {
    int highestTopLevel = orderedTopGraph.get(orderedTopGraph.size() - 1).getLevel();
    int highestBottomLevel = orderedBottomGraph.get(orderedBottomGraph.size() - 1).getLevel();
    int highestLevel;
    if (highestTopLevel > highestBottomLevel) {
      highestLevel = highestTopLevel;
    } else {
      highestLevel = highestBottomLevel;
    }
    boolean[] levelIsNotEmpty = new boolean[highestLevel + 1];
    for (Position topNode : orderedTopGraph) {
      levelIsNotEmpty[topNode.getLevel()] = true;
    }
    for (Position bottomNode : orderedBottomGraph) {
      levelIsNotEmpty[bottomNode.getLevel()] = true;
    }
    fillEmptyLevels(orderedTopGraph, levelIsNotEmpty);
    fillEmptyLevels(orderedBottomGraph, levelIsNotEmpty);
  }

  /**
   * Fill all of the empty graph levels by moving everything after it to the left.
   *
   * @param orderedTopGraph       the order of the nodes in the graph.
   * @param levelIsNotEmpty an arraylist containing all of the graph levels which containg no nodes.
   */
  private static void fillEmptyLevels(ArrayList<Position> orderedTopGraph,
      boolean[] levelIsNotEmpty) {
    int curLevel = 0;
    int emptyLevels = 0;
    for (Position node : orderedTopGraph) {
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
}
