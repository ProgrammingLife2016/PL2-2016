package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

/**
 * This interface must be implemented by all graph heatmaps.
 *
 * @author Faris
 */
public interface IHeatmapColorer {

  Color MUTATION_COLOR = Color.RED;
  Color TRANSPARENT_MUTATION_COLOR = Color.rgb(255, 0, 0, 0.2);
  Color DEFAULT_COLOR = Color.rgb(255, 255, 255, 0.0);

  /**
   * Color the heatmap for the given node.
   *
   * @param node       the node.
   * @param startLevel the start level of the graph in the current view.
   */
  void drawHeatmap(GraphNode node, double startLevel);
}
