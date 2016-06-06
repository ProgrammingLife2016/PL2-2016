package nl.tudelft.pl2016gr2.model;

import java.util.List;

import java.util.Collection;

/**
 * This interface must be implemented by the root of the phylogenetic tree.
 *
 * @author Faris
 */
public interface IPhylogeneticTreeRoot extends IPhylogeneticTreeNode {

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
   * Highlight a path in the phylogenetic tree.
   *
   * @param oldPath the previously highlighted paths.
   * @param newPath the new highlighted paths.
   */
  List<Annotation> getAnnotations();

  void highlightPaths(Collection<Integer> oldPath, Collection<Integer> newPath);
}
