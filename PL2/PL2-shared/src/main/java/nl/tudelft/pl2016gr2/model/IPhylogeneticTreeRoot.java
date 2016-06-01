package nl.tudelft.pl2016gr2.model;

import java.util.List;


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
  void setDrawnInTop(String genome, boolean isDrawn);

  /**
   * Notify the tree root that a genome has been added to (isDrawn = true), or removed from (isDrawn
   * = false) the bottom graph.
   *
   * @param genome  the genome which has been added or removed.
   * @param isDrawn if the given genome is added or removed.
   */
  void setDrawnInBottom(String genome, boolean isDrawn);
  
  /**
   * Gets the list of annotations.
   * 
   * @return the list of annotations.
   */
  List<Annotation> getAnnotations();
}
