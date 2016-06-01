package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.SemanticBubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to compare and align two subgraphs with each other, so all of the overlapping
 * nodes have the same level (graph depth) in both of the subgraphs and the
 * overlapping/non-overlapping nodes are marked.
 *
 * @author Faris
 */
public class CompareSubgraphs {

  private static final int VERTICAL_PRECISION = 1_000_000_000;

  /**
   * This is a class with only static methods, so let no one make an instance of it.
   */
  private CompareSubgraphs() {
  }

<<<<<<< HEAD
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
    return new Pair<>(orderedTopGraph, orderedBottomGraph);
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
=======
//  /**
//   * Align two subgraphs, so their overlapping nodes are in the same level (graph depth).
//   *
//   * @param mainGraphOrder the main graph node order.
//   * @param topGraph       the top graph.
//   * @param bottomGraph    the bottom graph.
//   * @return a pair containing as left value the graph node order of the top graph and as left value
//   *         the node graph order of the bottom graph.
//   */
//  public static Pair<ArrayList<GraphNode>, ArrayList<GraphNode>> compareGraphs(
//      SequenceGraph mainGraphOrder, SequenceGraph topGraph,
//      SequenceGraph bottomGraph) {
//    OverlapThread overlapThread = new OverlapThread(topGraph, bottomGraph);
//    SubGraphOrderer topGraphOrderer = new SubGraphOrderer(mainGraphOrder, topGraph, overlapThread);
//    SubGraphOrderer bottomGraphOrderer = new SubGraphOrderer(mainGraphOrder, bottomGraph,
//        overlapThread);
//    overlapThread.start();
//    topGraphOrderer.start();
//    bottomGraphOrderer.start();
//
//    ArrayList<GraphNode> orderedTopGraph = topGraphOrderer.getNodeOrder();
//    ArrayList<GraphNode> orderedBottomGraph = bottomGraphOrderer.getNodeOrder();
//
//    //alignOverlappingNodes(orderedTopGraph, orderedBottomGraph);
////    removeEmptyLevels(orderedTopGraph, orderedBottomGraph);
//    return new Pair<>(orderedTopGraph, orderedBottomGraph);
//  }
  public static void alignVertically(ArrayList<GraphNode> graphOrder) {
    ArrayList<GraphNode> rootNodes = new ArrayList<>();
    for (GraphNode node : graphOrder) {
      if (node.getInEdges().isEmpty()) {
        rootNodes.add(node);
      }
    }
    HashMap<GraphNode, ComplexVerticalArea> areaMap = new HashMap<>();
    int heightPerRoot = VERTICAL_PRECISION / rootNodes.size();
    int index = 0;
    for (GraphNode rootNode : rootNodes) {
      int startY = index * heightPerRoot;
      int endY = (index + 1) * heightPerRoot;
      rootNode.setRelativeYPos((startY + endY) / 2.0 / VERTICAL_PRECISION);
      areaMap.put(rootNode, new ComplexVerticalArea(startY, endY, rootNode.getOutEdges()));
      index++;
    }
    for (GraphNode node : graphOrder) {
      if (rootNodes.contains(node)) {
        continue;
      }
      ArrayList<ComplexVerticalArea> inAreas = new ArrayList<>();
      for (GraphNode inEdge : node.getInEdges()) {
        inAreas.add(areaMap.get(inEdge));
      }
      ComplexVerticalArea complexNodeArea = new ComplexVerticalArea(inAreas, node.getOutEdges());
      areaMap.put(node, complexNodeArea);

      SimpleVerticalArea nodeArea = complexNodeArea.getLargestArea();

      node.setRelativeYPos(nodeArea.getCenter() / VERTICAL_PRECISION);
      node.setMaxHeight(nodeArea.getHeight() / (double) VERTICAL_PRECISION);
    }
  }

  private static class ComplexVerticalArea {

    private final List<SimpleVerticalArea> areas;
    private final ArrayList<ComplexVerticalArea> splitParts = new ArrayList<>(1);
    private int curPart = 0;

    private ComplexVerticalArea(List<ComplexVerticalArea> complexAreas, Collection<GraphNode> nodes) {
      areas = new LinkedList<>();
      for (ComplexVerticalArea complexArea : complexAreas) {
        areas.addAll(complexArea.getPart().areas);
      }
      mergeSequentialAreas();
      splitParts(nodes);
    }

    private ComplexVerticalArea(int startY, int endY, Collection<GraphNode> nodes) {
      areas = new ArrayList<>();
      areas.add(new SimpleVerticalArea(startY, endY));
      splitParts(nodes);
    }

    private ComplexVerticalArea(List<SimpleVerticalArea> simpleAreas) {
      areas = new ArrayList<>();
      for (SimpleVerticalArea simpleArea : simpleAreas) {
        if (simpleArea.getHeight() > 0) {
          areas.add(simpleArea);
        }
      }
    }

    private ComplexVerticalArea(SimpleVerticalArea simpleAreas) {
      areas = new ArrayList<>();
      areas.add(simpleAreas);
    }

    private SimpleVerticalArea getLargestArea() {
      double maxHeight = 0;
      SimpleVerticalArea largestArea = null;
      for (SimpleVerticalArea area : areas) {
        if (area.getHeight() > maxHeight) {
          maxHeight = area.getHeight();
          largestArea = area;
        }
      }
      return largestArea;
    }

    private void splitParts(Collection<GraphNode> nodes) {
      if (nodes.isEmpty()) {
        return;
      }
      int totalHeight = 0;
      for (SimpleVerticalArea area : areas) {
        totalHeight += area.getHeight();
      }
      /////////////////////
      // TEMPORARY HACK TO AVOID BUG WITH 328 GRAPH:
      // TOO MANY NODES ARE DRAWN IN THE SAME LOCATION
      if (totalHeight < nodes.size()) {
        int start = areas.get(0).startBlock;
        for (int i = 0; i < nodes.size(); i++) {
          splitParts.add(new ComplexVerticalArea(new SimpleVerticalArea(start + i, start + i + 1)));
        }
        return;
      }
      /////////////////////
      int heightPerArea = totalHeight / nodes.size();
      Iterator<SimpleVerticalArea> it = areas.iterator();
      SimpleVerticalArea nextToAdd = it.next();
      for (int i = 0; i < nodes.size(); i++) {
        ArrayList<SimpleVerticalArea> partAreas = new ArrayList<>();
        partAreas.add(nextToAdd);
        int partHeight = nextToAdd.getHeight();
        while (partHeight < heightPerArea) {
          SimpleVerticalArea nextArea = it.next();
          partHeight += nextArea.getHeight();
          partAreas.add(nextArea);
        }
        if (i == nodes.size() - 1) {
          while (it.hasNext()) {
            partAreas.add(it.next());
>>>>>>> feat/zoom-bubbles
          }
        } else if (partHeight > heightPerArea) {
          Pair<SimpleVerticalArea, SimpleVerticalArea> split = partAreas.get(partAreas.size() - 1)
              .splitFromEnd(partHeight - heightPerArea);
          partAreas.set(partAreas.size() - 1, split.left);
          nextToAdd = split.right;
        } else {
          nextToAdd = it.next();
        }
        splitParts.add(new ComplexVerticalArea(partAreas));
      }
      centerMostCommonPath(nodes);
    }

    /**
     * Put the most common path in the center of the parts list.
     */
    private void centerMostCommonPath(Collection<GraphNode> nodes) {
      if (splitParts.size() <= 2) {
        return;
      }
      int mostGenomes = 0;
      GraphNode mostGenomeNode = null;
      for (GraphNode node : nodes) {
        if (node.getGenomes().size() > mostGenomes) {
          mostGenomes = node.getGenomes().size();
          mostGenomeNode = node;
        }
      }
      int mostGenomeNodeLevelIndex = 0;
      for (GraphNode node : nodes) {
        if (node.getLevel() < mostGenomeNode.getLevel()) {
          mostGenomeNodeLevelIndex++;
        }
      }
      int mid = splitParts.size() / 2;
      ComplexVerticalArea temp = splitParts.get(mid);
      splitParts.set(mid, splitParts.get(mostGenomeNodeLevelIndex));
      splitParts.set(mostGenomeNodeLevelIndex, temp);
    }

    private ComplexVerticalArea getPart() {
      return splitParts.get(curPart++);
    }

    private void mergeSequentialAreas() {
      if (areas.isEmpty()) {
        return;
      }
      Collections.sort(areas);
      int index = 0;
      while (index + 1 < areas.size()) {
        SimpleVerticalArea combined = areas.get(index).combineWithNext(areas.get(index + 1));
        if (combined != null) {
          areas.remove(index + 1);
          areas.set(index, combined);
        } else {
          index++;
        }
      }
    }
  }

  private static class SimpleVerticalArea implements Comparable<SimpleVerticalArea> {

    private final int startBlock;
    private final int endBlock;

    private SimpleVerticalArea(int startBlock, int endBlock) {
      this.startBlock = startBlock;
      this.endBlock = endBlock;
    }

    private SimpleVerticalArea combineWithNext(SimpleVerticalArea next) {
      if (next.startBlock <= endBlock) {
        return new SimpleVerticalArea(startBlock, next.endBlock);
      } else {
        return null;
      }
    }

    private int getHeight() {
      return endBlock - startBlock;
    }

    private Pair<SimpleVerticalArea, SimpleVerticalArea> splitFromEnd(int distanceFromEnd) {
      SimpleVerticalArea top = new SimpleVerticalArea(startBlock, endBlock - distanceFromEnd);
      SimpleVerticalArea bottom = new SimpleVerticalArea(endBlock - distanceFromEnd, endBlock);
      return new Pair<>(top, bottom);
    }

    private double getCenter() {
      return (endBlock + startBlock) / 2.0;
    }

    @Override
    public int compareTo(SimpleVerticalArea other) {
      return startBlock - other.startBlock;
    }
  }
}
