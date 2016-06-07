package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class PhyloBubble extends Bubble {

  private final IPhylogeneticTreeNode treeNode;

  private final PhyloFilter filter;
  private Collection<GraphNode> poppedNodes;
  private boolean isPopped;

  private final HashMap<Integer, Collection<GraphNode>> originalOutEdges = new HashMap<>();
  private final HashMap<Integer, Collection<GraphNode>> originalInEdges = new HashMap<>();
  private final HashMap<Integer, Collection<GraphNode>> unpoppedOutEdges = new HashMap<>();
  private final HashMap<Integer, Collection<GraphNode>> unpoppedInEdges = new HashMap<>();

  /**
   * Construct a phylo bubble.
   *
   * @param id       the id of the bubble.
   * @param treeNode the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter   the filter object to call when zooming in.
   */
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter) {
    super(id);
    this.treeNode = treeNode;
    this.filter = filter;
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
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(id, inEdges, outEdges);
    this.treeNode = treeNode;
    this.filter = filter;
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
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges,
      Collection<GraphNode> nestedNodes) {
    super(id, inEdges, outEdges, nestedNodes);
    this.treeNode = treeNode;
    this.filter = filter;
  }

  /**
   * Copy a phylo bubble.
   *
   * @param bubble   the super class of the phylo bubble to copy.
   * @param treeNode the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter   the filter object to call when zooming in.
   */
  private PhyloBubble(Bubble bubble, IPhylogeneticTreeNode treeNode, PhyloFilter filter) {
    super(bubble);
    this.treeNode = treeNode;
    this.filter = filter;
  }

  @Override
  public boolean needsVerticalAligning() {
    return poppedNodes == null;
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
    return new PhyloBubble(getId(), treeNode, filter);
  }

  @Override
  public GraphNode copyAll() {
    return new PhyloBubble(this, treeNode, filter);
  }

  @Override
  public Collection<GraphNode> pop() {
    if (!isPopped) {
      isPopped = true;
      if (poppedNodes == null) {
        for (GraphNode node : getInEdges()) {
          originalOutEdges.put(node.getId(), new HashSet<>(node.getOutEdges()));
        }
        for (GraphNode node : getOutEdges()) {
          originalInEdges.put(node.getId(), new HashSet<>(node.getInEdges()));
        }
        poppedNodes = filter.zoomIn(this);
      } else {
        for (GraphNode node : getInEdges()) {
          node.setOutEdges(unpoppedOutEdges.get(node.getId()));
        }
        for (GraphNode node : getOutEdges()) {
          node.setInEdges(unpoppedInEdges.get(node.getId()));
        }
      }
    }
    return poppedNodes;
  }

  @Override
  public void unpop() {
    if (isPopped) {
      isPopped = false;
      for (GraphNode node : getInEdges()) {
        unpoppedOutEdges.put(node.getId(), new HashSet<>(node.getOutEdges()));
      }
      for (GraphNode node : getOutEdges()) {
        unpoppedInEdges.put(node.getId(), new HashSet<>(node.getInEdges()));
      }
      for (GraphNode node : getInEdges()) {
        node.setOutEdges(originalOutEdges.get(node.getId()));
      }
      for (GraphNode node : getOutEdges()) {
        node.setInEdges(originalInEdges.get(node.getId()));
      }
    }
  }

  @Override
  public boolean isPopped() {
    return isPopped;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }
}
