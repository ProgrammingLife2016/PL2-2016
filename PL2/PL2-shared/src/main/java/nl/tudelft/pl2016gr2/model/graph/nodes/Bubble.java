package nl.tudelft.pl2016gr2.model.graph.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * An aggregate <code>GraphNode</code>, containing other <code>GraphNodes</code>.
 *
 * @author Wouter Smit
 */
public abstract class Bubble extends AbstractGraphNode implements GraphNode {

  private final List<GraphNode> nestedNodes;

  private int size = -1;
  private int level = -1;

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
    super(id);
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
    super(id, inEdges, outEdges);
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
    super(id, inEdges, outEdges);
    this.nestedNodes = nestedNodes;
    initOverlap();
  }

  /**
   * Constructor to copy a bubble.
   *
   * @param bubble the bubble to copy.
   */
  protected Bubble(Bubble bubble) {
    super(bubble);
    this.nestedNodes = bubble.nestedNodes;
  }

  /**
   * Initialize the overlap value, based on the nested nodes.
   */
  private void initOverlap() {
    for (GraphNode nestedNode : nestedNodes) {
      if (nestedNode.getGuiData().overlapping) {
        getGuiData().overlapping = true;
        return;
      }
    }
    getGuiData().overlapping = false;
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
      getGuiData().overlapping = true;
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
      for (GraphNode inEdge : getInEdges()) {
        if (inEdge.getLevel() > highestInEdgeLevel) {
          highestInEdgeLevel = inEdge.getLevel();
        }
      }
      size = getLevel() - highestInEdgeLevel;
    }
    return size;
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
  public void addAllGenomes(Collection<Integer> genomes) {
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
  public String toString() {

    StringBuilder out = new StringBuilder();

    for (GraphNode child : getChildren()) {
      out.append(child.toString());
    }

    return out.toString();
  }



}
