package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class PhyloBubble implements Bubble {

  private final int id;
  private final IPhylogeneticTreeNode treeNode;
  private final HashSet<GraphNode> nestedNodes;

  private ArrayList<GraphNode> inEdges;
  private ArrayList<GraphNode> outEdges;

  private int size = -1;
  private int level = -1;

  private double relativeYPos;
  private double relvativeStartYPos;
  private double relativeEndYPos;

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode) {
    this.id = id;
    this.treeNode = treeNode;
    this.inEdges = new ArrayList<>();
    this.outEdges = new ArrayList<>();
    this.nestedNodes = new HashSet<>();
  }

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    this.id = id;
    this.treeNode = treeNode;
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
    this.nestedNodes = new HashSet<>();
  }

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, Collection<GraphNode> nestedNodes) {
    this.id = id;
    this.treeNode = treeNode;
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
    this.nestedNodes = new HashSet<>(nestedNodes);
  }

  @Override
  public String toString() {
    String nested = "[";
    for (GraphNode node : nestedNodes) {
      nested = nested + ", " + node.getId();
    }
    nested += "]";

    return "id: " + id + ", in: " + inEdges + ", out: " + outEdges
        + ", nested: " + nested + ", tree leaves: " + treeNode.getGenomes();
  }

  @Override
  public int getId() {
    return id;
  }

  public IPhylogeneticTreeNode getTreeNode() {
    return treeNode;
  }

  public void addChild(GraphNode child) {
    nestedNodes.add(child);
  }

  @Override
  public boolean hasChildren() {
    return true;
  }

  @Override
  public boolean hasChild(GraphNode child) {
    return nestedNodes.contains(child);
  }

  @Override
  public Collection<GraphNode> getChildren() {
    return nestedNodes;
  }

  @Override
  public int size() {
    if (size == -1) {
      size = 0;
      int highestInEdgeLevel = 0;
      for (GraphNode inEdge : inEdges) {
        if (inEdge.getLevel() > highestInEdgeLevel) {
          highestInEdgeLevel = inEdge.getLevel();
        }
      }
      size = getLevel() - highestInEdgeLevel;
    }
    return size;
  }

  @Override
  public Collection<GraphNode> getInEdges() {
    return inEdges;
  }

  @Override
  public void setInEdges(Collection<GraphNode> edges) {
    inEdges = new ArrayList<>(edges);
    inEdges.trimToSize();
  }

  @Override
  public void addInEdge(GraphNode node) {
//    assert !inEdges.contains(
//        identifier) : "Adding existing in-edge: " + identifier + ". NodeID: " + this.getId();
    inEdges.add(node);
  }

  @Override
  public void removeInEdge(GraphNode node) {
//    assert inEdges.contains(
//        identifier) : "Removing non-existent in-edge: " + identifier + ". NodeID: " + this.getId();
    inEdges.remove(node);
  }

  @Override
  public Collection<GraphNode> getOutEdges() {
    return outEdges;
  }

  @Override
  public void setOutEdges(Collection<GraphNode> edges) {
    outEdges = new ArrayList<>(edges);
    outEdges.trimToSize();
  }

  @Override
  public void addOutEdge(GraphNode node) {
//    assert !outEdges.contains(
//        identifier) : "Adding existing out-edge: " + identifier + ". NodeID: " + this.getId();
    outEdges.add(node);
  }

  @Override
  public void removeOutEdge(GraphNode node) {
//    assert outEdges.contains(
//        identifier) : "Removing non-existent out-edge: " + identifier + ". NodeID: " + this.getId();
    outEdges.remove(node);
  }

  @Override
  public Collection<String> getGenomes() {
    HashSet<String> genomeSet = new HashSet<>();
    for (GraphNode nestedNode : nestedNodes) {
      genomeSet.addAll(nestedNode.getGenomes());
    }
    return genomeSet;
  }

  @Override
  public void addGenome(String genome) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public void removeGenome(String genome) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public Collection<String> getGenomesOverEdge(GraphNode node) {
    assert getOutEdges().contains(
        node) : "Tried to get genomes over edge for node " + node.getId() + "but it is "
        + "not a direct successor. This = " + this.getId();

    Collection<String> genomes = new ArrayList<>();
    getGenomes().stream().filter(genome -> node.getGenomes().contains(genome)).forEach(genomes::add);
    return genomes;
  }

  @Override
  public GraphNode copy() {
    return new PhyloBubble(getId(), treeNode);
  }

  @Override
  public GraphNode copyAll() {
    return new PhyloBubble(getId(), treeNode, inEdges, outEdges, nestedNodes);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public void pop() {

  }

  @Override
  public int hashCode() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !getClass().equals(obj.getClass())) {
      return false;
    }
    return id == ((PhyloBubble) obj).id;
  }

  @Override
  public boolean isOverlapping() {
    for (GraphNode nestedNode : nestedNodes) {
      if (nestedNode.isOverlapping()) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void setOverlapping(boolean overlapping) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public void addPositionOffset(int offset) {
    for (GraphNode nestedNode : nestedNodes) {
      nestedNode.addPositionOffset(offset);
    }
  }

  @Override
  public void setLevel(int level) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public double getRelvativeStartYPos() {
    return relvativeStartYPos;
  }

  @Override
  public double getRelvativeEndYPos() {
    return relativeEndYPos;
  }

  @Override
  public void setRelvativeYPosDomain(double relvativeStartYPos, double relativeEndYPos) {
    this.relvativeStartYPos = relvativeStartYPos;
    this.relativeEndYPos = relativeEndYPos;
  }

  @Override
  public double getRelativeYPos() {
    return relativeYPos;
  }

  @Override
  public void setRelativeYPos(double relativeYPos) {
    this.relativeYPos = relativeYPos;
  }

  @Override
  public int getLevel() {
    if (level == -1) {
      for (GraphNode nestedNode : nestedNodes) {
        if (nestedNode.getLevel() > level) {
          level = nestedNode.getLevel();
        }
      }
    }
    return level;
  }
}
