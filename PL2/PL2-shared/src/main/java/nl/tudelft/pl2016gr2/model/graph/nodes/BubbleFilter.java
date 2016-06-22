package nl.tudelft.pl2016gr2.model.graph.nodes;

import java.util.List;

/**
 * Interface of the bubble filter class. This interface needs to be 
 * implemented by classes that create GraphBubbles or PhyloBubbles, 
 * to be able to zoom in on the graph.
 *
 * @author Casper
 */
public interface BubbleFilter {

  /**
   * Zoom in on the bubble.
   *
   * @param bubble the bubble to zoom in on.
   * @return the list of aligned and sorted nested nodes.
   */
  List<GraphNode> zoomIn(Bubble bubble);
}
