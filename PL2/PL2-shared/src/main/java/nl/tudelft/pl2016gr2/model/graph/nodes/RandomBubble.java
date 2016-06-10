package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;
import java.util.List;

public class RandomBubble extends GraphBubble {

  /**
   * Construct a phylo bubble.
   *
   * @param id       the id of the bubble.
   * @param filter   the filter object to call when zooming in.
   */
  public RandomBubble(int id, PhyloFilter filter) {
    super(id, filter);
  }

  /**
   * Construct a phylo bubble.
   *
   * @param id       the id of the bubble.
   * @param filter   the filter object to call when zooming in.
   * @param inEdges  the in edges of the bubble.
   * @param outEdges the out edges of the bubble.
   */
  public RandomBubble(int id, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(id, filter, inEdges, outEdges);
  }

  /**
   * Construct a phylo bubble.
   *
   * @param id          the id of the bubble.
   * @param filter      the filter object to call when zooming in.
   * @param inEdges     the in edges of the bubble.
   * @param outEdges    the out edges of the bubble.
   * @param nestedNodes the nested nodes of the bubble.
   */
  public RandomBubble(int id, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges, List<GraphNode> nestedNodes) {
    super(id, filter, inEdges, outEdges, nestedNodes);
  }

  /**
   * Copy a phylo bubble.
   *
   * @param bubble   the super class of the phylo bubble to copy.
   * @param treeNode the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter   the filter object to call when zooming in.
   */
  private RandomBubble(Bubble bubble, PhyloFilter filter) {
    super(bubble, filter);
  }

  @Override
  public GraphNode copy() {
    return new RandomBubble(getId(), getFilter());
  }

  @Override
  public GraphNode copyAll() {
    return new RandomBubble(this, getFilter());
  }

  @Override
  public String toString() {
    return String.format("%s: \n%s", "Random Bubble", super.toString());
  }
  
  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }
}


