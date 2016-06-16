package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.GraphicsContext;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphBubble;

/**
 * This is a heatmap which indicates the positions of all of the graph bubbles.
 *
 * @author Faris
 */
public class GraphBubbleDensity extends AbstractHeatmap {

  public GraphBubbleDensity(GraphicsContext heatmapGraphics, ObservableDoubleValue zoomFactor) {
    super(heatmapGraphics, zoomFactor);
  }

  @Override
  public void visit(GraphBubble bubble) {
    setColor(MUTATION_COLOR);
  }
  
  @Override
  protected double getWidthMultiplier() {
    return 1.0;
  }
}
