package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;
import java.util.HashSet;

public class GraphBubble extends AbstractGraphBubble {

  /**
   * Construct a graph bubble.
   *
   * @param id     the id of the bubble.
   * @param filter the filter object to call when zooming in.
   */
  public GraphBubble(int id, BubbleFilter filter) {
    super(id, filter);
  }

  /**
   * Construct a graph bubble.
   *
   * @param id       the id of the bubble.
   * @param filter   the filter object to call when zooming in.
   * @param inEdges  the in edges of the bubble.
   * @param outEdges the out edges of the bubble.
   */
  public GraphBubble(int id, BubbleFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(id, filter, inEdges, outEdges);
  }

  /**
   * Construct a graph bubble.
   *
   * @param id          the id of the bubble.
   * @param filter      the filter object to call when zooming in.
   * @param inEdges     the in edges of the bubble.
   * @param outEdges    the out edges of the bubble.
   * @param nestedNodes the nested nodes of the bubble.
   */
  public GraphBubble(int id, BubbleFilter filter, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, HashSet<GraphNode> nestedNodes) {
    super(id, filter, inEdges, outEdges, nestedNodes);
  }

  /**
   * Copy a graph bubble.
   *
   * @param bubble the super class of the phylo bubble to copy.
   * @param filter the filter object to call when zooming in.
   */
  private GraphBubble(Bubble bubble, BubbleFilter filter) {
    super(bubble, filter);
  }

  @Override
  public GraphNode copy() {
    return new GraphBubble(getId(), getFilter());
  }

  @Override
  public GraphNode copyAll() {
    return new GraphBubble(this, getFilter());
  }

  @Override
  public String toString() {
    return String.format("%s: \n%s", "Random Bubble", super.toString());
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public int getGenomeSize() {
    int count = 0;
    for (GraphNode inEdge : getInEdges()) {
      count += inEdge.getGenomeSize();
    }
    return count;
  }
}
