package nl.tudelft.pl2016gr2.gui.view.graph;

import static nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner.FILENAME;
import static nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner.GRAPH_SIZE;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.Graph;
import nl.tudelft.pl2016gr2.parser.controller.GFAReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class is used to draw a graph in a pane.
 *
 * @author faris
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
    Graph graph = new GFAReader(FILENAME, GRAPH_SIZE).getGraph();

    HashMap<Integer, Bubble> bubbles = new HashMap<>();
    ArrayList<Bubble> rawBubbles = graph.getNodes();
    for (int i = 1; i < rawBubbles.size(); i++) { // skip first
      bubbles.put(rawBubbles.get(i).getId(), rawBubbles.get(i));
    }
    HashMap<Integer, Circle> circles = drawNodes(pane, bubbles);
    drawEdges(pane, bubbles, circles);

    ArrayList<ArrayList<Bubble>> bubbleDepths = createGraphDepth(graph.getRoot(), bubbles);
    setBubbleLocatios(bubbleDepths, paneHeight, circles);
  }

  /**
   * Draw all of the nodes as circles.
   *
   * @param pane    the pane to draw the edges into.
   * @param bubbles a hashmap containing all (id, bubble) pairs.
   * @return a hashmap containing all (id, circle) pairs, where the circle is the visual
   *         representation of the bubble.
   */
  private HashMap<Integer, Circle> drawNodes(Pane pane, HashMap<Integer, Bubble> bubbles) {
    HashMap<Integer, Circle> circles = new HashMap<>();
    bubbles.forEach((Integer id, Bubble bubble) -> {
      Circle circle = new Circle(15);
      circles.put(id, circle);
      pane.getChildren().add(circle);
      Label label = new Label(Integer.toString(bubble.getId()));
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
   * @param bubbles a hashmap containing all (id, bubble) pairs.
   * @param circles a hashmap containing all (id, circle) pairs.
   */
  private void drawEdges(Pane pane, HashMap<Integer, Bubble> bubbles,
          HashMap<Integer, Circle> circles) {
    bubbles.forEach((Integer id, Bubble from) -> {
      Circle fromCircle = circles.get(id);
      for (Integer outLink : from.getOutLinks()) {
        Circle toCircle = circles.get(outLink);
        Line edge = new Line();
        edge.startXProperty().bind(fromCircle.centerXProperty());
        edge.startYProperty().bind(fromCircle.centerYProperty());
        edge.endXProperty().bind(toCircle.centerXProperty());
        edge.endYProperty().bind(toCircle.centerYProperty());
        pane.getChildren().add(edge);
        edge.toBack();
        edge.setSmooth(true);

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
      }
    });
  }

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
   * Calculate the graph depth for each bubble.
   *
   * @param root    the root bubble.
   * @param bubbles a hashmap containing all (id, bubble) pairs.
   * @return an list of lists, where the 2nd list contains all of the bubbles at the depth of the
   *         tree which is equal to the index of the 2nd list in the first list.
   */
  private ArrayList<ArrayList<Bubble>> createGraphDepth(Bubble root,
          HashMap<Integer, Bubble> bubbles) {
    HashMap<Integer, BubbleDepth> bubbleDepths = new HashMap<>();
    bubbles.forEach((Integer id, Bubble bubble) -> {
      bubbleDepths.put(id, new BubbleDepth(bubble));
    });
    Set currentBubbles = new HashSet<>();
    currentBubbles.add(root.getId());
    int depth = 0;
    while (!currentBubbles.isEmpty()) {
      Set nextBubbles = new HashSet();
      Iterator<Integer> it = currentBubbles.iterator();
      while (it.hasNext()) {
        int currentBubble = it.next();
        bubbleDepths.get(currentBubble).depth = depth;
        nextBubbles.addAll(bubbles.get(currentBubble).getOutLinks());
      }
      currentBubbles = nextBubbles;
      depth++;
    }
    ArrayList<ArrayList<Bubble>> res = new ArrayList<>();
    for (int i = 0; i < depth; i++) {
      res.add(new ArrayList<>());
    }
    bubbleDepths.forEach((Integer id, BubbleDepth bubbleDepth) -> {
      res.get(bubbleDepth.depth).add(bubbleDepth.bubble);
    });
    return res;
  }

  /**
   * Set the x-coordinate of each circle representing a bubble according to the depth of the bubble
   * in the graph.
   *
   * @param bubbleDepths a list of lists of bubbles, where the inner lists are sorted according to
   *                     depth in the graph.
   * @param paneHeight   the height of the pane in which the graph is drawn.
   * @param circles      all of the circles representing the bubbles.
   */
  private void setBubbleLocatios(ArrayList<ArrayList<Bubble>> bubbleDepths,
          double paneHeight, HashMap<Integer, Circle> circles) {
    int xPos = 0;
    for (ArrayList<Bubble> bubbleDepth : bubbleDepths) {
      xPos += X_OFFSET;
      double bubbleAreaHeight = paneHeight / bubbleDepth.size();
      for (int i = 0; i < bubbleDepth.size(); i++) {
        Bubble bubble = bubbleDepth.get(i);
        double startY = bubbleAreaHeight * i;
        double endY = startY + bubbleAreaHeight;
        double centerY = (endY + startY) / 2.0;
        while (true) {
          int sameHeight = 0;
          for (Integer inLink : bubble.getInLinks()) {
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
        Circle circle = circles.get(bubble.getId());
        circle.setCenterX(xPos);
        circle.setCenterY(centerY);
      }
    }
  }

  /**
   * This class is used to decide the depth in the graph of a bubble.
   */
  private class BubbleDepth {

    private final Bubble bubble;
    private int depth;

    /**
     * Create an instance of this class.
     *
     * @param bubble the bubble.
     */
    public BubbleDepth(Bubble bubble) {
      this.bubble = bubble;
    }
  }
}
