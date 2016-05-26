package nl.tudelft.pl2016gr2.visitor;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.PhyloBubble;

public class BubbleChildrenVisitor implements NodeVisitor {
  
  int child;
  
  /**
   * Creates a visitor which can be accepted by any GraphNode,
   * but is specifically meant for PhyloBubble to add a child
   * to it (= a nested node). 
   * @param child : the child to add
   */
  public BubbleChildrenVisitor(int child) {
    this.child = child;
  }
  
  @Override
  public void visit(PhyloBubble node) {
    node.addChild(child);
  }

  @Override
  public void visit(GraphNode node) {
    // DO NOTHING
  }
}
