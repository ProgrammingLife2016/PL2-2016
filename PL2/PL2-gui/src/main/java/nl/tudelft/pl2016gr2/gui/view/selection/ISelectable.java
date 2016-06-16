package nl.tudelft.pl2016gr2.gui.view.selection;

/**
 * This interface must be implemented by all objects which can be selected.
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

  /**
   * Like Object.equals, but for selections
   *
   * <p>
   * Should return true when the other selection would be corresponding
   * to the same conceptual selection.
   * </p>
   *
   * @param other object to check against.
   * @return true when equal
   */
  boolean isEqualSelection(ISelectable other);

}
