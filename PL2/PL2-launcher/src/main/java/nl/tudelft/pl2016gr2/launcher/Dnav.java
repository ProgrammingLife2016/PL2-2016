package nl.tudelft.pl2016gr2.launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.gui.model.PhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The launcher (main class) of the application.
 *
 * @author Faris
 */
public class Dnav extends Application {

  private Tree tree;

  /**
   * Start the application. This method is automatically called by JavaFX when the API is
   * initialized, after the call to launch(args) in the main method.
   *
   * @param primaryStage the primary stage of the application.
   * @throws java.io.IOException this exception occurs when the fxml isn't found.
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    RootLayoutController rootLayout = RootLayoutController.loadView();
    Scene scene = new Scene(rootLayout.getPane(), 1000, 800);
    primaryStage.setMinHeight(700);
    primaryStage.setMinWidth(600);
    primaryStage.setScene(scene);
    primaryStage.show();
    loadTree(rootLayout);
  }

  /**
   * Load the data into the root layout.
   *
   * @param controller the controller of the root layout.
   */
  private void loadTree(RootLayoutController controller) {
    Reader reader = new InputStreamReader(
        GfaReader.class.getClassLoader().getResourceAsStream("10tree_custom.rooted.TKK.nwk"));
    BufferedReader br = new BufferedReader(reader);
    TreeParser tp = new TreeParser(br);

    tree = tp.tokenize("10tree_custom.rooted.TKK.nwk");
    controller.setData(new PhylogeneticTreeNode(tree.getRoot()));
    try {
      reader.close();
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
