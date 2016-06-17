package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import static nl.tudelft.pl2016gr2.gui.view.graph.heatmap.OverlapHeatmap.NO_OVERLAP_COLOR;
import static nl.tudelft.pl2016gr2.gui.view.graph.heatmap.OverlapHeatmap.OVERLAP_COLOR;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.GraphicsContext;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;

/**
 * This is a heatmap which indicates the positions of all of the non overlapping nodes (nodes which
 * are not in both the top and bottom graph).
 *
 * @author Faris
 */
public class NoOverlapHeatmap extends AbstractHeatmap {

  public NoOverlapHeatmap(GraphicsContext heatmapGraphics, ObservableDoubleValue zoomFactor) {
    super(heatmapGraphics, zoomFactor);
  }

  @Override
  protected double getWidthMultiplier() {
    return 1.0;
  }

  @Override
  public void visit(SequenceNode node) {
    if (node.getGuiData().overlapping) {
      setColor(NO_OVERLAP_COLOR);
    } else {
      setColor(OVERLAP_COLOR);
    }
  }
}
