package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.SequenceGraph;

import java.util.ArrayList;

/**
 * This is a data storage class. It stores the data of a compared graph.
 *
 * @author Faris
 */
public class OrderedGraph {

  private final SequenceGraph subgraph;
  private final ArrayList<NodePosition> graphOrder;

  /**
   * Construct an ordered graph.
   *
   * @param subgraph   the subgraph.
   * @param graphOrder the graph order.
   */
  public OrderedGraph(SequenceGraph subgraph, ArrayList<NodePosition> graphOrder) {
    this.subgraph = subgraph;
    this.graphOrder = graphOrder;
  }

  public SequenceGraph getSubgraph() {
    return subgraph;
  }

  public ArrayList<NodePosition> getGraphOrder() {
    return graphOrder;
  }
}
