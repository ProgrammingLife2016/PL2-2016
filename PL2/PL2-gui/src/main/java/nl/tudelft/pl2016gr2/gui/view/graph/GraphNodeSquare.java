package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Rectangle;

/**
 * A square representation of a bubble, which can be drawn in the user interface.
 *
 * @author Faris
 */
public class GraphNodeSquare extends Rectangle implements IGraphNode {

  // duplicated from graphnodecircle
  private final DoubleProperty centerXProperty = new SimpleDoubleProperty();
  private final DoubleProperty centerYProperty = new SimpleDoubleProperty();
  private final DoubleProperty relativeHeight = new SimpleDoubleProperty();
  private final double maxYOffset;

  /**
   * Constructor.
   *
   * @param radius         the radius of the square (2 * radius = width and height).
   * @param relativeHeight the relative height of the square.
   * @param maxYOffset     the maximum y offset of the square.
   */
  public GraphNodeSquare(double radius, double relativeHeight, double maxYOffset) {
    super(radius * 2.0, radius * 2.0);
    this.maxYOffset = maxYOffset;
    this.relativeHeight.set(relativeHeight);
    layoutXProperty().bind(centerXProperty.add(-radius));
    layoutYProperty().bind(centerYProperty.add(-radius));
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
  public DoubleProperty centerXProperty() {
    return centerXProperty;
  }

  @Override
  public DoubleProperty centerYProperty() {
    return centerYProperty;
  }

  @Override
  public double getRadius() {
    return getWidth() / 2.0;
  }
}
