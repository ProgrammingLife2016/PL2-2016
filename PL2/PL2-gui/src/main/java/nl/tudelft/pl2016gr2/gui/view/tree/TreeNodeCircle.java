package nl.tudelft.pl2016gr2.gui.view.tree;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.SnapshotParameters;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nl.tudelft.pl2016gr2.gui.view.events.AnimationEvent;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.gui.view.selection.TreeLeafDescription;
import nl.tudelft.pl2016gr2.gui.view.selection.TreeNodeDescription;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a node of the phylogenetic tree which can be drawn in the user interface (it
 * extends Circle).
 *
 * @author Faris
 */
public class TreeNodeCircle extends Circle implements ISelectable {

  public static final Color LEAF_COLOR = Color.BLACK;
  public static final Color NODE_COLOR = Color.ALICEBLUE;
  public static final double NODE_RADIUS = 10.0;
  private static final double NODE_DIAMETER = NODE_RADIUS * 2.0;
  public static final double NODE_BORDER_WIDTH = 4.0;
  private static final Duration ZOOM_IN_ANIMATION_DURATION = Duration.millis(750.0);
  private static final Duration ZOOM_OUT_ANIMATION_DURATION = Duration.millis(400.0);
  private static final double MAX_EDGE_LENGTH = 200.0;
  private static final double MIN_EDGE_LENGTH = 20.0;
  private static final double EDGE_LENGTH_SCALAR = 3000.0;
  private static final double EDGE_WIDTH = 2.0;
  private static final double HIGHLIGHTED_EDGE_WIDTH = 6.0;

  private static final List<Stop> MULTI_GRAPH_GRADIENT_STOPS = new ArrayList<>(2);

  /**
   * Initialize the colors of the multie graph gradient. DON'T MOVE THIS TO AFTER THE
   * MULTI_GRAPH_GRADIENT DECLARATION. THIS WILL BREAK IT.
   */
  static {
    MULTI_GRAPH_GRADIENT_STOPS.add(new Stop(0.0, GraphPaneController.TOP_GRAPH_COLOR));
    MULTI_GRAPH_GRADIENT_STOPS.add(new Stop(1.0, GraphPaneController.BOTTOM_GRAPH_COLOR));
  }

  public static final LinearGradient MULTI_GRAPH_GRADIENT = new LinearGradient(0.0, 0.0, 1.0, 1.0,
      true, CycleMethod.NO_CYCLE, MULTI_GRAPH_GRADIENT_STOPS);

  private final IPhylogeneticTreeNode dataNode;
  @TestId(id = "children")
  private final ArrayList<TreeNodeCircle> children = new ArrayList<>();
  private final Area area;
  private final TreePaneController treePaneController;
  private boolean isLeaf;
  private final Line edge = new Line();
  private Rectangle highlightArea;

  /**
   * Create a nl.tudelft.pl2016gr2.gui.view node.
   *
   * @param dataNode         the data of the node.
   * @param graphArea        the graph area in which the node has to be drawn.
   * @param treePaneController the selection manager.
   */
  private TreeNodeCircle(IPhylogeneticTreeNode dataNode, Area graphArea,
                         TreePaneController treePaneController) {
    super(NODE_RADIUS);
    setStrokeWidth(NODE_BORDER_WIDTH);
    this.dataNode = dataNode;
    this.area = graphArea;
    this.treePaneController = treePaneController;

    setColor();
    resetBorderColor();
    initializeNodeListeners();

    this.setCenterX(graphArea.getEndX() - this.getRadius());
    this.setCenterY(graphArea.getCenterY());
    initializeClickedEvent();
    initializeDragEvent();
  }

  /**
   * Initialize listeners which listen to the dataNode properties and change the node properties
   * accordingly.
   */
  private void initializeNodeListeners() {
    dataNode.getDrawnInTopProperty().addListener(invalid -> {
      resetBorderColor();
    });
    dataNode.getDrawnInBottomProperty().addListener(invalid -> {
      resetBorderColor();
    });
    dataNode.getInHighlightedPathProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        edge.setStrokeWidth(HIGHLIGHTED_EDGE_WIDTH);
      } else {
        edge.setStrokeWidth(EDGE_WIDTH);
      }
    });
  }

  /**
   * Initialize the click event for this object.
   */
  private void initializeClickedEvent() {
    this.setOnMouseClicked((MouseEvent event) -> {
      treePaneController.getSelectionManager().select(this);
      event.consume();
    });
  }

  /**
   * Initialize a drag event initializer.
   */
  private void initializeDragEvent() {
    this.setOnDragDetected((MouseEvent event) -> {
      StringBuilder genomeStringBuilder = new StringBuilder();
      for (int genome : dataNode.getGenomes()) {
        genomeStringBuilder.append(GenomeMap.getInstance().getGenome(genome)).append('\n');
      }
      genomeStringBuilder.deleteCharAt(genomeStringBuilder.length() - 1);

      ClipboardContent clipboard = new ClipboardContent();
      clipboard.putString(genomeStringBuilder.toString());
      Dragboard dragboard = startDragAndDrop(TransferMode.ANY);
      dragboard.setContent(clipboard);

      SnapshotParameters snapshotParams = new SnapshotParameters();
      snapshotParams.setFill(Color.TRANSPARENT);
      dragboard.setDragView(snapshot(snapshotParams, null));
      event.consume();
    });
  }

  /**
   * Recalculate the border color of this node.
   */
  private void resetBorderColor() {
    if (dataNode.getDrawnInTopProperty().get() && dataNode.getDrawnInBottomProperty().get()) {
      setStroke(MULTI_GRAPH_GRADIENT);
    } else if (dataNode.getDrawnInTopProperty().get()) {
      setStroke(GraphPaneController.TOP_GRAPH_COLOR);
    } else if (dataNode.getDrawnInBottomProperty().get()) {
      setStroke(GraphPaneController.BOTTOM_GRAPH_COLOR);
    } else {
      setStroke(null);
    }
  }

  /**
   * Set the default color of this node.
   */
  private void setColor() {
    if (dataNode.isLeaf()) {
      setFill(LEAF_COLOR);
    } else {
      setFill(NODE_COLOR);
    }
    edge.setStroke(dataNode.getLineageColor());
  }

  /**
   * Get the data of this node.
   *
   * @return the data of this node.
   */
  public IPhylogeneticTreeNode getDataNode() {
    return dataNode;
  }

  /**
   * Recursively draw the node and all of its children.
   *
   * @param dataNode         the data of the node to draw.
   * @param graphArea        the area in which the node should be drawn.
   * @param graphPane        the pane in which to draw the node.
   * @param treePaneController the treePane controller.
   * @return the drawn nl.tudelft.pl2016gr2.gui.view node.
   */
  protected static TreeNodeCircle drawNode(IPhylogeneticTreeNode dataNode, Area graphArea,
                                           Pane graphPane, TreePaneController treePaneController) {
    if (graphArea.getWidth() < NODE_DIAMETER || graphArea.getHeight() < NODE_DIAMETER
        || dataNode == null) {
      return null; // not enough space to draw the tree node.
    }
    TreeNodeCircle node = new TreeNodeCircle(dataNode, graphArea, treePaneController);
    graphPane.getChildren().add(node);
    drawChildren(node, dataNode, graphArea, graphPane);
    treePaneController.getSelectionManager().checkSelected(node);
    return node;
  }

  /**
   * Draw the child nodes and edges to the child node.
   *
   * @param node             the node.
   * @param dataNode         the data node.
   * @param area             the graph area of the node.
   * @param graphPane        the pane in which to draw the node.
   */
  private static void drawChildren(TreeNodeCircle node, IPhylogeneticTreeNode dataNode, Area area,
      Pane graphPane) {
    if (dataNode.isLeaf()) {
      node.isLeaf = true;
      return;
    }
    if (!canDrawChildren(dataNode, area)) {
      node.isLeaf = true;
      drawElipsis(node, graphPane);
      return;
    }
    double ySize = area.getHeight() / dataNode.getDirectChildCount();
    for (int i = 0; i < dataNode.getDirectChildCount(); i++) {
      IPhylogeneticTreeNode childDataNode = dataNode.getChild(i);
      double nextStartY = ySize * i + area.getStartY();
      double nextEndY = nextStartY + ySize;
      double nextEndX = area.getEndX() - calculateEdgeLength(dataNode.getChild(i));
      Area childArea = new Area(area.getStartX(), nextEndX, nextStartY, nextEndY);
      TreeNodeCircle child = drawNode(childDataNode, childArea, graphPane, node.treePaneController);
      node.children.add(child);
      child.drawEdge(node, child, graphPane);
    }
  }

  /**
   * Check if there is enough room to draw all of the children.
   *
   * @param dataNode   the parent node.
   * @param parentArea the area of the parent node.
   * @return if all of the children can be drawn.
   */
  private static boolean canDrawChildren(IPhylogeneticTreeNode dataNode, Area parentArea) {
    double ySize = parentArea.getHeight() / dataNode.getDirectChildCount();
    for (int i = 0; i < dataNode.getDirectChildCount(); i++) {
      double nextStartY = ySize * i + parentArea.getStartY();
      double nextEndY = nextStartY + ySize;
      double nextEndX = parentArea.getEndX() - calculateEdgeLength(dataNode.getChild(i));
      Area childArea = new Area(parentArea.getStartX(), nextEndX, nextStartY, nextEndY);
      if (!(childArea.getWidth() > NODE_DIAMETER) || !(childArea.getHeight() > NODE_DIAMETER)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Calculate the length of the edge to the given node.
   *
   * @param treeNode the node.
   * @return the length of the edge.
   */
  private static double calculateEdgeLength(IPhylogeneticTreeNode treeNode) {
    double edgeLength = treeNode.getEdgeLength() * EDGE_LENGTH_SCALAR;
    if (edgeLength < MIN_EDGE_LENGTH) {
      return MIN_EDGE_LENGTH;
    } else if (edgeLength > MAX_EDGE_LENGTH) {
      return MAX_EDGE_LENGTH;
    }
    return edgeLength;
  }

  /**
   * Draw the edge between a parent and a child node.
   *
   * @param parent    the parent node.
   * @param child     the child node.
   * @param graphPane the pane in which the edge should be drawn.
   */
  private void drawEdge(TreeNodeCircle parent, TreeNodeCircle child, Pane graphPane) {
    edge.setSmooth(true);
    edge.startXProperty().bind(parent.centerXProperty());
    edge.startYProperty().bind(parent.centerYProperty());
    edge.endXProperty().bind(child.centerXProperty());
    edge.endYProperty().bind(child.centerYProperty());
    graphPane.getChildren().add(edge);
    edge.toBack();
    if (dataNode.getInHighlightedPathProperty().get()) {
      edge.setStrokeWidth(HIGHLIGHTED_EDGE_WIDTH);
    } else {
      edge.setStrokeWidth(EDGE_WIDTH);
    }
  }

  /**
   * Draw an elipsis after a parent node to indicate that the parent has children, but there is not
   * enough space to draw the children.
   *
   * @param node      the parent node.
   * @param graphPane the pane in which to draw the elipsis.
   */
  private static void drawElipsis(TreeNodeCircle node, Pane graphPane) {
    Line elipsis = new Line();
    elipsis.startXProperty().bind(node.centerXProperty().add(-node.getRadius() * 1.25));
    elipsis.endXProperty().bind(node.centerXProperty().add(-node.getRadius() * 2.5));
    elipsis.startYProperty().bind(node.centerYProperty());
    elipsis.endYProperty().bind(node.centerYProperty());
    elipsis.getStrokeDashArray().addAll(2d, 5d);
    graphPane.getChildren().add(elipsis);
  }

  /**
   * Animate the zoom in on a node.
   *
   * @param newRoot  the node which should be the root after zooming in.
   * @param timeline the timeline which is used for the animation.
   */
  public void zoomIn(TreeNodeCircle newRoot, Timeline timeline) {
    zoomIn(this.area, newRoot.area, timeline);
  }

  /**
   * Zoom in on a node and animate the zooming proces.
   *
   * @param originalArea the area of the currently drawn tree.
   * @param zoomArea     the area of the tree to zoom in on.
   * @param timeline     the timeline which is used for the animation.
   */
  private void zoomIn(Area originalArea, Area zoomArea, Timeline timeline) {
    double newX = getCenterX() + originalArea.getEndX() - zoomArea.getEndX();
    double newY = getCenterY() - zoomArea.getStartY();
    newY = newY / zoomArea.getHeight() * originalArea.getHeight();
    newY += TreePaneController.GRAPH_BORDER_OFFSET;

    KeyValue kvX = new KeyValue(this.centerXProperty(), newX, Interpolator.EASE_BOTH);
    KeyValue kvY = new KeyValue(this.centerYProperty(), newY, Interpolator.EASE_BOTH);
    timeline.getKeyFrames().add(new KeyFrame(ZOOM_IN_ANIMATION_DURATION, kvX, kvY));
    fireEvent(new AnimationEvent(getCenterX(), getCenterY(), newX, newY,
        originalArea.getHeight() / zoomArea.getHeight(), timeline, ZOOM_IN_ANIMATION_DURATION));

    for (TreeNodeCircle child : children) {
      child.zoomIn(originalArea, zoomArea, timeline);
    }
  }

  /**
   * Animate the zoom out of 1 level (to the direct parent of the current root).
   *
   * @param timeline the timeline which is used for the animation.
   */
  public void zoomOut(Timeline timeline) {
    IPhylogeneticTreeNode newRoot = dataNode.getParent();
    double nextEndX = area.getEndX() - calculateEdgeLength(dataNode);
    double ySize = area.getHeight() / newRoot.getDirectChildCount();
    double nextStartY = ySize * newRoot.getChildIndex(this.dataNode) + area.getStartY();
    double nextEndY = nextStartY + ySize;
    Area newArea = new Area(area.getStartX(), nextEndX, nextStartY, nextEndY);
    zoomOut(this.area, newArea, timeline);
  }

  /**
   * Zoom out and animate the zooming proces.
   *
   * @param originalArea the area of the currently drawn tree.
   * @param zoomArea     the area of the tree where the current tree will be drawn after zooming
   *                     out.
   * @param timeline     the timeline which is used for the animation.
   */
  private void zoomOut(Area originalArea, Area zoomArea, Timeline timeline) {
    double newX = getCenterX() + zoomArea.getEndX() - originalArea.getEndX();
    double newY = getCenterY() - originalArea.getStartY();
    newY = newY * zoomArea.getHeight() / originalArea.getHeight() + zoomArea.getStartY();

    KeyValue kvX = new KeyValue(this.centerXProperty(), newX, Interpolator.EASE_BOTH);
    KeyValue kvY = new KeyValue(this.centerYProperty(), newY, Interpolator.EASE_BOTH);
    timeline.getKeyFrames().add(new KeyFrame(ZOOM_OUT_ANIMATION_DURATION, kvX, kvY));
    fireEvent(new AnimationEvent(getCenterX(), getCenterY(), newX, newY, 0.5, timeline,
        ZOOM_OUT_ANIMATION_DURATION));

    for (TreeNodeCircle child : children) {
      child.zoomOut(originalArea, zoomArea, timeline);
    }
  }

  /**
   * Get the area of this node.
   *
   * @return the area of this node.
   */
  public Area getGraphArea() {
    return area;
  }

  /**
   * Get the nodes which are currently being displayed as leaf nodes.
   *
   * @return the nodes which are currently being displayed as leaf nodes.
   */
  public ArrayList<TreeNodeCircle> getCurrentLeaves() {
    ArrayList<TreeNodeCircle> res = new ArrayList<>();
    if (!isLeaf) {
      for (TreeNodeCircle child : children) {
        res.addAll(child.getCurrentLeaves());
      }
    } else {
      res.add(this);
    }
    return res;
  }

  public List<TreeNodeCircle> getChildren() {
    return children;
  }

  /**
   * Get the closest parent node to the given x and y coordinates. Parent nodes contain the given x
   * and y coordinates in their {@code area}. The node which is the deepest in the tree and contains
   * the given x and y coordinate in its {@code area} is returned.
   *
   * @param xCoord the x coordinate.
   * @param yCoord the y coordinate.
   * @return the closest parent.
   */
  public TreeNodeCircle getClosestParentNode(double xCoord, double yCoord) {
    if (area.contains(xCoord, yCoord)) {
      for (TreeNodeCircle child : children) {
        TreeNodeCircle closest = child.getClosestParentNode(xCoord, yCoord);
        if (closest != null) {
          return closest;
        }
      }
      return this;
    } else {
      return null;
    }
  }

  /**
   * Highlight the area of this node.
   *
   * @param treePane the pane in which to draw the highlight area.
   */
  public void highlightArea(Pane treePane) {
    highlightArea = new Rectangle(area.getStartX(), area.getStartY(), area.getWidth(),
        area.getHeight());
    highlightArea.setFill(Color.rgb(0, 0, 0, 0.075));
    treePane.getChildren().add(highlightArea);
    highlightArea.toBack();
  }

  /**
   * Remove the highlight of the area of this node.
   *
   * @param treePane the pane from which to remove the highlight area.
   */
  public void removeHighlight(Pane treePane) {
    treePane.getChildren().remove(highlightArea);
    highlightArea = null;
  }

  @Override
  public void select() {
    setScaleX(1.5);
    setScaleY(1.5);
  }

  @Override
  public void deselect() {
    setScaleX(1.0);
    setScaleY(1.0);
  }

  @Override
  public ISelectionInfo getSelectionInfo() {
    if (dataNode.getChildCount() != 0) {
      return new TreeNodeDescription(treePaneController.getGraphPaneController(), dataNode);
    }
    return new TreeLeafDescription(dataNode);
  }

  @Override
  public boolean isEqualSelection(ISelectable other) {
    if (other instanceof TreeNodeCircle) {
      TreeNodeCircle that = (TreeNodeCircle) other;
      return this.dataNode == that.dataNode;
    }
    return false;
  }
}
