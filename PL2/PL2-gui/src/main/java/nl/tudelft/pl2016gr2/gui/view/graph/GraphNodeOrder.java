package nl.tudelft.pl2016gr2.gui.view.graph;

import nl.tudelft.pl2016gr2.model.AbstractNode;

/**
 * This class is used to decide the order of the nodes in the graph.
 *
 * @author Faris
 */
public class GraphNodeOrder implements Comparable<GraphNodeOrder> {

  private final AbstractNode node;
  private int level;
  private boolean unmovable = false;

  public GraphNodeOrder(AbstractNode node, int level) {
    this.node = node;
    this.level = level;
  }

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

  @Override
  public int compareTo(GraphNodeOrder other) {
    return this.node.getId() - other.node.getId();
  }
}
