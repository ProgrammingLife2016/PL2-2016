package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.GraphicsContext;
import nl.tudelft.pl2016gr2.model.graph.nodes.StraightSequenceBubble;

/**
 * This is a heatmap which indicates the positions of all of the straight sequence bubbles.
 *
 * @author Faris
 */
public class StraightSequenceDensity extends AbstractHeatmap {

  public StraightSequenceDensity(GraphicsContext heatmapGraphics, 
      ObservableDoubleValue zoomFactor) {
    super(heatmapGraphics, zoomFactor);
  }

  @Override
  public void visit(StraightSequenceBubble bubble) {
    setColor(TRANSPARENT_MUTATION_COLOR);
  }
  
  @Override
  protected double getWidthMultiplier() {
    return 1.0;
  }
}
