//package nl.tudelft.pl2016gr2.model;
//
//import nl.tudelft.pl2016gr2.visitor.NodeVisitor;
//
//import java.util.ArrayList;
//import java.util.Collection;
//
//public class SemanticBubble implements Bubble {
//  
//  private int id;
//  private boolean inDel = false;
//  private boolean pointMutation = false;
//  private boolean inBubble = false;
//  private ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
//  private ArrayList<Integer> inEdges;
//  private ArrayList<Integer> outEdges = new ArrayList<Integer>();
//  private String tag = "";
//  
//  public SemanticBubble(int id, GraphNode node) {
//    this.id = id;
//    this.nodes.add(node);
//    this.inEdges = (ArrayList<Integer>) node.getInEdges();
//  }
//
//  public void addNode(GraphNode node) {
//    this.nodes.add(node);
//  }
//  
//  public void setTag(String str) {
//    this.tag = str;
//  }
//  
//  public String getTag() {
//    return tag;    
//  }
//  
//  @Override
//  public int getId() {
//    return this.id;
//  }
//
//  @Override
//  public void setInDel(boolean bool) {
//    this.inDel = bool;
//  }
//
//  @Override
//  public boolean isInDel() {
//    return this.inDel;
//  }
//
//  @Override
//  public void setPoint(boolean bool) {
//    this.pointMutation = bool;
//  }
//
//  @Override
//  public boolean isPoint() {
//    return this.pointMutation;
//  }
//
//  @Override
//  public boolean hasChildren() {
//    return this.outEdges.size() > 0;
//  }
//
//  @Override
//  public Collection<Integer> getChildren() {
//    return this.outEdges;
//  }
//
//  @Override
//  public int size() {
//    return nodes.size();
//  }
//
//  @Override
//  public Collection<Integer> getInEdges() {
//    return this.inEdges;
//  }
//
//  @Override
//  public void setInEdges(Collection<Integer> edges) {
//    this.inEdges = (ArrayList<Integer>) edges;
//  }
//
//  @Override
//  public void addInEdge(int identifier) {
//    this.inEdges.add(identifier);
//  }
//
//  @Override
//  public void removeInEdge(int identifier) {
//    this.inEdges.remove(identifier);
//  }
//
//  @Override
//  public Collection<Integer> getOutEdges() {
//    return this.outEdges;
//  }
//
//  @Override
//  public void setOutEdges(Collection<Integer> edges) {
//    this.outEdges = (ArrayList<Integer>) edges;
//  }
//
//  @Override
//  public void addOutEdge(int identifier) {
//    this.outEdges.add(identifier);
//  }
//
//  @Override
//  public void removeOutEdge(int identifier) {
//    this.outEdges.remove(identifier);
//  }
//
//  @Override
//  public Collection<String> getGenomes() {
//    return null;
//  }
//
//  @Override
//  public void addGenome(String genome) {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  public void removeGenome(String genome) {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  public Collection<String> getGenomesOverEdge(GraphNode node) {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//  @Override
//  public GraphNode copy() {
//    // TODO Auto-generated method stub
//    return null;
//  }
//
//  @Override
//  public void accept(NodeVisitor visitor) {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  public void pop() {
//    // TODO Auto-generated method stub
//    
//  }
//
//  public ArrayList<GraphNode> getNodes() {
//    return this.nodes;
//  }
//
//  @Override
//  public void setInBubble(boolean bool) {
//    this.inBubble = bool;    
//  }
//
//  @Override
//  public boolean isInBubble() {
//    return this.inBubble;
//  }
//
//}
