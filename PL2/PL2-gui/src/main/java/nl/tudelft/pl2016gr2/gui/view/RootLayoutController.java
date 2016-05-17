package nl.tudelft.pl2016gr2.gui.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.events.GraphicsChangedEvent;
import nl.tudelft.pl2016gr2.gui.view.graph.DrawGraph;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeManager;
import nl.tudelft.pl2016gr2.gui.view.tree.heatmap.HeatmapManager;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

/**
 * FXML Controller class.
 *
 * @author Faris
 */
public class RootLayoutController implements Initializable {

  @FXML
  private Node rootLayoutNode;
  @FXML
  private Pane treePane;
  @FXML
  private Pane heatmapPane;
  @FXML
  private Pane selectionDescriptionPane;
  @FXML
  private Button zoomOutButton;
  @FXML
  private StackPane locationIdentifierPane;
  @FXML
  private Rectangle locationIdentifierRectangle;
  @FXML
  private ImageView treeIcon;
  @FXML
  private ImageView graphIcon;
  @FXML
  private SplitPane mainPane;
  private final Pane graphPane = new Pane();

  @FXML
  private MenuItem menuFileOpen;

  private static RootLayoutController controller;
  private TreeManager treeManager;
  private HeatmapManager heatmapManager;
  private SelectionManager selectionManager;
  private boolean zoomOutButtonDisabled = true;

  /**
   * Initializes the controller class.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    assert (controller == null);
    controller = this;
    heatmapManager = new HeatmapManager(heatmapPane);
    initializeSelectionManager();
    initializeTreeIcon();
    initializeGraphIcon();
    initializeFileMenu();
  }

  private void initializeFileMenu() {
    menuFileOpen.setOnAction(event -> {
      try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("pages/FileChooser.fxml"));
        Parent root = loader.load();

        Stage popupStage = new Stage();
        popupStage.setScene(new Scene(root));
        popupStage.setTitle("Select files to load");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(rootLayoutNode.getScene().getWindow());
        popupStage.showAndWait();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  /**
   * Initialize the selection manager (which manages showing the description of selected objects).
   */
  private void initializeSelectionManager() {
    selectionManager
        = new SelectionManager(selectionDescriptionPane, mainPane);
    mainPane.setOnMouseClicked((MouseEvent event) -> {
      if (!event.isConsumed()) {
        selectionManager.deselect();
        event.consume();
      }
    });
  }

  /**
   * Initialize the tree icon. When this icon is clicked, the tree will be shown.
   */
  private void initializeTreeIcon() {
    treeIcon.setOnMouseClicked((MouseEvent event) -> {
      if (mainPane.getItems().contains(treePane)) {
        return;
      }
      mainPane.getItems().clear();
      mainPane.getItems().addAll(treePane, heatmapPane);
      mainPane.setDividerPositions(0.8);
      zoomOutButton.setDisable(zoomOutButtonDisabled);
      mainPane.fireEvent(new GraphicsChangedEvent());
    });
  }

  /**
   * Initialize the tree icon. When this icon is clicked, the tree will be shown.
   */
  private void initializeGraphIcon() {
    graphIcon.setOnMouseClicked((MouseEvent event) -> {
      if (mainPane.getItems().contains(graphPane)) {
        return;
      }
      mainPane.getItems().clear();
      mainPane.getItems().add(graphPane);
      zoomOutButtonDisabled = zoomOutButton.isDisabled();
      zoomOutButton.setDisable(true);
      mainPane.fireEvent(new GraphicsChangedEvent());
    });
  }

  /**
   * Set the data which has to be visualized.
   *
   * @param root the root of the tree which has to be drawn.
   * @param graph the graph
   */
  public void setData(IPhylogeneticTreeNode root, OriginalGraph graph) {
    assert treeManager == null;
    treeManager = new TreeManager(treePane, root, zoomOutButton, selectionManager);
    heatmapManager.initLeaves(treeManager.getCurrentLeaves());
    treeManager.setOnLeavesChanged((Observable observable, Object arg) -> {
      heatmapManager.setLeaves(treeManager.getCurrentLeaves());
    });
    new DrawGraph().drawGraph(graphPane, graph);
  }
}
