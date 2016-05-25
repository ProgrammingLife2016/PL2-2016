package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.tree.Area;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

/**
 * This class tests the {@link NodeDensityHeatmap} class.
 *
 * @author Faris
 */
@RunWith(MockitoJUnitRunner.class)
public class NodeDensityHeatmapTest {

  /**
   * Test of onChange method, of class NodeDensityHeatmap.
   */
  @Test
  public void testOnChange() {
    TreeNodeCircle viewNode = Mockito.mock(TreeNodeCircle.class);
    IPhylogeneticTreeNode dataNode = Mockito.mock(IPhylogeneticTreeNode.class);
    Area area = new Area(0, 10, 0, 20);

    when(viewNode.getGraphArea()).thenReturn(area);
    when(viewNode.getDataNode()).thenReturn(dataNode);
    when(dataNode.getChildCount()).thenReturn(0);

    ArrayList<TreeNodeCircle> leaves = new ArrayList<>();
    leaves.add(viewNode);
    Pane paneSpy = Mockito.spy(new Pane());
    NodeDensityHeatmap heatmap = new NodeDensityHeatmap(paneSpy, leaves, area);
    verify(paneSpy, times(2)).getChildren();
  }

  /**
   * Test of getMaxChildren method, of class NodeDensityHeatmap.
   */
  @Test
  public void testGetMaxChildren() {
    TreeNodeCircle viewNode = Mockito.mock(TreeNodeCircle.class);
    IPhylogeneticTreeNode dataNode10 = Mockito.mock(IPhylogeneticTreeNode.class);
    IPhylogeneticTreeNode dataNode5 = Mockito.mock(IPhylogeneticTreeNode.class);
    when(viewNode.getDataNode()).thenReturn(dataNode5, dataNode10);
    when(dataNode10.getChildCount()).thenReturn(10);
    when(dataNode5.getChildCount()).thenReturn(5);
    ArrayList<TreeNodeCircle> nodeList = new ArrayList<>();
    nodeList.add(viewNode);
    nodeList.add(viewNode);
    NodeDensityHeatmap heatmap = Mockito.mock(NodeDensityHeatmap.class);
    AccessPrivate.setFieldValue("currentLeaves", NodeDensityHeatmap.class, heatmap, nodeList);
    int maxChildren = AccessPrivate.callMethod("getMaxChildren", NodeDensityHeatmap.class,
        heatmap);
    assertEquals(10, maxChildren);
  }

  /**
   * Test of mapColor method, of class NodeDensityHeatmap. Tests if the brightness of nodes with
   * more children is darker.
   */
  @Test
  public void testMapColor() {
    Color c5 = AccessPrivate.callMethod("mapColor", NodeDensityHeatmap.class, null, 5, 10);
    Color c6 = AccessPrivate.callMethod("mapColor", NodeDensityHeatmap.class, null, 6, 10);
    assertTrue(c5.getBrightness() > c6.getBrightness());
  }
}
