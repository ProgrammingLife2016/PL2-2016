package dnav.view;

import dnav.model.TreeNode;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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

	public final static double GRAPH_BORDER_OFFSET = 5.0;

	@FXML
	private Pane graphPane;
	@FXML
	private StackPane locationIdentifierPane;
	@FXML
	private Rectangle locationIdentifierRectangle;
	@FXML
	private Slider zoomInSlider;

	private TreeNode currentRoot;
	private final ArrayList<ZoomHandler> zoomListeners = new ArrayList();

	private static RootLayoutController controller;

	/**
	 * Initializes the controller class.
	 */
	public void initialize() {
		assert (controller == null);
		controller = this;
		graphPane.setOnScroll((ScrollEvent event) -> {
//			graphPane.getChildren().clear();
			ArrayList<ZoomHandler> copyHandlers = (ArrayList<ZoomHandler>) zoomListeners.clone();
			for (ZoomHandler zoomListener : copyHandlers) {
				zoomListener.handleZoom(new GraphArea(100, graphPane.getWidth(), 0, graphPane.getHeight()), graphPane);
			}
		});
	}

	public void handleSceneWidthChanged() {
		if (currentRoot != null) {
			setRoot(currentRoot);
		}
	}

	public void handleSceneHeightChanged() {
		if (currentRoot != null) {
			setRoot(currentRoot);
		}
	}

	public void insertData(TreeNode root) {
		assert currentRoot == null;
		setRoot(root);
	}

	private void setRoot(TreeNode root) {
		currentRoot = root;
		graphPane.getChildren().clear();
		ViewNode.drawRootNode(root);
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

	public final EventHandler<MouseEvent> clickNode = (MouseEvent event) -> {
		setRoot(((ViewNode) event.getSource()).getDataNode());
	};

	protected Pane getGraphPane() {
		return graphPane;
	}

	protected void addZoomListener(ZoomHandler zoomHandler) {
		zoomListeners.add(zoomHandler);
	}

	protected void removeZoomListener(ZoomHandler zoomHandler) {
		zoomListeners.remove(zoomHandler);
	}

	protected static RootLayoutController getController() {
		return controller;
	}
}
