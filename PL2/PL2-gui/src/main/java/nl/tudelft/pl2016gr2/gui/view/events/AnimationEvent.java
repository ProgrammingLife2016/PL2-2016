package nl.tudelft.pl2016gr2.gui.view.events;

import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.util.Duration;

/**
 * An event which indicates that an object is being animated from a start (x, y) coordinate to an
 * end (x, y) coordinate. It also includes the scale of the object, with which you can indicate that
 * the size of the object changed.
 *
 * @author Faris
 */
public class AnimationEvent extends Event {

  public static final EventType<AnimationEvent> ANIMATION_EVENT = new EventType(ANY, "ANIMATION");
  private final Timeline timeline;
  private final Duration duration;
  private final double startX;
  private final double startY;
  private final double endX;
  private final double endY;
  private final double scale;

  /**
   * Create an animation event.
   *
   * @param startX   the start position of the x coordinate.
   * @param startY   the start position of the y coordinate.
   * @param endX     the end position of the x coordinate.
   * @param endY     the end position of the u coordinate.
   * @param scale    the new relative size of the object.
   * @param timeline the timeline which is used to animate the animation.
   * @param duration the duration of the animation.
   */
  public AnimationEvent(double startX, double startY, double endX, double endY, double scale,
      Timeline timeline, Duration duration) {
    super(ANIMATION_EVENT);
    this.startX = startX;
    this.startY = startY;
    this.endX = endX;
    this.endY = endY;
    this.scale = scale;
    this.timeline = timeline;
    this.duration = duration;
  }

  /**
   * Get the new relative size.
   *
   * @return the new relative size.
   */
  public double getScale() {
    return scale;
  }

  /**
   * Get the start position of the x coordinate.
   *
   * @return the start position of the x coordinate.
   */
  public double getStartX() {
    return startX;
  }

  /**
   * Get the start position of the y coordinate.
   *
   * @return the start position of the y coordinate.
   */
  public double getStartY() {
    return startY;
  }

  /**
   * Get the end position of the x coordinate.
   *
   * @return the end position of the x coordinate.
   */
  public double getEndX() {
    return endX;
  }

  /**
   * Get the end position of the y coordinate.
   *
   * @return the end position of the y coordinate.
   */
  public double getEndY() {
    return endY;
  }

  /**
   * Get the timeline which is used to animate the animation.
   *
   * @return the timeline which is used to animate the animation.
   */
  public Timeline getTimeline() {
    return timeline;
  }

  /**
   * Get the duration of the animation.
   *
   * @return the duration of the animation.
   */
  public Duration getDuration() {
    return duration;
  }
}
