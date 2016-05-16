package nl.tudelft.pl2016gr2.gui.view.selection;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxJUnit4ClassRunner;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * This class tests the {@link DescriptionPane} class.
 *
 * @author Faris
 */
@RunWith(JavaFxJUnit4ClassRunner.class)
public class DescriptionPaneTest {

  /**
   * Test of constructor, of class DescriptionPane.
   */
  @Test
  public void descriptionPaneTest() {
    Pane parent = new Pane();
    Scene scene = new Scene(parent);
    DescriptionPane description = new DescriptionPane(parent, parent);
    assertTrue(parent.getChildren().contains(description));
    FrostGlassEffect effect = AccessPrivate.getFieldValue("frostGlassEffect",
        DescriptionPane.class, description);
    assertTrue(description.getChildren().contains(effect.getEffect()));
  }

  /**
   * Test of clear method, of class DescriptionPane.
   */
  @Test
  public void testClear() {
    Pane parent = new Pane();
    Scene scene = new Scene(parent);
    DescriptionPane description = new DescriptionPane(parent, parent);
    FrostGlassEffect effect = Mockito.mock(FrostGlassEffect.class);
    AccessPrivate.setFieldValue("frostGlassEffect", DescriptionPane.class, description, effect);
    description.clear();
    verify(effect, times(1)).clear();
  }

}
