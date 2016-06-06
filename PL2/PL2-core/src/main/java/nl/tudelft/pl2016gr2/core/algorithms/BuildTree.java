package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.PhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.PhylogeneticTreeRoot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class BuildTree {

  private IPhylogeneticTreeRoot treeRoot;
  private Set<Integer> genomes;
  private PhylogeneticTreeNode newRoot;

  public BuildTree(IPhylogeneticTreeRoot treeRoot, Collection<Integer> genomes) {
    this.treeRoot = treeRoot;
    this.genomes = new HashSet<>(genomes);
  }

  public IPhylogeneticTreeRoot getTree() {
    if (genomes.isEmpty()) {
      return treeRoot;
    }

    if (newRoot == null) {
      buildTree(treeRoot, null);
    }

    return new PhylogeneticTreeRoot(newRoot, treeRoot.getAnnotations());
  }

  // WARNING, O(n2) algorithm incomming
  private void buildTree(IPhylogeneticTreeNode node, PhylogeneticTreeNode parent) {
    if (node.isLeaf()) {
      PhylogeneticTreeNode newNode = new PhylogeneticTreeNode(node, parent, true);
      addChild(parent, newNode);
    } else {
      IPhylogeneticTreeNode child1 = node.getChild(0);
      boolean child1HasGenome = containsGenome(child1.getGenomes());
      IPhylogeneticTreeNode child2 = node.getChild(1);
      boolean child2HasGenome = containsGenome(child2.getGenomes());

      // Both branches contain at least one genome
      if (child1HasGenome && child2HasGenome) {
        PhylogeneticTreeNode copyNode = new PhylogeneticTreeNode(node, parent);
        addChild(parent, copyNode);
        buildTree(child1, copyNode);
        buildTree(child2, copyNode);
      } else if (child1HasGenome) {
        buildTree(child1, parent);
      } else if (child2HasGenome) {
        buildTree(child2, parent);
      }
    }
  }

  private void addChild(PhylogeneticTreeNode parent, PhylogeneticTreeNode node) {
    if (parent == null) {
      newRoot = node;
    } else {
      parent.addChild(node);
    }
  }

  private boolean containsGenome(ArrayList<Integer> leaves) {
    for (Integer leaf : leaves) {
      if (genomes.contains(leaf)) {
        return true;
      }
    }
    return false;
  }
}
