package nl.tudelft.pl2016gr2.gui.model;

import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;
import nl.tudelft.pl2016gr2.model.Annotation;

import java.util.HashMap;
import java.util.List;

/**
 * This phylogenetic tree root keeps track of all of the leaves in the phylogenetic tree. It
 * contains a mapping of genome names to the corresponding tree nodes, to allow for efficient
 * searching of phylogenetic tree nodes by genome name.
 *
 * @author Faris
 */
public class PhylogeneticTreeRoot extends PhylogeneticTreeNode implements IPhylogeneticTreeRoot {

  private final HashMap<String, PhylogeneticTreeNode> genomeToTreeMap = new HashMap<>();
  private final List<Annotation> annotations;

  /**
   * Construct a phylogenetic tree root node.
   *
   * @param node        the root node of the parsed tree.
   * @param annotations the read annotations.
   */
  public PhylogeneticTreeRoot(TreeNode node, List<Annotation> annotations) {
    super(node, null);
    this.annotations = annotations;
    for (PhylogeneticTreeNode leafNode : this) {
      genomeToTreeMap.put(leafNode.getLabel(), leafNode);
    }
    initLineages();
  }

  /**
   * Initialize the lineage colors of all of the nodes.
   */
  private void initLineages() {
    for (Annotation annotation : annotations) {
      PhylogeneticTreeNode node = genomeToTreeMap.get(annotation.specimenId);
      if (node == null) {
        continue;
      }
      node.setLineage(LineageColor.toLineage(annotation.lineage));
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
