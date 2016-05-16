package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.HashMap;

public class OriginalGraph implements GraphInterface {

  private final HashMap<Integer, Node> nodes;
  private final ArrayList<Integer> rootNodes;
  private ArrayList<String> genoms;

  public OriginalGraph() {
    nodes = new HashMap<>();
    rootNodes = new ArrayList<>();
  }

  /**
   * Construct an original graph.
   *
   * @param nodes  the nodes of the graph.
   * @param genoms the genomes which are contained in the graph.
   */
  public OriginalGraph(HashMap<Integer, Node> nodes, ArrayList<String> genoms) {
    this.nodes = nodes;
    this.genoms = genoms;
    this.rootNodes = getAllRootNodes();
  }

  /**
   * Construct an original graph.
   *
   * @param nodes     the nodes of the graph.
   * @param rootNodes the root nodes (nodes without inlinks) of the graph.
   * @param genoms    the genomes which are contained in the graph.
   */
  public OriginalGraph(HashMap<Integer, Node> nodes, ArrayList<Integer> rootNodes,
      ArrayList<String> genoms) {
    this.nodes = nodes;
    this.rootNodes = rootNodes;
    this.genoms = genoms;
  }

  /**
   * Get all of the root nodes (all nodes with 0 inlinks).
   *
   * @return all root nodes.
   */
  private ArrayList<Integer> getAllRootNodes() {
    ArrayList<Integer> newRootNodes = new ArrayList<>();
    nodes.forEach((Integer id, Node node) -> {
      if (node.getInlinks().isEmpty()) {
        newRootNodes.add(id);
      }
    });
    return newRootNodes;
  }

  @Override
  public ArrayList<Integer> getRootNodes() {
    return rootNodes;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Node node : nodes.values()) {
      sb.append(node).append('\n');
    }
    return sb.toString();
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
    nodes.put(node.getId(), (Node) node);
    if (node.getInlinks().isEmpty()) {
      rootNodes.add(node.getId());
    }
  }

  public HashMap<Integer, Node> getNodes() {
    return nodes;
  }

  public ArrayList<String> getGenoms() {
    return this.genoms;
  }

  public void setGenoms(ArrayList<String> gs) {
    this.genoms = gs;
  }
}
