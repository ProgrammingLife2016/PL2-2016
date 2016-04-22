package dnav.view;

import dnav.model.TreeNode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 *
 * @author Faris
 */
public class ViewNode extends Circle implements ZoomHandler {

	private final TreeNode dataNode;
	private final static double NODE_RADIUS = 10.0;
	private final static double NODE_DIAMETER = NODE_RADIUS * 2.0;

	private ViewNode(TreeNode dataNode, GraphArea gbox) {
		super(NODE_RADIUS);
		this.dataNode = dataNode;

		double red = (dataNode.getChildCount() * 13 % 200) / 255d;
		double green = (dataNode.getChildCount() * 34 % 200) / 255d;
		double blue = (dataNode.getChildCount() * 88 % 200) / 255d;
		this.setFill(new Color(red, green, blue, 1.0));

		this.setCenterX(this.getRadius() + gbox.startX);
		this.setCenterY(gbox.getCenterY());
	}

	public TreeNode getDataNode() {
		return dataNode;
	}

	private static ViewNode drawNode(TreeNode dataNode, GraphArea gbox, Pane graphPane) {
		if (gbox.getWidth() < NODE_DIAMETER || gbox.getHeight() < NODE_DIAMETER
				|| dataNode == null) {
			return null; // box too small to draw node.
		}
		ViewNode node = new ViewNode(dataNode, gbox);
		RootLayoutController.getController().addZoomListener(node);
		node.setOnMouseClicked(RootLayoutController.getController().clickNode); // TEMPORARY
		graphPane.getChildren().add(node);

		double nextStartX = gbox.getCenterX();
		double ySize = (gbox.endY - gbox.startY) / dataNode.getDirectChildCount();
		for (int i = 0; i < dataNode.getDirectChildCount(); i++) {
			TreeNode childDataNode = dataNode.getChild(i);
			double nextStartY = ySize * i + gbox.startY;
			double nextEndY = nextStartY + ySize;
			ViewNode child = drawNode(childDataNode,
					new GraphArea(nextStartX, gbox.endX, nextStartY, nextEndY), graphPane);
			drawEdge(node, child, graphPane);
		}

		return node;
	}

	private static void drawEdge(ViewNode parent, ViewNode child, Pane graphPane) {
		if (child == null) {
			Line l = new Line(parent.getCenterX() + parent.getRadius() + 10, parent.getCenterY(),
					parent.getCenterX() + parent.getRadius() + 28, parent.getCenterY());
			l.getStrokeDashArray().addAll(2d, 4d);
			graphPane.getChildren().add(l);
			return;
		}
		Line edge = new Line(parent.getCenterX(), parent.getCenterY(), child.getCenterX(),
				child.getCenterY());
		graphPane.getChildren().add(edge);
		edge.toBack();
	}

	public static void drawRootNode(TreeNode root) {
//        assert root.hasParent() == false;

		RootLayoutController controller = RootLayoutController.getController();
		Pane graphPane = controller.getGraphPane();
		double startX = RootLayoutController.GRAPH_BORDER_OFFSET;
		double endX = graphPane.getWidth() * 0.8 - 2 * RootLayoutController.GRAPH_BORDER_OFFSET;
		double startY = RootLayoutController.GRAPH_BORDER_OFFSET;
		double endY = graphPane.getHeight() - 2 * RootLayoutController.GRAPH_BORDER_OFFSET;
		GraphArea gbox = new GraphArea(startX, endX, startY, endY);
		drawNode(root, gbox, graphPane);
	}

	@Override
	public void handleZoom(GraphArea zoomedArea, Pane graphPane) {
		if (!zoomedArea.contains(this.getCenterX(), this.getCenterY())) {
			clear();
			graphPane.getChildren().remove(this);
			return;
		}
		setCenterX((getCenterX() - zoomedArea.startX) / zoomedArea.getWidth()
				* graphPane.getWidth());
		setCenterY((getCenterY() - zoomedArea.startY) / zoomedArea.getHeight()
				* graphPane.getHeight());
	}

	private void clear() {
		RootLayoutController.getController().removeZoomListener(this);
	}
}
