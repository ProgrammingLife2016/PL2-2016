package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.GraphicsContext;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;

/**
 * This is a heatmap which indicates the positions of all of the indel bubbles.
 *
 * @author Faris
 */
public class IndelDensity extends AbstractHeatmap {

  public IndelDensity(GraphicsContext heatmapGraphics, ObservableDoubleValue zoomFactor) {
    super(heatmapGraphics, zoomFactor);
  }

  @Override
  public void visit(IndelBubble bubble) {
    setColor(MUTATION_COLOR);
  }
  
  @Override
  protected double getWidthMultiplier() {
    return 2.0;
  }
}
