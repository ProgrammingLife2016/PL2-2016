package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;

public class PhyloBubble extends Bubble {

  private final IPhylogeneticTreeNode treeNode;
  private final PhyloFilter filter;
  private ArrayList<GraphNode> poppedNodes;

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter) {
    super(id);
    this.treeNode = treeNode;
    this.filter = filter;
  }

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(id, inEdges, outEdges);
    this.treeNode = treeNode;
    this.filter = filter;
  }

  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges,
      Collection<GraphNode> nestedNodes) {
    super(id, inEdges, outEdges, nestedNodes);
    this.treeNode = treeNode;
    this.filter = filter;
  }
  
  // constructor for copying bubbles
  private PhyloBubble(IPhylogeneticTreeNode treeNode, PhyloFilter filter, Bubble bubble) {
    super(bubble);
    this.treeNode = treeNode;
    this.filter = filter;
  }

  public IPhylogeneticTreeNode getTreeNode() {
    return treeNode;
  }

  @Override
  public GraphNode copy() {
    return new PhyloBubble(getId(), treeNode, filter);
  }

  @Override
  public GraphNode copyAll() {
    return new PhyloBubble(treeNode, filter, this);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Collection<GraphNode> pop(SequenceGraph graph) {
    if (poppedNodes == null) {
      poppedNodes = new ArrayList<>(filter.zoomIn(this, graph));
      poppedNodes.sort((GraphNode node1, GraphNode node2) -> node1.getLevel() - node2.getLevel());
    }
    return poppedNodes;
  }

  @Override
  public String toString() {
    return super.toString() + ", tree leaves: " + treeNode.getGenomes();
  }
}
