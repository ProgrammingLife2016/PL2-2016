package nl.tudelft.pl2016gr2.core.algorithms.bubbles;

import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.BubbleFilter;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * This is an AbstractBubbleFilter. This class stores the rootNodes of the original graph
 * and the original in and out edges. 
 *
 * @author Casper
 *
 */
public abstract class AbstractBubbleFilter implements BubbleFilter {

  private final Map<Integer, Collection<GraphNode>> originalInEdges;
  private final Map<Integer, Collection<GraphNode>> originalOutEdges;
  private final Collection<GraphNode> rootNodes;

  /**
   * Creates an AbstractBubbleFilter object, with the graph nodes to be filtered.
   *
   * @param orderedNodes : the nodes to be filtered.
   */
  public AbstractBubbleFilter(Collection<GraphNode> orderedNodes) {
    this.rootNodes = new ArrayList<>();
    for (GraphNode orderedNode : orderedNodes) {
      if (orderedNode.getInEdges().isEmpty()) {
        rootNodes.add(orderedNode);
      }
    }
    originalInEdges = new HashMap<>();
    originalOutEdges = new HashMap<>();

    Iterator<GraphNode> graphIterator = orderedNodes.iterator();
    while (graphIterator.hasNext()) {
      GraphNode node = graphIterator.next();
      originalInEdges.put(node.getId(), new ArrayList<>(node.getInEdges()));
      originalOutEdges.put(node.getId(), new ArrayList<>(node.getOutEdges()));
    }
  }

  /**
   * Returns a map which maps the ids of nodes in the graph before it was filterbubbled
   * to its inedges.
   * 
   * @return a map with node Ids mapped to original outedges of that node.
   */
  public Map<Integer, Collection<GraphNode>> getOriginalInEdges() {
    return originalInEdges;
  }

  /**
   * Returns a map which maps the ids of nodes in the graph before it was filterbubbled
   * to its outedges.
   * 
   * @return a map with node Ids mapped to original outedges of that node.
   */
  public Map<Integer, Collection<GraphNode>> getOriginalOutEdges() {
    return originalOutEdges;
  }

  /**
   * Returns the rootnodes of the graph.
   * 
   * @return a Collection of graphnodes which represent the rootnodes of the graph.
   */
  public Collection<GraphNode> getRootNodes() {
    return rootNodes;
  }

  /**
   * Prunes a list of GraphNodes. First, using the in and out edges of the bubbles, which are
   * assumed to be already correct, the nodes that are connected to these bubble get out/in edges to
   * these bubbles. After that, each node gets in/out edges from the original graph back if these
   * edges go to a node that is in the list of graphnodes.
   *
   * @param graphNodes a list of graphnodes for which the edges need to be set.
   * @param newBubbles a list of bubbles which already have correct edges.
   */
  public void pruneNodes(HashSet<GraphNode> graphNodes, ArrayList<Bubble> newBubbles) {
    pruneBubbles(newBubbles);

    Iterator<GraphNode> iterator = graphNodes.iterator();
    while (iterator.hasNext()) {
      GraphNode node = iterator.next();
      if (!(originalInEdges.containsKey(node.getId()) 
          && originalOutEdges.containsKey(node.getId()))) {
        continue;
      }
      // Get the original inedges and see which ones are still in the graph
      Collection<GraphNode> inlinks = pruneLinks(
          originalInEdges.get(node.getId()), graphNodes);
      inlinks.forEach(node::addInEdge);
      Collection<GraphNode> outlinks = pruneLinks(
          originalOutEdges.get(node.getId()), graphNodes);
      outlinks.forEach(node::addOutEdge);
    }
  }

  private void pruneBubbles(ArrayList<Bubble> newBubbles) {
    newBubbles.forEach(bubble -> {
      Iterator<GraphNode> inlinks = bubble.getInEdges().iterator();
      while (inlinks.hasNext()) {
        inlinks.next().addOutEdge(bubble);
      }

      Iterator<GraphNode> outlinks = bubble.getOutEdges().iterator();
      while (outlinks.hasNext()) {
        outlinks.next().addInEdge(bubble);
      }
    });
  }

  private Collection<GraphNode> pruneLinks(Collection<GraphNode> links,
      HashSet<GraphNode> graphNodes) {
    Collection<GraphNode> prunedLinks = new ArrayList<>();
    links.forEach(link -> {
      if (graphNodes.contains(link)) {
        prunedLinks.add(link);
      }
    });
    return prunedLinks;
  }
}
