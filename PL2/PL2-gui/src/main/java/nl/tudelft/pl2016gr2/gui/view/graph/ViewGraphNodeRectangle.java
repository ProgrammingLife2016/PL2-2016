package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.selection.GraphBubbleDescriptionBuilder;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.Collections;

/**
 * A square representation of a node, which can be drawn in the user interface.
 *
 * @author Faris
 */
public class ViewGraphNodeRectangle extends Rectangle implements IViewGraphNode {

  private final DoubleProperty centerXProperty = new SimpleDoubleProperty();
  private final DoubleProperty centerYProperty = new SimpleDoubleProperty();
  private final GraphNode dataNode;

  /**
   * Constructor.
   *
   * @param width    the width of the rectangle.
   * @param height   the height of the rectangle.
   * @param dataNode the data object.
   */
  public ViewGraphNodeRectangle(double width, double height, GraphNode dataNode) {
    super(width, height);
    layoutXProperty().bind(centerXProperty.add(-width / 2.0));
    layoutYProperty().bind(centerYProperty.add(-height / 2.0));
    this.dataNode = dataNode;
  }

  @Override
  public DoubleProperty centerXProperty() {
    return centerXProperty;
  }

  @Override
  public DoubleProperty centerYProperty() {
    return centerYProperty;
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
    return GraphBubbleDescriptionBuilder.buildInfo(dataNode);
  }

  @Override
  public boolean isEqualSelection(ISelectable other) {
    if (other instanceof ViewGraphNodeRectangle) {
      ViewGraphNodeRectangle that = (ViewGraphNodeRectangle) other;
      return this.dataNode.getId() == that.dataNode.getId();
    }
    return false;
  }
}
