package nl.tudelft.pl2016gr2.gui.view.events;

import static javafx.event.Event.ANY;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * This class can be used to fire events to notify listeners that the graphical content of the
 * object which the event was fired on has changed.
 *
 * @author Faris
 */
public class GraphicsChangedEvent extends Event {

  public static final EventType<GraphicsChangedEvent> GRAPHICS_CHANGED_EVENT
      = new EventType(ANY, "GRAPHICS_CHANGED");

  /**
   * Create a new graphics changed event.
   */
  public GraphicsChangedEvent() {
    super(GRAPHICS_CHANGED_EVENT);
  }
}
