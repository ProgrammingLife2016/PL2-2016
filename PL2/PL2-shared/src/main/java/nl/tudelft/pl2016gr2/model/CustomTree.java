package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.test.utility.TestId;

import java.util.ArrayList;

public class CustomTree implements IPhylogeneticTreeNode {
  
  private boolean isLeaf;
  private String label;
  private IPhylogeneticTreeNode child1;
  private IPhylogeneticTreeNode child2;
  public IPhylogeneticTreeNode parent;
  private boolean isRoot;
  
  public CustomTree(String label) {
    this.label = label;
    isLeaf = true;
    isRoot = false;
  }
  
  public CustomTree(IPhylogeneticTreeNode child1, IPhylogeneticTreeNode child2, boolean isRoot) {
    this.child1 = child1;
    CustomTree customChild = (CustomTree) child1;
    customChild.parent = this;
    this.child2 = child2;
    CustomTree customChild2 = (CustomTree) child2;
    customChild2.parent = this;
    isLeaf = false;
    this.isRoot = isRoot;
    
  }
  
  @Override
  public void print() {
    System.out.println(this.label);
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public boolean isLeaf() {
    return isLeaf;
  }

  @Override
  public boolean hasParent() {
    return !isRoot;
  }

  @Override
  public IPhylogeneticTreeNode getParent() {
    return parent;
  }

  @Override
  public int getDirectChildCount() {
    return 2;
  }

  @Override
  public int getChildCount() {
    return 0;
  }

  @Override
  public IPhylogeneticTreeNode getChild(int index) {
    if (index == 0) {
      return child1;
    } else {
      return child2;
    }
  }

  @Override
  public int getChildIndex(IPhylogeneticTreeNode child) {
    return 0;
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
