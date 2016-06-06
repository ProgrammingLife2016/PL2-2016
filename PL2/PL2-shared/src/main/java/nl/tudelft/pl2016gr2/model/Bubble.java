package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * An aggregate <code>GraphNode</code>, containing other <code>GraphNodes</code>.
 *
 * @author Wouter Smit
 */
public abstract class Bubble implements GraphNode {

  private final int id;
  protected ArrayList<GraphNode> inEdges;
  private int level = -1;
  private double maxHeight;
  protected final HashSet<GraphNode> nestedNodes;
  protected ArrayList<GraphNode> outEdges;
  private double relativeYPos;
  private int size = -1;

  public void addChild(GraphNode child) {
    nestedNodes.add(child);
  }

  public Bubble(int id) {
    this.id = id;
    this.nestedNodes = new HashSet<>();
  }

  public Bubble(int id, Collection<GraphNode> nestedNodes) {
    this.id = id;
    this.nestedNodes = new HashSet<>(nestedNodes);
  }

  public Bubble(int id, Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    this.id = id;
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
    this.nestedNodes = new HashSet<>();
  }

  public Bubble(int id, Collection<GraphNode> inEdges, Collection<GraphNode> outEdges,
      Collection<GraphNode> nestedNodes) {
    this.id = id;
    this.nestedNodes = new HashSet<>(nestedNodes);
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
  }
  
  public Bubble(int id, Collection<GraphNode> inEdges, Collection<GraphNode> outEdges,
      GraphNode node) {
    this.id = id;
    this.nestedNodes = new HashSet<>();
    nestedNodes.add(node);
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
  }
  
  public Bubble(int id, Collection<GraphNode> inEdges, GraphNode node) {
    this.id = id;
    this.nestedNodes = new HashSet<>();
    nestedNodes.add(node);
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>();
    this.inEdges.trimToSize();
  }

  // constructor to copy bubble
  protected Bubble(Bubble bubble) {
    this.id = bubble.id;
    this.inEdges = bubble.inEdges;
    this.outEdges = bubble.outEdges;
    this.nestedNodes = bubble.nestedNodes;
  }

  @Override
  public void addGenome(String genome) {
    throw new UnsupportedOperationException(
        "This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public void addInEdge(GraphNode node) {
    inEdges.add(node);
  }

  @Override
  public void addOutEdge(GraphNode node) {
    outEdges.add(node);
  }

  @Override
  public void addPositionOffset(int offset) {
    for (GraphNode nestedNode : nestedNodes) {
      nestedNode.addPositionOffset(offset);
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !getClass().equals(obj.getClass())) {
      return false;
    }
    return id == ((Bubble) obj).id;
  }

  @Override
  public Collection<GraphNode> getChildren() {
    return nestedNodes;
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
  public Collection<String> getGenomesOverEdge(GraphNode node) {
    assert getOutEdges().contains(node) : "Tried to get genomes over edge for node " + node.getId()
        + "but it is " + "not a direct successor. This = " + this.getId();
    Collection<String> genomes = new ArrayList<>();
    getGenomes().stream().
        filter((String genome) -> node.getGenomes().contains(genome)).
        forEach(genomes::add);
    return genomes;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public Collection<GraphNode> getInEdges() {
    return inEdges;
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
  public Collection<GraphNode> getOutEdges() {
    return outEdges;
  }

  @Override
  public double getRelativeYPos() {
    return relativeYPos;
  }

  @Override
  public boolean hasChild(GraphNode child) {
    return nestedNodes.contains(child);
  }

  @Override
  public boolean hasChildren() {
    return true;
  }

  @Override
  public int hashCode() {
    return id * 37;
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
  public void removeGenome(String genome) {
    throw new UnsupportedOperationException(
        "This must be performed on the nodes inside of the " + ""
        + "bubbles before the bubbles are made.");
  }

  @Override
  public void removeInEdge(GraphNode node) {
    inEdges.remove(node);
  }

  @Override
  public void removeOutEdge(GraphNode node) {
    outEdges.remove(node);
  }

  @Override
  public void setInEdges(Collection<GraphNode> edges) {
    inEdges = new ArrayList<>(edges);
    inEdges.trimToSize();
  }

  @Override
  public void setLevel(int level) {
    throw new UnsupportedOperationException(
        "This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public void setMaxHeight(double maxHeight) {
    this.maxHeight = maxHeight;
  }

  @Override
  public void setOutEdges(Collection<GraphNode> edges) {
    outEdges = new ArrayList<>(edges);
    outEdges.trimToSize();
  }

  @Override
  public void setOverlapping(boolean overlapping) {
    throw new UnsupportedOperationException(
        "This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public void setRelativeYPos(double relativeYPos) {
    this.relativeYPos = relativeYPos;
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
  public String toString() {
    String nested = "[";
    for (GraphNode node : nestedNodes) {
      nested = nested + ", " + node.getId();
    }
    nested += "]";

//    return "id: " + id + ", in: " + inEdges + ", out: " + outEdges
//        + ", nested: " + nested;
    //return "bubble: nested: " + nested + "Inedges: " + inEdges + "Outedges: " + outEdges;
    return "someBubble ";
  }
}
