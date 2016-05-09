package nl.tudelft.pl2016gr2.gui.view.selection;

import static org.junit.Assert.assertEquals;

import javafx.scene.control.TextArea;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests the {@link TextDescription} class.
 *
 * @author Faris
 */
@RunWith(JavaFxJUnit4ClassRunner.class)
public class TextDescriptionTest {

  /**
   * Test of getNode method, of class TextDescription.
   */
  @Test
  public void testGetNode() {
    assertEquals(TextArea.class, new TextDescription("test").getNode().getClass());
  }
}
