package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import nl.tudelft.pl2016gr2.model.MetaData;

/**
 * Check if the metadata object posseses the a certain property.
 *
 * @author Faris
 */
public interface IMetadataChecker {

  /**
   * Check if the metadata object posseses a certain property.
   *
   * @param metadata the metadata object.
   * @return if the metadata object posseses a certain property.
   */
  boolean check(MetaData metadata);
}
