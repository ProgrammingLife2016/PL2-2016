package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.view.tree.Area;
import nl.tudelft.pl2016gr2.gui.view.tree.ViewNode;
import nl.tudelft.pl2016gr2.test.utility.TestId;

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
  }

  /**
   * Set the initial leaves of the phylogenetic tree. Here leaves mean the nodes which don't have
   * any child nodes in the user interface. This doesn't mean that these nodes are real leave nodes,
   * as there may just be too few space to display the child nodes of a node.
   *
   * @param currentLeaves the initial leaves.
   */
  public void initLeaves(ArrayList<ViewNode> currentLeaves) {
    Area area = new Area(10, 20, 0, pane.getHeight());
    densityMap = new NodeDensityHeatmap(pane, currentLeaves, area);
  }

  /**
   * Change the leaves of the phylogenetic tree. Here leaves mean the nodes which don't have any
   * child nodes in the user interface. This doesn't mean that these nodes are real leave nodes, as
   * there may just be too few space to display the child nodes of a node.
   *
   * @param currentLeaves the new leaves.
   */
  public void setLeaves(ArrayList<ViewNode> currentLeaves) {
    densityMap.onChange(currentLeaves);
  }
}
