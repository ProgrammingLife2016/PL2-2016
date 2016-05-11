package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.HashMap;

public class OriginalGraph implements GraphInterface {

  private HashMap<Integer, Node> nodes;
  private HashMap<Integer, AbstractNode> abstractNodes;
  private int lowestId;
  private ArrayList<String> genoms;

  /**
   * Creates an originalgraph, which contains no bubbles and only nodes.
   */
  public OriginalGraph() {
    nodes = new HashMap<>();
    abstractNodes = new HashMap<>();
    lowestId = Integer.MAX_VALUE;
  }

  /**
   * Print a string representation of this graph.
   */
  @Override
  public void print() {
    for (Node node : nodes.values()) {
      System.out.println(node);
    }
  }

  @Override
  public Node getNode(int id) {
    return nodes.get(id);
  }

  /**
   * Get all of the nodes to which the node with the given ID has an outlink.
   *
   * @param id the id of the node.
   * @return the nodes of the outlinks.
   */
  public ArrayList<Node> getTargets(int id) {
    ArrayList<Integer> outLinks = nodes.get(id).getOutlinks();
    ArrayList<Node> targets = new ArrayList<>();

    for (Integer outLink : outLinks) {
      targets.add(nodes.get(outLink));
    }

    return targets;
  }

  @Override
  public int getSize() {
    return nodes.size();
  }

  @Override
  public void addNode(AbstractNode node) {
    assert node instanceof Node;

    int id = node.getId();
    if (id < lowestId) {
      lowestId = id;
    }

    nodes.put(id, (Node) node);
    abstractNodes.put(id, node);
  }

  @Override
  public Node getRoot() {
    return nodes.get(lowestId);
  }

  public HashMap<Integer, Node> getNodes() {
    return nodes;
  }
  
  @Override
  public HashMap<Integer, AbstractNode> getAbstractNodes() {
    return abstractNodes;
  }

  public ArrayList<String> getGenoms() {
    return this.genoms;
  }

  public void setGenoms(ArrayList<String> gs) {
    this.genoms = gs;
  }

}
