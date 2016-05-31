package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;

/**
 * Interface of all drawable graph nodes.
 *
 * @author Faris
 */
public interface IGraphNode {

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
