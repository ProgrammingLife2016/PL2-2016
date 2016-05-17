package nl.tudelft.pl2016gr2.gui.view;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

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

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // default 'values'
    initilizeTreeBrowseButton();
    initilizeGraphBrowseButton();
  }

  private void initilizeTreeBrowseButton() {
    treeBrowseButton.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      File dir = new File(treeTextField.getText()).getAbsoluteFile().getParentFile();
      if (dir.exists()) {
        fileChooser.setInitialDirectory(dir);
      }
      fileChooser.setTitle("Open Resource File");
      File file = fileChooser.showOpenDialog(root.getScene().getWindow());
      if (file != null) {
        treeTextField.setText(file.getAbsolutePath());
      }
    });
  }

  private void initilizeGraphBrowseButton() {
    graphBrowseButton.setOnAction(event -> {
      FileChooser fileChooser = new FileChooser();
      File dir = new File(graphTextField.getText()).getAbsoluteFile().getParentFile();
      if (dir.exists()) {
        fileChooser.setInitialDirectory(dir);
      }
      fileChooser.setTitle("Open Resource File");
      File file = fileChooser.showOpenDialog(root.getScene().getWindow());
      if (file != null) {
        graphTextField.setText(file.getAbsolutePath());
      }
    });
  }

  public File getTreeFile() {
    return new File(treeTextField.getText());
  }

  public File getGraphFile() {
    return new File(graphTextField.getText());
  }

  public Button getOpenButton() {
    return openButton;
  }
}
