package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.graph.data.BaseSequence;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple implementation of <code>Node</code> that offers the DNA sequence as a String, but
 * internally uses a more efficient storage mechanism (see {@link BaseSequence}).
 *
 * @author Wouter Smit
 */
public class SequenceNode extends AbstractGraphNode implements Node {

  private BaseSequence sequence;
  private final ArrayList<Integer> genomes;

  /**
   * The level of this node (the depth in the graph.
   */
  private int level;

  /**
   * Constructs a bare node with only an identifier.
   *
   * @param identifier The ID to assign to this node.
   */
  public SequenceNode(int identifier) {
    super(identifier);
    genomes = new ArrayList<>();
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
    this.genomes.trimToSize();
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
    super(identifier, inEdges, outEdges);
    this.sequence = sequence;
    this.genomes = new ArrayList<>(genomes);
  }

  /**
   * Constructor which coppies a nodes.
   *
   * @param node the node to copy.
   */
  private SequenceNode(SequenceNode node) {
    super(node);
    this.sequence = node.sequence;
    this.genomes = node.genomes;
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
  public Collection<Integer> getGenomes() {
    return genomes;
  }

  @Override
  public void addGenome(int genome) {
    //assert !genomes.contains(
    //    genome) : "Adding existing genome: " + genome + ". NodeID: " + this.getId();
    genomes.add(genome);
  }

  @Override
  public void removeGenome(int genome) {
    assert genomes.contains(
        genome) : "Removing non-existent genome: " + genome + ". NodeID: " + this.getId();
    genomes.remove((Integer) genome);
  }

  @Override
  public void addAllGenomes(Collection<Integer> genomes) {
    this.genomes.addAll(genomes);
    this.genomes.trimToSize();
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
    SequenceNode node = new SequenceNode(this.getId(), sequence);
    node.level = level;
    node.getGuiData().overlapping = getGuiData().overlapping;
    return node;
  }

  @Override
  public GraphNode copyAll() {
    SequenceNode node = new SequenceNode(this);
    node.level = level;
    node.getGuiData().overlapping = getGuiData().overlapping;
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
  public void setLevel(int level) {
    this.level = level;
  }

//  @Override
//  public String toString() {
//    StringBuilder sb = new StringBuilder();
//    sb.append('[');
//    for (GraphNode inEdge : getInEdges()) {
//      sb.append(inEdge.getId()).append(", ");
//    }
//    sb.append(']');
//    StringBuilder sb2 = new StringBuilder();
//    sb2.append('[');
//    for (GraphNode outEdge : getOutEdges()) {
//      sb2.append(outEdge.getId()).append(", ");
//    }
//    sb2.append(']');
//    return super.toString() + ", SequenceNode{" + ", inEdges=" + sb.toString() + ", outEdges="
//        + sb2.toString() + '}';
//  }

  @Override
  public String toString() {
    String sequenceString = getSequence();
    if (sequenceString.length() > 20) {
      sequenceString = sequenceString.substring(0, 20);
    }
    return String.format("Sequence %d:\n%s\n", getId(), sequenceString);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public void trimToSize() {
    super.trimToSize();
    genomes.trimToSize();
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

  @Override
  public boolean hasChildren() {
    return false;
  }

  @Override
  public boolean hasChild(GraphNode child) {
    return false;
  }

}
