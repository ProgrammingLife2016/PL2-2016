package nl.tudelft.pl2016gr2.gui.view.tree;


import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.tree.heatmap.HeatmapManager;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

/**
 * This class controlls the phylogenetic tree.
 *
 * @author Faris
 */
public class TreePaneController implements Initializable {

  public static final double GRAPH_BORDER_OFFSET = 5.0;

  @FXML
  @TestId(id = "mainPane")
  private AnchorPane mainPane;
  @FXML
  @TestId(id = "treePane")
  private Pane treePane;
  @FXML
  @TestId(id = "heatmapPane")
  private Pane heatmapPane;
  @TestId(id = "currentRoot")
  private TreeNodeCircle currentRoot;
  private TreeNodeCircle currentHighlightedNode;
  private boolean isZooming = false;
  private final ArrayList<Observer> childLeaveObservers = new ArrayList<>();
  @TestId(id = "selectionManager")
  private SelectionManager selectionManager;
  private HeatmapManager heatmapManager;

  private IPhylogeneticTreeRoot rootNode;
  private GraphPaneController graphPaneController;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeZoomEventHandler();
    initializeGraphSizeListeners();
    initializeZoomAreaHighlighter();
    heatmapManager = new HeatmapManager(heatmapPane);
    this.setOnLeavesChanged((Observable observable, Object arg) -> {
      heatmapManager.setLeaves(getCurrentLeaves());
    });
  }

  /**
   * Set the selection manager and initialize listeners to the selection manager.
   *
   * @param selectionManager the selection manager.
   * @param graphPaneController the graphPane controller.
   */
  public void setup(SelectionManager selectionManager, GraphPaneController graphPaneController) {
    this.selectionManager = selectionManager;
    this.graphPaneController = graphPaneController;
    selectionManager.addListener((observable, oldValue, newValue) -> {
      checkSelectedForNodeAndLeaves(currentRoot);
    });
    initializeTopGraphSelectionManger();
    initializeBottomGraphSelectionManger();
    initializeSearchBoxSelectionManager();
  }

  /**
   * Initialize the listener to the top graph genome set.
   */
  private void initializeTopGraphSelectionManger() {
    graphPaneController.getTopGraphGenomes().addListener(
        (SetChangeListener.Change<? extends Integer> change) -> {
          if (change.wasAdded()) {
            rootNode.setDrawnInTop(change.getElementAdded(), true);
          } else {
            rootNode.setDrawnInTop(change.getElementRemoved(), false);
          }
        });
  }

  /**
   * Initialize the listener to the bottom graph genome set.
   */
  private void initializeBottomGraphSelectionManger() {
    graphPaneController.getBottomGraphGenomes().addListener(
        (SetChangeListener.Change<? extends Integer> change) -> {
          if (change.wasAdded()) {
            rootNode.setDrawnInBottom(change.getElementAdded(), true);
          } else {
            rootNode.setDrawnInBottom(change.getElementRemoved(), false);
          }
        });
  }

  /**
   * Initialize the listener to the selected search box genome.
   */
  private void initializeSearchBoxSelectionManager() {
    selectionManager.getSearchBoxSelectedGenomes().addListener((ListChangeListener<Integer>) c -> {
      List<Integer> added = new ArrayList<>();
      List<Integer> removed = new ArrayList<>();
      while (c.next()) {
        if (c.wasAdded()) {
          added.addAll(c.getAddedSubList());
        }
        if (c.wasRemoved()) {
          removed.addAll(c.getRemoved());
        }
      }
      rootNode.highlightPaths(removed, added);
    });
  }

  /**
   * Load a new tree into the view.
   *
   * @param root the root of the tree.
   */
  public void loadTree(IPhylogeneticTreeRoot root) {
    rootNode = root;
    setCurrentRoot(root);
  }

  /**
   * Initialize the mouse listeners which highlights the area which will be zoomed in on when the
   * mouse scrolls.
   */
  private void initializeZoomAreaHighlighter() {
    treePane.setOnMouseMoved((MouseEvent event) -> {
      if (currentRoot == null || isZooming) {
        return;
      }
      TreeNodeCircle toHighlight = currentRoot.getClosestParentNode(event.getX(), event.getY());
      if (currentHighlightedNode != toHighlight) {
        removeHighlight();
        if (toHighlight != null) {
          toHighlight.highlightArea(treePane);
          currentHighlightedNode = toHighlight;
        }
      }
    });
    treePane.setOnMouseExited((MouseEvent event) -> {
      removeHighlight();
    });
  }

  /**
   * Remove the highlighted area.
   */
  private void removeHighlight() {
    if (currentHighlightedNode != null) {
      currentHighlightedNode.removeHighlight(treePane);
      currentHighlightedNode = null;
    }
  }

  /**
   * Initialize node clicked events, which cause a zoom in on the clicked node.
   */
  private void initializeZoomEventHandler() {
    treePane.setOnScroll((ScrollEvent event) -> {
      boolean scrollUp = event.getDeltaY() > 0;
      if (scrollUp) {
        zoomIn(currentRoot.getClosestParentNode(event.getX(), event.getY()));
      } else {
        zoomOut();
      }
      event.consume();
    });
  }

  /**
   * Zoom in on a node.
   *
   * @param zoomedInNode the node to zoom in on.
   */
  private void zoomIn(TreeNodeCircle zoomedInNode) {
    if (isZooming || zoomedInNode == null || zoomedInNode.equals(currentRoot)) {
      return;
    }
    removeHighlight();
    Timeline timeline = new Timeline();
    currentRoot.zoomIn(zoomedInNode, timeline);
    timeline.setOnFinished(e -> {
      setCurrentRoot(zoomedInNode.getDataNode());
      isZooming = false;
    });
    isZooming = true;
    timeline.play();
  }

  /**
   * Zoom out.
   */
  private void zoomOut() {
    if (isZooming || !currentRoot.getDataNode().hasParent()) {
      return;
    }
    removeHighlight();
    Timeline timeline = new Timeline();
    currentRoot.zoomOut(timeline);
    timeline.setOnFinished(event -> {
      setCurrentRoot(currentRoot.getDataNode().getParent());
      isZooming = false;
    });
    isZooming = true;
    timeline.play();
  }

  /**
   * Initialize the listeners which resizes the content of the graph area when the pane or the
   * window is resized.
   */
  private void initializeGraphSizeListeners() {
    treePane.heightProperty().addListener((observable, oldVal, newVal) -> {
      if (currentRoot != null) {
        setCurrentRoot(currentRoot.getDataNode());
      }
    });
    treePane.widthProperty().addListener((observable, oldVal, newVal) -> {
      if (currentRoot != null) {
        setCurrentRoot(currentRoot.getDataNode());
      }
    });
  }

  /**
   * Set the root of the part of the tree which should be shown.
   *
   * @param root the root of the part of the tree which should be shown.
   */
  @TestId(id = "setRoot")
  private void setCurrentRoot(IPhylogeneticTreeNode root) {
    treePane.getChildren().clear();
    currentRoot = TreeNodeCircle.drawNode(root, getGraphPaneArea(), treePane, this);
    childLeaveObservers.forEach((Observer observer) -> {
      observer.update(null, null);
    });
  }

  /**
   * Get the area of the graph pane.
   *
   * @return the area of the graph pane.
   */
  private Area getGraphPaneArea() {
    double startX = TreePaneController.GRAPH_BORDER_OFFSET;
    double endX = getTreeWidth() - TreePaneController.GRAPH_BORDER_OFFSET;
    double startY = TreePaneController.GRAPH_BORDER_OFFSET;
    double endY = getTreeHeight() - TreePaneController.GRAPH_BORDER_OFFSET;
    return new Area(startX, endX, startY, endY);
  }

  /**
   * Get the width of the tree. Returns the minimum width if the actual width is smaller than the
   * minimum width.
   *
   * @return the width of the tree.
   */
  private double getTreeWidth() {
    double width = treePane.getWidth();
    if (width < treePane.getMinWidth()) {
      return treePane.getMinWidth();
    }
    return width;
  }

  /**
   * Get the height of the tree. Returns the minimum height if the actual height is smaller than the
   * minimum height.
   *
   * @return the height of the tree.
   */
  private double getTreeHeight() {
    double height = treePane.getHeight();
    if (height < treePane.getMinHeight()) {
      return treePane.getMinHeight();
    }
    return height;
  }

  /**
   * Add an observer for the event which occurs when the currently displayed leaf nodes change.
   *
   * @param observer the observer to add.
   */
  public void setOnLeavesChanged(Observer observer) {
    childLeaveObservers.add(observer);
  }

  /**
   * Get the currently displayed leaf nodes.
   *
   * @return the currently displayed leaf nodes.
   */
  @TestId(id = "getCurrentLeaves()")
  private ArrayList<TreeNodeCircle> getCurrentLeaves() {
    return currentRoot.getCurrentLeaves();
  }

  /**
   * Recursively iterate the given node and its children and check Selection.
   *
   * <p>
   * This method will call {@link SelectionManager#checkSelected(ISelectable)} for
   * each child.
   * </p>
   *
   * @param node check for this node.
   */
  private void checkSelectedForNodeAndLeaves(TreeNodeCircle node) {
    if (node != null) {
      selectionManager.checkSelected(node);
      node.getChildren().forEach(this::checkSelectedForNodeAndLeaves);
    }
  }

  /**
   * Getter for the selectionManager
   * @return the selectionManager.
   */
  public SelectionManager getSelectionManager() {
    return selectionManager;
  }

  /**
   * Getter for the graphPaneController.
   * @return the graphPaneController.
   */
  public GraphPaneController getGraphPaneController() {
    return graphPaneController;
  }
}
