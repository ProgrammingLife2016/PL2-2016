//package nl.tudelft.pl2016gr2.model;
//
//import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
//import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
//import nl.tudelft.pl2016gr2.visitor.NodeVisitor;
//
//import java.util.Collection;
//
///**
// *
// * @author Cas
// */
//public class SimpleBubble extends Bubble {
//
//  public SimpleBubble(int id, Collection<GraphNode> inEdges,
//      Collection<GraphNode> outEdges, Collection<GraphNode> nestedNodes) {
//    super(id, inEdges, outEdges, nestedNodes);
//  }
//  
//  public SimpleBubble(int id, Collection<GraphNode> inEdges,
//      Collection<GraphNode> outEdges, GraphNode node) {
//    super(id, inEdges, outEdges, node);
//  }
//  
//  public SimpleBubble(int id, Collection<GraphNode> inEdges, GraphNode node) {
//    super(id, inEdges, node);
//  }
//  
//  public SimpleBubble(int id) {
//    super(id);
//  }
//
//  public SimpleBubble(Bubble bubble) {
//    super(bubble);
//  }
//
//  @Override
//  public GraphNode copy() {
//    return new SimpleBubble(this);
//  }
//
//  @Override
//  public GraphNode copyAll() {
//    return new SimpleBubble(this);
//  }
//
//  public Collection<GraphNode> pop(SequenceGraph graph) {
//    throw new UnsupportedOperationException("To be implemented.");
//  }
//
//  @Override
//  public void accept(NodeVisitor visitor) {
//    throw new UnsupportedOperationException("Do we need this?");
//  }
//
//  @Override
//  public void addGenome(int genome) {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  public void removeGenome(int genome) {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  public Collection<GraphNode> pop() {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//  @Override
//  public void unpop() {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  public boolean isPopped() {
//    // TODO Auto-generated method stub
//    return false;
//  }
//
//  @Override
//  public boolean needsVerticalAligning() {
//    // TODO Auto-generated method stub
//    return false;
//  }
//}
