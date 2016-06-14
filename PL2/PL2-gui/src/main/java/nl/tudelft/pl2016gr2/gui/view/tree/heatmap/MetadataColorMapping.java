package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;

import java.util.HashMap;

/**
 * This class colors the given set of heatmap rectangles according to the percentage of heatmap
 * rectangles which have the selected metadata property.
 *
 * @author Faris
 */
public class MetadataColorMapping implements IColorMapper {

  private final IMetadataChecker checker;

  public MetadataColorMapping(IMetadataChecker checker) {
    this.checker = checker;
  }

  @Override
  public void mapColors(HashMap<TreeNodeCircle, Rectangle> heatmapRectangles) {
    heatmapRectangles.forEach(this::colorRectangle);
  }

  /**
   * Color the rectangle according to the percentage of child nodes of the given tree node which
   * posses the chosen metadata property.
   *
   * @param treeNodeCircle the tree node.
   * @param rect           the rectangle to color.
   */
  private void colorRectangle(TreeNodeCircle treeNodeCircle, Rectangle rect) {
    int amountOfLeaves = 0;
    int amountWithProperty = 0;
    for (IPhylogeneticTreeNode phylogeneticTreeNode : treeNodeCircle.getDataNode()) {
      ++amountOfLeaves;
      if (checker.check(phylogeneticTreeNode.getMetaData())) {
        amountWithProperty++;
      }
    }
    rect.setFill(mapColor(amountWithProperty / (double) amountOfLeaves));
  }

  /**
   * Maps the fraction to a color.
   *
   * @param fraction the fraction of node with the property.
   * @return the color.
   */
  private static Color mapColor(double fraction) {
    if (fraction == 0.0) {
      return Color.WHITE;
    }
    double ratio = Math.sqrt(fraction);
    int red = (int) (255 - 255 * ratio);
    return Color.rgb(red, red / 4, red / 4);
  }

}
