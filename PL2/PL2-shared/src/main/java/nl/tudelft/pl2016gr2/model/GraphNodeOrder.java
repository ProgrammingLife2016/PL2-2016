package nl.tudelft.pl2016gr2.model;

/**
 * This class is used to decide the order of the nodes in the graph.
 *
 * @author Faris
 */
public class GraphNodeOrder implements Comparable<GraphNodeOrder> {

  /**
   * The node of this GraphNodeOrder.
   */
  private final AbstractNode node;

  /**
   * If this node is overlapping with a node from the other graph.
   */
  private boolean overlapping;

  /**
   * The level of this node (the depth in the graph.
   */
  private int level;

  /**
   * If this node can be moved to a different level. This is usefull if a node is aligned and may
   * not be moved anymore.
   */
  private boolean unmovable = false;

  /**
   * Construct a graph node order.
   *
   * @param node  the node.
   * @param level the initial level of the node (depth in the graph).
   */
  public GraphNodeOrder(AbstractNode node, int level) {
    this.node = node;
    this.level = level;
  }

  /**
   * Make it impossible to move the node to a different level. This is usefull if a node is aligned
   * and may not be moved anymore.
   */
  public void setUnmovable() {
    unmovable = true;
  }

  /**
   * Add an offset to the current level of the node.
   *
   * @param offset the offset.
   */
  public void addPositionOffset(int offset) {
    if (!unmovable) {
      level += offset;
    }
  }

  public int getLevel() {
    return level;
  }

  public AbstractNode getNode() {
    return node;
  }

  public void setOverlapping(boolean overlapping) {
    this.overlapping = overlapping;
  }

  public boolean isOverlapping() {
    return overlapping;
  }

  @Override
  public int compareTo(GraphNodeOrder other) {
    return this.level - other.level;
  }
}
