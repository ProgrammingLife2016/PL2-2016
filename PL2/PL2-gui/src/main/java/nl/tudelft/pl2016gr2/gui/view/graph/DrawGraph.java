package nl.tudelft.pl2016gr2.gui.view.graph;

import static nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner.FILENAME;
import static nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner.GRAPH_SIZE;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.FullGfaReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is used to draw a graph in a pane.
 *
 * @author Faris
 */
public class DrawGraph {

  private static final double X_OFFSET = 50.0;

  /**
   * Load and draw a graph in the given pane.
   *
   * @param pane the pane in which to draw the graph.
   */
  public void drawGraph(Pane pane) {
    double paneHeight = 600.0;
    OriginalGraph graph = new FullGfaReader(FILENAME, GRAPH_SIZE).getGraph();

    HashMap<Integer, Node> nodes = graph.getNodes();
    HashMap<Integer, Circle> circles = drawNodes(pane, nodes);
    drawEdges(pane, nodes, circles);

    ArrayList<ArrayList<Node>> nodeDepths = createGraphDepth(graph.getRoot(), nodes);
    setNodeLocatios(nodeDepths, paneHeight, circles);
  }

  /**
   * Draw all of the nodes as circles.
   *
   * @param pane  the pane to draw the edges into.
   * @param nodes a hashmap containing all (id, node) pairs.
   * @return a hashmap containing all (id, circle) pairs, where the circle is the visual
   *         representation of the node.
   */
  private HashMap<Integer, Circle> drawNodes(Pane pane, HashMap<Integer, Node> nodes) {
    HashMap<Integer, Circle> circles = new HashMap<>();
    nodes.forEach((Integer id, Node node) -> {
      Circle circle = new Circle(15);
      circles.put(id, circle);
      pane.getChildren().add(circle);
      Label label = new Label(Integer.toString(node.getId()));
      label.layoutXProperty().bind(circle.centerXProperty().add(-circle.getRadius() + 3));
      label.layoutYProperty().bind(circle.centerYProperty().add(-circle.getRadius() / 2));
      label.setTextFill(new javafx.scene.paint.Color(1, 1, 1, 1));
      pane.getChildren().add(label);
    });
    return circles;
  }

  /**
   * Draw all edges between the nodes.
   *
   * @param pane    the pane to draw the edges into.
   * @param nodes   a hashmap containing all (id, node) pairs.
   * @param circles a hashmap containing all (id, circle) pairs.
   */
  private void drawEdges(Pane pane, HashMap<Integer, Node> nodes,
      HashMap<Integer, Circle> circles) {
    nodes.forEach((Integer id, Node from) -> {
      Circle fromCircle = circles.get(id);
      for (Integer outLink : from.getOutlinks()) {
        Circle toCircle = circles.get(outLink);
        Line edge = new Line();
        edge.startXProperty().bind(fromCircle.centerXProperty());
        edge.startYProperty().bind(fromCircle.centerYProperty());
        edge.endXProperty().bind(toCircle.centerXProperty());
        edge.endYProperty().bind(toCircle.centerYProperty());
        pane.getChildren().add(edge);
        edge.toBack();
        edge.setSmooth(true);
      }
    });
  }

  //        QuadCurve curve = new QuadCurve();
  //
  //        curve.setFill(Color.TRANSPARENT);
  //        curve.setStroke(Color.BLACK);
  //        curve.setStrokeWidth(2.0d);
  //
  //        fromCircle.centerXProperty().addListener((observable, oldValue, newValue) -> {
  //          curve.startXProperty().setValue(newValue);
  //          bendCurve(curve, fromCircle, toCircle);
  //        });
  //        fromCircle.centerYProperty().addListener((observable, oldValue, newValue) -> {
  //          curve.startYProperty().setValue(newValue);
  //          bendCurve(curve, fromCircle, toCircle);
  //        });
  //        toCircle.centerXProperty().addListener((observable, oldValue, newValue) -> {
  //          curve.endXProperty().setValue(newValue);
  //          bendCurve(curve, fromCircle, toCircle);
  //        });
  //        toCircle.centerYProperty().addListener((observable, oldValue, newValue) -> {
  //          curve.endYProperty().setValue(newValue);
  //          bendCurve(curve, fromCircle, toCircle);
  //        });
  //
  //        pane.getChildren().add(curve);
  //        curve.toBack();
  //  private static void bendCurve(QuadCurve curve, Circle from, Circle to) {
  //    Point2D perpP;
  //    if (from.getCenterY() > to.getCenterY()) {
  //      perpP = getPerpendicularPoint(from.getCenterX(), from.getCenterY(), to.getCenterX(),
  //              to.getCenterY(), 20.0d);
  //    } else {
  //      perpP = getPerpendicularPoint(to.getCenterX(), to.getCenterY(), from.getCenterX(),
  //              from.getCenterY(), 20.0d);
  //    }
  //
  //    curve.setControlX(perpP.getX());
  //    curve.setControlY(perpP.getY());
  //  }
  //
  //  /**
  //   * Note that 4(!) objects are created for a single invocation. This is considerably slower 
  //   * than a more efficient way of handling thing. (atleast until the JIT kicks in it seems)
  //   *
  //   * @param startX   x of "from" point
  //   * @param startY   y of "from" point
  //   * @param stopX    x of "to" point
  //   * @param stopY    y of "to" point
  //   * @param distance distance to offset the point
  //   * @return Point2D object representing the point
  //   */
  //  private static Point2D getPerpendicularPoint(double startX, double startY, double stopX,
  //          double stopY, double distance) {
  //    Point2D mid = new Point2D((startX + stopX) / 2, (startY + stopY) / 2);
  //    Point2D vector = new Point2D(startX - stopX, startY - stopY);
  //    Point2D normal = new Point2D(-vector.getY(), vector.getX()).normalize();
  //
  //    return new Point2D(mid.getX() + (distance * normal.getX()),
  //            mid.getY() + (distance * normal.getY()));
  //  }
  
  /**
   * Calculate the graph depth for each node.
   *
   * @param root  the root node.
   * @param nodes a hashmap containing all (id, node) pairs.
   * @return an list of lists, where the 2nd list contains all of the nodes at the depth of the tree
   *         which is equal to the index of the 2nd list in the first list.
   */
  private ArrayList<ArrayList<Node>> createGraphDepth(Node root,
      HashMap<Integer, Node> nodes) {
    HashMap<Integer, NodeDepth> nodeDepths = new HashMap<>();
    nodes.forEach((Integer id, Node node) -> {
      nodeDepths.put(id, new NodeDepth(node));
    });
    Set currentNode = new HashSet<>();
    currentNode.add(root.getId());
    int depth = 0;
    while (!currentNode.isEmpty()) {
      Set nextNodes = new HashSet();
      Iterator<Integer> it = currentNode.iterator();
      while (it.hasNext()) {
        int currentNodes = it.next();
        nodeDepths.get(currentNodes).depth = depth;
        nextNodes.addAll(nodes.get(currentNodes).getOutlinks());
      }
      currentNode = nextNodes;
      depth++;
    }
    ArrayList<ArrayList<Node>> res = new ArrayList<>();
    for (int i = 0; i < depth; i++) {
      res.add(new ArrayList<>());
    }
    nodeDepths.forEach((Integer id, NodeDepth nodeDepth) -> {
      res.get(nodeDepth.depth).add(nodeDepth.node);
    });
    return res;
  }

  /**
   * Set the x-coordinate of each circle representing a node according to the depth of the node in
   * the graph.
   *
   * @param nodeDepths a list of lists of nodes, where the inner lists are sorted according to depth
   *                   in the graph.
   * @param paneHeight the height of the pane in which the graph is drawn.
   * @param circles    all of the circles representing the nodes.
   */
  private void setNodeLocatios(ArrayList<ArrayList<Node>> nodeDepths,
      double paneHeight, HashMap<Integer, Circle> circles) {
    int xPos = 0;
    for (ArrayList<Node> nodeDepth : nodeDepths) {
      xPos += X_OFFSET;
      double nodeAreaHeight = paneHeight / nodeDepth.size();
      for (int i = 0; i < nodeDepth.size(); i++) {
        Node node = nodeDepth.get(i);
        double startY = nodeAreaHeight * i;
        double endY = startY + nodeAreaHeight;
        double centerY = (endY + startY) / 2.0;
        while (true) {
          int sameHeight = 0;
          for (Integer inLink : node.getInlinks()) {
            Circle parent = circles.get(inLink);
            if (Double.compare(parent.getCenterY(), centerY) == 0) {
              ++sameHeight;
            }
          }
          if (sameHeight < 2) {
            break;
          }
          centerY = (centerY + startY) / 2.0;
        }
        Circle circle = circles.get(node.getId());
        circle.setCenterX(xPos);
        circle.setCenterY(centerY);
      }
    }
  }

  /**
   * This class is used to decide the depth in the graph of a node.
   */
  private class NodeDepth {

    private final Node node;
    private int depth;

    /**
     * Create an instance of this class.
     *
     * @param node the node.
     */
    public NodeDepth(Node node) {
      this.node = node;
    }
  }
}
