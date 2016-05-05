package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This class manages the currently selected node. It makes sure the correct data is displayed and
 * the node is deselected whenever a differentnode is selected.
 *
 * @author Faris
 */
public class SelectionManager {

  private final Pane selectionDescriptionPane;
  private final Region mainPane;
  private ISelectable selected;
  private Timeline timeline;
  private final TranslationListener translationListener;

  /**
   * Create a selection manager.
   *
   * @param selectionDescriptionPane the pane in which to draw information about selected items.
   * @param mainPane                 the main pane containing the tree and heatmap.
   */
  public SelectionManager(Pane selectionDescriptionPane, Region mainPane) {
    this.selectionDescriptionPane = selectionDescriptionPane;
    this.mainPane = mainPane;
    translationListener = new TranslationListener();

    selectionDescriptionPane.translateXProperty().addListener(translationListener);
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
    if (timeline != null && timeline.getStatus().equals(Status.RUNNING)) {
      timeline.setOnFinished((ActionEvent event) -> {
        selected.select();
        createDescription(selected);
      });
    } else {
      selected.select();
      createDescription(selected);
    }
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
   * Set the content of the selection description pane.
   *
   * @param selected the currently selected object.
   */
  private void createDescription(ISelectable selected) {
    Node description = selected.getSelectionInfo().getNode();
    selectionDescriptionPane.getChildren().add(description);
    selectionDescriptionPane.setVisible(true);

    selectionDescriptionPane.setBackground(new Background(
        new BackgroundFill(new Color(0.02, 0.05, 0.7, 1.0),
            new CornerRadii(0.0, 0.0, 0.0, 8.0, false), Insets.EMPTY)));

    WritableImage wIm = mainPane.snapshot(null, null);
    WritableImage wIm2 = new WritableImage(wIm.getPixelReader(),
        (int) (mainPane.getWidth() - selectionDescriptionPane.getWidth()), 0,
        (int) selectionDescriptionPane.getWidth(), (int) selectionDescriptionPane.getHeight());
    ImageView im = new ImageView(wIm2);
    selectionDescriptionPane.getChildren().add(0, im);
    im.setEffect(new GaussianBlur());
    im.setOpacity(0.9);
    im.setViewport(new Rectangle2D(selectionDescriptionPane.getWidth() - 1, 0, 1,
        selectionDescriptionPane.getHeight()));

    double translation = selectionDescriptionPane.getWidth();
    selectionDescriptionPane.setTranslateX(translation);
    timeline = new Timeline();
    timeline.getKeyFrames().add(
        new KeyFrame(Duration.millis(500),
            new KeyValue(selectionDescriptionPane.translateXProperty(), 0)));
    translationListener.setCurrentImage(im, selectionDescriptionPane.getWidth(),
        selectionDescriptionPane.getHeight());
    timeline.play();
  }

  /**
   * Clear the description pane.
   */
  private void clearDescription() {
    timeline.stop();
    timeline = new Timeline();
    timeline.getKeyFrames().add(
        new KeyFrame(Duration.millis(300),
            new KeyValue(selectionDescriptionPane.translateXProperty(),
                selectionDescriptionPane.getWidth())));
    timeline.setOnFinished((ActionEvent event) -> {
      selectionDescriptionPane.getChildren().clear();
      selectionDescriptionPane.setVisible(false);
    });
    timeline.play();
  }

  private static class TranslationListener implements ChangeListener<Number> {

    private ImageView image;
    private double width;
    private double height;

    public void setCurrentImage(ImageView image, double width, double height) {
      this.image = image;
      this.width = width;
      this.height = height;
    }

    @Override
    public void changed(ObservableValue<? extends Number> observable, Number oldValue,
        Number newValue) {
      if (image != null) {
        image.setViewport(new Rectangle2D(newValue.doubleValue(), 0,
            width - newValue.doubleValue(), height));
      }
    }
  }
}
