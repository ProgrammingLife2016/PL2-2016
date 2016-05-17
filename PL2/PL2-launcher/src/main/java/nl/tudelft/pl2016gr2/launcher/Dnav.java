package nl.tudelft.pl2016gr2.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.gui.model.PhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
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
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getClassLoader().getResource("pages/RootLayout.fxml"));

    Scene scene = new Scene(loader.load(), 1000, 800);
    primaryStage.setMinHeight(700);
    primaryStage.setMinWidth(600);
    primaryStage.setScene(scene);
    primaryStage.show();

    RootLayoutController controller = loader.getController();

    String treeFilename = "340tree.rooted.TKK.nwk";
    String graphFilename = "SMALL.gfa";

    insertData(controller, treeFilename, graphFilename);
  }

  /**
   * Load the data into the root layout.
   *
   * @param controller the controller of the root layout.
   * @param treeFilename filename of the tree you want to load.
   * @param graphFilename filename of the graph you want to load.
   */
  private void insertData(RootLayoutController controller, String treeFilename, String graphFilename) {

    // abusing NWKReader class as this class' classloader can access the correct resource
    Reader reader = new InputStreamReader(
        GfaReader.class.getClassLoader().getResourceAsStream(treeFilename));
    BufferedReader br = new BufferedReader(reader);
    TreeParser tp = new TreeParser(br);

    String treeName;
    int extensionOffset = treeFilename.lastIndexOf(".nwk");
    if (extensionOffset > 0) {
      treeName = treeFilename.substring(0, extensionOffset);
    } else {
      treeName = treeFilename;
    }
    tree = tp.tokenize(treeName);

    OriginalGraph graph = new GfaReader(graphFilename).read();

    controller.setData(new PhylogeneticTreeNode(tree.getRoot()), graph);
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
