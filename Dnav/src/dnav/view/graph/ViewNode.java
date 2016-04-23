package dnav.view.graph;

import dnav.model.IPhylogeneticTree;
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
    private final static Duration ANIMATION_DURATION = Duration.millis(750.0);

    private final IPhylogeneticTree dataNode;
    private final ViewNode parent;
    private final ArrayList<ViewNode> children = new ArrayList<>();
    private Line edge, elipsis;
    private final TreeController controller;
    private GraphArea graphArea;
    private boolean isLeaf = false;

    /**
     * Create a view node.
     *
     * @param dataNode the data of the node.
     * @param parent the parent view node.
     * @param graphArea the graph area in which the node has to be drawn.
     * @param controller the controller of the tree.
     */
    private ViewNode(IPhylogeneticTree dataNode, ViewNode parent, GraphArea graphArea,
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

    /**
     * Get the data of this node.
     *
     * @return the data of this node.
     */
    protected IPhylogeneticTree getDataNode() {
        return dataNode;
    }

    /**
     * Draw a root node and all of its children which fit on the screen.
     *
     * @param root the root node.
     * @param controller the controller of the tree.
     * @return the view node of the root.
     */
    protected static ViewNode drawRootNode(IPhylogeneticTree root, TreeController controller) {
        Pane graphPane = controller.getGraphPane();
        double startX = TreeController.GRAPH_BORDER_OFFSET;
        double endX = graphPane.getWidth() - TreeController.GRAPH_BORDER_OFFSET;
        double startY = TreeController.GRAPH_BORDER_OFFSET;
        double endY = graphPane.getHeight() - TreeController.GRAPH_BORDER_OFFSET;
        GraphArea gbox = new GraphArea(startX, endX, startY, endY);
        return drawNode(root, null, gbox, controller);
    }

    /**
     * Recursively draw the node and all of its children.
     *
     * @param dataNode the data of the node to draw.
     * @param parent the parent of the node to draw.
     * @param graphArea the area in which the node should be drawn.
     * @param controller the controller of the tree.
     * @return the drawn view node.
     */
    private static ViewNode drawNode(IPhylogeneticTree dataNode, ViewNode parent, GraphArea graphArea,
            TreeController controller) {
        if (graphArea.getWidth() < NODE_DIAMETER || graphArea.getHeight() < NODE_DIAMETER
                || dataNode == null) {
            return null; // box too small to draw node.
        }
        ViewNode node = new ViewNode(dataNode, parent, graphArea, controller);
        controller.getGraphPane().getChildren().add(node);
        double nextStartX = graphArea.getCenterX();
        double ySize = graphArea.getHeight() / dataNode.getDirectChildCount();
        for (int i = 0; i < dataNode.getDirectChildCount(); i++) {
            IPhylogeneticTree childDataNode = dataNode.getChild(i);
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

    /**
     * Draw the edge between a parent and a child node.
     *
     * @param parent the parent node.
     * @param child the child node.
     * @param graphPane the pane in which the edge should be drawn.
     */
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

    /**
     * Draw an elipsis after a parent node to indicate that the parent has children, but there is
     * not enough space to draw the children.
     *
     * @param node the parent node.
     * @param graphPane the pane in which to draw the elipsis.
     */
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

    /**
     * Animate the zoom in on a node.
     *
     * @param newRoot the node which should be the root after zooming in.
     * @param timeline the timeline which is used for the animation.
     */
    public void zoomIn(ViewNode newRoot, Timeline timeline) {
        zoomIn(this.graphArea, newRoot.graphArea, timeline);
    }

    /**
     * Zoom in on a node and animate the zooming proces.
     *
     * @param originalArea the area of the currently drawn tree.
     * @param zoomArea the area of the tree to zoom in on.
     * @param timeline the timeline which is used for the animation.
     */
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

    /**
     * Animate the zoom out of 1 level (to the direct parent of the current root).
     *
     * @param timeline the timeline which is used for the animation.
     */
    public void zoomOut(Timeline timeline) {
        IPhylogeneticTree newRoot = dataNode.getParent();
        double nextStartX = graphArea.getCenterX();
        double ySize = graphArea.getHeight() / newRoot.getDirectChildCount();
        double nextStartY = ySize * newRoot.getChildIndex(this.dataNode) + graphArea.startY;
        double nextEndY = nextStartY + ySize;
        GraphArea newArea = new GraphArea(nextStartX, this.graphArea.endX, nextStartY, nextEndY);
        zoomOut(this.graphArea, newArea, timeline);
    }

    /**
     * Zoom out and animate the zooming proces.
     *
     * @param originalArea the area of the currently drawn tree.
     * @param zoomArea the area of the tree where the current tree will be drawn after zooming out.
     * @param timeline the timeline which is used for the animation.
     */
    private void zoomOut(GraphArea originalArea, GraphArea zoomArea, Timeline timeline) {
        double newX = (getCenterX() - originalArea.startX - NODE_RADIUS);
        newX = newX * zoomArea.getWidth() / originalArea.getWidth() + zoomArea.startX;
        newX += NODE_RADIUS;
        double newY = (getCenterY() - originalArea.startY);
        newY = newY * zoomArea.getHeight() / originalArea.getHeight() + zoomArea.startY;

        KeyValue kvX = new KeyValue(this.centerXProperty(), newX, Interpolator.EASE_BOTH);
        KeyValue kvY = new KeyValue(this.centerYProperty(), newY, Interpolator.EASE_BOTH);
        timeline.getKeyFrames().add(new KeyFrame(ANIMATION_DURATION, kvX, kvY));

        for (ViewNode child : children) {
            child.zoomOut(originalArea, zoomArea, timeline);
        }
    }
}
