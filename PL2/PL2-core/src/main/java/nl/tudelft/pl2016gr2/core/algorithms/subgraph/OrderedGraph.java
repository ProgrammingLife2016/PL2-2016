package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.SequenceGraph;

import java.util.ArrayList;

/**
 * This is a data storage class. It stores the data of a compared graph.
 *
 * @author Faris
 */
public class OrderedGraph {

  private final SequenceGraph subgraph;
//  private final ArrayList<GraphNode> graphOrderStartX;
  private final ArrayList<GraphNode> graphOrderEndX;

  /**
   * Construct an ordered graph.
   *
   * @param subgraph   the subgraph.
   * @param graphOrder the graph order.
   */
  public OrderedGraph(SequenceGraph subgraph, ArrayList<GraphNode> graphOrder) {
    this.subgraph = subgraph;
    this.graphOrderEndX = graphOrder;
//    graphOrderStartX = new ArrayList<>(graphOrder);
//    graphOrderStartX.sort((GraphNode node1, GraphNode node2) -> node1.getLevel() - node1.size()
//        - (node2.getLevel() - node2.size()));
  }

  public SequenceGraph getSubgraph() {
    return subgraph;
  }

  public ArrayList<GraphNode> getGraphOrder() {
    return graphOrderEndX;
  }

//  public ArrayList<GraphNode> getGraphOrderByStartXPosition() {
//    return graphOrderStartX;
//  }
}
