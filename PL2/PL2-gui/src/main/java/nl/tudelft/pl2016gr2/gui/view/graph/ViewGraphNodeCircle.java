package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.scene.shape.Ellipse;

/**
 * A circle representation of a node, which can be drawn in the user interface.
 *
 * @author Faris
 */
public class ViewGraphNodeCircle extends Ellipse implements IViewGraphNode {

  /**
   * Construct a node circle.
   *
   * @param width  the width of the ellipse.
   * @param height the height of the ellipse.
   */
  public ViewGraphNodeCircle(double width, double height) {
    super(width * DrawComparedGraphs.NODE_MARGIN / 2.0, height / 2.0);
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