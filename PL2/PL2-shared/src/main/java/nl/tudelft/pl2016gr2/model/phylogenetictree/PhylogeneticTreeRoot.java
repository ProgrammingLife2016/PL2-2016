package nl.tudelft.pl2016gr2.model.phylogenetictree;

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
  private final List<MetaData> metaDatas;

  /**
   * Construct a phylogenetic tree root node.
   *
   * @param node      the root node of the parsed tree.
   * @param metaDatas the read metadata.
   */
  public PhylogeneticTreeRoot(TreeNode node, List<MetaData> metaDatas) {
    super(node, null);
    for (PhylogeneticTreeNode leafNode : this) {
      genomeToTreeMap.put(leafNode.getGenomeId(), leafNode);
    }
    this.metaDatas = metaDatas;
    initLineages(this.metaDatas);
  }

  /**
   * Construct a phylogenetic tree root node using a iphylogenetictreenode.
   *
   * @param node      the root node of the parsed tree.
   * @param metaDatas the read metaDatas.
   */
  public PhylogeneticTreeRoot(IPhylogeneticTreeNode node, List<MetaData> metaDatas) {
    super(node);
    for (PhylogeneticTreeNode leafNode : this) {
      genomeToTreeMap.put(leafNode.getGenomeId(), leafNode);
    }
    this.metaDatas = metaDatas;
    initLineages(metaDatas);
  }

  /**
   * Initialize the lineage colors of all of the nodes.
   *
   * @param metaDatas the list of metaDatas.
   */
  private void initLineages(List<MetaData> metaDatas) {
    for (MetaData metaData : metaDatas) {
      Integer genomeId = GenomeMap.getInstance().getId(metaData.specimenId);
      if (genomeId == null) {
        continue;
      }
      PhylogeneticTreeNode node = genomeToTreeMap.get(genomeId);
      if (node != null) {
        node.setMetaData(metaData);
      }
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
  public List<MetaData> getMetaDatas() {
    return metaDatas;
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
