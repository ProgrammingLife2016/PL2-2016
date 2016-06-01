package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;

/**
 *
 * @author Faris
 */
public class IndelBubble extends Bubble {

  public IndelBubble(int id, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, Collection<GraphNode> nestedNodes) {
    super(id, inEdges, outEdges, nestedNodes);
  }

  private IndelBubble(Bubble bubble) {
    super(bubble);
  }

  @Override
  public GraphNode copy() {
    return new IndelBubble(this);
  }

  @Override
  public GraphNode copyAll() {
    return new IndelBubble(this);
  }

  @Override
  public Collection<GraphNode> pop(SequenceGraph graph) {
    throw new UnsupportedOperationException("To be implemented.");
  }

  @Override
  public void accept(NodeVisitor visitor) {
    throw new UnsupportedOperationException("Do we need this?");
  }
}
