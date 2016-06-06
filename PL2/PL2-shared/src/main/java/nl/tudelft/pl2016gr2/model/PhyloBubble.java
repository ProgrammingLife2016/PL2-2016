package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class PhyloBubble extends Bubble {

  private final IPhylogeneticTreeNode treeNode;
  private final HashSet<GraphNode> nestedNodes;

  private HashSet<GraphNode> inEdges;
  private HashSet<GraphNode> outEdges;

  private final HashMap<Integer, Collection<GraphNode>> originalOutEdges = new HashMap<>();
  private final HashMap<Integer, Collection<GraphNode>> originalInEdges = new HashMap<>();

  private final PhyloFilter filter;
  private Collection<GraphNode> poppedNodes;
  private boolean isPopped;

  private int size = -1;
  private int level = -1;

  private double relativeYPos = -1;
  private double maxHeight;

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter) {
    super(id);
    this.treeNode = treeNode;
    this.filter = filter;
    this.inEdges = new HashSet<>();
    this.outEdges = new HashSet<>();
    this.nestedNodes = new HashSet<>();
  }

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(id, inEdges, outEdges);
    this.treeNode = treeNode;
    this.filter = filter;
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
    //this.inEdges.trimToSize();
    //this.outEdges.trimToSize();
    this.nestedNodes = new HashSet<>();
  }

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges,
      Collection<GraphNode> nestedNodes) {
    super(id, inEdges, outEdges, nestedNodes);
    this.treeNode = treeNode;
    this.filter = filter;
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
    //this.inEdges.trimToSize();
    //this.outEdges.trimToSize();
    this.nestedNodes = new HashSet<>(nestedNodes);
  }

  @Override
  public String toString() {
    return "id: " + id + ", in: " + getIds(inEdges) + ", out: " + getIds(outEdges)
        + ", nested: " + getIds(nestedNodes) + ", tree leaves: " + treeNode.getGenomes();
  }

  @Override
  public boolean needsVerticalAligning() {
    return poppedNodes == null;
  }

  private String getIds(Collection<GraphNode> nodes) {
    StringBuilder builder = new StringBuilder("[");
    for (GraphNode node : nodes) {
      builder.append(node.getId() + ", ");
    }
    builder.append("]");
    return builder.toString();
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
    inEdges = new HashSet<>(edges);
    //inEdges.trimToSize();
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
    outEdges = new HashSet<>(edges);
    //outEdges.trimToSize();
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
  public Collection<Integer> getGenomes() {
    HashSet<Integer> genomeSet = new HashSet<>();
    for (GraphNode nestedNode : nestedNodes) {
      genomeSet.addAll(nestedNode.getGenomes());
    }
    return genomeSet;
  }

  @Override
  public void addGenome(int genome) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public void removeGenome(int genome) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public Collection<Integer> getGenomesOverEdge(GraphNode node) {
    assert getOutEdges().contains(
        node) : "Tried to get genomes over edge for node " + node.getId() + "but it is "
        + "not a direct successor. This = " + this.getId();

    Collection<Integer> genomes = new ArrayList<>();
    getGenomes().stream().filter(genome -> node.getGenomes().contains(genome)).forEach(genomes::add);
    return genomes;
  }

  @Override
  public GraphNode copy() {
    return new PhyloBubble(getId(), treeNode, filter);
  }

//  @Override
//  public GraphNode copyAll() {
//    return new PhyloBubble(treeNode, filter, this);
//  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Collection<GraphNode> pop() {
    if (!isPopped) {
      isPopped = true;
      for (GraphNode node : inEdges) {
        originalOutEdges.put(node.getId(), new HashSet<>(node.getOutEdges()));
      }
      for (GraphNode node : outEdges) {
        originalInEdges.put(node.getId(), new HashSet<>(node.getInEdges()));
      }
      poppedNodes = filter.zoomIn(this);
    }
    return poppedNodes;
  }

  @Override
  public void unpop() {
    if (isPopped) {
      isPopped = false;
      for (GraphNode node : inEdges) {
        node.setOutEdges(originalOutEdges.get(node.getId()));
      }

      for (GraphNode node : outEdges) {
        node.setInEdges(originalInEdges.get(node.getId()));
      }
    }
  }

  @Override
  public boolean isPopped() {
    return isPopped;
  }

  @Override
  public int hashCode() {
    return id * 37;
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

  @Override
  public double getMaxHeightPercentage() {
    return maxHeight;
  }

  @Override
  public void setMaxHeight(double maxHeight) {
    this.maxHeight = maxHeight;
  }

  @Override
  public GraphNode copyAll() {
    // TODO Auto-generated method stub
    return null;
  }
}
