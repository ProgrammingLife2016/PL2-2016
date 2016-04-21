package dnav.view;

import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * FXML Controller class
 *
 * @author Faris
 */
public class RootLayoutController {

    @FXML
    private Pane graphPane;
    @FXML
    private StackPane locationIdentifierPane;
    @FXML
    private Rectangle locationIdentifierRectangle;
    @FXML
    private Slider zoomInSlider;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }

    public void handleSceneWidthChanged() {
    }

    public void handleSceneHeightChanged() {
    }
}
