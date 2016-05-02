package nl.tudelft.pl2016gr2.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.gui.model.PhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.gui.view.tempGraph.DrawGraph;
import nl.tudelft.pl2016gr2.parser.controller.GFAReader;

/**
 *
 * @author Faris
 */
public class Dnav extends Application {

	private Tree tree;

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
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getClassLoader().getResource("RootLayout.fxml"));

		Scene scene = new Scene(loader.load(), 1000, 800);
		RootLayoutController controller = loader.getController();
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(600);
		primaryStage.setScene(scene);
		primaryStage.show();

		insertData(controller);

		new DrawGraph().drawGraph(new Stage());
	}

	private void insertData(RootLayoutController controller) {

		// abusing NWKReader class as this class' classloader can access the correct resource
		Reader r = new InputStreamReader(GFAReader.class.getClassLoader().getResourceAsStream("340tree.rooted.TKK.nwk"));

		BufferedReader br = new BufferedReader(r);
		TreeParser tp = new TreeParser(br);

		tree = tp.tokenize("340tree.rooted.TKK");
		controller.setData(new PhylogeneticTreeNode(tree.getRoot()));
		try {
			r.close();
		} catch (IOException ex) {
			Logger.getLogger(Dnav.class.getName()).log(Level.SEVERE, null, ex);
		}
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
