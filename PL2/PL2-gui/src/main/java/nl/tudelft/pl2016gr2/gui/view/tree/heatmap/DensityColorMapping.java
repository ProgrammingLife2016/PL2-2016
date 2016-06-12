package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;

import java.util.HashMap;

/**
 * Maps the amount of children of a node to a color and sets it for the heatmap rectangles.
 *
 * @author Faris
 */
public class DensityColorMapping implements IColorMapper {

  @Override
  public void mapColors(HashMap<TreeNodeCircle, Rectangle> heatmapRectangles) {
    HashMap<Rectangle, Integer> densityMap = new HashMap<>();
    IntegerProperty maxDensity = new SimpleIntegerProperty(1);
    heatmapRectangles.forEach((TreeNodeCircle node, Rectangle rect) -> {
      int density = node.getDataNode().getChildCount();
      densityMap.put(rect, density);
      if (density > maxDensity.get()) {
        maxDensity.set(density);
      }
    });
    densityMap.forEach((Rectangle rect, Integer density) -> {
      rect.setFill(mapColor(density, maxDensity.get()));
    });
  }

  /**
   * Maps the amount of children to a color.
   *
   * @param amountOfChildren the amount of children.
   * @param maxChildren      the maximum amount of children of any current leave node.
   * @return the color.
   */
  private static Color mapColor(int amountOfChildren, int maxChildren) {
    if (amountOfChildren == 0) {
      return Color.WHITE;
    }
    double ratio = Math.sqrt(amountOfChildren / (double) maxChildren);
    int red = (int) (255 - 255 * ratio);
    return Color.rgb(red, red / 4, red / 4);
  }
}
