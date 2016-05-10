package nl.tudelft.pl2016gr2.gui.view.selection;

/**
 * This interface must be implemented by all objects which can be seleted.
 *
 * @author Faris
 */
public interface ISelectable {

  /**
   * Set the state of the object to selected.
   */
  void select();

  /**
   * Set the state of the object to deselected.
   */
  void deselect();

  /**
   * Get the {@link ISelectionInfo}.
   *
   * @return the {@link ISelectionInfo}.
   */
  ISelectionInfo getSelectionInfo();
}
