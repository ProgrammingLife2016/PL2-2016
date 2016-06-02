package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;

/**
 *
 * @author Cas
 */
public class SimpleBubble extends Bubble {

  public SimpleBubble(int id, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, Collection<GraphNode> nestedNodes) {
    super(id, inEdges, outEdges, nestedNodes);
  }
  
  public SimpleBubble(int id, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, GraphNode node) {
    super(id, inEdges, outEdges, node);
  }
  
  public SimpleBubble(int id, Collection<GraphNode> inEdges, GraphNode node) {
    super(id, inEdges, node);
  }
  
  public SimpleBubble(int id) {
    super(id);
  }

  public SimpleBubble(Bubble bubble) {
    super(bubble);
  }

  @Override
  public GraphNode copy() {
    return new SimpleBubble(this);
  }

  @Override
  public GraphNode copyAll() {
    return new SimpleBubble(this);
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
