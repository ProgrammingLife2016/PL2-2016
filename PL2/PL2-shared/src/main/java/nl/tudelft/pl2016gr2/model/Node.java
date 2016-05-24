package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

/**
 * Mainly a dataholder class which represents a Node.
 *
 * @author Cas
 *
 */
public class Node extends Bubble {

  private ArrayList<String> genomes;
  private final int snips;
  private String bases = "";

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
  }

  @Override
  public String toString() {
    return super.toString() + " " + this.genomes;
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
  
  public Node copy() {
    return new Node(getId(), getSequenceLength(), this.genomes, this.snips);
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

  public void setBases(String bs) {
    this.bases = bs;
  }

  public int getSnips() {
    return snips;
  }

}
