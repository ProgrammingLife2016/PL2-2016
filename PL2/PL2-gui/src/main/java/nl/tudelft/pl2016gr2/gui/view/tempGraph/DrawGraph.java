package nl.tudelft.pl2016gr2.gui.view.tempGraph;

import static nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner.FILENAME;
import static nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner.GRAPH_SIZE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.GFAReader;

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
		GFAReader r = new GFAReader(FILENAME, GRAPH_SIZE);
		OriginalGraph g = r.getGraph();
		System.out.println("Size of the graph: " + g.getSize());
		long e = System.currentTimeMillis();
		System.out.println("The loading took " + (e - s) + " milliseconds to run");
		s = e;
//		FindBubbles findBubbles = new FindBubbles(g);
//		findBubbles.calculateBubbles();
//		long f = System.currentTimeMillis();
//		System.out.println("The algorithm took " + (f - e) + " milliseconds to run");

		HashMap<Integer, Node> bubbles = g.getNodes();
//		ArrayList<AbstractNode> rawBubbles = g.getNodes();
//		for (int i = 1; i < rawBubbles.size(); i++) { // skip first
//			bubbles.put(rawBubbles.get(i).getId(), rawBubbles.get(i));
//		}
		HashMap<Integer, Circle> circles = drawCircles(pane, bubbles);
		drawEdges(pane, bubbles, circles);

		ArrayList<ArrayList<Node>> bubbleDepths = createGraphDepth(g.getRoot(), bubbles);
		setBubbleLocatios(bubbleDepths, paneHeight, circles);
		e = System.currentTimeMillis();
		System.out.println("Drawing took " + (e - s) + " milliseconds");
	}

	private void setBubbleLocatios(ArrayList<ArrayList<Node>> bubbleDepths,
			double paneHeight, HashMap<Integer, Circle> circles) {
		int xPos = 0;
		for (ArrayList<Node> bubbleDepth : bubbleDepths) {
			xPos += X_OFFSET;
			double bubbleAreaHeight = paneHeight / bubbleDepth.size();
			for (int i = 0; i < bubbleDepth.size(); i++) {
				Node bubble = bubbleDepth.get(i);
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
		bubbles.forEach((Integer id, Node bubble) -> {
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
		bubbles.forEach((Integer id, Node from) -> {
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
//				edge.setSmooth(true);
			}
		});
	}

	private ArrayList<ArrayList<Node>> createGraphDepth(Node root, HashMap<Integer, Node> bubbles) {
		HashMap<Integer, BubbleDepth> bubbleDepths = new HashMap<>();
		bubbles.forEach((Integer id, Node bubble) -> {
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
		ArrayList<ArrayList<Node>> res = new ArrayList<>();
		for (int i = 0; i < depth; i++) {
			res.add(new ArrayList<>());
		}
		bubbleDepths.forEach((Integer id, BubbleDepth bubbleDepth) -> {
			res.get(bubbleDepth.depth).add(bubbleDepth.bubble);
		});
		return res;
	}

	private class BubbleDepth {

		private final Node bubble;
		private int depth;

		public BubbleDepth(Node bubble) {
			this.bubble = bubble;
		}
	}
}
