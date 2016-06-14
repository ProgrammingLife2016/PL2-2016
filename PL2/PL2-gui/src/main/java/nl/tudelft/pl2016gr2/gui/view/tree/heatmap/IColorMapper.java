package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;

import java.util.HashMap;

/**
 * This interface can be used to set the color of a set of heatmap rectangles.
 *
 * @author Faris
 */
public interface IColorMapper {

  /**
   * Map the value found in the tree node to a color and set if for each rectangle in the given map
   * of tree node -> rectangle pairs.
   *
   * @param heatmapRectangles the tree node -> rectangle mapping.
   */
  void mapColors(HashMap<TreeNodeCircle, Rectangle> heatmapRectangles);
}
