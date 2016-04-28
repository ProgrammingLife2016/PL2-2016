package nl.tudelft.pl2016gr2.gui.view;

import java.util.Observable;
import java.util.Observer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeController;
import nl.tudelft.pl2016gr2.gui.view.tree.heatmap.HeatmapManager;

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

    private static RootLayoutController controller;
    private TreeController treeController;
	private HeatmapManager heatmapManager;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        assert (controller == null);
        controller = this;
		heatmapManager = new HeatmapManager(heatmapPane);
    }

    /**
     * Set the data which has to be visualized.
     *
     * @param root the root of the tree which has to be drawn.
     */
    public void setData(IPhylogeneticTreeNode root) {
        assert treeController == null;
        treeController = new TreeController(graphPane, root, zoomOutButton);
		treeController.setOnChildLeavesChanged((Observable o, Object arg) -> {
			heatmapManager.setLeaves(treeController.getCurrentLeaves());
		});
    }
}
