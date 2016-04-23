package dnav.view.graph;

import dnav.model.TreeNode;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 *
 * @author Faris
 */
public class TreeController {

    public final static double GRAPH_BORDER_OFFSET = 5.0;

    private final Pane graphPane;
    private final Button zoomOutButton;
    private ViewNode currentRoot;

    /**
     * Create a new tree controller which manages the pholygenetic tree.
     *
     * @param graphPane the pane in which to draw the tree.
     * @param root the root node of the tree.
     * @param zoomOutButton the zoom out button.
     */
    public TreeController(Pane graphPane, TreeNode root, Button zoomOutButton) {
        this.graphPane = graphPane;
        this.zoomOutButton = zoomOutButton;
        initializeZoomEventHandlers();
        initializeGraphSizeListeners();
        setRoot(root);
    }

    /**
     * Initialize the event handlers which zoom in when a node is clicked or zoom out when the zoom
     * out button is clicked.
     */
    private void initializeZoomEventHandlers() {
        zoomOutButton.setOnAction(e -> {
            if (currentRoot.getDataNode().hasParent()) {
                setRoot(currentRoot.getDataNode().getParent());
//                currentRoot.zoomOut(currentRoot, currentRoot.getDataNode().getParent());
            } else {
                zoomOutButton.setDisable(true);
            }
        });
        graphPane.setOnMouseClicked((MouseEvent event) -> {
            if (event.getTarget().getClass().equals(ViewNode.class)) {
                ViewNode clickedNode = (ViewNode) event.getTarget();
                currentRoot.zoomIn(clickedNode);
            }
        });
    }

    /**
     * Initialize the listeners which resize the content of the graph area when the pane or the
     * window is resized.
     */
    private void initializeGraphSizeListeners() {
        graphPane.heightProperty().addListener((observable, oldVal, newVal) -> {
            if (currentRoot != null) {
                setRoot(currentRoot.getDataNode());
            }
        });
        graphPane.widthProperty().addListener((observable, oldVal, newVal) -> {
            if (currentRoot != null) {
                setRoot(currentRoot.getDataNode());
            }
        });
    }

    /**
     * Set the root of the part of the tree which should be shown.
     *
     * @param root the root of the part of the tree which should be shown.
     */
    protected final void setRoot(TreeNode root) {
        zoomOutButton.setDisable(!root.hasParent());
        graphPane.getChildren().clear();
        currentRoot = ViewNode.drawRootNode(root, this);
    }

    /**
     * Get the pane of the graph/tree.
     *
     * @return the pane of the graph/tree.
     */
    protected Pane getGraphPane() {
        return graphPane;
    }
}
