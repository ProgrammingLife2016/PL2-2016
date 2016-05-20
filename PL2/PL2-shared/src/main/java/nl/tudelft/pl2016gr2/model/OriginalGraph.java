package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OriginalGraph implements GraphInterface, Iterable<Node> {

  private final HashMap<Integer, AbstractNode> nodes;
  private final ArrayList<Integer> rootNodes;

  private ArrayList<String> genomes;

  /**
   * Constructs an empty OriginalGraph.
   */
  public OriginalGraph() {
    nodes = new HashMap<>();
    rootNodes = new ArrayList<>();
    genomes = new ArrayList<>();
  }

  /**
   * Construct an original graph.
   *
   * @param nodes  the nodes of the graph.
   * @param genoms the genomes which are contained in the graph.
   */
  public OriginalGraph(HashMap<Integer, AbstractNode> nodes, ArrayList<String> genoms) {
    this.nodes = nodes;
    this.genomes = genoms;
    this.rootNodes = getAllRootNodes();
  }

  /**
   * Construct an original graph.
   *
   * @param nodes     the nodes of the graph.
   * @param rootNodes the root nodes (nodes without inlinks) of the graph.
   * @param genoms    the genomes which are contained in the graph.
   */
  public OriginalGraph(HashMap<Integer, AbstractNode> nodes, ArrayList<Integer> rootNodes,
      ArrayList<String> genoms) {
    this.nodes = nodes;
    this.rootNodes = rootNodes;
    this.genomes = genoms;
  }

  /**
   * Get all of the root nodes (all nodes with 0 inlinks).
   *
   * @return all root nodes.
   */
  private ArrayList<Integer> getAllRootNodes() {
    ArrayList<Integer> newRootNodes = new ArrayList<>();
    nodes.forEach((Integer id, AbstractNode node) -> {
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
    for (AbstractNode node : nodes.values()) {
      sb.append(node).append('\n');
    }
    return sb.toString();
  }

  @Override
  public AbstractNode getNode(int id) {
    return nodes.get(id);
  }

  /**
   * Get all of the nodes to which the node with the given ID has an outlink.
   *
   * @param id the id of the node.
   * @return the nodes of the outlinks.
   */
  public ArrayList<AbstractNode> getTargets(int id) {
    ArrayList<Integer> outLinks = nodes.get(id).getOutlinks();
    ArrayList<AbstractNode> targets = new ArrayList<>();

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
  
  public HashMap<Integer, AbstractNode> getNodes() {
    return nodes;
  }

  public ArrayList<String> getGenomes() {
    return this.genomes;
  }

  public void setGenoms(ArrayList<String> genomes) {
    this.genomes = genomes;
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
