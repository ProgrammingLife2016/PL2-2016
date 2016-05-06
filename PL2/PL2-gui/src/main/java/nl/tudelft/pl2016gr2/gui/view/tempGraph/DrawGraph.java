package nl.tudelft.pl2016gr2.gui.view.tempGraph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.QuadCurve;
import javafx.stage.Stage;

import static nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner.FILENAME;
import static nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner.GRAPH_SIZE;
import nl.tudelft.pl2016gr2.core.algorithms.FilterSnips;
import nl.tudelft.pl2016gr2.model.AbstractNode;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.FullGfaReader;

/**
 *
 * @author faris
 */
public class DrawGraph {

	private static final double X_OFFSET = 50.0;

	public void drawGraph(Stage stage) {
		Pane pane = new Pane();
		ScrollPane scrollPane = new ScrollPane(pane);
		double paneHeight = 600;
		Scene scene = new Scene(scrollPane, 1000, paneHeight);
		stage.setMinHeight(400);
		stage.setMinWidth(600);
		stage.setScene(scene);
		stage.show();

		long s = System.currentTimeMillis();
		FullGfaReader r = new FullGfaReader(FILENAME, GRAPH_SIZE);
		OriginalGraph g = r.getGraph();
		System.out.println("Size of the graph: " + g.getSize());
		long e = System.currentTimeMillis();
		System.out.println("The loading took " + (e - s) + " milliseconds to run");
		s = e;
		
		FilterSnips filter = new FilterSnips(g);
		g = filter.filter();

		long f = System.currentTimeMillis();
		System.out.println("The algorithm took " + (f - e) + " milliseconds to run");

		HashMap<Integer, Node> bubbles = g.getNodes();
		HashMap<Integer, Circle> circles = drawCircles(pane, bubbles);
		drawEdges(pane, bubbles, circles);

		ArrayList<ArrayList<AbstractNode>> bubbleDepths = createGraphDepth(g.getRoot(), bubbles);
		setBubbleLocatios(bubbleDepths, paneHeight, circles);
		e = System.currentTimeMillis();
		System.out.println("Drawing took " + (e - s) + " milliseconds");
	}

	private void setBubbleLocatios(ArrayList<ArrayList<AbstractNode>> bubbleDepths,
			double paneHeight, HashMap<Integer, Circle> circles) {
		int xPos = 0;
		for (ArrayList<AbstractNode> bubbleDepth : bubbleDepths) {
			xPos += X_OFFSET;
			double bubbleAreaHeight = paneHeight / bubbleDepth.size();
			for (int i = 0; i < bubbleDepth.size(); i++) {
				AbstractNode bubble = bubbleDepth.get(i);
				double startY = bubbleAreaHeight * i;
				double endY = startY + bubbleAreaHeight;
				double centerY = (endY + startY) / 2.0;
				while (true) {
					int sameHeight = 0;
					for (Integer inLink : bubble.getInlinks()) {
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
				Circle c = circles.get(bubble.getId());
				c.setCenterX(xPos);
				c.setCenterY(centerY);
			}
		}
	}

	private HashMap<Integer, Circle> drawCircles(Pane pane, HashMap<Integer, Node> bubbles) {
		HashMap<Integer, Circle> circles = new HashMap<>();
		bubbles.forEach((Integer id, AbstractNode bubble) -> {
			Circle c = new Circle(15);
			circles.put(id, c);
			pane.getChildren().add(c);
			Label label = new Label(Integer.toString(bubble.getId()));
			label.layoutXProperty().bind(c.centerXProperty().add(-c.getRadius() + 3));
			label.layoutYProperty().bind(c.centerYProperty().add(-c.getRadius() / 2));
			label.setTextFill(new javafx.scene.paint.Color(1, 1, 1, 1));
			pane.getChildren().add(label);
		});
		return circles;
	}

	private void drawEdges(Pane pane, HashMap<Integer, Node> bubbles, HashMap<Integer, Circle> circles) {
		bubbles.forEach((Integer id, AbstractNode from) -> {
			Circle fromCircle = circles.get(id);
			for (Integer outLink : from.getOutlinks()) {
				Circle toCircle = circles.get(outLink);
				QuadCurve curve = new QuadCurve();

                curve.setFill(Color.TRANSPARENT);
                curve.setStroke(Color.BLACK);
                curve.setStrokeWidth(2.0d);

                fromCircle.centerXProperty().addListener((observable, oldValue, newValue) -> {
                    curve.startXProperty().setValue(newValue);
                    bendCurve(curve, fromCircle, toCircle);
                });
                fromCircle.centerYProperty().addListener((observable, oldValue, newValue) -> {
                    curve.startYProperty().setValue(newValue);
                    bendCurve(curve, fromCircle, toCircle);
                });
                toCircle.centerXProperty().addListener((observable, oldValue, newValue) -> {
                    curve.endXProperty().setValue(newValue);
                    bendCurve(curve, fromCircle, toCircle);
                });
                toCircle.centerYProperty().addListener((observable, oldValue, newValue) -> {
                    curve.endYProperty().setValue(newValue);
                    bendCurve(curve, fromCircle, toCircle);
                });

                pane.getChildren().add(curve);
				curve.toBack();
//				edge.setSmooth(true);
			}
		});
	}

    private static void bendCurve(QuadCurve curve, Circle from, Circle to) {
        Point2D perpP;
        if (from.getCenterY() > to.getCenterY()) {
            perpP = getPerpendicularPoint(from.getCenterX(), from.getCenterY(), to.getCenterX(), to.getCenterY(), 20.0d);
        } else {
            perpP = getPerpendicularPoint(to.getCenterX(), to.getCenterY(), from.getCenterX(), from.getCenterY(), 20.0d);
        }

        curve.setControlX(perpP.getX());
        curve.setControlY(perpP.getY());
    }

    /**
     *
     * Not that 4(!) objects are created for a single invocation. This is considerably slower than
     * a more efficient way of handling thing. (atleast until the JIT kicks in it seems)
     *
     * @param startX x of "from" point
     * @param startY y of "from" point
     * @param stopX x of "to" point
     * @param stopY y of "to" point
     * @param distance distance to offset the point
     * @return Point2D object representing the point
     */
    private static Point2D getPerpendicularPoint(double startX, double startY, double stopX, double stopY, double distance) {
        Point2D M = new Point2D((startX + stopX) / 2, (startY + stopY) / 2);
        Point2D p = new Point2D(startX - stopX, startY - stopY);
        Point2D n = new Point2D(-p.getY(), p.getX()).normalize();

        return new Point2D(M.getX() + (distance * n.getX()), M.getY() + (distance * n.getY()));
    }

	private ArrayList<ArrayList<AbstractNode>> createGraphDepth(Node root, HashMap<Integer, Node> bubbles) {
		HashMap<Integer, BubbleDepth> bubbleDepths = new HashMap<>();
		bubbles.forEach((Integer id, AbstractNode bubble) -> {
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
				nextBubbles.addAll(bubbles.get(currentBubble).getOutlinks());
			}
			currentBubbles = nextBubbles;
			depth++;
		}
		ArrayList<ArrayList<AbstractNode>> res = new ArrayList<>();
		for (int i = 0; i < depth; i++) {
			res.add(new ArrayList<>());
		}
		bubbleDepths.forEach((Integer id, BubbleDepth bubbleDepth) -> {
			res.get(bubbleDepth.depth).add(bubbleDepth.bubble);
		});
		return res;
	}

	private class BubbleDepth {

		private final AbstractNode bubble;
		private int depth;

		public BubbleDepth(AbstractNode bubble) {
			this.bubble = bubble;
		}
	}
}
