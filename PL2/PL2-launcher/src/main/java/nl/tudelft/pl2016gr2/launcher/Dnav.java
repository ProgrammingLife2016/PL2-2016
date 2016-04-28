package nl.tudelft.pl2016gr2.launcher;

import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.gui.model.TreeNode;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.gui.view.tempGraph.DrawGraph;

/**
 *
 * @author Faris
 */
public class Dnav extends Application {

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

		new DrawGraph().drawGraph(primaryStage);
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
