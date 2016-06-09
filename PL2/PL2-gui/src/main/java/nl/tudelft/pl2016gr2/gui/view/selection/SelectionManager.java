package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;

/**
 * This class manages the currently selected node. It makes sure the correct data is displayed and
 * the node is deselected whenever a differentnode is selected.
 *
 * @author Faris
 */
public class SelectionManager {

  private final RootLayoutController rootLayoutController;
  @TestId(id = "selectionPaneController")
  private final SelectionPaneController selectionPaneController;
  @TestId(id = "selected")
  private ISelectable selected;

  /**
   * The selected nodes in the graph. -1 means no genome is selected.
   */
  private final ObservableSet<Integer> graphSelectedNodes
      = FXCollections.observableSet();

  /**
   * The selected genome in the search box. -1 means no genome is selected.
   */
  private final ObservableList<Integer> searchBoxSelectedGenomes
      = FXCollections.observableArrayList();

  /**
   * Create a selection manager.
   *
   * @param rootLayoutController     the root layout controller class.
   * @param selectionPaneController  the controller of the selectionPane.
   */
  public SelectionManager(RootLayoutController rootLayoutController,
      SelectionPaneController selectionPaneController) {
    this.rootLayoutController = rootLayoutController;
    this.selectionPaneController = selectionPaneController;
  }

  /**
   * Request the selection manager to select the given object.
   *
   * @param selected the object to select.
   */
  public void select(ISelectable selected) {
    if (selected.equals(this.selected)) {
      return;
    }
    deselect();
    this.selected = selected;
    selected.select();
    createDescription(selected);
  }

  /**
   * Deselect the selected object. If no object is selected calling this method will have no effect.
   */
  public void deselect() {
    if (selected != null) {
      selected.deselect();
      selected = null;
      clearDescription();
    }
  }

  /**
   * Draw the two given graphs.
   *
   * @param topGenomes    the genomes to draw in the top graph.
   * @param bottomGenomes the genomes to draw in the bottom graph.
   */
  protected void drawGraph(ArrayList<Integer> topGenomes, ArrayList<Integer> bottomGenomes) {
    rootLayoutController.drawGraph(topGenomes, bottomGenomes);
  }

  /**
   * Set the content of the selection description pane.
   *
   * @param selected the currently selected object.
   */
  private void createDescription(ISelectable selected) {
    Node description = selected.getSelectionInfo(this).getNode();
    selectionPaneController.setContent(description);
  }

  /**
   * Clear the description pane.
   */
  private void clearDescription() {
    selectionPaneController.clearContent();
  }

  /**
   * Get the observable list of genomes which is selected in the search box.
   *
   * @return the observable list of genomes which is selected in the search box.
   */
  public ObservableList<Integer> getSearchBoxSelectedGenomes() {
    return searchBoxSelectedGenomes;
  }

  public ObservableSet<Integer> getSelectedGraphNodes() {
    return graphSelectedNodes;
  }

}
