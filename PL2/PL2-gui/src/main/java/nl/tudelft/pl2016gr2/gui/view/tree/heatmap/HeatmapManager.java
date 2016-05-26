package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.view.tree.Area;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;

/**
 * Manages the different kinds of heatmaps.
 *
 * @author faris
 */
public class HeatmapManager {

  private final Pane pane;
  @TestId(id = "densityMap")
  private NodeDensityHeatmap densityMap;

  /**
   * Create a heatmap manager.
   *
   * @param pane the pane in which to draw the heatmaps.
   */
  public HeatmapManager(Pane pane) {
    this.pane = pane;
    initHeatmaps();
  }

  /**
   * Initialize the heatmaps with an empty list of leaves.
   */
  @TestId(id = "initHeatmaps()")
  private void initHeatmaps() {
    Area area = new Area(10, 20, 0, pane.getHeight());
    densityMap = new NodeDensityHeatmap(pane, new ArrayList<>(), area);
  }

  /**
   * Change the leaves of the phylogenetic tree. Here leaves mean the nodes which don't have any
   * child nodes in the user interface. This doesn't mean that these nodes are real leave nodes, as
   * there may just be too few space to display the child nodes of a node.
   *
   * @param currentLeaves the new leaves.
   */
  public void setLeaves(ArrayList<TreeNodeCircle> currentLeaves) {
    densityMap.onChange(currentLeaves);
  }
}
