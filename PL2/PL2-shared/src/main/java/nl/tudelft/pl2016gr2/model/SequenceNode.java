package nl.tudelft.pl2016gr2.model;

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
  private HashSet<String> genomes;
  private ArrayList<GraphNode> inEdges;
  private ArrayList<GraphNode> outEdges;

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
  private double relativeYPos;
  private double relvativeStartYPos;
  private double relativeEndYPos;

  /**
   * Constructs a bare node with only an identifier.
   *
   * @param identifier The ID to assign to this node.
   */
  public SequenceNode(int identifier) {
    super(identifier);
    genomes = new HashSet<>();
    inEdges = new ArrayList<>();
    outEdges = new ArrayList<>();
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
    genomes = new HashSet<>();
    inEdges = new ArrayList<>();
    outEdges = new ArrayList<>();
  }

  /**
   * Constructs a node without in or out edges, but with a sequence and corresponding genomes.
   *
   * @param identifier The identifier of the node
   * @param sequence   The DNA sequence that this node holds
   * @param genomes    The genomes that go through this node
   */
  public SequenceNode(int identifier, BaseSequence sequence, Collection<String> genomes) {
    super(identifier);
    this.sequence = sequence;
    this.genomes = new HashSet<>(genomes);
    inEdges = new ArrayList<>();
    outEdges = new ArrayList<>();
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
  public SequenceNode(int identifier, BaseSequence sequence, Collection<String> genomes,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(identifier);
    this.sequence = sequence;
    this.genomes = new HashSet<>(genomes);
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
  }

  @Override
  public void setSequence(BaseSequence sequence) {
    this.sequence = sequence;
  }

  @Override
  public String getSequence() {
    return sequence.getBaseSequence();
  }

  /**
   * {@inheritDoc}
   * <p>
   * The collection is backed by the node. Any changes will be reflected in the node.
   * </p>
   */
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
//        node) : "Adding existing in-edge: " + node.getId() + ". NodeID: " + this.getId();
    inEdges.add(node);
  }

  @Override
  public void removeInEdge(GraphNode node) {
//    assert inEdges.contains(
//        node) : "Removing non-existent in-edge: " + node.getId() + ". NodeID: " + this.getId();
    inEdges.remove(node);
  }

  /**
   * {@inheritDoc}
   * <p>
   * The collection is backed by the node. Any changes will be reflected in the node.
   * </p>
   */
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
  public Collection<String> getGenomes() {
    return genomes;
  }

  @Override
  public void addGenome(String genome) {
//    assert !genomes.contains(
//        genome) : "Adding existing genome: " + genome + ". NodeID: " + this.getId();
    genomes.add(genome);
  }

  @Override
  public void removeGenome(String genome) {
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
    return super.toString() + ", SequenceNode{" + "genomes=" + genomes + ", inEdges=" + sb.
        toString() + ", outEdges=" + sb2.toString() + '}';
  }

  @Override
  public void accept(NodeVisitor visitor) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
