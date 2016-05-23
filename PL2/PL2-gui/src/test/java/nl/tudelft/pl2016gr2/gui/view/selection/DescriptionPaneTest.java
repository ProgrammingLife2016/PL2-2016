package nl.tudelft.pl2016gr2.gui.view.selection;

import static org.junit.Assert.assertTrue;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxIntegrationTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests the {@link DescriptionPane} class.
 *
 * @author Faris
 */
@RunWith(JavaFxIntegrationTestRunner.class)
public class DescriptionPaneTest {

  /**
   * Test of constructor, of class DescriptionPane.
   */
  @Test
  public void descriptionPaneTest() {
    Pane parent = new Pane();
    Scene scene = new Scene(parent);
    DescriptionPane description = new DescriptionPane(parent);
    assertTrue(parent.getChildren().contains(description));
  }
}
