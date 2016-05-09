package nl.tudelft.pl2016gr2.model;


/**
 * This class represents a bubble. A bubble exists of multiple nodes which form a common group.
 *
 * @author Cas
 */
public class Bubble extends AbstractNode {

  private int level;

  /**
   * Construct a bubble.
   *
   * @param id             the ID of the bubble.
   * @param sequenceLength the sequence length.
   */
  public Bubble(int id, int sequenceLength) {
    super(id, sequenceLength);
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
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
