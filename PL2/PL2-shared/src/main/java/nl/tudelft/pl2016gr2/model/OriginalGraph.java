package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OriginalGraph implements GraphInterface, Iterable<Node> {

  private HashMap<Integer, Node> nodes;
  private int lowestId;
  private ArrayList<Integer> rootNodes;
  private ArrayList<String> genoms;

  /**
   * Constructs an empty OriginalGraph.
   */
  public OriginalGraph() {
    nodes = new HashMap<>();
    lowestId = Integer.MAX_VALUE;
    rootNodes = new ArrayList<>();
    genoms = new ArrayList<>();
  }

  /**
   * Construct an OriginalGraph.
   *
   * @param nodes    the nodes.
   * @param lowestId the lowest node id.
   * @param genoms   the genoms.
   */
  public OriginalGraph(HashMap<Integer, Node> nodes, int lowestId, ArrayList<String> genoms) {
    this.nodes = nodes;
    this.lowestId = lowestId;
    this.genoms = genoms;
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

  public ArrayList<Integer> getRootNodes() {
    return rootNodes;
  }

  /**
   * Adds a <code>Node</code> to the root nodes of this graph.
   * <p>
   * If the node is not yet in the graph, the node will be added.
   * </p>
   *
   * @param node The root node to add.
   */
  public void addRootNode(Node node) {
    assert node.getInlinks().isEmpty() : "Tried adding a non-root node as root. NodeID = " + node
        .getId();

    if (!nodes.containsKey(node.getId())) {
      nodes.put(node.getId(), node);
    }

    rootNodes.add(node.getId());
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

    int id = node.getId();
    if (id < lowestId) {
      lowestId = id;
    }

    nodes.put(id, (Node) node);
  }

  @Override
  public Node getRoot() {
    return nodes.get(lowestId);
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

  /**
   * Iterates over the collection of nodes in the graph.
   *
   * @return An iterator over all nodes in the graph
   */
  @Override
  public Iterator<Node> iterator() {
    return nodes.values().iterator();
  }
}
