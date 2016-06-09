package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;


/**
 * This class manages the currently selected node. It makes sure the correct data is displayed and
 * the node is deselected whenever a differentnode is selected.
 *
 * @author Faris
 */
public class SelectionManager {

  private final ObservableList<Integer> searchBoxSelectedGenomes
      = FXCollections.observableArrayList();

  private final SimpleObjectProperty<ISelectable> selection;
  /**
   * This Selectable represents no selection.
   *
   * <p>
   * This makes sure that there will not be unexpected null-references
   * when doing stuff with selections.
   * </p>
   */
  public static final ISelectable NO_SELECTION = new ISelectable() {
    @Override
    public void select() {

    }

    @Override
    public void deselect() {

    }

    @Override
    public ISelectionInfo getSelectionInfo() {
      return () -> {
        TextArea textArea = new TextArea("Nothing selected");

        textArea.setWrapText(true);

        StackPane.setAlignment(textArea, Pos.CENTER);

        return new StackPane(textArea);
      };
    }

    @Override
    public boolean isEqualSelection(ISelectable other) {
      return false;
    }
  };

  /**
   * Create a selection manager.
   */
  public SelectionManager() {
    this.selection = new SimpleObjectProperty<>();
  }

  /**
   * Get the observable list of genomes which is selected in the search box.
   *
   * @return the observable list of genomes which is selected in the search box.
   */
  public ObservableList<Integer> getSearchBoxSelectedGenomes() {
    return searchBoxSelectedGenomes;
  }

  public void addListener(ChangeListener<? super ISelectable> listener) {
    selection.addListener(listener);
  }

  public void deselect() {
    selection.set(NO_SELECTION);
  }

  /**
   * Returns the selected item
   *
   * @return the currently selected item.
   */
  public ISelectable getSelection() {
    return selection.get();
  }

  /**
   * Select the given selectable.
   *
   * @param selectable the item to be "selected"
   */
  public void select(ISelectable selectable) {
    if (!isSelected(selectable)) {
      selection.set(selectable);
    }
  }

  /**
   * This method calls select or deselect depending on the state of viewNode.
   *
   * @param selectable the selectable in question
   */
  public void checkSelected(ISelectable selectable) {
    if (isSelected(selectable)) {
      selectable.select();
    } else {
      selectable.deselect();
    }
  }

  private boolean isSelected(ISelectable selectable) {
    return selectable.isEqualSelection(selection.get());
  }

}
