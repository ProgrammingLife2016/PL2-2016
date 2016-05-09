package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.events.AnimationEvent;
import nl.tudelft.pl2016gr2.gui.view.tree.Area;
import nl.tudelft.pl2016gr2.gui.view.tree.ViewNode;
import nl.tudelft.pl2016gr2.test.utility.TestId;

import java.util.ArrayList;

/**
 * This is a density heatmap. The generated colors will depend on the amount of child nodes which
 * the leaves in the user interface have. Note that leaves in the user interface might actually have
 * child nodes, since there might just be too few space to display the child nodes. This heatmap is
 * intended to make it easier for the user to see how many children are not being displayed.
 *
 * @author faris
 */
public class NodeDensityHeatmap implements INodeHeatmap {

  private final Pane pane;
  private final Area area;
  @TestId(id = "currentLeaves")
  private ArrayList<ViewNode> currentLeaves;

  /**
   * Create an instance of this class.
   *
   * @param pane          the pane in which to draw the heatmap.
   * @param currentLeaves the current leaves.
   * @param heatmapArea   the area of the pane that may be user to draw the heatmap.
   */
  public NodeDensityHeatmap(Pane pane, ArrayList<ViewNode> currentLeaves, Area heatmapArea) {
    this.pane = pane;
    this.area = heatmapArea;
    onChange(currentLeaves);
  }

  @Override
  public void onChange(ArrayList<ViewNode> newLeaves) {
    currentLeaves = newLeaves;
    pane.getChildren().clear();
    int maxChildren = getMaxChildren();
    double startY;
    double height;
    double width = area.getWidth();
    double startX = area.getStartX();
    for (ViewNode currentLeaf : currentLeaves) {
      Area nodeArea = currentLeaf.getGraphArea();
      startY = nodeArea.getStartY();
      height = nodeArea.getHeight();
      Rectangle rect = new Rectangle(startX, startY, width, height);
      startY += height;
      int children = currentLeaf.getDataNode().getChildCount();
      rect.setFill(mapColor(children, maxChildren));
      rect.setStrokeWidth(3.0);
      rect.setStroke(Color.BLACK);
      pane.getChildren().add(rect);

      currentLeaf.addEventHandler(AnimationEvent.ANIMATION_EVENT, (AnimationEvent event) -> {
        double newHeight = rect.getHeight() * event.getScale();
        double newY = rect.getY() - (event.getStartY() - event.getEndY())
            - (newHeight - rect.getHeight()) / 2.0;
        KeyValue kv = new KeyValue(rect.yProperty(), newY, Interpolator.EASE_BOTH);
        KeyValue kv2
            = new KeyValue(rect.heightProperty(), newHeight, Interpolator.EASE_BOTH);
        event.getTimeline().getKeyFrames().add(new KeyFrame(event.getDuration(), kv, kv2));
      });
    }
  }

  /**
   * Get the maximum amount of children of the leaf nodes.
   *
   * @return the maximum amount of children of the leaf nodes.
   */
  @TestId(id = "getMaxChildren")
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

  /**
   * Maps the amount of children to a color.
   *
   * @param amountOfChildren the amount of children.
   * @param maxChildren      the maximum amount of children of any current leave node.
   * @return the color.
   */
  @TestId(id = "mapColor")
  private static Color mapColor(int amountOfChildren, int maxChildren) {
    if (amountOfChildren == 0) {
      return Color.WHITE;
    }
    double ratio = Math.sqrt(amountOfChildren / (double) maxChildren);
    int red = (int) (255 - 255 * ratio);
    return Color.rgb(red, red / 4, red / 4);
  }
}
