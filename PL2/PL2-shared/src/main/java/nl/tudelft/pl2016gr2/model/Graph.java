package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

/**
 * Mainly a dataholder class which represents a graph.
 *
 * @author Cas
 */
public class Graph {

  private ArrayList<Bubble> nodes;

  /**
   * Constructs a graph from an arrayList of nodes. (the edges are already in these nodes)
   *
   * @param nodes all of the nodes of this graph.
   */
  public Graph(ArrayList<Bubble> nodes) {
    this.nodes = nodes;
  }

  /**
   * Print a string representation of this node for testing purposes.
   */
  public void print() {
    for (Bubble n : nodes) {
      if (n.getId() != 0) {
        System.out.println(n);
      }
    }
  }

  public Bubble getRoot() {
    return nodes.get(1);
  }

  public int getSize() {
    return nodes.size();
  }

  public ArrayList<Bubble> getNodes() {
    return nodes;
  }

  public void addNode(Bubble node) {
    nodes.add(node);
  }
}
