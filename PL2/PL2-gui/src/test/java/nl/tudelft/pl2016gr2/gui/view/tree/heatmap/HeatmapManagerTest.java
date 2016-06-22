package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxIntegrationTestRunner;
import nl.tudelft.pl2016gr2.gui.view.MetadataPropertyMap;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;

/**
 * This class tests the {@link HeatmapManager} class.
 *
 * @author Faris
 */
@RunWith(JavaFxIntegrationTestRunner.class)
public class HeatmapManagerTest {

  /**
   * Test of initLeaves method, of class HeatmapManager.
   */
  @Test
  public void testInitLeaves() {
    HeatmapManager heatmapManager = new HeatmapManager(new Pane(), new Pane(), new MenuButton(), 
        new MenuButton(), new SimpleObjectProperty<>(new MetadataPropertyMap(new ArrayList<>())));
    PropertyHeatmap densityMap = AccessPrivate.getFieldValue("firstHeatmap", HeatmapManager.class,
        heatmapManager);
    ArrayList<TreeNodeCircle> leaves = AccessPrivate.getFieldValue("leaves",
        PropertyHeatmap.class, densityMap);
    assertTrue(leaves.isEmpty());
  }

  /**
   * Test of setLeaves method, of class HeatmapManager.
   */
  @Test
  public void testSetLeaves() {
    HeatmapManager heatmapManager = new HeatmapManager(new Pane(), new Pane(), new MenuButton(), 
        new MenuButton(), new SimpleObjectProperty<>(new MetadataPropertyMap(new ArrayList<>())));
    PropertyHeatmap firstHeatmap = Mockito.mock(PropertyHeatmap.class);
    PropertyHeatmap secoHeatmap = Mockito.mock(PropertyHeatmap.class);
    AccessPrivate.setFieldValue("firstHeatmap", HeatmapManager.class, heatmapManager, firstHeatmap);
    AccessPrivate.setFieldValue("secondHeatmap", HeatmapManager.class, heatmapManager, secoHeatmap);
    ArrayList<TreeNodeCircle> leaves = new ArrayList<>();
    heatmapManager.setLeaves(leaves);
    verify(firstHeatmap, times(1)).onChange(leaves);
    verify(secoHeatmap, times(1)).onChange(leaves);
  }
}
