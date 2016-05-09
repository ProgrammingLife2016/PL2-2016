package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.view.tree.ViewNode;
import nl.tudelft.pl2016gr2.test.utility.AccessPrivate;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;

/**
 * This class tests the {@link HeatmapManager} class.
 *
 * @author Faris
 */
public class HeatmapManagerTest {

  /**
   * Test of initLeaves method, of class HeatmapManager.
   */
  @Test
  public void testInitLeaves() {
    HeatmapManager heatmapManager = new HeatmapManager(new Pane());
    NodeDensityHeatmap densityMap = (NodeDensityHeatmap) AccessPrivate.getFieldValue("densityMap",
        HeatmapManager.class, heatmapManager);
    assertEquals(null, densityMap);
    heatmapManager.initLeaves(new ArrayList<>());
    densityMap = (NodeDensityHeatmap) AccessPrivate.getFieldValue("densityMap",
        HeatmapManager.class, heatmapManager);
    ArrayList<ViewNode> leaves = (ArrayList) AccessPrivate.getFieldValue("currentLeaves",
        NodeDensityHeatmap.class, densityMap);
    assertTrue(leaves.isEmpty());
  }

  /**
   * Test of setLeaves method, of class HeatmapManager.
   */
  @Test
  public void testSetLeaves() {
    HeatmapManager heatmapManager = new HeatmapManager(new Pane());
    NodeDensityHeatmap densityMap = Mockito.mock(NodeDensityHeatmap.class);
    AccessPrivate.setFieldValue("densityMap", HeatmapManager.class, heatmapManager, densityMap);
    ArrayList<ViewNode> leaves = new ArrayList<>();
    heatmapManager.setLeaves(leaves);
    verify(densityMap, times(1)).onChange(leaves);
  }
}
