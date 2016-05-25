package nl.tudelft.pl2016gr2.gui.model;

import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

import java.util.HashMap;

/**
 * This phylogenetic tree root keeps track of all of the leaves in the phylogenetic tree. It
 * contains a mapping of genome names to the corresponding tree nodes, to allow for efficient
 * searching of phylogenetic tree nodes by genome name.
 *
 * @author Faris
 */
public class PhylogeneticTreeRoot extends PhylogeneticTreeNode implements IPhylogeneticTreeRoot {

  private final HashMap<String, PhylogeneticTreeNode> genomeToTreeMap = new HashMap<>();

  /**
   * Construct a phylogenetic tree root node.
   *
   * @param node the root node of the parsed tree.
   */
  public PhylogeneticTreeRoot(TreeNode node) {
    super(node, null);
    for (PhylogeneticTreeNode leafNode : this) {
      genomeToTreeMap.put(leafNode.getLabel(), leafNode);
    }
  }

  @Override
  public void setDrawnInTop(String genome, boolean isDrawn) {
    genomeToTreeMap.get(genome).setDrawnInTop(isDrawn);
  }

  @Override
  public void setDrawnInBottom(String genome, boolean isDrawn) {
    genomeToTreeMap.get(genome).setDrawnInBottom(isDrawn);
  }
}
