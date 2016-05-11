package nl.tudelft.pl2016gr2.gui.view.events;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.gui.view.tree.ViewNode;
import org.junit.Test;

/**
 * This class tests the {@link ViewNode} class.
 *
 * @author Faris
 */
public class GraphicsChangedEventTest {
  
  @Test
  public void eventTypeTest() {
    GraphicsChangedEvent event = new GraphicsChangedEvent();
    assertEquals(GraphicsChangedEvent.GRAPHICS_CHANGED_EVENT, event.getEventType());
  }
  
}
