package nl.tudelft.pl2016gr2.gui.model;

import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;
import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;

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

  private final HashMap<Integer, PhylogeneticTreeNode> genomeToTreeMap = new HashMap<>();

  /**
   * Construct a phylogenetic tree root node.
   *
   * @param node        the root node of the parsed tree.
   * @param annotations the read annotations.
   */
  public PhylogeneticTreeRoot(TreeNode node, List<Annotation> annotations) {
    super(node, null);
    for (PhylogeneticTreeNode leafNode : this) {
      genomeToTreeMap.put(leafNode.getGenomeId(), leafNode);
    }
    initLineages(annotations);
  }

  /**
   * Initialize the lineage colors of all of the nodes.
   *
   * @param annotations the list of annotations.
   */
  private void initLineages(List<Annotation> annotations) {
    for (Annotation annotation : annotations) {
      Integer genomeId = GenomeMap.getInstance().getId(annotation.specimenId);
      if (genomeId == null) {
        continue;
      }
      PhylogeneticTreeNode node = genomeToTreeMap.get(genomeId);
      node.setAnnotation(annotation);
    }
  }

  @Override
  public void setDrawnInTop(int genome, boolean isDrawn) {
    genomeToTreeMap.get(genome).setDrawnInTop(isDrawn);
  }

  @Override
  public void setDrawnInBottom(int genome, boolean isDrawn) {
    genomeToTreeMap.get(genome).setDrawnInBottom(isDrawn);
  }

  @Override
  public void highlightPath(int oldPath, int newPath) {
    PhylogeneticTreeNode oldSelection = genomeToTreeMap.get(oldPath);
    PhylogeneticTreeNode newSelection = genomeToTreeMap.get(newPath);
    if (oldSelection != null) {
      oldSelection.unhighlightPath();
    }
    if (newSelection != null) {
      newSelection.highlightPath();
    }
  }
}
