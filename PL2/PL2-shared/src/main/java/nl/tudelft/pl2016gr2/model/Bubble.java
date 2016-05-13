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
  private Bubble startBubble;
  private Bubble endBubble;

  /**
   * Construct a bubble.
   *
   * @param identifier the ID of the bubble.
   * @param sequenceLength the sequence length.
   */
  public Bubble(int identifier, int sequenceLength) {
    super(identifier, sequenceLength);
    nestedBubbles = new ArrayList<>();
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
