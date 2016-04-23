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
 * FXML Controller class.
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

    /**
     * Set the data which has to be visualized.
     *
     * @param root the root of the tree which has to be drawn.
     */
    public void setData(TreeNode root) {
        assert treeController == null;
        treeController = new TreeController(graphPane, root, zoomOutButton);
    }
}
