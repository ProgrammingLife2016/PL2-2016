package dnav.view.graph;

import dnav.model.TreeNode;
import java.util.ArrayList;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

/**
 *
 * @author Faris
 */
public class ViewNode extends Circle {

    private final static double NODE_RADIUS = 10.0;
    private final static double NODE_DIAMETER = NODE_RADIUS * 2.0;
    private final static Duration ANIMATION_DURATION = Duration.millis(500.0);

    private final TreeNode dataNode;
    private final ViewNode parent;
    private final ArrayList<ViewNode> children = new ArrayList<>();
    private Line edge, elipsis;
    private final TreeController controller;
    private GraphArea graphArea;
    private boolean isLeaf = false;

    private ViewNode(TreeNode dataNode, ViewNode parent, GraphArea graphArea,
            TreeController controller) {
        super(NODE_RADIUS);
        this.dataNode = dataNode;
        this.graphArea = graphArea;
        this.parent = parent;
        this.controller = controller;

        double red = (dataNode.getChildCount() * 13 % 200) / 255d;
        double green = (dataNode.getChildCount() * 34 % 200) / 255d;
        double blue = (dataNode.getChildCount() * 88 % 200) / 255d;
        this.setFill(new Color(red, green, blue, 1.0));

        this.setCenterX(this.getRadius() + graphArea.startX);
        this.setCenterY(graphArea.getCenterY());
    }

    protected TreeNode getDataNode() {
        return dataNode;
    }

    protected static ViewNode drawRootNode(TreeNode root, TreeController controller) {
        Pane graphPane = controller.getGraphPane();
        double startX = TreeController.GRAPH_BORDER_OFFSET;
        double endX = graphPane.getWidth() - TreeController.GRAPH_BORDER_OFFSET;
        double startY = TreeController.GRAPH_BORDER_OFFSET;
        double endY = graphPane.getHeight() - TreeController.GRAPH_BORDER_OFFSET;
        GraphArea gbox = new GraphArea(startX, endX, startY, endY);
        return drawNode(root, null, gbox, controller);
    }

    private static ViewNode drawNode(TreeNode dataNode, ViewNode parent, GraphArea graphArea,
            TreeController controller) {
        if (graphArea.getWidth() < NODE_DIAMETER || graphArea.getHeight() < NODE_DIAMETER
                || dataNode == null) {
            return null; // box too small to draw node.
        }
        ViewNode node = new ViewNode(dataNode, parent, graphArea, controller);
        controller.getGraphPane().getChildren().add(node);
        return redrawNode(node);
    }

    private static ViewNode redrawNode(ViewNode node) {
        TreeNode dataNode = node.dataNode;
        GraphArea graphArea = node.graphArea;
        TreeController controller = node.controller;
        double nextStartX = graphArea.getCenterX();
        double ySize = (graphArea.endY - graphArea.startY) / dataNode.getDirectChildCount();
        for (int i = 0; i < dataNode.getDirectChildCount(); i++) {
            TreeNode childDataNode = dataNode.getChild(i);
            double nextStartY = ySize * i + graphArea.startY;
            double nextEndY = nextStartY + ySize;
            GraphArea childArea = new GraphArea(nextStartX, graphArea.endX, nextStartY, nextEndY);
            ViewNode child = drawNode(childDataNode, node, childArea, controller);
            if (child == null) {
                drawElipsis(node, controller.getGraphPane());
                node.isLeaf = true;
                break;
            }
            node.isLeaf = false;
            node.children.add(child);
            drawEdge(node, child, controller.getGraphPane());
        }
        return node;
    }

    private static void drawEdge(ViewNode parent, ViewNode child, Pane graphPane) {
        Line edge = new Line();
        edge.startXProperty().bind(parent.centerXProperty());
        edge.startYProperty().bind(parent.centerYProperty());
        edge.endXProperty().bind(child.centerXProperty());
        edge.endYProperty().bind(child.centerYProperty());
        graphPane.getChildren().add(edge);
        edge.toBack();
        child.edge = edge;
    }

    private static void drawElipsis(ViewNode node, Pane graphPane) {
        Line l = new Line();
        l.startXProperty().bind(node.centerXProperty().add(node.getRadius() * 2));
        l.endXProperty().bind(node.centerXProperty().add(node.getRadius() * 4));
        l.startYProperty().bind(node.centerYProperty());
        l.endYProperty().bind(node.centerYProperty());
        l.getStrokeDashArray().addAll(2d, 5d);
        graphPane.getChildren().add(l);
        node.elipsis = l;
    }

    public void zoomIn(ViewNode oldRoot, ViewNode newRoot) {
        Timeline tl = new Timeline();
        zoomIn(oldRoot.graphArea, newRoot.graphArea, tl);
        tl.setOnFinished(e -> {
            controller.setRoot(newRoot.dataNode);
        });
        tl.play();
    }

    private void zoomIn(GraphArea originalArea, GraphArea zoomArea, Timeline timeline) {
        double newX = (getCenterX() - zoomArea.startX - NODE_RADIUS);
        newX = newX / zoomArea.getWidth() * originalArea.getWidth();
        newX += NODE_RADIUS + TreeController.GRAPH_BORDER_OFFSET;
        double newY = (getCenterY() - zoomArea.startY);
        newY = newY / zoomArea.getHeight() * originalArea.getHeight();
        newY += TreeController.GRAPH_BORDER_OFFSET;

        KeyValue kvX = new KeyValue(this.centerXProperty(), newX, Interpolator.EASE_BOTH);
        KeyValue kvY = new KeyValue(this.centerYProperty(), newY, Interpolator.EASE_BOTH);
        timeline.getKeyFrames().add(new KeyFrame(ANIMATION_DURATION, kvX, kvY));

        for (ViewNode child : children) {
            child.zoomIn(originalArea, zoomArea, timeline);
        }
    }
}
