package nl.tudelft.pl2016gr2.gui.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

public class FileChooserController implements Initializable {

  private static final Preferences PREFERENCES
      = Preferences.userNodeForPackage(FileChooserController.class);
  private static final String PREF_KEY_WORKSPACE = "last_used_workspace";

  @FXML
  private Node root;

  @FXML
  private ComboBox<File> treeComboBox;
  @FXML
  private ComboBox<File> graphComboBox;
  @FXML
  private ComboBox<File> metadataComboBox;
  @FXML
  private ComboBox<File> annotationComboBox;

  @FXML
  private Button workspaceBrowseButton;

  private File workspaceFile;

  private final File browseFile = new File("");

  @FXML
  private Button openButton;

  private Stage stage;

  private InputFileConsumer inputFileConsumer;

  /**
   * Initializes an instance of the FileChooserController for the given window+stage.
   *
   * @param ownerWindow The window over which you want to show the dialog.
   * @return A (javafx) pair containing the controller and stage respectively.
   * @throws IOException When fxml is not found
   */
  public static FileChooserController initialize(Window ownerWindow)
      throws IOException {
    Stage stage = new Stage();
    FXMLLoader loader = new FXMLLoader(
        FileChooserController.class.getClassLoader().getResource("pages/FileChooser.fxml"));

    Parent root = loader.load();

    stage.setScene(new Scene(root));
    stage.setTitle("Select files to load");
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initOwner(ownerWindow);

    FileChooserController controller = loader.getController();

    controller.stage = stage;

    return controller;
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // TODO get file from user prefs oid
    String lastWorkspaceFileString = PREFERENCES.get(PREF_KEY_WORKSPACE, null);
    if (lastWorkspaceFileString != null) {
      File lastWorkspaceFile = new File(lastWorkspaceFileString);
      if (lastWorkspaceFile.exists()) {
        this.workspaceFile = lastWorkspaceFile;
      }
    }
    initializeOpenButton();
    initializeBrowseButton();
    initializeComboBoxes();
  }

  /**
   * Initialize the open button action event handler.
   */
  private void initializeOpenButton() {
    openButton.setOnAction(event -> {
      final File treeFile = treeComboBox.getSelectionModel().getSelectedItem();
      final File graphFile = graphComboBox.getSelectionModel().getSelectedItem();
      final File metadataFile = metadataComboBox.getSelectionModel().getSelectedItem();
      final File annotationFile = annotationComboBox.getSelectionModel().getSelectedItem();
      boolean treeExists = checkFileExistsOrShowAlert(treeFile,
          "File for the tree does not exist or none selected");
      boolean graphExists = checkFileExistsOrShowAlert(graphFile,
          "File for the graph does not exist or none selected");
      boolean metadataExists = checkFileExistsOrShowAlert(metadataFile,
          "File for the metadata does not exist or none selected");
      boolean annotationExists = checkFileExistsOrShowAlert(annotationFile,
          "File for the annotation does not exist or none selected");
      if (treeExists && graphExists && metadataExists && annotationExists) {
        if (workspaceFile != null && workspaceFile.exists()) {
          PREFERENCES.put(PREF_KEY_WORKSPACE, workspaceFile.getAbsolutePath());
        }
        if (inputFileConsumer != null) {
          loadFiles(treeFile, graphFile, metadataFile, annotationFile);
        }
        getStage().close();
      }
    });
  }

  /**
   * Load the files.
   *
   * @param treeFile     the tree file.
   * @param graphFile    the graph file.
   * @param metadataFile the meta data file.
   * @param annotationFile the annotation file.
   */
  private void loadFiles(File treeFile, File graphFile, File metadataFile, File annotationFile) {
    try {
      inputFileConsumer.filesLoaded(
          new FileInputStream(treeFile),
          new FileInputStream(graphFile),
          new FileInputStream(metadataFile),
          new FileInputStream(annotationFile));
    } catch (FileNotFoundException ex) {
      Logger.getLogger(FileChooserController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Check if the file exists and if it doesn't, show the alert.
   *
   * @param file    the file to check for existence.
   * @param message the message to show if the file doesn't exist.
   * @return if the file exists.
   */
  private boolean checkFileExistsOrShowAlert(File file, String message) {
    if (file == null || !file.exists() || !file.isFile()) {
      showErrorDialog("Error", message);
      return false;
    }
    return true;
  }

  /**
   * Initialize the browse button action event handler.
   */
  private void initializeBrowseButton() {
    workspaceBrowseButton.setOnAction(event -> {
      DirectoryChooser dirChooser = new DirectoryChooser();
      // TODO: select last openend workspace?
      dirChooser.setTitle("Select workspace directory");
      File workspaceFile = dirChooser.showDialog(root.getScene().getWindow());
      if (workspaceFile != null) {
        this.workspaceFile = workspaceFile;
        updateWorkspace();
      }
    });
  }

  /**
   * Initialize the cell factories of the combo boxes.
   */
  private void initializeComboBoxes() {
    Stream.of(treeComboBox, graphComboBox, metadataComboBox, annotationComboBox)
        .forEach(comboBox -> {
          comboBox.setCellFactory(getCellFactory());
          comboBox.getSelectionModel()
              .selectedItemProperty().addListener(getComboBoxChangeListener(comboBox));
        });
    updateWorkspace();
  }

  /**
   * Creates a {@link Callback} instance for use as a Cell factory for the ComboBoxes.
   *
   * <p>
   * The returned cellfactory will show the path of the file, and has an exception for empty and the
   * browseFile (for which it will list "Empty" and "browse..." respectively).
   * </p>
   *
   * @return the Callback to use for the ComboBox
   */
  private Callback<ListView<File>, ListCell<File>> getCellFactory() {
    return param -> new ListCell<File>() {
      @Override
      protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);
        if (browseFile == item) {
          setText("browse...");
        } else if (empty) {
          setText("Empty");
        } else {
          setText(item.getAbsolutePath());
        }
      }
    };
  }

  /**
   * Returns a File that can be used as starting directory for the file dialog.
   *
   * <p>
   * Either returns the given parameter, or its parent. When the file doesn't exist or not a
   * directory the working directory is returned.
   * </p>
   *
   * @param file file to begin
   * @return File that should be a directory
   */
  private File getSafeStartDir(File file) {
    if (file != null && file.exists()) {
      if (file.isFile()) {
        file = file.getAbsoluteFile().getParentFile();
      }
    } else {
      file = new File("").getAbsoluteFile();
    }
    return file;
  }

  /**
   * Creates an {@link ChangeListener} instance that looks for the special {@link #browseFile} and
   * opens a dialog when its selected.
   *
   *
   * @param comboBox The combobox this is set to. Needed to revert or select new value.
   * @return The {@link ChangeListener} that can be used.
   */
  private ChangeListener<File> getComboBoxChangeListener(ComboBox<File> comboBox) {
    return (observable, oldValue, newValue) -> {
      if (newValue == browseFile) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(getSafeStartDir(oldValue));
        fileChooser.setTitle("Select file");

        File file = fileChooser.showOpenDialog(root.getScene().getWindow());

        // need to give the JavaFX thread some time, otherwise
        // a weird race condition between returning from this method
        // and informing Observers of the data set occurs.
        Platform.runLater(() -> {
          if (file != null) {
            ObservableList<File> items = comboBox.getItems();
            items.add(0, file);
            comboBox.getSelectionModel().select(0);
          } else {
            comboBox.getSelectionModel().select(oldValue);
          }
        });

      }
    };
  }

  /**
   * Update the shown files when a new workspace has been set.
   */
  private void updateWorkspace() {
    updateWorkspaceForComboBox(".nwk", treeComboBox);
    updateWorkspaceForComboBox(".gfa", graphComboBox);
    updateWorkspaceForComboBox(".xlsx", metadataComboBox);
    updateWorkspaceForComboBox(".gff", annotationComboBox);
  }

  /**
   * Updates a combobox for a new workspace.
   *
   * <p>
   * This method sets the items that are found in the new workspace.
   * </p>
   *
   * @param extension Extension to look for in the workspace.
   * @param comboBox  the combobox in question.
   */
  private void updateWorkspaceForComboBox(String extension, ComboBox<File> comboBox) {

    final File oldSelection = comboBox.getSelectionModel().getSelectedItem();

    File[] files = findFilesInWorkspace(extension);
    ObservableList<File> items = FXCollections.observableArrayList();
    items.addAll(files);
    items.add(browseFile);
    comboBox.setItems(items);

    // select last selection when it existed only when no file
    // with correct extension is found in new workspace
    if (items.size() > 1) {
      comboBox.getSelectionModel().select(0);
    } else if (oldSelection != null) {
      items.add(0, oldSelection);
      comboBox.getSelectionModel().select(oldSelection);
    }

  }

  /**
   * Shows a simple error dialog.
   */
  private void showErrorDialog(String title, String content) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText("");
    alert.setContentText(content);
    alert.show();
  }

  /**
   * Returns an array of files with a given extension in the {@link #workspaceFile}.
   *
   * @param extension The extension to look for
   * @return File[] File array with the files found
   */
  private File[] findFilesInWorkspace(String extension) {
    if (workspaceFile != null && workspaceFile.exists()) {
      return workspaceFile.listFiles((dir, name) -> name.endsWith(extension));
    }
    return new File[0];
  }

  /**
   * Set the input file consumer.
   *
   * @param inputFileConsumer the input file consumer.
   */
  public void setInputFileConsumer(InputFileConsumer inputFileConsumer) {
    this.inputFileConsumer = inputFileConsumer;
  }

  /**
   * Get the stage of the file chooser window.
   *
   * @return the stage of the file chooser window.
   */
  public Stage getStage() {
    return this.stage;
  }

  /**
   * The interface of the input file consumer.
   */
  public interface InputFileConsumer {

    /**
     * Load the given files.
     *
     * @param treeFile     the file of the phylogenetic tree.
     * @param graphFile    the file of the graph.
     * @param metadataFile the file of the metadata.
     * @param annotationFile the file of the annotations.
     */
    void filesLoaded(InputStream treeFile, InputStream graphFile,
                     InputStream metadataFile, InputStream annotationFile);
  }
}
