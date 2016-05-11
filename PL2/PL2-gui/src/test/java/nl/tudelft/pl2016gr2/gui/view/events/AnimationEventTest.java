package nl.tudelft.pl2016gr2.gui.view.events;

import static org.junit.Assert.assertEquals;

import javafx.animation.Timeline;
import javafx.util.Duration;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the {@link AnimationEvent} class.
 *
 * @author Faris
 */
public class AnimationEventTest {

  private static final double DELTA = 0.0001;
  private AnimationEvent animationEvent;
  private Timeline timeline;
  private final Duration duration = new Duration(500);

  /**
   * Initialize the animation event.
   */
  @Before
  public void loadTree() {
    timeline = new Timeline();
    animationEvent = new AnimationEvent(0, 5, 10, 15, 2.0, timeline, duration);
  }

  /**
   * Test of getScale method, of class AnimationEvent.
   */
  @Test
  public void testGetScale() {
    assertEquals(2.0, animationEvent.getScale(), DELTA);
  }

  /**
   * Test of getStartX method, of class AnimationEvent.
   */
  @Test
  public void testGetStartX() {
    assertEquals(0.0, animationEvent.getStartX(), DELTA);
  }

  /**
   * Test of getStartY method, of class AnimationEvent.
   */
  @Test
  public void testGetStartY() {
    assertEquals(5.0, animationEvent.getStartY(), DELTA);
  }

  /**
   * Test of getEndX method, of class AnimationEvent.
   */
  @Test
  public void testGetEndX() {
    assertEquals(10.0, animationEvent.getEndX(), DELTA);
  }

  /**
   * Test of getEndY method, of class AnimationEvent.
   */
  @Test
  public void testGetEndY() {
    assertEquals(15.0, animationEvent.getEndY(), DELTA);
  }

  /**
   * Test of getTimeline method, of class AnimationEvent.
   */
  @Test
  public void testGetTimeline() {
    assertEquals(timeline, animationEvent.getTimeline());
  }

  /**
   * Test of getDuration method, of class AnimationEvent.
   */
  @Test
  public void testGetDuration() {
    assertEquals(duration, animationEvent.getDuration());
  }

}
