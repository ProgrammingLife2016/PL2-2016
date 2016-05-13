package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.test.utility.TestId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
 * <li>The nodes will be newly instantiated, meaning that the subgraph datastructure is not backed
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
   * When {@link #getSubgraph(Collection)} is called, the class will construct a new instance of
   * the
   * graph containing only the specified genomes.
   * </p>
   *
   * @param mainGraph The graph to split
   */
  public SplitGraphs(OriginalGraph mainGraph) {
    this.mainGraph = mainGraph;
  }


  /**
   * Builds a subgraph of the {@link #mainGraph} containing only the nodes that contain (at least)
   * one of the specified genomes.
   *
   * @param genomes The genomes that should be included in the resulting graph
   * @return An <code>OriginalGraph</code> that is a subgraph of <code>mainGraph</code>
   * @throws NoSuchElementException Iff the genome collection is not a
   *                                subset of the main graph's genomes.
   */
  public OriginalGraph getSubgraph(Collection<String> genomes) {
    if (!mainGraph.getGenoms().containsAll(genomes)) {
      throw new NoSuchElementException("All genomes must be present in the main graph.");
    }
    OriginalGraph subGraph = buildSubgraph(genomes);
    return prune(subGraph, genomes);
  }

  /**
   * Creates a new graph containing only the nodes that will be part of the subGraph.
   * <p>
   * The nodes are not a deep copy, however the graph object is newly instantiated.
   * </p>
   *
   * @param genomes The genomes that should be included in the subgraph
   * @return The subgraph
   */
  private OriginalGraph buildSubgraph(Collection<String> genomes) {
    OriginalGraph subGraph = new OriginalGraph();
    // Add all nodes for every genome
    for (String genome : genomes) {
      Iterator<Node> iterator = mainGraph.iterator(genome);
      // add genome root
      subGraph.addNode(iterator.next());

      while (iterator.hasNext()) {
        subGraph.addNode(iterator.next());
      }

    }
    return subGraph;
  }


  /**
   * Removes all redundant links from the nodes in the graph.
   * <p>
   * Redundant links are links that point to a {@link Node} that is not part of the subgraph.
   * The method will return a new {@link OriginalGraph} object with new <code>Node</code>s.
   * The Nodes will contain identical fields, except for the {@link Node#genomes}, {@link
   * Node#inLinks} and {@link Node#outLinks}.
   * </p>
   * <p>
   * While this method is intended solely for subgraphs, it will provide the same behaviour when
   * called with any graph.
   * </p>
   *
   * @param graph The graph to prune.
   * @return A new <code>OriginalGraph</code> with only the relevant links.
   */
  private OriginalGraph prune(OriginalGraph graph, Collection<String> genomes) {
    HashMap<Integer, Node> nodeMap = graph.getNodes();
    // Create new mirror graph
    OriginalGraph pruned = new OriginalGraph();

    for (Node node : nodeMap.values()) {
      // Create mirror of original Node.
      Node mirrorNode = new Node(node.getId(), node.getSequenceLength(),
          pruneGenomes(node, genomes), node.getSnips());

      node.getInlinks().forEach((inLink) -> {
        if (nodeMap.containsKey(inLink)) {
          mirrorNode.addInlink(inLink);
        }
      });
      node.getOutlinks().forEach((outLink) -> {
        if (nodeMap.containsKey(outLink)) {
          mirrorNode.addOutlink(outLink);
        }
      });
      pruned.addNode(mirrorNode);
    }

    return pruned;
  }

  /**
   * Builds a subset of gGenomes that is the intersection of the genomes in the graph and the node.
   * The intersection is formally described as <code>node.getGenomes &#8745 graphGenomes</code>
   *
   * @param node         The node to build the subset for
   * @param graphGenomes The set of genomes in the graph
   * @return The intersection of the node and graph genomes
   */
  private ArrayList<String> pruneGenomes(Node node, Collection<String> graphGenomes) {
    ArrayList<String> prunedGenomes = new ArrayList<>();
    node.getGenomes().forEach(genome -> {
      if (graphGenomes.contains(genome)) {
        prunedGenomes.add(genome);
      }
    });
    return prunedGenomes;
  }
}
