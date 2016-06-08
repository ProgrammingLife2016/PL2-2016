package nl.tudelft.pl2016gr2.gui.model;

import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.MetaData;

import java.util.Collection;
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
   * @param metaDatas the read annotations.
   */
  public PhylogeneticTreeRoot(TreeNode node, List<MetaData> metaDatas) {
    super(node, null);
    for (PhylogeneticTreeNode leafNode : this) {
      genomeToTreeMap.put(leafNode.getGenomeId(), leafNode);
    }
    initLineages(metaDatas);
  }

  /**
   * Initialize the lineage colors of all of the nodes.
   *
   * @param metaDatas the list of annotations.
   */
  private void initLineages(List<MetaData> metaDatas) {
    for (MetaData metaData : metaDatas) {
      Integer genomeId = GenomeMap.getInstance().getId(metaData.specimenId);
      if (genomeId == null) {
        continue;
      }
      PhylogeneticTreeNode node = genomeToTreeMap.get(genomeId);
      node.setAnnotation(metaData);
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
  public void highlightPaths(Collection<Integer> oldPaths, Collection<Integer> newPaths) {
    for (Integer oldPath : oldPaths) {
      PhylogeneticTreeNode oldSelection = genomeToTreeMap.get(oldPath);
      if (oldSelection != null) {
        oldSelection.unhighlightPath();
      }
    }
    for (Integer newPath : newPaths) {
      PhylogeneticTreeNode newSelection = genomeToTreeMap.get(newPath);
      if (newSelection != null) {
        newSelection.highlightPath();
      }
    }
  }
}
