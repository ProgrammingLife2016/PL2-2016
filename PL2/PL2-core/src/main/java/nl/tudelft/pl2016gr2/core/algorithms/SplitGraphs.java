package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.HashGraph;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
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
  private final SequenceGraph mainGraph;

  /**
   * Instantiates an algorithmic class on a <code>SequenceGraph</code>.
   * <p>
   * When {@link #getSubgraph(Collection)} is called, the class will construct a new instance of
   * the
   * graph containing only the specified genomes.
   * </p>
   *
   * @param mainGraph The graph to split
   */
  public SplitGraphs(SequenceGraph mainGraph) {
    this.mainGraph = mainGraph;
  }

  /**
   * Builds a subgraph of the <code>mainGraph</code> containing only the nodes that contain at
   * least
   * one of the specified <code>genomes</code>.
   *
   * @param genomes The genomes that should be included in the resulting graph
   * @return An <code>SequenceGraph</code> that is a subgraph of <code>mainGraph</code>
   * @throws NoSuchElementException Iff the genome collection is not a subset of the main graph's
   *                                genomes
   */
  public SequenceGraph getSubgraph(Collection<String> genomes) {
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
    Iterator<GraphNode> nodeIterator = mainGraph.iterator();
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
   * This operation is performed in <code>O(n)</code> for <code>n</code> nodes. This method
   * requires
   * a {@link HashSet} to provide this time complexity.
   * </p>
   *
   * @param nodeSet   The set containing the original nodes which are contained in the subgraph.
   * @param genomeSet The set of all genomes that should be included in the subgraph.
   * @return the new graph which contains all of the newly created nodes.
   */
  private SequenceGraph createNewGraph(HashSet<Integer> nodeSet, HashSet<String> genomeSet) {
    SequenceGraph newGraph = new HashGraph();
    nodeSet.forEach(nodeId -> {
      GraphNode originalNode = mainGraph.getNode(nodeId);
      GraphNode newNode = pruneNode(originalNode, genomeSet, nodeSet);
      newGraph.add(newNode);
    });
    genomeSet.forEach(newGraph::addGenome);
    return newGraph;
  }

  /**
   * Creates a copy of the <code>original</code> node and prunes irrelevant fields.
   * <p>
   * The method returns a newly instantiated {@link GraphNode} object that is identical, except for
   * the genomes, in-edges and out-edges.
   * </p>
   * <p>
   * This operation is performed in <code>O(l+m)</code> for <code>l, m</code> genomes and links
   * respectively.
   * </p>
   *
   * @param original  The original <code>node</code> to prune and copy
   * @param genomeSet The set of genomes that should be retained in the pruning process
   * @param nodeSet   The set of nodes that should be retained in the pruning process
   * @return A new <code>GraphNode</code> identical to the <code>original</code> after pruning
   */
  private GraphNode pruneNode(
      GraphNode original, HashSet<String> genomeSet, HashSet<Integer> nodeSet) {
    GraphNode newNode = original.copy();

    pruneGenomes(original, genomeSet).forEach(newNode::addGenome);
    pruneInLinks(original, nodeSet).forEach(newNode::addInEdge);
    pruneOutLinks(original, nodeSet).forEach(newNode::addOutEdge);

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
  private Collection<Integer> pruneInLinks(GraphNode original, HashSet<Integer> graphNodes) {
    Collection<Integer> prunedInLinks = new ArrayList<>();
    original.getInEdges().forEach(edge -> {
      if (graphNodes.contains(edge)) {
        prunedInLinks.add(edge);
      }
    });
    return prunedInLinks;
  }

  /**
   * Builds a subset of outLinks that is the intersection of the nodes in the graph and the
   * outLinks
   * in the node.
   * <p>
   * This operation is performed in <code>O(m)</code> for <code>m</code> outLinks.
   * </p>
   *
   * @param original   The node to build the subset for
   * @param graphNodes The set of nodes in the graph
   * @return The intersection of the node outLinks and the graph nodes
   */
  private Collection<Integer> pruneOutLinks(GraphNode original, HashSet<Integer> graphNodes) {
    Collection<Integer> prunedOutLinks = new ArrayList<>();
    original.getOutEdges().forEach(edge -> {
      if (graphNodes.contains(edge)) {
        prunedOutLinks.add(edge);
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
  private ArrayList<String> pruneGenomes(GraphNode node, HashSet<String> graphGenomes) {
    ArrayList<String> prunedGenomes = new ArrayList<>();
    node.getGenomes().forEach(genome -> {
      if (graphGenomes.contains(genome)) {
        prunedGenomes.add(genome);
      }
    });
    return prunedGenomes;
  }
}
