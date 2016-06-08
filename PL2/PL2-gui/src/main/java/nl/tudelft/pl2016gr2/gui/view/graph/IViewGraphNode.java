package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

/**
 * Interface of all drawable graph nodes, see: {@link GraphNode}.
 *
 * @author Faris
 */
public interface IViewGraphNode extends ISelectable {

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
   * Get the width of this node.
   *
   * @return the width of this node.
   */
  double getWidth();

  /**
   * Get the height of this node.
   *
   * @return the height of this node.
   */
  double getHeight();

  /**
   * Get the layout y coordinate of this node.
   *
   * @return the layout y coordinate of this node.
   */
  double getLayoutY();

  /**
   * Get the JavaFX node of this node.
   *
   * @return the JavaFX node of this node.
   */
  Node get();
}
