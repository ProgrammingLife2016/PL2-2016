package dnav;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dnav.view.RootLayoutController;

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
     * @throws java.io.IOException this exception occurs when the fxml isn't found.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Dnav.class.getResource("view/RootLayout.fxml"));
        Scene scene = new Scene(loader.load(), 600, 400);
        RootLayoutController controller = loader.getController();
        scene.widthProperty().addListener(o -> {
            controller.handleSceneWidthChanged();
        });
        scene.heightProperty().addListener(o -> {
            controller.handleSceneHeightChanged();
        });
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
