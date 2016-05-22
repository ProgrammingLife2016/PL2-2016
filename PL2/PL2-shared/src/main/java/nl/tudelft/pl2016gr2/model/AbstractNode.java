package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

public abstract class AbstractNode {

  private final int identifier;
  private ArrayList<Integer> inLinks;
  private ArrayList<Integer> outLinks;
  private int sequenceLength;
  private boolean inBubble = false;
  private boolean point = false;
  private boolean inDel = false;

  /**
   * Construct an abstract node.
   *
   * @param iden             the id of the node.
   * @param sequenceLength the sequence length of the node.
   */
  public AbstractNode(int iden, int sequenceLength) {
    this.identifier = iden;
    this.sequenceLength = sequenceLength;
    this.inLinks = new ArrayList<>();
    this.outLinks = new ArrayList<>();
  }
  
  public abstract int getGenomesOverEdge(AbstractNode to);

  @Override
  public String toString() {
    return "id: " + identifier + " sequencelength: " + sequenceLength + " inlinks: " + inLinks
        + " outlinks: " + outLinks;
  }

  public void replaceInlink(int oldLink, int newLink) {
    inLinks.remove((Integer) oldLink);
    inLinks.add(newLink);
  }

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
    return identifier;
  }

  /**
   * Return inbubble.
   * @return the inBubble
   */
  public boolean isInBubble() {
    return inBubble;
  }

  /**
   * Set inbubble.
   * @param inBubble the inBubble to set
   */
  public void setInBubble(boolean inBubble) {
    this.inBubble = inBubble;
  }

  public boolean isPoint() {
    return point;
  }

  public void setPoint(boolean point) {
    this.point = point;
  }

  public boolean isInDel() {
    return inDel;
  }

  public void setInDel(boolean inDel) {
    this.inDel = inDel;
  }
}
