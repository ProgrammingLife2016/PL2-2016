package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;

/**
 * This is a heatmap which indicates the positions of all of the overlapping nodes (nodes which are
 * in both the top and bottom graph).
 *
 * @author Faris
 */
public class OverlapHeatmap extends AbstractHeatmap {

  private static final Color OVERLAP_COLOR = Color.rgb(0, 0, 0, 0.2);
  private static final Color NO_OVERLAP_COLOR = Color.rgb(255, 255, 255, 0.2);

  public OverlapHeatmap(GraphicsContext heatmapGraphics, ObservableDoubleValue zoomFactor) {
    super(heatmapGraphics, zoomFactor);
  }

  @Override
  protected double getWidthMultiplier() {
    return 1.0;
  }

  @Override
  public void visit(SequenceNode node) {
    if (node.getGuiData().overlapping) {
      setColor(OVERLAP_COLOR);
    } else {
      setColor(NO_OVERLAP_COLOR);
    }
  }
}
