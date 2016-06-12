package nl.tudelft.pl2016gr2.model.phylogenetictree;

import nl.tudelft.pl2016gr2.model.MetaData;

import java.util.Collection;
import java.util.List;

/**
 * This interface must be implemented by the root of the phylogenetic tree.
 *
 * @author Faris
 * @param <T> the type of the iterator.
 */
public interface IPhylogeneticTreeRoot<T extends IPhylogeneticTreeNode> 
    extends IPhylogeneticTreeNode<T> {

  /**
   * Notify the tree root that a genome has been added to (isDrawn = true), or removed from (isDrawn
   * = false) the top graph.
   *
   * @param genome  the genome which has been added or removed.
   * @param isDrawn if the given genome is added or removed.
   */
  void setDrawnInTop(int genome, boolean isDrawn);

  /**
   * Notify the tree root that a genome has been added to (isDrawn = true), or removed from (isDrawn
   * = false) the bottom graph.
   *
   * @param genome  the genome which has been added or removed.
   * @param isDrawn if the given genome is added or removed.
   */
  void setDrawnInBottom(int genome, boolean isDrawn);

  /**
   * Get the annotations of all tree nodes.
   *
   * @return the annotations of all tree nodes.
   */
  List<MetaData> getMetaDatas();

  /**
   * Highlight a path in the phylogenetic tree.
   *
   * @param oldPath the previously highlighted paths.
   * @param newPath the new highlighted paths.
   */
  void highlightPaths(Collection<Integer> oldPath, Collection<Integer> newPath);
}
