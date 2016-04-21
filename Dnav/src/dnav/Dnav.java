package dnav;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *
 * @author Faris
 */
public class Dnav extends Application {

    /**
     * Start the application. This method is automatically called by JavaFX when the API is
     * initialized, after the call to launch(args) in the main method.
     *
     * @param primaryStage the primary stage of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        Scene scene = new Scene(loadUi(), 600, 400);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Load the initial view of the user interface.
     *
     * @return the pane containing the initial view of the user interface.
     */
    private static Pane loadUi() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Dnav.class.getResource("view/RootLayout.fxml"));
        try {
            Pane pane = loader.load();
            return pane;
        } catch (IOException ex) {
            Logger.getLogger(Dnav.class.getName()).log(Level.SEVERE, "Failed to load fxml file: RootLayout.fxml", ex);
            return null;
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
