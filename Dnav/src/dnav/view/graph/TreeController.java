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

    public TreeController(Pane graphPane, TreeNode root, Button zoomOutButton) {
        this.graphPane = graphPane;
        this.zoomOutButton = zoomOutButton;
        initializeEventHandlers();
        initializeGraphSizeListeners();
        setRoot(root);
    }

    private void initializeEventHandlers() {
        zoomOutButton.setOnAction(e -> {
            if (currentRoot.getDataNode().hasParent()) {
                setRoot(currentRoot.getDataNode().getParent());
            } else {
                zoomOutButton.setDisable(true);
            }
        });
        graphPane.setOnMouseClicked((MouseEvent event) -> {
            if (event.getTarget().getClass().equals(ViewNode.class)) {
                ViewNode clickedNode = (ViewNode) event.getTarget();
//                setRoot(clickedNode.getDataNode());
                currentRoot.zoomIn(currentRoot, clickedNode);
            }
        });
    }

    private void initializeGraphSizeListeners() {
        graphPane.heightProperty().addListener((observable, oldVal, newVal) -> {
            if (currentRoot != null) {
//                currentRoot.handleGraphHeightChanged(oldVal.doubleValue(), newVal.doubleValue());
                setRoot(currentRoot.getDataNode());
            }
        });
        graphPane.widthProperty().addListener((observable, oldVal, newVal) -> {
            if (currentRoot != null) {
//                currentRoot.handleGraphWidthChanged(oldVal.doubleValue(), newVal.doubleValue());
                setRoot(currentRoot.getDataNode());
            }
        });
    }

    protected final void setRoot(TreeNode root) {
        zoomOutButton.setDisable(!root.hasParent());
        graphPane.getChildren().clear();
        currentRoot = ViewNode.drawRootNode(root, this);
    }

    protected Pane getGraphPane() {
        return graphPane;
    }
}
