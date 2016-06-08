package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * This is a description pane. The pane contains a description about a selected item. It extends the
 * {@link Pane} class.
 *
 * @author Faris
 */
public class DescriptionPane extends Pane {

  private static final Color BACKGROUND_COLOR = new Color(0.4, 0.4, 0.45, 0.85);
  private static final CornerRadii CORNER_RADII = new CornerRadii(8.0, false);

  /**
   * Create a description pane.
   *
   * @param parent the parent of this pane.
   */
  public DescriptionPane(Pane parent) {
    prefHeightProperty().bind(parent.heightProperty());
    prefWidthProperty().bind(parent.widthProperty());
    parent.getChildren().add(this);
    setBackground(new Background(
        new BackgroundFill(BACKGROUND_COLOR, CORNER_RADII, Insets.EMPTY)));
  }
}
