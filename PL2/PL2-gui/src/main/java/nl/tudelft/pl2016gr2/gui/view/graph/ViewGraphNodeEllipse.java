package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.scene.Node;
import javafx.scene.shape.Ellipse;
import nl.tudelft.pl2016gr2.gui.view.selection.GraphBubbleDescription;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.Collections;

/**
 * An ellipse representation of a node, which can be drawn in the user interface.
 *
 * @author Faris
 */
public class ViewGraphNodeEllipse extends Ellipse implements IViewGraphNode {

  private final GraphNode dataNode;

  /**
   * Construct a node circle.
   *
   * @param width  the width of the ellipse.
   * @param height the height of the ellipse.
   * @param dataNode the data node.
   */
  public ViewGraphNodeEllipse(double width, double height, GraphNode dataNode) {
    super(width * GraphPaneController.NODE_MARGIN / 2.0, height / 2.0);
    this.dataNode = dataNode;
  }

  @Override
  public double getWidth() {
    return getRadiusX() * 2.0;
  }

  @Override
  public double getHeight() {
    return getRadiusY() * 2.0;
  }

  @Override
  public Node get() {
    return this;
  }

  @Override
  public void select() {
    Collections.replaceAll(getStyleClass(), "graphUnselectedNode", "graphSelectedNode");
  }

  @Override
  public void deselect() {
    Collections.replaceAll(getStyleClass(), "graphSelectedNode", "graphUnselectedNode");
  }

  @Override
  public ISelectionInfo getSelectionInfo() {
    return new GraphBubbleDescription(dataNode.toString());
  }

  @Override
  public boolean isEqualSelection(ISelectable other) {
    if (other instanceof ViewGraphNodeEllipse) {
      ViewGraphNodeEllipse that = (ViewGraphNodeEllipse) other;
      return this.dataNode.getId() == that.dataNode.getId();
    }
    return false;
  }
}
