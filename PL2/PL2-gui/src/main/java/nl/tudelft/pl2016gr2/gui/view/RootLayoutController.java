package nl.tudelft.pl2016gr2.gui.view;

import java.util.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.graph.DrawGraph;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeController;
import nl.tudelft.pl2016gr2.gui.view.tree.heatmap.HeatmapManager;

/**
 * FXML Controller class.
 *
 * @author Faris
 */
public class RootLayoutController {

  @FXML
  private Pane treePane, heatmapPane;
  @FXML
  private Button zoomOutButton;
  @FXML
  private StackPane locationIdentifierPane;
  @FXML
  private Rectangle locationIdentifierRectangle;
  @FXML
  private ImageView treeIcon, graphIcon;
  @FXML
  private SplitPane mainPane;
  private final Pane graphPane = new Pane();

  private static RootLayoutController controller;
  private TreeController treeController;
  private HeatmapManager heatmapManager;
  private boolean zoomOutButtonDisabled = true;

  /**
   * Initializes the controller class.
   */
  public void initialize() {
    assert (controller == null);
    controller = this;
    heatmapManager = new HeatmapManager(heatmapPane);
    initializeTreeIcon();
    initializeGraphIcon();
    new DrawGraph().drawGraph(graphPane);
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
    });
  }

  /**
   * Set the data which has to be visualized.
   *
   * @param root the root of the tree which has to be drawn.
   */
  public void setData(IPhylogeneticTreeNode root) {
    assert treeController == null;
    treeController = new TreeController(treePane, root, zoomOutButton);
    heatmapManager.initLeaves(treeController.getCurrentLeaves());
    treeController.setOnChildLeavesChanged((Observable o, Object arg) -> {
      heatmapManager.setLeaves(treeController.getCurrentLeaves());
    });
  }
}
