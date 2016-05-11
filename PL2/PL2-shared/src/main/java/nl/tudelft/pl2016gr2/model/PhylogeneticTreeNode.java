package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.test.utility.TestId;

import java.util.ArrayList;
import java.util.Objects;

/**
 * This class is an adapter class which maps the methods of the
 * net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode class to the interface which is needed by
 * our application.
 *
 * @author Faris
 */
public class PhylogeneticTreeNode implements IPhylogeneticTreeNode {

  private final net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode node;

  /**
   * Construct a phylogenetic tree node.
   *
   * @param node the TreeNode of this node.
   */
  public PhylogeneticTreeNode(net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode node) {
    this.node = node;
  }
  
  @Override
  public String toString() {
    return node.label;
  }
  
  @Override
  public void print() {
    node.print();
  }
  
  @Override
  public String getLabel() {
    return node.label;
  }

  @Override
  public boolean isLeaf() {
    return node.isLeaf();
  }
  
  @Override
  public boolean hasParent() {
    return !node.isRoot();
  }

  @Override
  public IPhylogeneticTreeNode getParent() {
    return new PhylogeneticTreeNode(node.parent());
  }

  @Override
  public int getDirectChildCount() {
    return node.numberChildren();
  }

  @Override
  public int getChildCount() {
    int total = node.numberChildren();

    for (int i = 0; i < getDirectChildCount(); i++) {
      total += new PhylogeneticTreeNode(node.getChild(i)).getChildCount();
    }

    return total;
  }

  @Override
  public IPhylogeneticTreeNode getChild(int index) {
    return new PhylogeneticTreeNode(node.getChild(index));
  }

  @Override
  public int getChildIndex(IPhylogeneticTreeNode child) {
    int idx = 0;
    while (true) {
      if (getChild(idx).equals(child)) {
        return idx;
      }
      ++idx;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PhylogeneticTreeNode other = (PhylogeneticTreeNode) obj;
    return this.node == other.node;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 41 * hash + Objects.hashCode(this.node);
    return hash;
  }
  
  @Override
  public ArrayList<String> getLeaves() {
    ArrayList<String> leaves = new ArrayList<>();
    addLeaf(leaves, this);    
    return leaves;
  }
  
  /**
   * Recursively walks through the phylogenetic tree, and adds the label of
   * a node to the list of leaves when it is a leaf. 
   * @param leaves : the resulting list of leaves.
   * @param node : the current node.
   */
  @TestId(id = "method_addLeaf")
  private void addLeaf(ArrayList<String> leaves, IPhylogeneticTreeNode node) {
    if (node.isLeaf()) {
      leaves.add(node.getLabel());
      return;
    }
    
    for (int i = 0; i < node.getDirectChildCount(); i++) {
      addLeaf(leaves, node.getChild(i));
    }
  }

}
