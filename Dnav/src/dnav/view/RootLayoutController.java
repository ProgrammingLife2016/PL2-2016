package dnav.view;

import dnav.model.TreeNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * FXML Controller class
 *
 * @author Faris
 */
public class RootLayoutController {

    public final static double GRAPH_BORDER_OFFSET = 5.0;

    @FXML
    private Pane graphPane, heatmapPane;
    @FXML
    private Button zoomOutButton;
    @FXML
    private StackPane locationIdentifierPane;
    @FXML
    private Rectangle locationIdentifierRectangle;
    @FXML
    private Slider zoomInSlider;

    private TreeNode currentRoot;

    private static RootLayoutController controller;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        assert (controller == null);
        controller = this;
        graphPane.heightProperty().addListener(o -> {
            handleSceneWidthChanged();
        });
        graphPane.widthProperty().addListener(o -> {
            handleSceneHeightChanged();
        });
    }

    private void handleSceneWidthChanged() {
        if (currentRoot != null) {
            setRoot(currentRoot);
        }
    }

    private void handleSceneHeightChanged() {
        if (currentRoot != null) {
            setRoot(currentRoot);
        }
    }

    public void insertData(TreeNode root) {
        assert currentRoot == null;
        setRoot(root);
    }

    private void setRoot(TreeNode root) {
        zoomOutButton.setDisable(!root.hasParent());
        currentRoot = root;
        graphPane.getChildren().clear();
        ViewNode.drawRootNode(root);
    }

    public final EventHandler<MouseEvent> clickNode = (MouseEvent event) -> {
        setRoot(((ViewNode) event.getSource()).getDataNode());
    };

    protected Pane getGraphPane() {
        return graphPane;
    }

    protected static RootLayoutController getController() {
        return controller;
    }

    @FXML
    private void zoomOutButtonClicked() {
        if (currentRoot.hasParent()) {
            setRoot(currentRoot.getParent());
        }
    }
}
