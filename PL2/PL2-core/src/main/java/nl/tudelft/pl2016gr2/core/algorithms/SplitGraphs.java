package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Provides functionality for extracting a subgraph of a sequence graph based on a subset of
 * genomes.
 * <p>
 * The resulting subgraph will provide the following characteristics.
 * <ul>
 * <li>The nodes in the subgraph are a subset of the nodes in the original graph.</li>
 * <li>The nodes in the subgraph will have an identical ID as their counterparts in the original
 * graph.</li>
 * <li>The nodes will be newly instantiated, meaning that the subgraph data structure is not backed
 * by the original graph.</li>
 * </ul>
 * </p>
 *
 * @author Wouter Smit
 */
public class SplitGraphs {

  @TestId(id = "graph_field")
  private final OriginalGraph mainGraph;

  /**
   * Instantiates an algorithmic class on a <code>OriginalGraph</code>.
   * <p>
   * When {@link #getSubgraph(Collection)} is called, the class will construct a new instance of the
   * graph containing only the specified genomes.
   * </p>
   *
   * @param mainGraph The graph to split
   */
  public SplitGraphs(OriginalGraph mainGraph) {
    this.mainGraph = mainGraph;
  }

  /**
   * Builds a subgraph of the <code>mainGraph</code> containing only the nodes that contain at least
   * one of the specified <code>genomes</code>.
   *
   * @param genomes The genomes that should be included in the resulting graph
   * @return An <code>OriginalGraph</code> that is a subgraph of <code>mainGraph</code>
   * @throws NoSuchElementException Iff the genome collection is not a subset of the main graph's
   *                                genomes
   */
  public OriginalGraph getSubgraph(Collection<String> genomes) {
    assert mainGraph.getGenomes().containsAll(genomes) : "Tried splitting graph on absent genomes";

    HashSet<String> genomeSet = new HashSet<>(genomes);
    HashSet<Integer> nodeSet = findSubgraphNodes(genomeSet);
    return createNewGraph(nodeSet, genomeSet);
  }

  /**
   * Find all of the nodes which are part of the subgraph and put them into a map.
   * <p>
   * The nodes in the returned map are backed by the original nodes.
   * </p>
   *
   * @param genomeSet the genomes that should be included in the subgraph.
   * @return a map containing all of the nodes which are part of the subgraph.
   */
  private HashSet<Integer> findSubgraphNodes(HashSet<String> genomeSet) {
    HashSet<Integer> nodeSet = new HashSet<>();
    Iterator<Node> nodeIterator = mainGraph.iterator();
    nodeIterator.forEachRemaining(node -> {
      for (String genome : node.getGenomes()) {
        if (genomeSet.contains(genome)) {
          nodeSet.add(node.getId());
          break;
        }
      }
    });
    return nodeSet;
  }

  /**
   * Create a new graph, containing all of the nodes in the node map with the correct in/out links
   * and genome lists.
   * <p>
   * This operation is performed in <code>O(n)</code> for <code>n</code> nodes. This method requires
   * a {@link HashSet} to provide this time complexity.
   * </p>
   *
   * @param nodeSet   The set containing the original nodes which are contained in the subgraph.
   * @param genomeSet The set of all genomes that should be included in the subgraph.
   * @return the new graph which contains all of the newly created nodes.
   */
  private OriginalGraph createNewGraph(HashSet<Integer> nodeSet, HashSet<String> genomeSet) {
    OriginalGraph newGraph = new OriginalGraph();
    nodeSet.forEach(nodeId -> {
      Node originalNode = mainGraph.getNode(nodeId);
      Node newNode = pruneNode(originalNode, genomeSet, nodeSet);
      newGraph.addNode(newNode);

      if (newNode.getInlinks().isEmpty()) {
        newGraph.addRootNode(newNode);
      }
    });
    newGraph.setGenoms(new ArrayList<>(genomeSet));
    return newGraph;
  }

  /**
   * Creates a copy of the <code>original</code> node and prunes irrelevant fields.
   * <p>
   * The method returns a newly instantiated {@link Node} object that is identical, except for the
   * {@link Node#genomes}, {@link Node#inLinks} and {@link Node#outLinks} fields.
   * </p>
   * <p>
   * This operation is performed in <code>O(l+m)</code> for <code>l, m</code> genomes and links
   * respectively.
   * </p>
   *
   * @param original  The original <code>node</code> to prune and copy
   * @param genomeSet The set of genomes that should be retained in the pruning process
   * @param nodeSet   The set of nodes that should be retained in the pruning process
   * @return A new <code>Node</code> identical to the <code>original</code> after pruning
   */
  private Node pruneNode(Node original, HashSet<String> genomeSet, HashSet<Integer> nodeSet) {
    Node newNode = new Node(original.getId(), 1, pruneGenomes(original, genomeSet), 0);

    newNode.setBases(original.getBases());
    newNode.setAlignment(original.getAlignment());

    Collection<Integer> inLinks = pruneInLinks(original, nodeSet);
    inLinks.forEach(newNode::addInlink);
    Collection<Integer> outLinks = pruneOutLinks(original, nodeSet);
    outLinks.forEach(newNode::addOutlink);

    return newNode;
  }

  /**
   * Builds a subset of inLinks that is the intersection of the nodes in the graph and the inLinks
   * in the node.
   * <p>
   * This operation is performed in <code>O(m)</code> for <code>m</code> inLinks.
   * </p>
   *
   * @param original   The node to build the subset for
   * @param graphNodes The set of nodes in the graph
   * @return The intersection of the node inLinks and the graph nodes
   */
  private Collection<Integer> pruneInLinks(Node original, HashSet<Integer> graphNodes) {
    Collection<Integer> prunedInLinks = new ArrayList<>();
    original.getInlinks().forEach(link -> {
      if (graphNodes.contains(link)) {
        prunedInLinks.add(link);
      }
    });
    return prunedInLinks;
  }

  /**
   * Builds a subset of outLinks that is the intersection of the nodes in the graph and the outLinks
   * in the node.
   * <p>
   * This operation is performed in <code>O(m)</code> for <code>m</code> outLinks.
   * </p>
   *
   * @param original   The node to build the subset for
   * @param graphNodes The set of nodes in the graph
   * @return The intersection of the node outLinks and the graph nodes
   */
  private Collection<Integer> pruneOutLinks(Node original, HashSet<Integer> graphNodes) {
    Collection<Integer> prunedOutLinks = new ArrayList<>();
    original.getOutlinks().forEach(link -> {
      if (graphNodes.contains(link)) {
        prunedOutLinks.add(link);
      }
    });
    return prunedOutLinks;
  }

  /**
   * Builds a subset of genomes that is the intersection of the genomes in the graph and the node.
   * <p>
   * This operation is performed in <code>O(m)</code> for <code>m</code> genomes.
   * </p>
   *
   * @param node         The node to build the subset for
   * @param graphGenomes The set of genomes in the graph
   * @return The intersection of the node and graph genomes
   */
  private ArrayList<String> pruneGenomes(Node node, HashSet<String> graphGenomes) {
    ArrayList<String> prunedGenomes = new ArrayList<>();
    node.getGenomes().forEach(genome -> {
      if (graphGenomes.contains(genome)) {
        prunedGenomes.add(genome);
      }
    });
    return prunedGenomes;
  }
}
