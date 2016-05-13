package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Compares two graphs by drawing them above each other in a Pane.
 *
 * @author Faris
 */
public class CompareGraphs {

  private static final double X_OFFSET = 50.0;
  private static final double NODE_RADIUS = 15.0;
  private static final Color OVERLAP_COLOR = Color.rgb(0, 73, 73);
  private static final Color NO_OVERLAP_COLOR = Color.rgb(146, 0, 0);

  private final Pane topPane;
  private final Pane bottomPane;

  /**
   * Initialize the class by splitting the given pane into 2 parts.
   *
   * @param pane the pane in which to draw the graph.
   */
  public CompareGraphs(Pane pane) {
    pane.getChildren().clear();
    topPane = new Pane();
    bottomPane = new Pane();
    pane.getChildren().addAll(topPane, bottomPane);
    topPane.prefHeightProperty().bind(pane.heightProperty().divide(2.0));
    bottomPane.prefHeightProperty().bind(pane.heightProperty().divide(2.0));
    bottomPane.layoutYProperty().bind(pane.heightProperty().divide(2.0));
  }

  /**
   * Draw two graphs to compare.
   *
   * @param topGraph    the top graph.
   * @param bottomGraph the bottom graph.
   */
  public void drawGraphs(OriginalGraph topGraph, OriginalGraph bottomGraph) {
    topPane.getChildren().clear();
    bottomPane.getChildren().clear();
    long start = System.nanoTime();
    HashMap<Integer, AbstractNode> overlappedNodes = getOverlappedNodes(topGraph, bottomGraph);
    ArrayList<GraphNodeOrder> topOrder = calculateGraphOrder(topGraph);
    ArrayList<GraphNodeOrder> bottomOrder = calculateGraphOrder(bottomGraph);
    alignOverlappingNodes(topOrder, bottomOrder, overlappedNodes);
    System.out.println("align nodes = " + (System.nanoTime() - start));
    drawGraph(topPane, topOrder, overlappedNodes);
    drawGraph(bottomPane, bottomOrder, overlappedNodes);
  }

  /**
   * Draw the given graph in the given pane.
   *
   * @param pane            the pane to draw the graph in.
   * @param graph           the graph to draw.
   * @param overlappedNodes the overlapping nodes. Nodes in this map will be drawn in a different
   *                        color.
   */
  private void drawGraph(Pane pane, ArrayList<GraphNodeOrder> graph,
      HashMap<Integer, AbstractNode> overlappedNodes) {
    HashMap<Integer, NodeCircle> circleMap = new HashMap<>();
    int curLevel = 0;
    ArrayList<AbstractNode> levelNodes = new ArrayList<>();
    for (GraphNodeOrder node : graph) {
      if (node.getLevel() == curLevel) {
        levelNodes.add(node.getNode());
      } else {
        drawNode(pane, circleMap, levelNodes, curLevel, overlappedNodes);
        curLevel = node.getLevel();
        levelNodes.clear();
        levelNodes.add(node.getNode());
      }
    }
    drawNode(pane, circleMap, levelNodes, curLevel, overlappedNodes);
    repositionOverlappingEdges(graph, circleMap);
    drawEdges(pane, graph, circleMap);
  }

  /**
   * Draw the given list of nodes as circles in the given pane.
   *
   * @param pane            the pane in which to draw the nodes.
   * @param circleMap       the circle map to which to add all of the drawn circles (which represent
   *                        the nodes).
   * @param nodes           the list of nodes to draw.
   * @param level           the level in the tree at which to draw the nodes.
   * @param overlappedNodes a map which contains all of the overlapped nodes.
   */
  private void drawNode(Pane pane, HashMap<Integer, NodeCircle> circleMap,
      ArrayList<AbstractNode> nodes, int level, HashMap<Integer, AbstractNode> overlappedNodes) {
    for (int i = 0; i < nodes.size(); i++) {
      AbstractNode node = nodes.get(i);
      double relativeHeight = (i + 0.5) / nodes.size();
      NodeCircle circle = new NodeCircle(NODE_RADIUS, relativeHeight);
      circle.setOnMouseClicked((MouseEvent event) -> {
        circle.setVisible(false);
      });
      pane.getChildren().add(circle);
      circleMap.put(node.getId(), circle);
      if (overlappedNodes.containsKey(node.getId())) {
        circle.setFill(OVERLAP_COLOR);
      } else {
        circle.setFill(NO_OVERLAP_COLOR);
      }
      circle.setCenterX(X_OFFSET * (level + 1));
      circle.centerYProperty().bind(pane.heightProperty().multiply(
          circle.getRelativeHeightProperty()));
      addLabel(pane, circle, node.getId());
    }
  }

  /**
   * Draw the edges between all of the nodes.
   *
   * @param pane      the pane to draw the edges in.
   * @param graph     the graph containing the nodes to draw edges between.
   * @param circleMap a map which maps each node id to the circle which represents the node in the
   *                  user interface.
   */
  private void drawEdges(Pane pane, ArrayList<GraphNodeOrder> graph,
      HashMap<Integer, NodeCircle> circleMap) {
    for (GraphNodeOrder nodeOrder : graph) {
      AbstractNode node = nodeOrder.getNode();
      Circle fromCircle = circleMap.get(node.getId());
      for (Integer outlink : node.getOutlinks()) {
        Circle toCircle = circleMap.get(outlink);
        Line edge = new Line();
        pane.getChildren().add(edge);
        edge.startXProperty().bind(fromCircle.centerXProperty());
        edge.startYProperty().bind(fromCircle.centerYProperty());
        edge.endXProperty().bind(toCircle.centerXProperty());
        edge.endYProperty().bind(toCircle.centerYProperty());
        edge.toBack();
      }
    }
  }

  /**
   * Reposition the nodes, so there are no overlapping (horizontal) edges. It is still possible for
   * non-horizontal edges to overlap, but this rarely happens.
   *
   * @param graph     the graph containing the nodes.
   * @param circleMap a map which maps each node id to a circle.
   */
  private void repositionOverlappingEdges(ArrayList<GraphNodeOrder> graph,
      HashMap<Integer, NodeCircle> circleMap) {
    for (GraphNodeOrder graphNode : graph) {
      AbstractNode node = graphNode.getNode();
      NodeCircle circle = circleMap.get(node.getId());
      double subtract = 0.5;
      while (true) {
        subtract /= 2.0;
        int sameHeight = 0;
        for (Integer inLink : node.getInlinks()) {
          NodeCircle parent = circleMap.get(inLink);
          if (Double.compare(parent.getRelativeHeightProperty().doubleValue(),
              circle.getRelativeHeightProperty().doubleValue()) == 0) {
            ++sameHeight;
          }
        }
        if (sameHeight < 2) {
          break;
        }
        circle.getRelativeHeightProperty().set(
            circle.getRelativeHeightProperty().doubleValue() - subtract);
      }
    }
  }

  /**
   * Add a label with the ID of the node to the circle.
   *
   * @param pane   the pane to add the label to.
   * @param circle the circle to which to add the label.
   * @param id     the id to write in the label.
   */
  private static void addLabel(Pane pane, Circle circle, int id) {
    Label label = new Label(Integer.toString(id));
    label.setMouseTransparent(true);
    label.layoutXProperty().bind(circle.centerXProperty().add(-circle.getRadius() + 3.0));
    label.layoutYProperty().bind(circle.centerYProperty().add(-circle.getRadius() / 2.0));
    label.setTextFill(Color.ALICEBLUE);
    pane.getChildren().add(label);
  }

  /**
   * Align the shared graph nodes, so when the top and bottom graph are drawn separately the
   * overlapping nodes are drawn at the same x coordinate.
   *
   * @param topOrder        the order of the nodes in the top graph.
   * @param bottomOrder     the order of the nodes in the bottom graph.
   * @param overlappedNodes the overlapping nodes.
   */
  private void alignOverlappingNodes(ArrayList<GraphNodeOrder> topOrder,
      ArrayList<GraphNodeOrder> bottomOrder, HashMap<Integer, AbstractNode> overlappedNodes) {
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
      if (overlappedNodes.containsKey(topNodeId)) {
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
  private Pair<GraphNodeOrder, Pair<Integer, Integer>> findMatchInBottomGraph(
      ArrayList<GraphNodeOrder> bottomOrder, int curBottomLevel, int startIndexOfCurBottomLevel,
      int topNodeId) {
    GraphNodeOrder bottomNode = null;
    int startIndexOfPrevBottomLevel = 0;
    for (int i = startIndexOfCurBottomLevel; i < bottomOrder.size(); i++) {
      bottomNode = bottomOrder.get(i);
      if (bottomNode.getLevel() > curBottomLevel) {
        curBottomLevel = bottomNode.getLevel();
        startIndexOfPrevBottomLevel = startIndexOfCurBottomLevel;
        startIndexOfCurBottomLevel = i;
      }
      if (bottomNode.getNode().getId() == topNodeId) {
        break;
      }
    }
    return new Pair(bottomNode, new Pair(startIndexOfPrevBottomLevel, curBottomLevel));
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
  private void alignOverlappingNode(ArrayList<GraphNodeOrder> topOrder,
      ArrayList<GraphNodeOrder> bottomOrder, int curTopLevel, int startIndexOfCurTopLevel,
      int curBottomLevel, int startIndexOfPrevBottomLevel, GraphNodeOrder topNode,
      GraphNodeOrder bottomNode) {
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
   * Calculate the order of the nodes of the graph, so there are no backward edges. The given array
   * list contains the nodes in order from left to right. Nodes which are at the same horizontal
   * position have sequential positions in the array list and have the same value for their level
   * field.
   *
   * @param graph the graph.
   * @return the node order.
   */
  private ArrayList<GraphNodeOrder> calculateGraphOrder(OriginalGraph graph) {
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
  private void updateReachedCount(HashMap<Integer, Integer> reachedCount,
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
  private HashMap<Integer, AbstractNode> getOverlappedNodes(OriginalGraph topGraph,
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
}
