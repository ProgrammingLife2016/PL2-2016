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
// * @author Faris
// */
//public class IndelBubble extends Bubble {
//
//  public IndelBubble(int id, Collection<GraphNode> inEdges,
//      Collection<GraphNode> outEdges, Collection<GraphNode> nestedNodes) {
//    super(id, inEdges, outEdges, nestedNodes);
//  }
//
//  public IndelBubble(Bubble bubble) {
//    super(bubble);
//  }
//
//  @Override
//  public GraphNode copy() {
//    return new IndelBubble(this);
//  }
//
//  @Override
//  public GraphNode copyAll() {
//    return new IndelBubble(this);
//  }
//
//  public Collection<GraphNode> pop(SequenceGraph graph) {
//    throw new UnsupportedOperationException("To be implemented.");
//  }
//
//  @Override
//  public void accept(NodeVisitor visitor) {
//    visitor.visit(this);
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
