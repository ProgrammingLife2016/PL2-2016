package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import java.util.ArrayList;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.view.tree.GraphArea;
import nl.tudelft.pl2016gr2.gui.view.tree.ViewNode;

/**
 * Manages the different kinds of heatmaps.
 *
 * @author faris
 */
public class HeatmapManager {

  private final Pane pane;
  private NodeDensityHeatmap densityMap;

  public HeatmapManager(Pane pane) {
    this.pane = pane;
  }

  public void initLeaves(ArrayList<ViewNode> currentLeaves) {
    GraphArea area = new GraphArea(10, 20, 0, pane.getHeight());
    densityMap = new NodeDensityHeatmap(pane, currentLeaves, area);
  }

  public void setLeaves(ArrayList<ViewNode> currentLeaves) {
    densityMap.onChange(currentLeaves);
  }
}
