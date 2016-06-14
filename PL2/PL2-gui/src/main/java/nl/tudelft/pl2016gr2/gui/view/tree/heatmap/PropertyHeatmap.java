package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.events.AnimationEvent;
import nl.tudelft.pl2016gr2.gui.view.tree.Area;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class populated a heatmap in the userinterface according to the selected value to display in
 * the heatmap.
 *
 * @author Faris
 */
public class PropertyHeatmap {

  private final Pane heatmapPane;
  private IColorMapper valueChecker;
  @TestId(id = "leaves")
  private ArrayList<TreeNodeCircle> leaves = new ArrayList<>(0);

  public PropertyHeatmap(Pane heatmapPane, IColorMapper valueChecker) {
    this.heatmapPane = heatmapPane;
    this.valueChecker = valueChecker;
  }

  public void setValueChecker(IColorMapper valueChecker) {
    this.valueChecker = valueChecker;
    onChange(leaves);
  }

  /**
   * Notify a heatmap that the displayed leaves in the user interface have changed.
   *
   * @param newLeaves the new list of leave nodes.
   */
  public void onChange(ArrayList<TreeNodeCircle> newLeaves) {
    leaves = newLeaves;
    heatmapPane.getChildren().clear();
    double startY;
    double height;
    HashMap<TreeNodeCircle, Rectangle> nodeRectMapping = new HashMap<>();
    for (TreeNodeCircle currentLeaf : newLeaves) {
      Area nodeArea = currentLeaf.getGraphArea();
      startY = nodeArea.getStartY();
      height = nodeArea.getHeight();
      Rectangle rect = new Rectangle(0, startY, heatmapPane.getWidth(), height);
      nodeRectMapping.put(currentLeaf, rect);
      startY += height;
      rect.setStrokeWidth(3.0);
      rect.setStroke(Color.BLACK);
      heatmapPane.getChildren().add(rect);
      addAnimationEventHandler(currentLeaf, rect);
    }
    valueChecker.mapColors(nodeRectMapping);
  }

  /**
   * Add an animation handler to the given view node, which animates the heatmap rectangle when the
   * view node is animated.
   *
   * @param leaf        the view node.
   * @param heatmapRect the heatmap rectangle associated with the given view node.
   */
  private void addAnimationEventHandler(TreeNodeCircle leaf, Rectangle heatmapRect) {
    leaf.addEventHandler(AnimationEvent.ANIMATION_EVENT, (AnimationEvent event) -> {
      double newHeight = heatmapRect.getHeight() * event.getScale();
      double newY = heatmapRect.getY() - (event.getStartY() - event.getEndY())
          - (newHeight - heatmapRect.getHeight()) / 2.0;
      KeyValue kv = new KeyValue(heatmapRect.yProperty(), newY, Interpolator.EASE_BOTH);
      KeyValue kv2
          = new KeyValue(heatmapRect.heightProperty(), newHeight, Interpolator.EASE_BOTH);
      event.getTimeline().getKeyFrames().add(new KeyFrame(event.getDuration(), kv, kv2));
    });
  }
}
