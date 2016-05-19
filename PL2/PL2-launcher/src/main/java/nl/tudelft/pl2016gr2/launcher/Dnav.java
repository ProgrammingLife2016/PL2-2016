package nl.tudelft.pl2016gr2.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.UserConfiguration;
import nl.tudelft.pl2016gr2.core.FileGraphFactory;
import nl.tudelft.pl2016gr2.core.FileTreeFactory;
import nl.tudelft.pl2016gr2.gui.view.FileChooserController;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The launcher (main class) of the application.
 *
 * @author Faris
 */
public class Dnav extends Application {

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

    showFilechooser(loader.getController(), primaryStage);

  }

  private void showFilechooser(RootLayoutController controller,
                               Stage primaryStage) throws IOException {

    Pair<FileChooserController, Stage> result = FileChooserController.initialize(
        primaryStage.getScene().getWindow());
    result.getValue().showAndWait();
    File treeFile = result.getKey().getTreeFile();
    File graphFile = result.getKey().getGraphFile();
    if (!treeFile.exists() || !treeFile.isFile() || !treeFile.exists() || !treeFile.isFile()) {
      Logger.getLogger(Dnav.class.getName()).log(Level.SEVERE,
          "File does not exist or is not a file!");
    } else {
      Tree tree = new FileTreeFactory(treeFile).getTree();
      OriginalGraph graph = new FileGraphFactory(graphFile).getGraph();
      controller.setData(tree, graph);
      // everything seems to be fine, lets remember the file-paths
      UserConfiguration.preferences.put(
          UserConfiguration.USER_CONFIG_TREE_PATH, treeFile.getAbsolutePath());
      UserConfiguration.preferences.put(
          UserConfiguration.USER_CONFIG_GRAPH_PATH, graphFile.getAbsolutePath());
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
