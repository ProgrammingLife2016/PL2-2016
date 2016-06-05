package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple implementation of <code>Node</code> that offers the DNA sequence as a String, but
 * internally uses a more efficient storage mechanism (see {@link BaseSequence}).
 *
 * @author Wouter Smit
 */
public class SequenceNode extends AbstractNode {

  private BaseSequence sequence;
  private ArrayList<Integer> genomes;
  private ArrayList<GraphNode> inEdges;
  private ArrayList<GraphNode> outEdges;

  /**
   * Constructs a bare node with only an identifier.
   *
   * @param identifier The ID to assign to this node.
   */
  public SequenceNode(int identifier) {
    super(identifier);
    genomes = new ArrayList<>();
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
    genomes = new ArrayList<>();
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
  public SequenceNode(int identifier, BaseSequence sequence, Collection<Integer> genomes) {
    super(identifier);
    this.sequence = sequence;
    this.genomes = new ArrayList<>(genomes);
    inEdges = new ArrayList<>();
    outEdges = new ArrayList<>();

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
    super(identifier);
    this.sequence = sequence;
    this.genomes = new ArrayList<>(genomes);
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);

    this.genomes.trimToSize();
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
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
  public void addInEdge(GraphNode node) {
    assert !inEdges.contains(
        node) : "Adding existing in-edge: " + node.getId() + ". NodeID: " + this.getId();
    inEdges.add(node);
  }

  @Override
  public void addAllInEdges(Collection<GraphNode> nodes) {
    nodes.forEach(node -> {
      assert !inEdges.contains(node);
    });

    inEdges.addAll(nodes);
    inEdges.trimToSize();
  }

  @Override
  public void removeInEdge(GraphNode node) {
    assert inEdges.contains(
        node) : "Removing non-existent in-edge: " + node.getId() + ". NodeID: " + this.getId();
    inEdges.remove(node);
  }

  @Override
  public Collection<GraphNode> getOutEdges() {
    return outEdges;
  }

  @Override
  public void addOutEdge(GraphNode node) {
    assert !outEdges.contains(
        node) : "Adding existing out-edge: " + node.getId() + ". NodeID: " + this.getId();
    outEdges.add(node);
  }

  @Override
  public void addAllOutEdges(Collection<GraphNode> nodes) {
    nodes.forEach(node -> {
      assert !outEdges.contains(node);
    });

    outEdges.addAll(nodes);
    outEdges.trimToSize();
  }

  @Override
  public void removeOutEdge(GraphNode node) {
    assert outEdges.contains(
        node) : "Removing non-existent out-edge: " + node.getId() + ". NodeID: " + this.getId();
    outEdges.remove(node);
  }

  @Override
  public Collection<Integer> getGenomes() {
    return genomes;
  }

  @Override
  public void addGenome(int genome) {
    assert !genomes.contains(
        genome) : "Adding existing genome: " + genome + ". NodeID: " + this.getId();
    genomes.add(genome);
  }

  @Override
  public void addAllGenomes(Collection<Integer> genomes) {
    genomes.forEach(genome -> {
      assert !this.genomes.contains(genome);
    });

    this.genomes.addAll(genomes);
    this.genomes.trimToSize();
  }

  @Override
  public void removeGenome(int genome) {
    assert genomes.contains(
        genome) : "Removing non-existent genome: " + genome + ". NodeID: " + this.getId();
    genomes.remove(genome);
  }

  @Override
  public void trimToSize() {
    inEdges.trimToSize();
    outEdges.trimToSize();
    genomes.trimToSize();
  }

  @Override
  public GraphNode copy() {
    return new SequenceNode(this.getId(), this.sequence);
  }
}
