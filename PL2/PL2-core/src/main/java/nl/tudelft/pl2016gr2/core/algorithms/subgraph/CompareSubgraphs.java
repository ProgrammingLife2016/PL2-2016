package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
  public static void alignVertically(Collection<GraphNode> graphOrder,
      Collection<GraphNode> bubbleInEdges) {
    HashMap<GraphNode, ComplexVerticalArea> areaMap = new HashMap<>();
    int index = 0;
    int heightPerRoot = VERTICAL_PRECISION / bubbleInEdges.size();
    for (GraphNode bubble : bubbleInEdges) {
      int startY = index * heightPerRoot;
      int endY = (index + 1) * heightPerRoot;
      areaMap.put(bubble, new ComplexVerticalArea(startY, endY, getExclusiveNodes(bubble.
          getOutEdges(), graphOrder)));
    }
    for (GraphNode node : graphOrder) {
      if (areaMap.containsKey(node)) {
        continue;
      }
      assert !node.getInEdges().isEmpty();
      calculateGraphArea(node, areaMap);
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
    ArrayList<GraphNode> res = new ArrayList<>();
    for (GraphNode node : nodes) {
      if (exclusiveList.contains(node)) {
        res.add(node);
      }
    }
    return res;
  }

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
      rootNode.setRelativeYPos((startY + endY) / 2.0 / VERTICAL_PRECISION);
      areaMap.put(rootNode, new ComplexVerticalArea(startY, endY, rootNode.getOutEdges()));
      index++;
    }
    for (GraphNode node : graphOrder) {
      if (rootNodes.contains(node)) {
        continue;
      }
      calculateGraphArea(node, areaMap);
    }
  }

  private static ComplexVerticalArea calculateGraphArea(GraphNode node,
      HashMap<GraphNode, ComplexVerticalArea> areaMap) {
    ArrayList<ComplexVerticalArea> inAreas = new ArrayList<>();
    assert !node.getInEdges().isEmpty();
    for (GraphNode inEdge : node.getInEdges()) {
      ComplexVerticalArea area = areaMap.get(inEdge);
      if (area == null) {
        System.out.println("added backwards edge caused by bubble");
//        System.out.println("inEdge.getLevel() = " + inEdge.getLevel());
        area = calculateGraphArea(inEdge, areaMap);
      }
      inAreas.add(area);
    }
    assert !inAreas.isEmpty();
    ComplexVerticalArea complexNodeArea = new ComplexVerticalArea(inAreas, node.getOutEdges());
    areaMap.put(node, complexNodeArea);

    SimpleVerticalArea nodeArea = complexNodeArea.getLargestArea();

    node.setRelativeYPos(nodeArea.getCenter() / VERTICAL_PRECISION);
    node.setMaxHeight(nodeArea.getHeight() / (double) VERTICAL_PRECISION);
    return complexNodeArea;
  }

  private static class ComplexVerticalArea {

    private final List<SimpleVerticalArea> areas;
    private final ArrayList<ComplexVerticalArea> splitParts = new ArrayList<>(1);
    private int curPart = 0;

    private ComplexVerticalArea(List<ComplexVerticalArea> complexAreas, Collection<GraphNode> nodes) {
      assert !complexAreas.isEmpty();
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
      assert !simpleAreas.isEmpty();
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
