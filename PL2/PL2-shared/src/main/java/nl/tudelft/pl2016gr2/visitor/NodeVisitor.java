package nl.tudelft.pl2016gr2.visitor;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.StraightSequenceBubble;

/**
 * Defines the visitor of a node.
 * <p>
 * This interface is part of the <i>visitor design pattern</i>. See also {@link Visitable}.
 * </p>
 * <p>
 * To use this class, extend it and overload the <code>visit()</code> method with functionality
 * specific to the desired subtypes of {@link GraphNode}.
 * </p>
 *
 * @author Wouter Smit
 */
public interface NodeVisitor {

  /**
   * Peforms that action that the visitor must apply to the specified node.
   *
   * @param node The node to act on
   */
  void visit(GraphNode node);

  /**
   * Peforms that action that the visitor must apply to the specified node.
   *
   * @param bubble The bubble to act on
   */
  void visit(PhyloBubble bubble);

  /**
   * Peforms that action that the visitor must apply to the specified node.
   *
   * @param bubble The bubble to act on
   */
  void visit(StraightSequenceBubble bubble);

  /**
   * Peforms that action that the visitor must apply to the specified node.
   *
   * @param bubble The bubble to act on
   */
  void visit(IndelBubble bubble);

  /**
   * Peforms that action that the visitor must apply to the specified node.
   *
   * @param bubble The bubble to act on
   */
  void visit(PointMutationBubble bubble);

  /**
   * Peforms that action that the visitor must apply to the specified node.
   *
   * @param node The node to act on
   */
  void visit(SequenceNode node);
}
