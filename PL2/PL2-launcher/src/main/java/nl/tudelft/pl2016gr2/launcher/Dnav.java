package nl.tudelft.pl2016gr2.launcher;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.core.algorithms.AlgoRunner;
import nl.tudelft.pl2016gr2.gui.model.TreeNode;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.Graph;
import nl.tudelft.pl2016gr2.parser.controller.GFAReader;

/**
 *
 * @author Faris
 */
public class Dnav extends Application {

	public static final String FILENAME = AlgoRunner.class.getClassLoader().getResource("TB10.gfa").getFile();
	public static final int GRAPH_SIZE = 8728;
	private static final double X_OFFSET = 50.0;

	/**
	 * Start the application. This method is automatically called by JavaFX when
	 * the API is initialized, after the call to launch(args) in the main
	 * method.
	 *
	 * @param primaryStage the primary stage of the application.
	 * @throws java.io.IOException this exception occurs when the fxml isn't
	 * found.
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
//		FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(getClass().getClassLoader().getResource("RootLayout.fxml"));
//
//		Scene scene = new Scene(loader.load(), 1000, 800);
//		RootLayoutController controller = loader.getController();
//		primaryStage.setMinHeight(400);
//		primaryStage.setMinWidth(600);
//		primaryStage.setScene(scene);
//		primaryStage.show();
//
//		insertData(controller);

		Pane pane = new Pane();
		ScrollPane scrollPane = new ScrollPane(pane);
		double paneHeight = 800;
		Scene scene = new Scene(scrollPane, 1000, 800);
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(600);
		primaryStage.setScene(scene);
		primaryStage.show();

		long s = System.currentTimeMillis();
		GFAReader r = new GFAReader(FILENAME, GRAPH_SIZE);
		Graph g = r.getGraph();
		System.out.println("Size of the graph: " + g.getSize());
		long e = System.currentTimeMillis();
		System.out.println("The loading took " + (e - s) + " milliseconds to run");
//		FindBubbles findBubbles = new FindBubbles(g);
//		findBubbles.calculateBubbles();
//		long f = System.currentTimeMillis();
//		System.out.println("The algorithm took " + (f - e) + " milliseconds to run");

		ArrayList<Bubble> bubbles = g.getNodes();
		bubbles.remove(0);
		ArrayList<Circle> circles = new ArrayList<>();
		// create a circle for each bubble
		for (Bubble bubble : bubbles) {
			Circle c = new Circle(15);
			circles.add(c);
			pane.getChildren().add(c);
			Label label = new Label(Integer.toString(bubble.getId()));
			label.layoutXProperty().bind(c.centerXProperty().add(-c.getRadius() / 2));
			label.layoutYProperty().bind(c.centerYProperty().add(-c.getRadius() / 2));
			label.setTextFill(new javafx.scene.paint.Color(1, 1, 1, 1));
			pane.getChildren().add(label);
		}
		// create lines for each edge between bubbles
		for (int i = 0; i < circles.size(); i++) {
			Bubble from = bubbles.get(i);
			Circle fromCircle = circles.get(i);
			for (Integer outLink : from.getOutLinks()) {
				Circle toCircle = circles.get(outLink - 1);
				Line edge = new Line();
				edge.startXProperty().bind(fromCircle.centerXProperty());
				edge.startYProperty().bind(fromCircle.centerYProperty());
				edge.endXProperty().bind(toCircle.centerXProperty());
				edge.endYProperty().bind(toCircle.centerYProperty());
				pane.getChildren().add(edge);
			}
		}
//		Circle rootCircle = circles.get(g.getRoot().getId() - 1);
//		rootCircle.setCenterX(X_OFFSET);
//		rootCircle.setCenterY(pane.getHeight() / 2.0);
		Set currentBubbles = new HashSet<>();
		currentBubbles.add(g.getRoot().getId() - 1);
		double curXPos = 0;
		while (!currentBubbles.isEmpty()) {
			curXPos += X_OFFSET;
			Set nextBubbles = new HashSet();
//			ArrayList<Integer> nextBubbles = new ArrayList<>();
			double bubbleAreaHeight = paneHeight / currentBubbles.size();
			Iterator<Integer> it = currentBubbles.iterator();
			for (int i = 0; it.hasNext(); i++) {
				int currentBubble = it.next() - 1;
				Circle c = circles.get(currentBubble);
				double startY = bubbleAreaHeight * i;
				double endY = startY + bubbleAreaHeight;
				double centerY = (endY + startY) / 2.0;
				c.setCenterY(centerY);
				c.setCenterX(curXPos);
				nextBubbles.addAll(setBubblePositions(
						bubbles.get(currentBubble), circles, curXPos, centerY
				));
			}
			currentBubbles = nextBubbles;
		}
	}

	private ArrayList<Integer> setBubblePositions(Bubble currentBubble,
			ArrayList<Circle> circles, double xPos, double yPos) {
		Circle c = circles.get(currentBubble.getId() - 1);
		c.setCenterX(xPos);
		c.setCenterY(yPos);
		return currentBubble.getOutLinks();
	}

	private void insertData(RootLayoutController controller) {
		controller.setData(TreeNode.createRandomGraph(10, 2));
	}

	/**
	 * Launch the application.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
