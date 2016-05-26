package nl.tudelft.pl2016gr2.model;

public class BubblePosition implements Position {
  
  private SemanticBubble bubble;

  /**
   * If this node is overlapping with a node from the other graph.
   */
  private boolean overlapping;

  /**
   * The level of this node (the depth in the graph.
   */
  private int level;
  
  public BubblePosition(SemanticBubble node, int level) {
    this.level = level;
    this.setBubble(node);
    this.overlapping = false;
  }
  
  public int size() {
    return this.bubble.size();
  }

  /**
   * Add an offset to the current level of the node.
   *
   * @param offset the offset.
   */
  public void addPositionOffset(int offset) {
    level += offset;
  }

  public int getLevel() {
    return level;
  }                               

  public void setOverlapping(boolean overlapping) {
    this.overlapping = overlapping;
  }

  public boolean isOverlapping() {
    return overlapping;
  }

  /**
   * @return the bubble
   */
  public SemanticBubble getBubble() {
    return bubble;
  }

  /**
   * @param node the bubble to set
   */
  public void setBubble(SemanticBubble node) {
    this.bubble = node;
  }

  @Override
  public int compareTo(Position other) {
   return this.level - other.getLevel();
  }
  

}
