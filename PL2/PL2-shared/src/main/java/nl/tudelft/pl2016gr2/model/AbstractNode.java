package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

public abstract class AbstractNode {

  private final int id;
  private ArrayList<Integer> inLinks;
  private ArrayList<Integer> outLinks;
  private int sequenceLength;

  /**
   * Construct an abstract node.
   *
   * @param id             the id of the node.
   * @param sequenceLength the sequence length of the node.
   */
  public AbstractNode(int id, int sequenceLength) {
    this.id = id;
    this.sequenceLength = sequenceLength;
    this.inLinks = new ArrayList<>();
    this.outLinks = new ArrayList<>();
  }

  @Override
  public String toString() {
    return "id: " + id + " inlinks: " + getInlinks()
        + " outlinks: " + getOutlinks();
  }

  public void replaceInlink(int oldLink, int newLink) {
    inLinks.remove((Integer) oldLink);
    inLinks.add(newLink);
  }
  
  public abstract AbstractNode copyAll();

  public void addInlink(int inlink) {
    inLinks.add(inlink);
  }

  public ArrayList<Integer> getInlinks() {
    return inLinks;
  }

  public void setInlinks(ArrayList<Integer> inlinks) {
    this.inLinks = inlinks;
  }

  public void addOutlink(int outlink) {
    outLinks.add(outlink);
  }

  public ArrayList<Integer> getOutlinks() {
    return outLinks;
  }

  public void setOutlinks(ArrayList<Integer> outlinks) {
    this.outLinks = outlinks;
  }

  public int getSequenceLength() {
    return sequenceLength;
  }

  public void setSequenceLength(int length) {
    sequenceLength = length;
  }

  public int getId() {
    return id;
  }

}
