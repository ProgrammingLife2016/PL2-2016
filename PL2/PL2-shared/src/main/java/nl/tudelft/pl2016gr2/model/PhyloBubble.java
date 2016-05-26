package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class PhyloBubble implements Bubble {
  
  private int id;
  private IPhylogeneticTreeNode treeNode;
  private ArrayList<Integer> inEdges;
  private ArrayList<Integer> outEdges;
  private HashSet<Integer> nestedNodes;
  
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode) {
    this.id = id;
    this.treeNode = treeNode;
    this.nestedNodes = new HashSet<>();
  }
  
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, 
      Collection<Integer> inEdges, Collection<Integer> outEdges) {
    this.id = id;
    this.treeNode = treeNode;
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
    this.nestedNodes = new HashSet<>();
  }

  @Override
  public int getId() {
    return id;
  }
  
  public IPhylogeneticTreeNode getTreeNode() {
    return treeNode;
  }
  
  public void addChild(int child) {
    nestedNodes.add(child);
  }

  @Override
  public boolean hasChildren() {
    return true;
  }

  @Override
  public Collection<Integer> getChildren() {
    return nestedNodes;
  }

  @Override
  public int size() {
    return nestedNodes.size();
  }

  @Override
  public Collection<Integer> getInEdges() {
    return inEdges;
  }

  @Override
  public void setInEdges(Collection<Integer> edges) {
    inEdges = new ArrayList<>(edges);
    inEdges.trimToSize();
  }

  @Override
  public void addInEdge(int identifier) {
    assert !inEdges.contains(
        identifier) : "Adding existing in-edge: " + identifier + ". NodeID: " + this.getId();
    inEdges.add(identifier);
  }

  @Override
  public void removeInEdge(int identifier) {
    assert inEdges.contains(
        identifier) : "Removing non-existent in-edge: " + identifier + ". NodeID: " + this.getId();
    inEdges.remove(identifier);
  }

  @Override
  public Collection<Integer> getOutEdges() {
    return outEdges;
  }

  @Override
  public void setOutEdges(Collection<Integer> edges) {
    outEdges = new ArrayList<>(edges);
    outEdges.trimToSize();
  }

  @Override
  public void addOutEdge(int identifier) {
    assert !outEdges.contains(
        identifier) : "Adding existing out-edge: " + identifier + ". NodeID: " + this.getId();
    outEdges.add(identifier);
  }

  @Override
  public void removeOutEdge(int identifier) {
    assert outEdges.contains(
        identifier) : "Removing non-existent out-edge: " + identifier + ". NodeID: " + this.getId();
    outEdges.remove(identifier);
  }

  @Override
  public Collection<String> getGenomes() {
    return treeNode.getGenomes();
  }

  @Override
  public void addGenome(String genome) {
    // NOT RELEVANT FOR BUBBLE
  }

  @Override
  public void removeGenome(String genome) {
    // NOT RELEVANT FOR BUBBLE
  }

  @Override
  public Collection<String> getGenomesOverEdge(GraphNode node) {
    assert getOutEdges().contains(
        node.getId()) : "Tried to get genomes over edge for node " + node.getId() + "but it is "
        + "not a direct successor. This = " + this.getId();

    Collection<String> genomes = new ArrayList<>();
    getGenomes().stream().filter(genome -> node.getGenomes().contains(genome)).forEach(genomes
        ::add);
    return genomes;
  }

  @Override
  public GraphNode copy() {
    return new PhyloBubble(getId(), treeNode);
  }
  
  @Override
  public GraphNode copyAll() {
    return new PhyloBubble(getId(), treeNode, inEdges, outEdges);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public void pop() {
    
  }

}
