package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;


/**
 * This class represents a bubble. A bubble exists of multiple nodes which form a common group.
 *
 * @author Cas
 */
public class Bubble extends AbstractNode {

  private IPhylogeneticTreeNode treeNode;
  private ArrayList<Integer> nestedNodes = new ArrayList<>();

  /**
   * Construct a bubble.
   *
   * @param id             the ID of the bubble.
   * @param sequenceLength the sequence length.
   */
  public Bubble(int id, int sequenceLength) {
    super(id, sequenceLength);
  }
  
  @Override
  public String toString() {
    String leaves = treeNode != null ? treeNode.getLeaves().toString() : "no phylo";
    return super.toString() + " | " + nestedNodes + ", phylo node: " + leaves;
  }
  
  public void setTreeNode(IPhylogeneticTreeNode treeNode) {
    this.treeNode = treeNode;
  }
  
  public IPhylogeneticTreeNode getTreeNode() {
    return treeNode;
  }
  
  public void addNestedNode(int node) {
    nestedNodes.add(node);
  }
  
  public ArrayList<Integer> getNestedNodes() {
    return nestedNodes;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Bubble) {
      Bubble bubble = (Bubble) object;
      return bubble.getId() == this.getId() && bubble.treeNode.equals(this.treeNode);
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
