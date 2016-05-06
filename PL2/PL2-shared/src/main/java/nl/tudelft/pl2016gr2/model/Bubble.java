package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

/**
 * This class represents a bubble. A bubble exists of multiple nodes which form a common group.
 *
 * @author Cas
 */
public class Bubble extends AbstractNode {

  private int level;
  private final ArrayList<Integer> nestedBubbles;
  private ArrayList<Integer> inLinks;
  private ArrayList<Integer> outLinks;
  private Bubble startBubble;
  private Bubble endBubble;

  /**
   * Construct a bubble.
   *
   * @param id             the ID of the bubble.
   * @param sequenceLength the sequence length.
   */
  public Bubble(int id, int sequenceLength) {
    super(id, sequenceLength);
    nestedBubbles = new ArrayList<>();
    inLinks = new ArrayList<>();
    outLinks = new ArrayList<>();
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void setStartBubble(Bubble bubble) {
    this.startBubble = bubble;
  }

  public Bubble getStartBubble() {
    return startBubble;
  }

  public void setEndBubble(Bubble bubble) {
    this.endBubble = bubble;
  }

  public Bubble getEndBubble() {
    return endBubble;
  }

  public ArrayList<Integer> getInLinks() {
    return inLinks;
  }

  public void addInLink(int bubble) {
    inLinks.add(bubble);
  }

  public ArrayList<Integer> getOutLinks() {
    return outLinks;
  }

  public void setInLinks(ArrayList<Integer> inLinks) {
    this.inLinks = inLinks;
  }

  public void setOutLinks(ArrayList<Integer> outLinks) {
    this.outLinks = outLinks;
  }

  public void addOutLink(int bubble) {
    outLinks.add(bubble);
  }

  public ArrayList<Integer> getNestedBubbles() {
    return nestedBubbles;
  }

  public void addNestedBubble(int bubble) {
    nestedBubbles.add(bubble);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Bubble) {
      Bubble bubble = (Bubble) object;
      return bubble.getId() == this.getId() && bubble.level == this.level;
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 41 * hash + this.getId();
    return hash;
  }
}
