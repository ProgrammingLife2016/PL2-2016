package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.Position;
import nl.tudelft.pl2016gr2.model.SequenceGraph;

import java.util.ArrayList;

/**
 * This is a data storage class. It stores the data of a compared graph.
 *
 * @author Faris
 */
public class OrderedGraph {

  private final SequenceGraph subgraph;
  private final ArrayList<Position> graphOrder;

  /**
   * Construct an ordered graph.
   *
   * @param subgraph   the subgraph.
   * @param arrayList the graph order.
   */
  public OrderedGraph(SequenceGraph subgraph, ArrayList<Position> arrayList) {
    this.subgraph = subgraph;
    this.graphOrder = arrayList;
  }

  public SequenceGraph getSubgraph() {
    return subgraph;
  }

  public ArrayList<Position> getGraphOrder() {
    return graphOrder;
  }
}
