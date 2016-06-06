package nl.tudelft.pl2016gr2.model.graph.nodes;

import java.util.Collection;

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
  Collection<GraphNode> zoomIn(Bubble bubble);
}
