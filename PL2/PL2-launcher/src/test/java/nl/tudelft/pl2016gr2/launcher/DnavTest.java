package nl.tudelft.pl2016gr2.launcher;

import static org.junit.Assert.assertNotEquals;

import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.launcher.javafxrunner.JavaFxIntegrationTestRunner;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test the {@link Dnav} class with an integration test.
 *
 * @author Faris
 */
@RunWith(JavaFxIntegrationTestRunner.class)
public class DnavTest {

  /**
   * Test if the scene is correctly set when the application is launcher.
   */
  @Test
  public void launchApplicationTest() {
    assertNotEquals(null, getPrimaryStage().getScene());
  }

  private static Stage getPrimaryStage() {
    return AccessPrivate.getFieldValue("primaryStage", Dnav.class, null);
  }
}
