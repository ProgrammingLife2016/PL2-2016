package nl.tudelft.pl2016gr2.gui.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import nl.tudelft.pl2016gr2.UserConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileChooserController implements Initializable {

  @FXML
  private Node root;

  @FXML
  private TextField treeTextField;
  @FXML
  private TextField graphTextField;
  @FXML
  private Button treeBrowseButton;
  @FXML
  private Button graphBrowseButton;
  @FXML
  private Button openButton;
  private Stage stage;

  /**
   * Initializes an instance of the FileChooserController for the given window+stage.
   *
   * @param ownerWindow The window over which you want to show the dialog.
   * @return A (javafx) pair containing the controller and stage respectively.
   * @throws IOException When fxml is not found
   */
  public static Pair<FileChooserController, Stage> initialize(Window ownerWindow)
      throws IOException {
    Stage stage = new Stage();
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(
        FileChooserController.class.getClassLoader().getResource("pages/FileChooser.fxml"));
    Parent root = loader.load();

    stage.setScene(new Scene(root));
    stage.setTitle("Select files to load");
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initOwner(ownerWindow);

    FileChooserController controller = loader.getController();
    controller.setStage(stage);

    return new Pair<>(controller, stage);
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    treeTextField.setText(UserConfiguration.preferences.get(
        UserConfiguration.USER_CONFIG_TREE_PATH,
        new File("").getAbsolutePath()));
    graphTextField.setText(UserConfiguration.preferences.get(
        UserConfiguration.USER_CONFIG_GRAPH_PATH,
        new File("").getAbsolutePath()));
    initializeBrowseButtons();
    initializeOpenButton();

  }

  private void initializeOpenButton() {
    openButton.setOnAction(event -> {
      String error =
          getTreeFile().exists()
              ? getGraphFile().exists()
              ? null
              : "Graph file not found"
              : "Tree file not found";
      if (error == null) {
        if (stage != null) {
          stage.close();
        } else {
          Logger.getLogger(FileChooserController.class.getName()).log(
              Level.SEVERE,
              "Please call setStage when using this controller");
        }
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("File not found");
        alert.setHeaderText(error);
        alert.setContentText("Please choose an existing file");
        alert.showAndWait();
      }
    });
  }

  /**
   * Creates an eventHandler for the buttons to use.
   *
   * <p>
   *    The following folder is shown, the text in the TextField is
   *    used as path and:
   *    When the file exists and is a file, its parent directory is shown.
   *    When the file exists and is a directory, that 'file' is shown.
   *    Otherwise the working directory of the program (usually 'repo/PL2') is shown.
   * </p>
   * @param textField the textfield it should check (corresponding to button)
   */
  private EventHandler<ActionEvent> getEventHandler(TextField textField) {
    return event -> {
      FileChooser fileChooser = new FileChooser();
      File dir = new File(textField.getText());
      if (dir.exists()) {
        if (dir.isFile()) {
          dir = new File(textField.getText()).getAbsoluteFile().getParentFile();
        }
      } else {
        dir = new File("").getAbsoluteFile();
      }
      if (dir.exists()) {
        fileChooser.setInitialDirectory(dir);
      }
      fileChooser.setTitle("Open tree File");
      File file = fileChooser.showOpenDialog(root.getScene().getWindow());
      if (file != null) {
        textField.setText(file.getAbsolutePath());
      }
    };
  }

  private void initializeBrowseButtons() {
    treeBrowseButton.setOnAction(getEventHandler(treeTextField));
    graphBrowseButton.setOnAction(getEventHandler(graphTextField));
  }


  public File getTreeFile() {
    return new File(treeTextField.getText());
  }

  public File getGraphFile() {
    return new File(graphTextField.getText());
  }

  public void setStage(Stage stage) {
    this.stage = stage;
  }
}
