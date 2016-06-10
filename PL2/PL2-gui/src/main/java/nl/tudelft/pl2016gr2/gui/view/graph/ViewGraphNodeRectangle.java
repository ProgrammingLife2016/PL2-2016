package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.selection.GraphBubbleDescription;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

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
   *  @param width  the width of the rectangle.
   * @param height the height of the rectangle.
   * @param dataNode the data object.
   */
  public ViewGraphNodeRectangle(double width, double height,
                                GraphNode dataNode) {
    super(width/* * GraphPaneController.NODE_MARGIN*/, height);
    layoutXProperty().bind(centerXProperty.add(-width / 2.0));
    layoutYProperty().bind(centerYProperty.add(-height / 2.0));
    setFill(Color.ALICEBLUE);
    setStrokeWidth(height / 20.0d);
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
    setStroke(Color.BLACK);
  }

  @Override
  public void deselect() {
    setStroke(null);
  }

  @Override
  public ISelectionInfo getSelectionInfo() {
    return new GraphBubbleDescription(dataNode.toString());
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
