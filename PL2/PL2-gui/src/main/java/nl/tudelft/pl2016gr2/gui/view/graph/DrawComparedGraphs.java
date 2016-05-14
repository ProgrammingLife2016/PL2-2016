package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.GraphNodeOrder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Draws two compared graphs above each other in a pane.
 *
 * @author Faris
 */
public class DrawComparedGraphs {

  private static final double X_OFFSET = 50.0;
  private static final double NODE_RADIUS = 15.0;
  private static final Color OVERLAP_COLOR = Color.rgb(0, 73, 73);
  private static final Color NO_OVERLAP_COLOR = Color.rgb(146, 0, 0);

  private final Pane bottomPane;
  private final Pane topPane;

  /**
   * Initialize the class by splitting the given pane into 2 parts.
   *
   * @param pane the pane in which to draw the graph.
   */
  public DrawComparedGraphs(Pane pane) {
    pane.getChildren().clear();
    topPane = new Pane();
    bottomPane = new Pane();
    pane.getChildren().addAll(topPane, bottomPane);
    topPane.prefHeightProperty().bind(pane.heightProperty().divide(2.0));
    bottomPane.prefHeightProperty().bind(pane.heightProperty().divide(2.0));
    bottomPane.layoutYProperty().bind(pane.heightProperty().divide(2.0));
  }

  /**
   * Draw the given graph in the given pane.
   *
   * @param pane  the pane to draw the graph in.
   * @param graph the graph to draw.
   */
  private static void drawGraph(Pane pane, ArrayList<GraphNodeOrder> graph) {
    HashMap<Integer, NodeCircle> circleMap = new HashMap<>();
    int curLevel = 0;
    ArrayList<GraphNodeOrder> levelNodes = new ArrayList<>();
    for (int i = 0; i < graph.size(); i++) {
      GraphNodeOrder node = graph.get(i);
      if (node.getLevel() == curLevel) {
        levelNodes.add(node);
      } else {
        drawNode(pane, circleMap, levelNodes, curLevel);
        curLevel = node.getLevel();
        levelNodes.clear();
        levelNodes.add(node);
      }
    }
    drawNode(pane, circleMap, levelNodes, curLevel);
    repositionOverlappingEdges(graph, circleMap);
    drawEdges(pane, graph, circleMap);
  }

  /**
   * Draw the given list of nodes as circles in the given pane.
   *
   * @param pane      the pane in which to draw the nodes.
   * @param circleMap the circle map to which to add all of the drawn circles (which represent the
   *                  nodes).
   * @param nodes     the list of nodes to draw.
   * @param level     the level in the tree at which to draw the nodes.
   */
  private static void drawNode(Pane pane, HashMap<Integer, NodeCircle> circleMap,
      ArrayList<GraphNodeOrder> nodes, int level) {
    for (int i = 0; i < nodes.size(); i++) {
      GraphNodeOrder graphNodeOrder = nodes.get(i);
      AbstractNode node = graphNodeOrder.getNode();
      double relativeHeight = (i + 0.5) / nodes.size();
      NodeCircle circle = new NodeCircle(NODE_RADIUS, relativeHeight);
      pane.getChildren().add(circle);
      circleMap.put(node.getId(), circle);
      if (graphNodeOrder.isOverlapping()) {
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
  private static void drawEdges(Pane pane, ArrayList<GraphNodeOrder> graph,
      HashMap<Integer, NodeCircle> circleMap) {
    graph.stream().map(nodeOrder -> nodeOrder.getNode()).forEach(node -> {
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
    });
  }

  /**
   * Reposition the nodes, so there are no overlapping (horizontal) edges. It is still possible for
   * non-horizontal edges to overlap, but this rarely happens.
   *
   * @param graph     the graph containing the nodes.
   * @param circleMap a map which maps each node id to a circle.
   */
  private static void repositionOverlappingEdges(ArrayList<GraphNodeOrder> graph,
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
   * Draw two graphs to compare.
   *
   * @param topGraph    the top graph.
   * @param bottomGraph the bottom graph.
   */
  public void drawGraphs(ArrayList<GraphNodeOrder> topGraph,
      ArrayList<GraphNodeOrder> bottomGraph) {
    topPane.getChildren().clear();
    bottomPane.getChildren().clear();
    drawGraph(topPane, topGraph);
    drawGraph(bottomPane, bottomGraph);
  }
}
