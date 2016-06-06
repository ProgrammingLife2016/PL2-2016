package nl.tudelft.pl2016gr2.visitor;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;

/**
 * This class is used to visit the phylo bubbles and add children to them.
 *
 * @author Casper
 */
public class BubbleChildrenVisitor implements NodeVisitor {

  private final GraphNode child;

  /**
   * Creates a visitor which can be accepted by any GraphNode, but is specifically meant for
   * PhyloBubble to add a child to it (= a nested node).
   *
   * @param child : the child to add
   */
  public BubbleChildrenVisitor(GraphNode child) {
    this.child = child;
  }

  @Override
  public void visit(PhyloBubble node) {
    node.addChild(child);
  }

  @Override
  public void visit(GraphNode node) {
  }

  @Override
  public void visit(SequenceNode node) {
  }
}
