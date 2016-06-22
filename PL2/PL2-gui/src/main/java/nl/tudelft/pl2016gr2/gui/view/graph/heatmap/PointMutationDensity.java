package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.GraphicsContext;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;

/**
 * This is a heatmap which indicates the positions of all of the point mutation bubbles.
 *
 * @author Faris
 */
public class PointMutationDensity extends AbstractHeatmap {

  public PointMutationDensity(GraphicsContext heatmapGraphics, ObservableDoubleValue zoomFactor) {
    super(heatmapGraphics, zoomFactor);
  }

  @Override
  public void visit(PointMutationBubble bubble) {
    setColor(MUTATION_COLOR);
  }
  
  @Override
  protected double getWidthMultiplier() {
    return 2.0;
  }
}
