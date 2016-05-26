package nl.tudelft.pl2016gr2.model;

/**
 * This class is used to decide the order of the nodes in the graph.
 *
 * @author Faris
 */
public class NodePosition implements Comparable<NodePosition> {

  /**
   * The node of this NodePosition.
   */
  private final GraphNode node;

  /**
   * If this node is overlapping with a node from the other graph.
   */
  private boolean overlapping;

  /**
   * The level of this node (the depth in the graph.
   */
  private int level;

  /**
   * Construct a graph node order.
   *
   * @param node  the node.
   * @param level the initial level of the node (depth in the graph).
   */
  public NodePosition(GraphNode node, int level) {
    this.node = node;
    this.level = level;
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

  public GraphNode getNode() {
    return node;
  }

  public void setOverlapping(boolean overlapping) {
    this.overlapping = overlapping;
  }

  public boolean isOverlapping() {
    return overlapping;
  }

  @Override
  public int compareTo(NodePosition other) {
    return this.level - other.level;
  }
}
