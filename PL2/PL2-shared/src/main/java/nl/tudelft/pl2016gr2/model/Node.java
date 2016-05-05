package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

public class Node extends AbstractNode {

  private ArrayList<String> genomes;
  private int snips;
  private String bases = "";

  public Node(int id, int sequenceLength, ArrayList<String> genomes, int snips) {
    super(id, sequenceLength);
    this.genomes = genomes;
    this.snips = snips;
  }

  @Override
  public String toString() {
    return super.toString() + " snips: " + snips;
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
