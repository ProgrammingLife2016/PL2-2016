package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

  private static final int VERTICAL_PRECISION = 1_000_000_000;

  /**
   * This is a class with only static methods, so let no one make an instance of it.
   */
  private CompareSubgraphs() {
  }

  /**
   * Align two subgraphs: mark the overlapping nodes and calculate the vertical position of all
   * nodes.
   *
   * @param orderedTopGraph    the ordered top graph.
   * @param orderedBottomGraph the ordered bottom graph.
   */
  public static void compareGraphs(ArrayList<GraphNode> orderedTopGraph,
      ArrayList<GraphNode> orderedBottomGraph) {
    OverlapThread overlapThread = new OverlapThread(orderedTopGraph, orderedBottomGraph);
    GraphAlignThread topAligner = new GraphAlignThread(orderedTopGraph);
    GraphAlignThread bottomAligner = new GraphAlignThread(orderedBottomGraph);

    overlapThread.start();
    topAligner.start();
    bottomAligner.start();
    
    try {
      overlapThread.join();
      topAligner.join();
      bottomAligner.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Align the given graph nodes vertically.
   *
   * @param graphOrder    the list of ordered graph node (by x axis).
   * @param bubbleInEdges the in edges of the bubble.
   */
  public static void alignVertically(Collection<GraphNode> graphOrder,
      Collection<GraphNode> bubbleInEdges) {
    HashMap<GraphNode, ComplexVerticalArea> areaMap = new HashMap<>();
    int index = 0;
    int heightPerRoot = VERTICAL_PRECISION / bubbleInEdges.size();
    for (GraphNode bubble : bubbleInEdges) {
      int startY = index * heightPerRoot;
      int endY = (index + 1) * heightPerRoot;
      areaMap.put(bubble, new ComplexVerticalArea(startY, endY, getExclusiveNodes(bubble
          .getOutEdges(), graphOrder)));
    }
    for (GraphNode node : graphOrder) {
      if (areaMap.containsKey(node)) {
        continue;
      }
      calculateGraphArea(node, areaMap, graphOrder);
    }
  }

  /**
   * Align the given graph nodes vertically.
   *
   * @param graphOrder the list of ordered graph node (by x axis).
   */
  public static void alignVertically(Collection<GraphNode> graphOrder) {
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
      rootNode.getGuiData().relativeYPos = (startY + endY) / 2.0 / VERTICAL_PRECISION;
      areaMap.put(rootNode, new ComplexVerticalArea(startY, endY, rootNode.getOutEdges()));
      index++;
    }
    for (GraphNode node : graphOrder) {
      if (rootNodes.contains(node)) {
        continue;
      }
      calculateGraphArea(node, areaMap, null);
    }
  }

  /**
   * Get all of the VIP nodes which are contained in the exclusive list.
   *
   * @param nodes         the list of nodes to filter.
   * @param exclusiveList the list of VIP nodes which can get access to the return value.
   * @return the VIP nodes which were found in the list of nodes.
   */
  private static Collection<GraphNode> getExclusiveNodes(Collection<GraphNode> nodes,
      Collection<GraphNode> exclusiveList) {
    Collection<GraphNode> exclusiveChildren = new HashSet<>();
    for (GraphNode node : exclusiveList) {
      if (node.hasChildren()) {
        for (GraphNode child : node.getChildren()) {
          exclusiveChildren.add(child);
        }
      }
    }
    ArrayList<GraphNode> res = new ArrayList<>();
    for (GraphNode node : nodes) {
      if (exclusiveList.contains(node) || exclusiveChildren.contains(node)) {
        res.add(node);
      }
    }
    return res;
  }

  /**
   * Calculate the graph area of the given node.
   *
   * @param node       the node.
   * @param areaMap    the area map to which to add the node -> area mapping.
   * @param graphOrder the ordered graph nodes.
   * @return the calculated graph area.
   */
  private static ComplexVerticalArea calculateGraphArea(GraphNode node,
      HashMap<GraphNode, ComplexVerticalArea> areaMap, Collection<GraphNode> graphOrder) {
    if (areaMap.containsKey(node)) {
      return areaMap.get(node);
    }
    ArrayList<ComplexVerticalArea> inAreas = new ArrayList<>();
    for (GraphNode inEdge : node.getInEdges()) {
      ComplexVerticalArea area = areaMap.get(inEdge);
      if (area == null) {
        area = calculateGraphArea(inEdge, areaMap, graphOrder);
      }
      inAreas.add(area);
    }
    ComplexVerticalArea complexNodeArea = new ComplexVerticalArea(inAreas, node.getOutEdges());
    areaMap.put(node, complexNodeArea);

    SimpleVerticalArea nodeArea = complexNodeArea.getLargestArea();

    node.getGuiData().relativeYPos = nodeArea.getCenter() / VERTICAL_PRECISION;
    node.getGuiData().maxHeight = nodeArea.getHeight() / (double) VERTICAL_PRECISION;
    return complexNodeArea;
  }

  private static class ComplexVerticalArea {

    private final List<SimpleVerticalArea> areas;
    private final ArrayList<ComplexVerticalArea> splitParts = new ArrayList<>(1);
    private int curPart = 0;

    private ComplexVerticalArea(List<ComplexVerticalArea> complexAreas,
        Collection<GraphNode> nodes) {
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
//      if(splitParts.size() == curPart) {
//        return splitParts.get(curPart - 1);
//      }
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

  /**
   * Thread which can be used to vertically align a subgraph and mark the overlapping nodes.
   */
  private static class GraphAlignThread extends Thread {

    private final ArrayList<GraphNode> orderedGraph;

    private GraphAlignThread(ArrayList<GraphNode> orderedGraph) {
      this.orderedGraph = orderedGraph;
    }

    @Override
    public void run() {
      alignVertically(orderedGraph);
    }
  }
}
