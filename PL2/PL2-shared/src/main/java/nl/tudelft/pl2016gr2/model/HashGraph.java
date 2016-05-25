package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class HashGraph implements SequenceGraph {

  private final HashMap<Integer, GraphNode> nodes;
  private final ArrayList<Integer> rootNodes;
  private final HashSet<String> genomes;

  /**
   * Constructs an empty <code>HashGraph</code>.
   */
  public HashGraph() {
    nodes = new HashMap<>();
    rootNodes = new ArrayList<>();
    genomes = new HashSet<>();
  }

  /**
   * Constructs a <code>HashGraph</code> with the specified elements and genomes.
   *
   * @param nodes   The nodes of the graph
   * @param genomes The genomes that are represented in the graph
   */
  public HashGraph(Map<Integer, ? extends GraphNode> nodes, Collection<String> genomes) {
    this.nodes = new HashMap<>(nodes);
    this.genomes = new HashSet<>(genomes);
    this.rootNodes = parseRootNodes();
  }

  /**
   * Construct an <code>HashGraph</code> with the specified elements and genomes, as well as
   * pre-determined root nodes.
   * <p>
   * A root node is a node that has no in-edges.
   * </p>
   *
   * @param nodes     The nodes of the graph
   * @param rootNodes The root nodes of the graph
   * @param genomes   The genomes that are represented in the graph
   */
  public HashGraph(Map<Integer, ? extends GraphNode> nodes, Collection<Integer> rootNodes,
      Collection<String> genomes) {
    this.nodes = new HashMap<>(nodes);
    this.rootNodes = new ArrayList<>(rootNodes);
    this.genomes = new HashSet<>(genomes);
  }

  /**
   * Finds all root nodes in the graph.
   * <p>
   * This operation runs in <code>O(n)</code> for <code>n</code> nodes.
   * </p>
   *
   * @return all root nodes.
   */
  private ArrayList<Integer> parseRootNodes() {
    ArrayList<Integer> rootNodes = new ArrayList<>();
    nodes.forEach((Integer elemId, GraphNode elem) -> {
      if (elem.isRoot()) {
        rootNodes.add(elemId);
      }
    });
    return rootNodes;
  }

  @Override
  public void addAsRootNode(int identifier) {
    assert nodes.get(identifier).isRoot() : "Adding non-root node as root. NodeID: " + identifier;
    rootNodes.add(identifier);
  }

  @Override
  public Collection<Integer> getRootNodes() {
    return rootNodes;
  }

  @Override
  public Collection<String> getGenomes() {
    return genomes;
  }

  @Override
  public void addGenome(String genome) {
    assert !genomes.contains(genome) : "Adding already existing genome to the graph.";
    genomes.add(genome);
  }

  @Override
  public void removeGenome(String genome) {
    assert genomes.contains(genome) : "Removing non-existent genome from the graph.";
    genomes.remove(genome);
  }

  @Override
  public int size() {
    return nodes.size();
  }

  @Override
  public boolean isEmpty() {
    return nodes.isEmpty();
  }

  @Override
  public boolean contains(int identifier) {
    return nodes.containsKey(identifier);
  }

  @Override
  public GraphNode getNode(int identifier) {
    return nodes.get(identifier);
  }

  @Override
  public void add(GraphNode node) {
    assert !nodes.containsKey(node.getId()) : "Adding already existing element to the graph.";

    if (node.isRoot()) {
      rootNodes.add(node.getId());
    }
    nodes.put(node.getId(), node);
  }

  @Override
  public GraphNode remove(int identifier) {
    return nodes.remove(identifier);
  }

  /**
   * Iterates over the collection of nodes in the graph.
   *
   * @return An iterator over all nodes in the graph
   */
  @Override
  public Iterator<GraphNode> iterator() {
    return nodes.values().iterator();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (GraphNode node : nodes.values()) {
      sb.append(node).append('\n');
    }
    return sb.toString();
  }

  @Override
  public ArrayList<GraphNode> getNodes() {
    return new ArrayList<>(nodes.values());
  }
}
