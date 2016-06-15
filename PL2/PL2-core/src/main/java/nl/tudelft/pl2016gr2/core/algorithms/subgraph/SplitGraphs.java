package nl.tudelft.pl2016gr2.core.algorithms.subgraph;


import nl.tudelft.pl2016gr2.model.graph.HashGraph;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
   * When {@link #getSubgraph(Collection)} is called, this class will construct a new instance of
   * the graph containing only the specified genomes.
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
  public SequenceGraph getSubgraph(Collection<Integer> genomes) {
    HashSet<Integer> genomeSet = new HashSet<>(genomes);
    assert mainGraph.getGenomes().containsAll(
        genomeSet) : "Tried splitting graph on absent genomes";

    HashMap<Integer, GraphNode> nodeMap = findSubgraphNodes(genomeSet);
    return createNewGraph(nodeMap, genomeSet);
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
  private HashMap<Integer, GraphNode> findSubgraphNodes(HashSet<Integer> genomeSet) {
    HashMap<Integer, GraphNode> nodeMap = new HashMap<>();
    mainGraph.forEach(node -> {
      if (node.getGenomes().stream().anyMatch(genomeSet::contains)) {
        nodeMap.put(node.getId(), node);
      }
    });
    assert !nodeMap.isEmpty() : "Subgraph returned zero nodes. This is impossible at this stage";
    return nodeMap;
  }

  /**
   * Create a new graph, containing copies of all the nodes in the node map with the correct in/out
   * links
   * and genome lists.
   * <p>
   * This operation is performed in <code>O(n)</code> for <code>n</code> nodes. This method
   * requires a {@link HashSet} to provide this time complexity.
   * </p>
   *
   * @param oldNodes The set containing the original nodes which are contained in the subgraph.
   * @param genomes  The set of all genomes that should be included in the subgraph.
   * @return the new graph which contains all of the newly created nodes.
   */
  private SequenceGraph createNewGraph(HashMap<Integer, GraphNode> oldNodes,
      HashSet<Integer> genomes) {
    HashMap<Integer, GraphNode> newNodes = new HashMap<>();

    oldNodes.forEach((id, originalNode) -> newNodes.put(id, originalNode.copy()));
    HashMap<Integer, GraphNode> prunedNodes = pruneNodes(newNodes, oldNodes, genomes);
    return new HashGraph(prunedNodes, genomes);
  }

  /**
   * Prunes irrelevant fields of the original nodes and applies them to the new nodes.
   * <p>
   * This method returns the same <code>newNodes</code> map, with all nodes updated correctly.
   * </p>
   * <p>
   * This operation is performed in <code>O(n*(k+l+m)</code> for <code>n</code> nodes and <code>k,
   * l, m</code> genomes, in edges and out edges respectively.
   * </p>
   *
   * @param newNodes The new nodes to apply the pruned data to
   * @param oldNodes The original <code>node</code>s whose data to prune
   * @param genomes  The set of genomes that should be retained in the pruning process
   * @return The updated map of nodes
   */
  private HashMap<Integer, GraphNode> pruneNodes(HashMap<Integer, GraphNode> newNodes,
      HashMap<Integer, GraphNode> oldNodes, HashSet<Integer> genomes) {
    newNodes.forEach((id, node) -> {
      node.addAllInEdges(pruneInEdges(node, newNodes, oldNodes, genomes));
      node.addAllOutEdges(pruneOutEdges(node, newNodes, oldNodes, genomes));
      node.addAllGenomes(pruneGenomes(oldNodes.get(id), genomes));
    });

    return newNodes;
  }

  /**
   * Builds a subset of inLinks that is the intersection of the nodes in the graph and the inLinks
   * in the node.
   * <p>
   * This operation is performed in <code>O(m)</code> for <code>m</code> inLinks.
   * </p>
   *
   * @param newNode  The node to build the subset for
   * @param newNodes The nodes of the new graph
   * @param oldNodes The nodes of the old graph
   * @return The intersection of the node inLinks and the graph nodes
   */
  private Collection<GraphNode> pruneInEdges(GraphNode newNode,
      HashMap<Integer, GraphNode> newNodes, HashMap<Integer, GraphNode> oldNodes,
      Collection<Integer> genomes) {
    Collection<GraphNode> prunedInEdges = new ArrayList<>();
    GraphNode oldNode = oldNodes.get(newNode.getId());
    oldNode.getInEdges().forEach(oldInEdge -> {
      if (oldInEdge.getGenomesOverEdge(oldNode).stream().anyMatch(genomes::contains)) {
        // Edge is valid
        GraphNode newInEdge = newNodes.get(oldInEdge.getId());
        assert newInEdge != null;
        prunedInEdges.add(newInEdge);
      }
    });
    return prunedInEdges;
  }

  /**
   * Builds a subset of outLinks that is the intersection of the nodes in the graph and the
   * outLinks
   * in the node.
   * <p>
   * This operation is performed in <code>O(m)</code> for <code>m</code> outLinks.
   * </p>
   *
   * @param newNode  The node to build the subset for
   * @param newNodes The nodes of the new graph
   * @param oldNodes The nodes of the old graph
   * @return The intersection of the node outLinks and the subgraph nodes
   */
  private Collection<GraphNode> pruneOutEdges(GraphNode newNode,
      HashMap<Integer, GraphNode> newNodes, HashMap<Integer, GraphNode> oldNodes,
      Collection<Integer> genomes) {
    Collection<GraphNode> prunedOutEdges = new ArrayList<>();
    GraphNode oldNode = oldNodes.get(newNode.getId());
    oldNode.getOutEdges().forEach(outEdge -> {
      if (oldNode.getGenomesOverEdge(outEdge).stream().anyMatch(genomes::contains)) {
        // Edge is valid
        GraphNode newOutEdge = newNodes.get(outEdge.getId());
        assert newOutEdge != null;
        prunedOutEdges.add(newOutEdge);
      }
    });
    return prunedOutEdges;
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
  private ArrayList<Integer> pruneGenomes(GraphNode node, HashSet<Integer> graphGenomes) {
    ArrayList<Integer> prunedGenomes = new ArrayList<>();
    node.getGenomes().forEach(genome -> {
      if (graphGenomes.contains(genome)) {
        prunedGenomes.add(genome);
      }
    });
    return prunedGenomes;
  }
}
