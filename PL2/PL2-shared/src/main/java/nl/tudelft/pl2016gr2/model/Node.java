package nl.tudelft.pl2016gr2.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * Mainly a dataholder class which represents a Node.
 *
 * @author Cas
 *
 */
public class Node extends Bubble {

  private double flow;

  private HashSet<String> genomes;
  private final int snips;
  private String bases = "";
  private int alignment;

  /**
   * Construct a new node.
   *
   * @param id             the id (index) of the node.
   * @param sequenceLength the sequence length.
   * @param genomes        the list of genomes.
   * @param snips          the amount of snips.
   */
  public Node(int id, int sequenceLength, Collection<String> genomes, int snips) {
    super(id, sequenceLength);
    setGenomes(genomes);
    this.snips = snips;
    this.flow = 0;
    setLevel(0);
  }

  @Override
  public String toString() {
    return super.toString() + " snips: " + snips + ", id: " + getId() + ", flow: " + flow;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Node) {
      Node node = (Node) object;
      return node.getId() == this.getId() && node.getOutlinks().equals(this.getOutlinks())
          && node.getInlinks().equals(this.getInlinks());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 41 * hash + this.getId();
    return hash;
  }

  public Collection<String> getGenomes() {
    return genomes;
  }

  /**
   * Get the amount of genomes which travel from this node over an edge to the given node.
   *
   * @param to the node to which the edge goes.
   * @return the amount of genomes which travel over the given edge.
   */
  @Override
  public int getGenomesOverEdge(AbstractNode to) {
    assert getOutlinks().contains(to.getId());
    int genomeCount = 0;
    for (String genome : getGenomes()) {
      if (((Node) to).genomes.contains(genome)) {
        genomeCount++;
      }
    }
    return genomeCount;
  }

  /**
   * Set the genomes of this node.
   *
   * @param genomes the genomes.
   */
  public final void setGenomes(Collection<String> genomes) {
    this.genomes = new HashSet<>();
    for (String genome : genomes) {
      this.genomes.add(genome);
    }
  }

  public String getBases() {
    return this.bases;
  }

  /**
   * Set the bases.
   *
   * @param bases the bases.
   */
  public void setBases(String bases) {
    setSequenceLength(bases.length());
    this.bases = bases;
  }

  public int getSnips() {
    return snips;
  }

  public double getFlow() {
    return flow;
  }

  public void setFlow(double flow) {
    this.flow = flow;
  }

  public void addFlow(double flow) {
    this.flow += flow;
  }

  public int getAlignment() {
    return alignment;
  }

  public void setAlignment(int alignment) {
    this.alignment = alignment;
  }

}
