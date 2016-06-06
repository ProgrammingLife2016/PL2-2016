package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.graph.data.BaseSequence;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * A simple implementation of <code>Node</code> that offers the DNA sequence as a String, but
 * internally uses a more efficient storage mechanism (see {@link BaseSequence}).
 *
 * @author Wouter Smit
 */
public class SequenceNode extends AbstractNode {

  private BaseSequence sequence;
  private ArrayList<Integer> genomes;
  private HashSet<GraphNode> inEdges;
  private HashSet<GraphNode> outEdges;

  /**
   * If this node is overlapping with a node from the other graph.
   */
  private boolean overlapping;

  /**
   * The level of this node (the depth in the graph.
   */
  private int level;

  /**
   * The relative y-position of the node
   */
  private double relativeYPos = -1;
  private double maxHeight;

  /**
   * Constructs a bare node with only an identifier.
   *
   * @param identifier The ID to assign to this node.
   */
  public SequenceNode(int identifier) {
    super(identifier);
    genomes = new ArrayList<>();
    inEdges = new HashSet<>();
    outEdges = new HashSet<>();
  }

  /**
   * Constructs a node with only a specified DNA sequence.
   *
   * @param identifier The identifier of the node
   * @param sequence   The DNA sequence that this node holds
   */
  public SequenceNode(int identifier, BaseSequence sequence) {
    super(identifier);
    this.sequence = sequence;
    genomes = new ArrayList<>();
    inEdges = new HashSet<>();
    outEdges = new HashSet<>();
  }

  /**
   * Constructs a node without in or out edges, but with a sequence and corresponding genomes.
   *
   * @param identifier The identifier of the node
   * @param sequence   The DNA sequence that this node holds
   * @param genomes    The genomes that go through this node
   */
  public SequenceNode(int identifier, BaseSequence sequence, Collection<Integer> genomes) {
    super(identifier);
    this.sequence = sequence;
    this.genomes = new ArrayList<>(genomes);
    inEdges = new HashSet<>();
    outEdges = new HashSet<>();
  }

  /**
   * Constructs a node with all specified fields.
   * <p>
   * Any duplicates in the in or out edges will be filtered away.
   * </p>
   *
   * @param identifier The identifier of the node
   * @param sequence   The DNA sequence that this node holds
   * @param genomes    The genomes that go through this node
   * @param inEdges    The IDs of the nodes that are direct predecessors of this node
   * @param outEdges   The IDs of the nodes that are direct successors of this node
   */
  public SequenceNode(int identifier, BaseSequence sequence, Collection<Integer> genomes,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(identifier);
    this.sequence = sequence;
    this.genomes = new ArrayList<>(genomes);
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
    //this.inEdges.trimToSize();
    //this.outEdges.trimToSize();
  }

  @Override
  public void setSequence(BaseSequence sequence) {
    this.sequence = sequence;
  }

  @Override
  public String getSequence() {
    if (sequence == null) {
      return null;
    }
    return sequence.getBaseSequence();
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
//        node) : "Adding existing in-edge: " + node.getId() + ". NodeID: " + this.getId();
    inEdges.add(node);
  }

  @Override
  public void removeInEdge(GraphNode node) {
//    assert inEdges.contains(
//        node) : "Removing non-existent in-edge: " + node.getId() + ". NodeID: " + this.getId();
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
//        node) : "Adding existing out-edge: " + node.getId() + ". NodeID: " + this.getId();
    outEdges.add(node);
  }

  @Override
  public void removeOutEdge(GraphNode node) {
//    assert outEdges.contains(
//        node) : "Removing non-existent out-edge: " + node.getId() + ". NodeID: " + this.getId();
    outEdges.remove(node);
  }

  @Override
  public Collection<Integer> getGenomes() {
    return genomes;
  }

  @Override
  public void addGenome(int genome) {
//    assert !genomes.contains(
//        genome) : "Adding existing genome: " + genome + ". NodeID: " + this.getId();
    genomes.add(genome);
  }

  @Override
  public void removeGenome(int genome) {
    assert genomes.contains(
        genome) : "Removing non-existent genome: " + genome + ". NodeID: " + this.getId();
    genomes.remove(genome);
  }

  /**
   * Returns the length of the sequence in this node.
   *
   * @return The sequence length
   */
  @Override
  public int size() {
    return sequence.size();
  }

  @Override
  public GraphNode copy() {
    SequenceNode node = new SequenceNode(this.getId(), sequence, getGenomes());
    node.level = level;
    node.overlapping = overlapping;
    return node;
  }

  @Override
  public GraphNode copyAll() {
    SequenceNode node = new SequenceNode(getId(), sequence, getGenomes(), inEdges, outEdges);
    node.level = level;
    node.overlapping = overlapping;
    return node;
  }

  @Override
  public void addPositionOffset(int offset) {
    level += offset;
  }

  @Override
  public int getLevel() {
    return level;
  }

  @Override
  public void setOverlapping(boolean overlapping) {
    this.overlapping = overlapping;
  }

  @Override
  public boolean isOverlapping() {
    return overlapping;
  }

  @Override
  public void setLevel(int level) {
    this.level = level;
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
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    for (GraphNode inEdge : inEdges) {
      sb.append(inEdge.getId()).append(", ");
    }
    sb.append(']');
    StringBuilder sb2 = new StringBuilder();
    sb2.append('[');
    for (GraphNode outEdge : outEdges) {
      sb2.append(outEdge.getId()).append(", ");
    }
    sb2.append(']');
    return super.toString() + ", SequenceNode{" + ", inEdges="
        + sb.toString() + ", outEdges=" + sb2.toString() + '}';
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
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
  public Collection<GraphNode> pop() {
    ArrayList<GraphNode> res = new ArrayList<>(1);
    res.add(this);
    return res;
  }

  @Override
  public void unpop() {
  }

  @Override
  public boolean isPopped() {
    return false;
  }
}
