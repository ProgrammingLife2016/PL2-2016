package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.shape.Circle;

/**
 * A circle representation of a node, which can be drawn in the user interface.
 *
 * @author Faris
 */
public class NodeCircle extends Circle {

  private final DoubleProperty relativeHeight = new SimpleDoubleProperty();

  /**
   * Construct a node circle.
   *
   * @param radius         the radius of the circle.
   * @param relativeHeight the relative height of the circle (compared to the height of the pane).
   */
  public NodeCircle(double radius, double relativeHeight) {
    super(radius);
    this.relativeHeight.set(relativeHeight);
  }

  /**
   * Get the relative height property.
   *
   * @return the relative height property.
   */
  public DoubleProperty getRelativeHeightProperty() {
    return relativeHeight;
  }
}
