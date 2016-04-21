package dnav.view;

import dnav.model.TreeNode;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Line;
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

    private TreeNode currentRoot;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        // TODO
    }

    public void handleSceneWidthChanged() {
//        setRoot(currentRoot);
    }

    public void handleSceneHeightChanged() {
        setRoot(currentRoot);
    }

    public void insertData(TreeNode root) {
        assert currentRoot == null;
        setRoot(root);
    }

    private void setRoot(TreeNode root) {
//        System.out.println("root.getChildCount() = " + root.getChildCount());
        currentRoot = root;
        graphPane.getChildren().clear();
        drawNode(root, 5, 10, graphPane.getWidth() * 0.8, 10, graphPane.getHeight() - 10);
    }

    private ViewNode drawNode(TreeNode dataNode, int levels, double startX, double endX,
            double startY, double endY) {
        ViewNode r = new ViewNode(dataNode);
        r.setCenterX(r.getRadius() + startX);
        r.setCenterY((endY - startY) / 2.0 + startY);
        r.setOnMouseClicked(clickNode);
        graphPane.getChildren().add(r);

        if (levels >= 1) {
            int directChildren = dataNode.getDirectChildCount();
            for (int i = 0; i < directChildren; i++) {
                TreeNode data = dataNode.getChild(i);
                double nextStartX = (endX - startX) / 2.0 + startX;
                double ySize = (endY - startY) / directChildren;
                double nextStartY = ySize * i + startY;
                double nextEndY = nextStartY + ySize;
                ViewNode child = drawNode(data, levels - 1, nextStartX, endX, nextStartY,
                        nextEndY);
                drawEdge(r, child);
            }
        }
        return r;
    }

    private void drawEdge(ViewNode from, ViewNode to) {
        Line edge = new Line();
        edge.setStartX(from.getCenterX());
        edge.setStartY(from.getCenterY());
        edge.setEndX(to.getCenterX());
        edge.setEndY(to.getCenterY());
        graphPane.getChildren().add(edge);
        edge.toBack();
    }

    private final EventHandler<MouseEvent> clickNode = (MouseEvent event) -> {
        setRoot(((ViewNode) event.getSource()).getDataNode());
    };
}
