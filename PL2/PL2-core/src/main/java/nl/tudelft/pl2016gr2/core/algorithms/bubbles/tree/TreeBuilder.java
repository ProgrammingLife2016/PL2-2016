package nl.tudelft.pl2016gr2.core.algorithms.bubbles.tree;

import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.phylogenetictree.PhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.PhylogeneticTreeRoot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class to build a new phylogenetictree given an existing phylogenetic
 * tree and a set of genomes that should be in the new tree.
 * 
 * @author Casper
 *
 */
public class TreeBuilder {

  private final IPhylogeneticTreeRoot<?> treeRoot;
  private final Set<Integer> genomes;
  private PhylogeneticTreeNode newRoot;

  /**
   * Constructs an object to build a new tree.
   * 
   * @param treeRoot : the root of the existing tree, which is used for
   *     the new tree.
   * @param genomes : the list of genomes that should be in the new tree.
   */
  public TreeBuilder(IPhylogeneticTreeRoot<?> treeRoot, Collection<Integer> genomes) {
    this.treeRoot = treeRoot;
    this.genomes = new HashSet<>(genomes);
  }

  /**
   * Builds a new tree, based on the existing tree and the given genomes.
   * 
   * @return the root of the new tree.
   */
  public IPhylogeneticTreeRoot<?> getTree() {
    if (genomes.isEmpty()) {
      return treeRoot;
    }

    if (newRoot == null) {
      buildTree(treeRoot, null);
    }

    return new PhylogeneticTreeRoot(newRoot, treeRoot.getMetaDatas());
  }

  // WARNING, O(n2) algorithm incomming
  private void buildTree(IPhylogeneticTreeNode<?> node, PhylogeneticTreeNode parent) {
    if (node.isLeaf()) {
      PhylogeneticTreeNode newNode = new PhylogeneticTreeNode(node.getGenomeId(), parent,
          node.getEdgeLength());
      addChild(parent, newNode);
    } else {
      IPhylogeneticTreeNode<?> child1 = node.getChild(0);
      boolean child1HasGenome = containsGenome(child1.getGenomes());
      IPhylogeneticTreeNode<?> child2 = node.getChild(1);
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
