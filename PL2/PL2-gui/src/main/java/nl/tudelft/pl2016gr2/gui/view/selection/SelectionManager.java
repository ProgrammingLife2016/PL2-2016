package nl.tudelft.pl2016gr2.gui.view.selection;

import com.sun.javafx.collections.ObservableSetWrapper;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * This class manages the currently selected node. It makes sure the correct data is displayed and
 * the node is deselected whenever a differentnode is selected.
 *
 * @author Faris
 */
public class SelectionManager {

  private final RootLayoutController rootLayoutController;
  private final Pane selectionDescriptionPane;
  private final Region background;
  @TestId(id = "contentPane")
  private DescriptionPane contentPane;
  @TestId(id = "selected")
  private ISelectable selected;
  private Timeline timeline;

  private final ObservableSet<String> topGraphGenomes = new ObservableSetWrapper<>(new HashSet<>());
  private final ObservableSet<String> bottomGraphGenomes
      = new ObservableSetWrapper<>(new HashSet<>());

  /**
   * Create a selection manager.
   *
   * @param rootLayoutController     the root layout controller class.
   * @param selectionDescriptionPane the pane in which to draw information about selected items.
   * @param background               the background pane which is positioned behind the description.
   */
  public SelectionManager(RootLayoutController rootLayoutController, Pane selectionDescriptionPane,
      Region background) {
    this.rootLayoutController = rootLayoutController;
    this.selectionDescriptionPane = selectionDescriptionPane;
    this.background = background;

    selectionDescriptionPane.getChildren().addListener((Observable observable) -> {
      if (selectionDescriptionPane.getChildren().isEmpty()) {
        selectionDescriptionPane.setVisible(false);
      } else {
        selectionDescriptionPane.setVisible(true);
      }
    });
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

  protected void drawGraph(ArrayList<String> topGenomes, ArrayList<String> bottomGenomes) {
    rootLayoutController.drawGraph(topGenomes, bottomGenomes);
  }

  /**
   * Set the content of the selection description pane.
   *
   * @param selected the currently selected object.
   */
  private void createDescription(ISelectable selected) {
    createNewContentPane();
    Node description = selected.getSelectionInfo(this).getNode();
    contentPane.getChildren().add(description);
    contentPane.setOpacity(0);
    timeline = new Timeline();
    timeline.getKeyFrames().add(
        new KeyFrame(Duration.millis(500),
            new KeyValue(contentPane.opacityProperty(), 1.0, Interpolator.EASE_OUT)));
    timeline.play();
  }

  /**
   * Create a new content pane.
   */
  private void createNewContentPane() {
    contentPane = new DescriptionPane(background, selectionDescriptionPane);
  }

  /**
   * Clear the description pane.
   */
  private void clearDescription() {
    DescriptionPane curContentPane = this.contentPane;
    timeline.stop();
    timeline = new Timeline();
    timeline.getKeyFrames().add(
        new KeyFrame(Duration.millis(300),
            new KeyValue(curContentPane.opacityProperty(),
                0)));
    timeline.setOnFinished((ActionEvent event) -> {
      curContentPane.getChildren().clear();
      selectionDescriptionPane.getChildren().remove(curContentPane);
      curContentPane.clear();
    });
    timeline.play();
  }

  public ObservableSet<String> getTopGraphGenomes() {
    return topGraphGenomes;
  }

  public ObservableSet<String> getBottomGraphGenomes() {
    return bottomGraphGenomes;
  }
}
