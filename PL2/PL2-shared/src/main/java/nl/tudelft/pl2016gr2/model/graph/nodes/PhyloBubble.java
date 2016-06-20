package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;
import java.util.HashSet;

public class PhyloBubble extends AbstractGraphBubble {

  private final IPhylogeneticTreeNode treeNode;

  /**
   * Construct a phylo bubble.
   *
   * @param id       the id of the bubble.
   * @param treeNode the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter   the filter object to call when zooming in.
   */
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, BubbleFilter filter) {
    super(id, filter);
    this.treeNode = treeNode;
  }

  /**
   * Construct a phylo bubble.
   *
   * @param id       the id of the bubble.
   * @param treeNode the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter   the filter object to call when zooming in.
   * @param inEdges  the in edges of the bubble.
   * @param outEdges the out edges of the bubble.
   */
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, BubbleFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(id, filter, inEdges, outEdges);
    this.treeNode = treeNode;
  }

  /**
   * Construct a phylo bubble.
   *
   * @param id          the id of the bubble.
   * @param treeNode    the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter      the filter object to call when zooming in.
   * @param inEdges     the in edges of the bubble.
   * @param outEdges    the out edges of the bubble.
   * @param nestedNodes the nested nodes of the bubble.
   */
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, BubbleFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges, 
      HashSet<GraphNode> nestedNodes) {
    super(id, filter, inEdges, outEdges, nestedNodes);
    this.treeNode = treeNode;
  }

  /**
   * Copy a phylo bubble.
   *
   * @param bubble   the super class of the phylo bubble to copy.
   * @param treeNode the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter   the filter object to call when zooming in.
   */
  private PhyloBubble(Bubble bubble, IPhylogeneticTreeNode treeNode, BubbleFilter filter) {
    super(bubble, filter);
    this.treeNode = treeNode;
  }

  /**
   * Get the phylogenetic tree node.
   *
   * @return the phylogenetic tree node.
   */
  public IPhylogeneticTreeNode getTreeNode() {
    return treeNode;
  }

  @Override
  public GraphNode copy() {
    return new PhyloBubble(getId(), treeNode, getFilter());
  }

  @Override
  public GraphNode copyAll() {
    return new PhyloBubble(this, treeNode, getFilter());
  }

  @Override
  public String toString() {
    return String.format("%s: \n%s", "Phylogenetic Bubble", super.toString());
  }
  
  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }
}
