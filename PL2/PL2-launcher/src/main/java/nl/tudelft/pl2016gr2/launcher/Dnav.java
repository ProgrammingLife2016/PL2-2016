package nl.tudelft.pl2016gr2.launcher;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;

import java.io.IOException;

/**
 * The launcher (main class) of the application.
 *
 * @author Faris
 */
public class Dnav extends Application {

  /**
   * Start the application. This method is automatically called by JavaFX when the API is
   * initialized, after the call to launch(args) in the main method.
   *
   * @param primaryStage the primary stage of the application.
   * @throws java.io.IOException this exception occurs when the fxml isn't found.
   */
  @Override
  public void start(Stage primaryStage) throws IOException {
    RootLayoutController rootLayout = RootLayoutController.loadView();
    Scene scene = new Scene(rootLayout.getPane(), 1000, 800);
    primaryStage.setMinHeight(700);
    primaryStage.setMinWidth(600);
    primaryStage.setScene(scene);
    primaryStage.show();
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
