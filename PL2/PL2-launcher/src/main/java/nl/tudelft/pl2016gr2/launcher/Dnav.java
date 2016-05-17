package nl.tudelft.pl2016gr2.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.gui.model.PhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.FileChooserController;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

    FileChooserController fileChooserController =
        showFilechooserPopup(primaryStage.getScene().getWindow());

    File treeFile = fileChooserController.getTreeFile();
    File graphFile = fileChooserController.getGraphFile();

    if (treeFile.exists() && graphFile.exists()) {
      insertData(controller, treeFile, graphFile);
    }
  }

  /**
   * Shows a file chooser dialog
   *
   * @param ownerWindow The window of the owner Node, usually the primary stages scene window
   * @return The popups controller, can be used to retrieve selections form.
   * @throws java.io.IOException this exception occurs when the fxml isn't found.
   */
  private FileChooserController showFilechooserPopup(Window ownerWindow) throws IOException {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getClassLoader().getResource("pages/FileChooser.fxml"));
    Parent root = loader.load();

    Stage popupStage = new Stage();
    popupStage.setScene(new Scene(root));
    popupStage.setTitle("Select files to load");
    popupStage.initModality(Modality.APPLICATION_MODAL);
    popupStage.initOwner(ownerWindow);

    FileChooserController controller = loader.getController();
    controller.getOpenButton().setOnAction(event -> {
        String error =
            controller.getTreeFile().exists() ?
            controller.getGraphFile().exists() ?
                null :
                "Graph file not found" :
                "Tree file not found";
        if (error == null) {
          popupStage.close();
        } else {
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("File not found");
          alert.setHeaderText(error);
          alert.setContentText("Please choose an existing file");
          alert.showAndWait();
          return;
        }

    });

    popupStage.showAndWait();
    return controller;
  }

  /**
   * Load the data into the root layout.
   *
   * @param controller the controller of the root layout.
   * @param treeFile file of the tree you want to load.
   * @param graphFile file of the graph you want to load.
   */
  private void insertData(RootLayoutController controller,
                          File treeFile,
                          File graphFile) throws FileNotFoundException {
    // abusing NWKReader class as this class' classloader can access the correct resource
    Reader reader = new InputStreamReader(new FileInputStream(treeFile));
    BufferedReader br = new BufferedReader(reader);
    TreeParser tp = new TreeParser(br);

    tree = tp.tokenize("Tree");

    OriginalGraph graph = new GfaReader(graphFile).read();

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
