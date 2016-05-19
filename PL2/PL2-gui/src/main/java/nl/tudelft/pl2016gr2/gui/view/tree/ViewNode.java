package nl.tudelft.pl2016gr2.gui.view.tree;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.events.AnimationEvent;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.selection.TextDescription;
import nl.tudelft.pl2016gr2.gui.view.selection.TreeNodeDescription;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;

/**
 * This class represent a node of the phylogenetic tree which can be drawn in the user interface (it
 * extends Circle).
 *
 * @author Faris
 */
public class ViewNode extends Circle implements ISelectable {

  private static final double NODE_RADIUS = 10.0;
  private static final double NODE_DIAMETER = NODE_RADIUS * 2.0;
  private static final Duration ZOOM_IN_ANIMATION_DURATION = Duration.millis(750.0);
  private static final Duration ZOOM_OUT_ANIMATION_DURATION = Duration.millis(400.0);
  private static final double MAX_EDGE_LENGTH = 200.0;
  private static final double MIN_EDGE_LENGTH = 20.0;
  private static final double EDGE_LENGTH_SCALAR = 3000.0;

  private final IPhylogeneticTreeNode dataNode;
  @TestId(id = "children")
  private final ArrayList<ViewNode> children = new ArrayList<>();
  private final Area area;
  private final SelectionManager selectionManager;
  private boolean isLeaf;

  /**
   * Create a nl.tudelft.pl2016gr2.gui.view node.
   *
   * @param dataNode         the data of the node.
   * @param graphArea        the graph area in which the node has to be drawn.
   * @param selectionManager the selection manager.
   */
  private ViewNode(IPhylogeneticTreeNode dataNode, Area graphArea,
      SelectionManager selectionManager) {
    super(NODE_RADIUS);
    this.dataNode = dataNode;
    this.area = graphArea;
    this.selectionManager = selectionManager;

    double red = Math.abs(((dataNode.getChildCount() * 13) % 200) / 255d);
    double green = Math.abs(((dataNode.getChildCount() * 34) % 200) / 255d);
    double blue = Math.abs(((dataNode.getChildCount() * 88) % 200) / 255d);
    this.setFill(new Color(red, green, blue, 1.0));

    this.setCenterX(graphArea.getEndX() - this.getRadius());
    this.setCenterY(graphArea.getCenterY());
    initializeClickedEvent();
  }

  /**
   * Initialize the click event for this object.
   */
  private void initializeClickedEvent() {
    setOnMouseClicked((MouseEvent event) -> {
      selectionManager.select(this);
      event.consume();
    });
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
   * @param selectionManager the selection manager.
   * @return the drawn nl.tudelft.pl2016gr2.gui.view node.
   */
  protected static ViewNode drawNode(IPhylogeneticTreeNode dataNode, Area graphArea,
      Pane graphPane, SelectionManager selectionManager) {
    if (graphArea.getWidth() < NODE_DIAMETER || graphArea.getHeight() < NODE_DIAMETER
        || dataNode == null) {
      return null; // box too small to draw node.
    }
    ViewNode node = new ViewNode(dataNode, graphArea, selectionManager);
    graphPane.getChildren().add(node);
    drawChildren(node, dataNode, graphArea, graphPane, selectionManager);
    return node;
  }

  /**
   * Draw the child nodes and edges to the child node.
   *
   * @param node             the node.
   * @param dataNode         the data node.
   * @param area             the graph area of the node.
   * @param graphPane        the pane in which to draw the node.
   * @param selectionManager the selection manager.
   */
  private static void drawChildren(ViewNode node, IPhylogeneticTreeNode dataNode, Area area,
      Pane graphPane, SelectionManager selectionManager) {
    if (dataNode.getDirectChildCount() == 0) {
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
      ViewNode child = drawNode(childDataNode, childArea, graphPane, selectionManager);
      node.children.add(child);
      drawEdge(node, child, graphPane);
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
  private static void drawEdge(ViewNode parent, ViewNode child, Pane graphPane) {
    Line edge = new Line();
    edge.startXProperty().bind(parent.centerXProperty());
    edge.startYProperty().bind(parent.centerYProperty());
    edge.endXProperty().bind(child.centerXProperty());
    edge.endYProperty().bind(child.centerYProperty());
    graphPane.getChildren().add(edge);
    edge.toBack();
  }

  /**
   * Draw an elipsis after a parent node to indicate that the parent has children, but there is not
   * enough space to draw the children.
   *
   * @param node      the parent node.
   * @param graphPane the pane in which to draw the elipsis.
   */
  private static void drawElipsis(ViewNode node, Pane graphPane) {
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
  public void zoomIn(ViewNode newRoot, Timeline timeline) {
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
    newY += TreeManager.GRAPH_BORDER_OFFSET;

    KeyValue kvX = new KeyValue(this.centerXProperty(), newX, Interpolator.EASE_BOTH);
    KeyValue kvY = new KeyValue(this.centerYProperty(), newY, Interpolator.EASE_BOTH);
    timeline.getKeyFrames().add(new KeyFrame(ZOOM_IN_ANIMATION_DURATION, kvX, kvY));
    fireEvent(new AnimationEvent(getCenterX(), getCenterY(), newX, newY,
        originalArea.getHeight() / zoomArea.getHeight(), timeline, ZOOM_IN_ANIMATION_DURATION));

    for (ViewNode child : children) {
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

    for (ViewNode child : children) {
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
  public ArrayList<ViewNode> getCurrentLeaves() {
    ArrayList<ViewNode> res = new ArrayList<>();
    if (!isLeaf) {
      for (ViewNode child : children) {
        res.addAll(child.getCurrentLeaves());
      }
    } else {
      res.add(this);
    }
    return res;
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
  public ViewNode getClosestParentNode(double xCoord, double yCoord) {
    if (area.contains(xCoord, yCoord)) {
      for (ViewNode child : children) {
        ViewNode closest = child.getClosestParentNode(xCoord, yCoord);
        if (closest != null) {
          return closest;
        }
      }
      return this;
    } else {
      return null;
    }
  }

  private Rectangle highlightArea;

  /**
   * Highlight the area of this node.
   *
   * @param treePane the pane in which to draw the highlight area.
   */
  public void highlight(Pane treePane) {
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
  }

  @Override
  public void deselect() {
  }

  @Override
  public ISelectionInfo getSelectionInfo(SelectionManager selectionManager) {
    if (dataNode.getChildCount() != 0) {
      return new TreeNodeDescription(selectionManager, dataNode);
    }
    return new TextDescription("this is a root node");
  }
}
