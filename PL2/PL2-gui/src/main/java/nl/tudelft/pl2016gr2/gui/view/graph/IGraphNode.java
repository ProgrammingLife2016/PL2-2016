package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;

/**
 * Interface of all drawable graph nodes.
 *
 * @author Faris
 */
public interface IGraphNode {

  public static final double NODE_HEIGHT_RADIUS = 10.0;
  
  /**
   * Get the relative height property.
   *
   * @return the relative height property.
   */
  DoubleProperty getRelativeHeightProperty();

  /**
   * Get the maximum y offset (to above).
   *
   * @return the maximum y offset (to above).
   */
  double getMaxYOffset();

  /**
   * Get the center x property.
   *
   * @return the center x property.
   */
  DoubleProperty centerXProperty();

  /**
   * Get the center y property.
   *
   * @return the center y property.
   */
  DoubleProperty centerYProperty();

  double getWidth();
  
  double getHeight();
}
