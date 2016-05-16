package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

/**
 * Mainly a dataholder class which represents a Node.
 *
 * @author Cas
 *
 */
public class Node extends Bubble {

  private double flow;

  private ArrayList<String> genomes;
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
  public Node(int id, int sequenceLength, ArrayList<String> genomes, int snips) {
    super(id, sequenceLength);
    this.genomes = genomes;
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

  public ArrayList<String> getGenomes() {
    return genomes;
  }

  public void setGenomes(ArrayList<String> gs) {
    this.genomes = gs;
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
