package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import nl.tudelft.pl2016gr2.gui.view.events.GraphicsChangedEvent;

/**
 * This class creates an imageview which acts as a frost glass effect.
 *
 * @author Faris
 */
public class FrostGlassEffect {

  private final Region background;
  private final Region container;
  private final ImageView imageView = new ImageView();

  private final InvalidationListener sizeChangeListener = (Observable observable) -> {
    redrawImage();
  };
  private final EventHandler<GraphicsChangedEvent> graphicsChangedHandler = event -> {
    redrawImage();
  };

  /**
   * Construct a frost glass effect.
   *
   * @param background the background region, containing the view over which to apply the frost
   *                   glass effect.
   * @param container  the container of the frost glass effect.
   */
  public FrostGlassEffect(Region background, Region container) {
    this.background = background;
    this.container = container;

    imageView.setEffect(new GaussianBlur());
    imageView.setOpacity(0.8);
    redrawImage();

    background.addEventHandler(GraphicsChangedEvent.GRAPHICS_CHANGED_EVENT, graphicsChangedHandler);
    background.getScene().widthProperty().addListener(sizeChangeListener);
    background.getScene().heightProperty().addListener(sizeChangeListener);
  }

  /**
   * Redraw the image and set its position.
   */
  private void redrawImage() {
    imageView.setImage(background.snapshot(null, null));
    repositionImage();
  }

  /**
   * Reposition the image according to the new coordinates of the container pane.
   */
  private void repositionImage() {
    Point2D containerTransform = container.localToScene(Point2D.ZERO);
    Point2D backgroundTransform = background.localToScene(Point2D.ZERO);
    int relativeX = (int) (containerTransform.getX() - backgroundTransform.getX());
    int relativeY = (int) (containerTransform.getY() - backgroundTransform.getY());
    int width = (int) (background.getWidth() - relativeX);
    int height = (int) (background.getHeight() - relativeY);
    if (width > container.getWidth()) {
      width = (int) container.getWidth();
    }
    if (height > container.getHeight()) {
      height = (int) container.getHeight();
    }
    if (width <= 0 || height <= 0) {
      imageView.setViewport(new Rectangle2D(relativeX, relativeY, 1, 1));
      return;
    }
    imageView.setViewport(new Rectangle2D(relativeX, relativeY, width, height));
  }

  public ImageView getEffect() {
    return imageView;
  }

  /**
   * Clear the content of this object. Removes all of the listeners.
   */
  public void clear() {
    background.removeEventHandler(GraphicsChangedEvent.GRAPHICS_CHANGED_EVENT,
        graphicsChangedHandler);
    background.getScene().widthProperty().removeListener(sizeChangeListener);
    background.getScene().heightProperty().removeListener(sizeChangeListener);
  }
}
