package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class HashGraph implements SequenceGraph {

  @TestId(id = "nodes")
  private final HashMap<Integer, GraphNode> nodes;
  private final ArrayList<GraphNode> rootNodes;
  private final ArrayList<Integer> genomes;

  /**
   * Constructs an empty <code>HashGraph</code>.
   */
  public HashGraph() {
    nodes = new HashMap<>();
    rootNodes = new ArrayList<>();
    genomes = new ArrayList<>();
  }

  /**
   * Constructs a <code>HashGraph</code> with the specified elements and genomes.
   *
   * @param nodes   The nodes of the graph
   * @param genomes The genomes that are represented in the graph
   */
  public HashGraph(Map<Integer, ? extends GraphNode> nodes, Collection<Integer> genomes) {
    this.nodes = new HashMap<>(nodes);
    this.genomes = new ArrayList<>(genomes);
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
  public HashGraph(Map<Integer, ? extends GraphNode> nodes,
      Collection<? extends GraphNode> rootNodes, Collection<Integer> genomes) {
    this.nodes = new HashMap<>(nodes);
    this.rootNodes = new ArrayList<>(rootNodes);
    this.genomes = new ArrayList<>(genomes);
  }

  public void print() {
    nodes.forEach((id, node) -> {
      System.out.println(node);
    });
  }

  @Override
  public ArrayList<GraphNode> getOrderedGraph() {
    ArrayList<GraphNode> graphOrder = new ArrayList<>(nodes.values());
    graphOrder.sort((GraphNode node1, GraphNode node2) -> node1.getLevel() - node2.getLevel());
    return graphOrder;
  }

  /**
   * Finds all root nodes in the graph.
   * <p>
   * This operation runs in <code>O(n)</code> for <code>n</code> nodes.
   * </p>
   *
   * @return all root nodes.
   */
  private ArrayList<GraphNode> parseRootNodes() {
    ArrayList<GraphNode> rootNodes = new ArrayList<>();
    nodes.forEach((Integer id, GraphNode node) -> {
      if (node.isRoot()) {
        rootNodes.add(node);
      }
    });
    rootNodes.trimToSize();
    return rootNodes;
  }

  @Override
  public void addAsRootNode(GraphNode rootNode) {
    assert rootNode.isRoot() : "Adding non-root node as root. NodeID: " + rootNode;
    rootNodes.add(rootNode);
  }

  @Override
  public Collection<GraphNode> getRootNodes() {
    return rootNodes;
  }

  @Override
  public Collection<Integer> getGenomes() {
    return genomes;
  }

  @Override
  public void addGenome(int genome) {
    assert !genomes.contains(genome) : "Adding already existing genome to the graph.";
    genomes.add(genome);
  }

  @Override
  public void removeGenome(int genome) {
    assert genomes.contains(genome) : "Removing non-existent genome from the graph.";
    genomes.remove((Integer) genome);
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
  public boolean contains(GraphNode node) {
    return nodes.containsKey(node.getId());
  }

  @Override
  public GraphNode getNode(int identifier) {
    return nodes.get(identifier);
  }

  @Override
  public void add(GraphNode node) {
    //assert !nodes.containsKey(node.getId()) : "Adding already existing element to the graph.";
    if (node.isRoot()) {
      rootNodes.add(node);
    }
    nodes.put(node.getId(), node);
  }

  @Override
  public void remove(GraphNode node, boolean updateInEdges, boolean updateOutEdges) {
    if (updateInEdges) {
      node.getInEdges().forEach(inEdge -> {
        if (nodes.containsKey(inEdge.getId())) {
          inEdge.removeOutEdge(node);
        }
      });
    }
    
    if (updateOutEdges) {
      node.getOutEdges().forEach(outEdge -> {
        if (nodes.containsKey(outEdge.getId())) {
          outEdge.removeInEdge(node);
        }
      });
    }
    nodes.remove(node.getId());
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
      sb.append(node.toString()).append('\n');
    }
    return sb.toString();
  }
}
