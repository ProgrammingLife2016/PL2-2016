package dnav.view;

import dnav.model.TreeNode;
import dnav.view.graph.TreeController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private TreeController treeController;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        assert (controller == null);
        controller = this;
    }

    public void setData(TreeNode root) {
        assert treeController == null;
        treeController = new TreeController(graphPane, root, zoomOutButton);
//        setRoot(root);
    }

//    public void setRoot(TreeNode root) {
//        zoomOutButton.setDisable(!root.hasParent());
//        currentRoot = root;
//        graphPane.getChildren().clear();
//        ViewNode.drawRootNode(root, graphPane);
//    }
//
//    public static RootLayoutController getController() {
//        return controller;
//    }

//    @FXML
//    private void zoomOutButtonClicked() {
////        if (currentRoot.hasParent()) {
////            setRoot(currentRoot.getParent());
////        }
//    }
}
