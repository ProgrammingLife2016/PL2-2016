package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

  private OriginalGraph mainGraph;
  private int part;

  /**
   * Instantiates an algorithmic class on a <code>OriginalGraph</code>.
   * <p>
   * When {@link #getSubgraph(Collection)} is called, the class will construct a new instance of the
   * graph containing only the specified genomes.
   * </p>
   *
   * @param mainGraph The graph to split
   * @param part      temporary variable because the tree doesn't match the graph.
   */
  public SplitGraphs(OriginalGraph mainGraph, int part) {
    this.mainGraph = mainGraph;
    this.part = part;
  }

  /**
   * Builds a subgraph of the {@link #mainGraph} containing only the nodes that contain (at least)
   * one of the specified genomes.
   *
   * @param genomes The genomes that should be included in the resulting graph
   * @return An <code>OriginalGraph</code> that is a subgraph of <code>mainGraph</code>
   * @throws NoSuchElementException Iff the genome collection is not a subset of the main graph's
   *                                genomes.
   */
  public OriginalGraph getSubgraph(Collection<String> genomes) {
    genomes.forEach((String genome) -> {
      if (mainGraph.getGenoms().contains(genome)) {
        System.out.println("genome = " + genome);
      }
    });
    Collection<String> halfGens;
    if (part == 0) {
      halfGens = mainGraph.getGenoms().subList(0, mainGraph.getGenoms().size() / 2);
    } else {
      halfGens = mainGraph.getGenoms().subList(mainGraph.getGenoms().size() / 2 + 1, mainGraph
          .getGenoms().size());
    }
    if (!mainGraph.getGenoms().containsAll(halfGens)) {
      throw new NoSuchElementException("All genomes must present in the main graph.");
    }
    HashMap<String, String> genomeMap = new HashMap<>();
    for (String genome : halfGens) {
      genomeMap.put(genome, genome);
    }
    HashMap<Integer, Node> nodeMap = findSubgraphNodes(genomeMap);
    return createNewGraph(nodeMap, genomeMap, halfGens);
  }

  /**
   * Find all of the nodes which are part of the subgraph and put them into a map.
   *
   * @param genomeMap the genomes that should be included in the subgraph.
   * @return a map containing all of the nodes which are part of the subgraph.
   */
  private HashMap<Integer, Node> findSubgraphNodes(Map<String, String> genomeMap) {
    HashMap<Integer, Node> nodeMap = new HashMap<>();
    mainGraph.getNodes().forEach((Integer id, Node node) -> {
      for (String genome : node.getGenomes()) {
        if (genomeMap.containsKey(genome)) {
          nodeMap.put(id, node);
          break;
        }
      }
    });
    return nodeMap;
  }

  /**
   * Create a new graph, containing all of the nodes in the node map with the correct in/out links
   * and genome lists.
   *
   * @param nodeMap   the map containing the original nodes which are contained in the subgraph.
   * @param genomeMap the map containing all of the genome names which should be contained in the
   *                  subgraph.
   * @param genomes   all of the genomes in a collection (instead of in a map).
   * @return the new graph which contains all of the newly created nodes.
   */
  private OriginalGraph createNewGraph(HashMap<Integer, Node> nodeMap,
      Map<String, String> genomeMap, Collection<String> genomes) {
    ArrayList<Integer> rootNodes = new ArrayList<>();
    HashMap<Integer, Node> newNodeMap = new HashMap<>();
    nodeMap.forEach((Integer id, Node originalNode) -> {
      ArrayList<String> nodeGenomes = new ArrayList<>();
      for (String genome : originalNode.getGenomes()) {
        if (genomeMap.containsKey(genome)) {
          nodeGenomes.add(genome);
        }
      }
      Node newNode = new Node(id, 1, nodeGenomes, 0);
      newNodeMap.put(id, newNode);
      newNode.setBases(originalNode.getBases());
      newNode.setAlignment(originalNode.getAlignment());
      setInOutLinks(originalNode, newNode, nodeMap, rootNodes);
    });
    return new OriginalGraph(newNodeMap, rootNodes, new ArrayList<>(genomes));
  }

  /**
   * Set the in and out links of the node.
   *
   * @param originalNode the node from which this node is derived.
   * @param newNode      the new node.
   * @param nodeMap      the original node map.
   * @param rootNodes    the list of root nodes to which to add any new nodes.
   */
  private void setInOutLinks(Node originalNode, Node newNode, HashMap<Integer, Node> nodeMap,
      ArrayList<Integer> rootNodes) {
    for (Integer inlink : originalNode.getInlinks()) {
      if (nodeMap.containsKey(inlink)) {
        newNode.addInlink(inlink);
      }
    }
    if (newNode.getInlinks().isEmpty()) {
      rootNodes.add(originalNode.getId());
    }
    for (Integer outlink : originalNode.getOutlinks()) {
      if (nodeMap.containsKey(outlink)) {
        newNode.addOutlink(outlink);
      }
    }
  }
}
