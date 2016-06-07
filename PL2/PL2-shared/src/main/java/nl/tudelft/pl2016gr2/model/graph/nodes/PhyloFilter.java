package nl.tudelft.pl2016gr2.model.graph.nodes;

import java.util.List;

/**
 * Interface of the phylo bubble filter class.
 *
 * @author Casper
 */
public interface PhyloFilter {

  /**
   * Zoom in on the bubble.
   *
   * @param bubble the bubble to zoom in on.
   * @return the list of aligned and sorted nested nodes.
   */
  List<GraphNode> zoomIn(Bubble bubble);
}
