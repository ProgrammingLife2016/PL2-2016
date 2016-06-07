package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.graph.data.GraphNodeGuiData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * An aggregate <code>GraphNode</code>, containing other <code>GraphNodes</code>.
 *
 * @author Wouter Smit
 */
public abstract class Bubble implements GraphNode {

  private final int id;
  private final List<GraphNode> nestedNodes;

  private HashSet<GraphNode> inEdges;
  private HashSet<GraphNode> outEdges;

  private int size = -1;
  private int level = -1;
  private final GraphNodeGuiData guiData = new GraphNodeGuiData();

  /**
   * If the nodes in the bubble have to be vertically aligned.
   *
   * @return if the nodes in the bubble have to be vertically aligned.
   */
  public abstract boolean needsVerticalAligning();

  /**
   * Construct a bubble.
   *
   * @param id the id of the bubble.
   */
  public Bubble(int id) {
    this.id = id;
    this.inEdges = new HashSet<>();
    this.outEdges = new HashSet<>();
    this.nestedNodes = new ArrayList<>();
  }

  /**
   * Construct a bubble.
   *
   * @param id       the id of the bubble.
   * @param inEdges  the in edges of the bubble.
   * @param outEdges the out edges of the bubble.
   */
  public Bubble(int id, Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    this.id = id;
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
    //this.inEdges.trimToSize();
    //this.outEdges.trimToSize();
    this.nestedNodes = new ArrayList<>();
  }

  /**
   * Construct a bubble.
   *
   * @param id          the id of the bubble.
   * @param inEdges     the in edges of the bubble.
   * @param outEdges    the out edges of the bubble.
   * @param nestedNodes the nested nodes of the bubble.
   */
  public Bubble(int id, Collection<GraphNode> inEdges, Collection<GraphNode> outEdges,
      List<GraphNode> nestedNodes) {
    this.id = id;
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
    //this.inEdges.trimToSize();
    //this.outEdges.trimToSize();
    this.nestedNodes = nestedNodes;
    initOverlap();
  }

  /**
   * Constructor to copy a bubble.
   *
   * @param bubble the bubble to copy.
   */
  protected Bubble(Bubble bubble) {
    this.id = bubble.id;
    this.inEdges = bubble.inEdges;
    this.outEdges = bubble.outEdges;
    this.nestedNodes = bubble.nestedNodes;
  }

  /**
   * Initialize the overlap value, based on the nested nodes.
   */
  private void initOverlap() {
    for (GraphNode nestedNode : nestedNodes) {
      if (nestedNode.getGuiData().overlapping) {
        guiData.overlapping = true;
        return;
      }
    }
    guiData.overlapping = false;
  }

  @Override
  public int getId() {
    return id;
  }

  /**
   * Add another child node to the nested nodes.
   *
   * @param child the child node.
   */
  public void addChild(GraphNode child) {
    if (nestedNodes.contains(child)) {
      return;
    }
    nestedNodes.add(child);
    if (child.getGuiData().overlapping) {
      guiData.overlapping = true;
    }
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
    inEdges.add(node);
  }

  @Override
  public void removeInEdge(GraphNode node) {
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
    outEdges.add(node);
  }

  @Override
  public void removeOutEdge(GraphNode node) {
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
    getGenomes().stream().filter(genome -> node.getGenomes().contains(genome))
        .forEach(genomes::add);
    return genomes;
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
    return id == ((Bubble) obj).id;
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
  public GraphNodeGuiData getGuiData() {
    return guiData;
  }
}
