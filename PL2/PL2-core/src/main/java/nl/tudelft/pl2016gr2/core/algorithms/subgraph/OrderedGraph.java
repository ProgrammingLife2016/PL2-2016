package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.ArrayList;

/**
 * This is a data storage class. It stores the data of a compared graph.
 *
 * @author Faris
 */
public class OrderedGraph {

  private final int amountOfGenomes;
  private final ArrayList<GraphNode> graphOrder;

  /**
   * Construct an ordered graph.
   *
   * @param amountOfGenomes the amount of genomes in the graph.
   * @param graphOrder      the graph order.
   */
  public OrderedGraph(int amountOfGenomes, ArrayList<GraphNode> graphOrder) {
    this.amountOfGenomes = amountOfGenomes;
    this.graphOrder = graphOrder;
    graphOrder.trimToSize();
  }

  public int getGenomeSize() {
    return amountOfGenomes;
  }

  public ArrayList<GraphNode> getGraphOrder() {
    return graphOrder;
  }
}
