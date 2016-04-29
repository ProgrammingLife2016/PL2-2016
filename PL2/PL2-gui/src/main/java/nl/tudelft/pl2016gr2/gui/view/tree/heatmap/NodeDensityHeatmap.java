package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import java.util.ArrayList;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.tree.GraphArea;
import nl.tudelft.pl2016gr2.gui.view.tree.ViewNode;

/**
 *
 * @author faris
 */
public class NodeDensityHeatmap {

	private final Pane pane;
	private final GraphArea area;
	private ArrayList<ViewNode> currentLeaves;

	public NodeDensityHeatmap(Pane pane, ArrayList<ViewNode> currentLeaves,
			GraphArea heatmapArea) {
		this.pane = pane;
		this.currentLeaves = currentLeaves;
		this.area = heatmapArea;
		onChange();
	}

	private void onChange() {
		pane.getChildren().clear();
		int maxChildren = 0;
		for (ViewNode currentLeave : currentLeaves) {
			int curChildren = currentLeave.getDataNode().getChildCount();
			if (curChildren > maxChildren) {
				maxChildren = curChildren;
			}
		}
		double startY, height;
		double width = area.getWidth();
		double startX = area.getStartX();
		for (ViewNode currentLeave : currentLeaves) {
			int children = currentLeave.getDataNode().getChildCount();
			GraphArea nodeArea = currentLeave.getGraphArea();
			startY = nodeArea.getStartY();
			height = nodeArea.getHeight();
			Rectangle rect = new Rectangle(startX, startY, width, height);
			startY += height;
			int red = (children * children);
			red = red > 255 ? 255 : red;
			int blue = children == 0 ? 255 : 0;
			int green = 0;//(int) (children / (double) maxChildren * 255.0);
			rect.setFill(Color.rgb(red, green, blue));
			rect.setStrokeWidth(3.0);
			rect.setStroke(Color.BLACK);
			pane.getChildren().add(rect);
		}
	}
}
