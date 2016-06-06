package nl.tudelft.pl2016gr2.visitor;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;


/**
 * Defines the implementing class to be visitable.
 * <p>
 * This interface is part of the <i>visitor design pattern</i>.
 * See also {@link NodeVisitor}.
 * </p>
 * <p>
 * This interface is specific to the {@link GraphNode} interface.
 * </p>
 *
 * @author Wouter Smit
 */
public interface Visitable {

  /**
   * Allows a visitor to the implementing class to performs its actions on the class.
   * <p>
   * This method is used by invoking the <code>visitor</code>'s {@link
   * NodeVisitor#visit(GraphNode)} method.
   * </p>
   *
   * @param visitor The visitor that wants to visit this class.
   */
  void accept(NodeVisitor visitor);
}
