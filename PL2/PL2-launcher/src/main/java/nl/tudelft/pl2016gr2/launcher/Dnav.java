package nl.tudelft.pl2016gr2.launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.io.IOException;

/**
 * The launcher (main class) of the application.
 *
 * @author Faris
 */
public class Dnav extends Application {

  private static final double INITIAL_WINDOW_WIDTH = 960d;
  private static final double INITIAL_WINDOW_HEIGHT = 1000d;
  private static final double MINIMUM_WINDOW_WIDTH = 960d;
  private static final double MINIMUM_WINDOW_HEIGHT = 700d;

  @TestId(id = "primaryStage")
  private static Stage primaryStage;
  @TestId(id = "rootLayout")
  private static RootLayoutController rootLayout;

  /**
   * Start the application. This method is automatically called by JavaFX when the API is
   * initialized, after the call to launch(args) in the main method.
   *
   * @param stage the primary stage of the application.
   * @throws java.io.IOException this exception occurs when the fxml isn't found.
   */
  @Override
  public void start(Stage stage) throws IOException {
    primaryStage = stage;
    rootLayout = RootLayoutController.loadView();
    Scene scene = new Scene(rootLayout.getPane(), INITIAL_WINDOW_WIDTH, INITIAL_WINDOW_HEIGHT);
    primaryStage.setMinWidth(MINIMUM_WINDOW_WIDTH);
    primaryStage.setMinHeight(MINIMUM_WINDOW_HEIGHT);
    primaryStage.setScene(scene);
    primaryStage.setMaximized(true);
    if (!Boolean.getBoolean("test")) {
      primaryStage.show();
      primaryStage.setMaximized(true);
      rootLayout.promptFileChooser();
    }
  }

  /**
   * Launch the application.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
