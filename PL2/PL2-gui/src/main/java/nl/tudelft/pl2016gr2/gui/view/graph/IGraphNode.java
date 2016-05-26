package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

/**
 * Interface of all drawable graph nodes.
 *
 * @author Faris
 */
public interface IGraphNode {

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

  /**
   * Get the radius of the object.
   *
   * @return the radius of the object.
   */
  double getRadius();
}
