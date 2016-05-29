package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Ellipse;

/**
 * A circle representation of a node, which can be drawn in the user interface.
 *
 * @author Faris
 */
public class GraphNodeCircle extends Ellipse implements IGraphNode {

  private final DoubleProperty relativeHeight = new SimpleDoubleProperty();
  private final double maxYOffset;

  /**
   * Construct a node circle.
   *
   * @param radius         the radius of the circle.
   * @param relativeHeight the relative height of the circle (compared to the height of the pane).
   * @param maxYOffset     the maximum value by which this node may be offset (to above).
   */
  public GraphNodeCircle(double radius, double relativeHeight, double maxYOffset) {
    super(radius, NODE_HEIGHT_RADIUS * 2.0);
    this.relativeHeight.set(relativeHeight);
    this.maxYOffset = maxYOffset;
  }

  @Override
  public DoubleProperty getRelativeHeightProperty() {
    return relativeHeight;
  }

  @Override
  public double getMaxYOffset() {
    return maxYOffset;
  }

  @Override
  public double getWidth() {
    return getRadiusX() * 2.0;
  }

  @Override
  public double getHeight() {
    return getRadiusY() * 2.0;
  }
}
