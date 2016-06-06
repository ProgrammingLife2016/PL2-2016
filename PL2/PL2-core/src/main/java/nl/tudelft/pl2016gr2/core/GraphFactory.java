package nl.tudelft.pl2016gr2.core;

import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;

public interface GraphFactory {

  /**
   * Get the graph.
   *
   * @return the graph.
   */
  SequenceGraph getGraph();
}
