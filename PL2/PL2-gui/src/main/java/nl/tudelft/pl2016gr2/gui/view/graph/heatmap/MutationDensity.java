package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;

/**
 * This heatmap colors a node darker if it has many children, or a small size. This way nodes with
 * many children, or many nodes which are close togheter, will produce a darker color, indicating
 * more mutations.
 *
 * @author Faris
 */
public class MutationDensity extends AbstractHeatmap {
  
  private static final Color NODE_COLOR = Color.rgb(0, 0, 0, 0.3);

  public MutationDensity(GraphicsContext heatmapGraphics, ObservableDoubleValue zoomFactor) {
    super(heatmapGraphics, zoomFactor);
  }

  @Override
  protected double getWidthMultiplier() {
    return 1.0;
  }
  
  @Override
  public void visit(SequenceNode node) {
    setColor(NODE_COLOR);
  }
}
