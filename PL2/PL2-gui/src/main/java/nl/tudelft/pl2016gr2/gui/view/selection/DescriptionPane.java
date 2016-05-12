package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

/**
 * This is a description pane. The pane contains a description about a selected item. It extends the
 * {@link Pane} class.
 *
 * @author Faris
 */
public class DescriptionPane extends Pane {

  private static final Color BACKGROUND_COLOR = new Color(0.02, 0.08, 0.3, 1.0);
  private static final CornerRadii CORNER_RADII = new CornerRadii(8.0, false);
  @TestId(id = "frostGlassEffect")
  private FrostGlassEffect frostGlassEffect;

  /**
   * Create a description pane.
   *
   * @param background the pane which is behind the description pane.
   * @param parent     the parent of this pane.
   */
  public DescriptionPane(Region background, Pane parent) {
    prefHeightProperty().bind(parent.heightProperty());
    prefWidthProperty().bind(parent.widthProperty());
    parent.getChildren().add(this);

    frostGlassEffect = new FrostGlassEffect(background, this);
    getChildren().add(frostGlassEffect.getEffect());
    setBackground(new Background(
        new BackgroundFill(BACKGROUND_COLOR, CORNER_RADII, Insets.EMPTY)));
  }

  /**
   * Clear the content of this object.
   */
  public void clear() {
    frostGlassEffect.clear();
  }
}
