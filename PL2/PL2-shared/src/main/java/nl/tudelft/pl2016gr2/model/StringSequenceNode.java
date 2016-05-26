package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * A simple implementation of <code>Node</code> that represents the DNA sequence as String.
 *
 * @author Wouter Smit
 */
public class StringSequenceNode extends AbstractNode {

  private String sequence;
  private HashSet<String> genomes;
  private ArrayList<Integer> inEdges;
  private ArrayList<Integer> outEdges;

  /**
   * Constructs a bare node with only an identifier.
   *
   * @param identifier The ID to assign to this node.
   */
  public StringSequenceNode(int identifier) {
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
  public StringSequenceNode(int identifier, String sequence) {
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
  public StringSequenceNode(int identifier, String sequence, Collection<String> genomes) {
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
  public StringSequenceNode(int identifier, String sequence, Collection<String> genomes,
      Collection<Integer> inEdges, Collection<Integer> outEdges) {
    super(identifier);
    this.sequence = sequence;
    this.genomes = new HashSet<>(genomes);
    this.inEdges = new ArrayList<>(inEdges);
    this.outEdges = new ArrayList<>(outEdges);
    this.inEdges.trimToSize();
    this.outEdges.trimToSize();
  }

  @Override
  public void setSequence(String sequence) {
    this.sequence = sequence;
  }

  @Override
  public String getSequence() {
    return sequence;
  }

  /**
   * {@inheritDoc}
   * <p>
   * The collection is backed by the node.
   * Any changes will be reflected in the node.
   * </p>
   */
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

  /**
   * {@inheritDoc}
   * <p>
   * The collection is backed by the node.
   * Any changes will be reflected in the node.
   * </p>
   */
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
    return genomes;
  }

  @Override
  public void addGenome(String genome) {
    assert !genomes.contains(
        genome) : "Adding existing genome: " + genome + ". NodeID: " + this.getId();
    genomes.add(genome);
  }

  @Override
  public void removeGenome(String genome) {
    assert genomes.contains(
        genome) : "Removing non-existent genome: " + genome + ". NodeID: " + this.getId();
    genomes.remove(genome);
  }

  @Override
  public GraphNode copy() {
    return new StringSequenceNode(this.getId(), this.getSequence());
  }
  
  @Override
  public GraphNode copyAll() {
    return new StringSequenceNode(getId(), getSequence(), getGenomes(), inEdges, outEdges);
  }
}
