package nl.tudelft.pl2016gr2.gui.model;

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
  public ArrayList<String> getGenomes() {
    ArrayList<String> res = new ArrayList<>();
    if (node.isLeaf()) {
      res.add(node.label);
    } else {
      for (int i = 0; i < node.numberChildren(); i++) {
        res.addAll(new PhylogeneticTreeNode(node.getChild(i)).getGenomes());
      }
    }
    return res;
  }

  @Override
  public double getEdgeLength() {
    return node.weight;
  }

  @Override
  public boolean isLeaf() {
    return node.numberChildren() == 0;
  }
}
