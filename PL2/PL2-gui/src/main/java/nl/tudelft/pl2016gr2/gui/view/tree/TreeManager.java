package nl.tudelft.pl2016gr2.gui.view.tree;

import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import nl.tudelft.pl2016gr2.gui.view.events.GraphicsChangedEvent;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;

import java.util.ArrayList;
import java.util.Observer;

/**
 * This class controlls the phylogenetic tree.
 *
 * @author Faris
 */
public class TreeManager {

  public static final double GRAPH_BORDER_OFFSET = 5.0;

  private final Pane graphPane;
  private final Button zoomOutButton;
  private ViewNode currentRoot;
  boolean isZooming = false;
  private final ArrayList<Observer> childLeaveObservers = new ArrayList<>();
  private final SelectionManager selectionManager;

  /**
   * Create a new tree controller which manages the pholygenetic tree.
   *
   * @param graphPane        the pane in which to draw the tree.
   * @param root             the root node of the tree.
   * @param zoomOutButton    the zoom out button.
   * @param selectionManager the selection manager.
   */
  public TreeManager(Pane graphPane, IPhylogeneticTreeNode root, Button zoomOutButton,
      SelectionManager selectionManager) {
    this.graphPane = graphPane;
    this.zoomOutButton = zoomOutButton;
    this.selectionManager = selectionManager;
    initializeZoomEventHandler();
    initializeZoomOutEventHandler();
    initializeGraphSizeListeners();
    setRoot(root);
  }

  /**
   * Initialize the event handlers which zoom in when a node is clicked or zoom out when the zoom
   * out button is clicked.
   */
  private void initializeZoomOutEventHandler() {
    zoomOutButton.setOnAction(e -> {
      zoomOut();
    });
  }

  /**
   * Initialize node clicked events, which cause a zoom in on the clicked node.
   */
  private void initializeZoomEventHandler() {
    graphPane.setOnScroll((ScrollEvent event) -> {
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
  private void zoomIn(ViewNode zoomedInNode) {
    if (isZooming || zoomedInNode == null || zoomedInNode.equals(currentRoot)) {
      return;
    }
    Timeline timeline = new Timeline();
    currentRoot.zoomIn(zoomedInNode, timeline);
    timeline.setOnFinished(e -> {
      setRoot(zoomedInNode.getDataNode());
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
    Timeline timeline = new Timeline();
    currentRoot.zoomOut(timeline);
    timeline.setOnFinished(event -> {
      setRoot(currentRoot.getDataNode().getParent());
      isZooming = false;
    });
    isZooming = true;
    timeline.play();
  }

  /**
   * Initialize the listeners which resize the content of the graph area when the pane or the window
   * is resized.
   */
  private void initializeGraphSizeListeners() {
    graphPane.heightProperty().addListener((observable, oldVal, newVal) -> {
      if (currentRoot != null) {
        setRoot(currentRoot.getDataNode());
      }
    });
    graphPane.widthProperty().addListener((observable, oldVal, newVal) -> {
      if (currentRoot != null) {
        setRoot(currentRoot.getDataNode());
      }
    });
  }

  /**
   * Set the root of the part of the tree which should be shown.
   *
   * @param root the root of the part of the tree which should be shown.
   */
  private void setRoot(IPhylogeneticTreeNode root) {
    zoomOutButton.setDisable(!root.hasParent());
    graphPane.getChildren().clear();
    currentRoot = ViewNode.drawRootNode(root, graphPane, selectionManager);
    childLeaveObservers.forEach((Observer observer) -> {
      observer.update(null, null);
    });
    graphPane.fireEvent(new GraphicsChangedEvent());
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
  public ArrayList<ViewNode> getCurrentLeaves() {
    return currentRoot.getCurrentLeaves();
  }
}
