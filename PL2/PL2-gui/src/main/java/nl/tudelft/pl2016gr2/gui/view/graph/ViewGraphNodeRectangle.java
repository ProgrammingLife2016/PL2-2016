package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;

/**
 * A square representation of a node, which can be drawn in the user interface.
 *
 * @author Faris
 */
public class ViewGraphNodeRectangle extends Rectangle implements IViewGraphNode {

  private final DoubleProperty centerXProperty = new SimpleDoubleProperty();
  private final DoubleProperty centerYProperty = new SimpleDoubleProperty();
  private final ISelectionInfo selectionInfo;

  /**
   * Constructor.
   *
   * @param width  the width of the rectangle.
   * @param height the height of the rectangle.
   */
  public ViewGraphNodeRectangle(double width, double height, ISelectionInfo selectionInfo) {
    super(width/* * DrawComparedGraphs.NODE_MARGIN*/, height);
    layoutXProperty().bind(centerXProperty.add(-width / 2.0));
    layoutYProperty().bind(centerYProperty.add(-height / 2.0));
    setFill(Color.ALICEBLUE);
    setStrokeWidth(height / 20.0d);
    this.selectionInfo = selectionInfo;
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
  public ISelectionInfo getSelectionInfo(SelectionManager selectionManager) {
    return selectionInfo;
  }
}
