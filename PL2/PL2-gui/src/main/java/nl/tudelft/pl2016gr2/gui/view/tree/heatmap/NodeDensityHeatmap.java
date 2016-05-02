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
public class NodeDensityHeatmap implements INodeHeatmap {

    private final Pane pane;
    private final GraphArea area;
    private ArrayList<ViewNode> currentLeaves;

    public NodeDensityHeatmap(Pane pane, ArrayList<ViewNode> currentLeaves,
            GraphArea heatmapArea) {
        this.pane = pane;
        this.area = heatmapArea;
        onChange(currentLeaves);
    }

    @Override
    public final void onChange(ArrayList<ViewNode> newLeaves) {
        currentLeaves = newLeaves;
        pane.getChildren().clear();
        int maxChildren = getMaxChildren();
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
            rect.setFill(mapColor(children, maxChildren));
            rect.setStrokeWidth(3.0);
            rect.setStroke(Color.BLACK);
            pane.getChildren().add(rect);
        }
    }

    private int getMaxChildren() {
        int maxChildren = 1;
        for (ViewNode currentLeave : currentLeaves) {
            int curChildren = currentLeave.getDataNode().getChildCount();
            if (curChildren > maxChildren) {
                maxChildren = curChildren;
            }
        }
        return maxChildren;
    }

    private Color mapColor(int amountOfChildren, int maxChildren) {
        if (amountOfChildren == 0) {
            return Color.WHITE;
        }
        double ratio = Math.sqrt(amountOfChildren / (double) maxChildren);
        int red = (int) (255 - 255 * ratio);
        return Color.rgb(red, red / 4, red / 4);
    }
}
