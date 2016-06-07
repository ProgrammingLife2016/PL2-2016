//package nl.tudelft.pl2016gr2.model;
//
//import nl.tudelft.pl2016gr2.visitor.NodeVisitor;
//
//import java.util.Collection;
//
///**
// *
// * @author Faris
// */
//public class StraightSequenceBubble extends Bubble {
//
//  public StraightSequenceBubble(int id, Collection<GraphNode> inEdges,
//      Collection<GraphNode> outEdges, Collection<GraphNode> nestedNodes) {
//    super(id, inEdges, outEdges, nestedNodes);
//  }
//
//  public StraightSequenceBubble(Bubble bubble) {
//    super(bubble);
//  }
//
//  @Override
//  public GraphNode copy() {
//    return new StraightSequenceBubble(this);
//  }
//
//  @Override
//  public GraphNode copyAll() {
//    return new StraightSequenceBubble(this);
//  }
//
//  @Override
//  public Collection<GraphNode> pop() {
//    return this.getChildren();
//  }
//
//  @Override
//  public void accept(NodeVisitor visitor) {
//    visitor.visit(this);
//  }
//}