package nl.tudelft.pl2016gr2.core.algorithms.bubbles.graph;

import nl.tudelft.pl2016gr2.core.algorithms.bubbles.AbstractBubbleFilter;
import nl.tudelft.pl2016gr2.core.algorithms.bubbles.FilterHelpers;
import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Class to make graph bubbles. These are bubbles that are made solely based
 * on the graph, not looking at phylogeny.
 * 
 * @author Casper
 *
 */
public class GraphBubbleFilter extends AbstractBubbleFilter {
  
  private int mutationId = -1;
  
  /**
   * Creates an instance of this class.
   * 
   * @param orderedNodes the nodes that need to be bubbled.
   */
  public GraphBubbleFilter(Collection<GraphNode> orderedNodes) {
    super(orderedNodes);
  }
  
  /**
   * Makes bubbles.
   * 
   * @return A list containing all the nodes in the graph after it has been
   *     bubbled.
   */
  public ArrayList<GraphNode> filter() {
    Collection<GraphNode> rootNodes = getRootNodes();
    Iterator<GraphNode> rootNodesIterator = rootNodes.iterator();
    Collection<GraphNode> startNodes = new ArrayList<>();
    while (rootNodesIterator.hasNext()) {
      startNodes.addAll(rootNodesIterator.next().getOutEdges());
    }
    
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    ArrayList<GraphNode> graphNodes = new ArrayList<>(findBubbles(startNodes, null, newBubbles));
    graphNodes.addAll(rootNodes);
    pruneNodes(graphNodes, newBubbles);
    
    Collections.sort(graphNodes, (GraphNode first, GraphNode second) -> {
      return first.getLevel() - second.getLevel();
    });
    return graphNodes;
  }
  
  @Override
  public List<GraphNode> zoomIn(Bubble bubble) {
    return new GraphBubbleZoom(this).zoom(bubble);
  }
  
  /**
   * Method to find bubbles given a list of startnodes and an end node.
   * 
   * @param startNodes the starting nodes of the new bubbles.
   * @param end the end node of the new bubbles, the algorithm will never go 
   *     past this node.
   * @param newBubbles a list which contains all the newly made bubbles.
   * @return A list of new graphnodes in the specified interval after they have been bubbled.
   */
  protected Collection<GraphNode> findBubbles(Collection<GraphNode> startNodes, GraphNode end, 
      ArrayList<Bubble> newBubbles) {
    Set<GraphNode> poppedNodes = new HashSet<>();
    Set<GraphNode> visited = new HashSet<>();
    Queue<GraphNode> toVisit = new LinkedList<>();
    
    for (GraphNode startNode : startNodes) {
      toVisit.addAll(getOriginalOutEdges().get(startNode.getId()));
    }
    poppedNodes.addAll(startNodes);
    
    return visitNodes(toVisit, visited, end, poppedNodes, newBubbles);
  }
  
  /**
   * Visits all nodes until the end is reached (if the end != null) and makes bubbles.
   * 
   * @param toVisit : nodes to visit.
   * @param visited : nodes already visited.
   * @param end : the end node.
   * @param poppedNodes : list of new nodes.
   * @param newBubbles : list of new bubbles.
   * @return a list of new nodes.
   */
  private Collection<GraphNode> visitNodes(Queue<GraphNode> toVisit, Set<GraphNode> visited, 
      GraphNode end, Set<GraphNode> poppedNodes, ArrayList<Bubble> newBubbles) {
    while (!toVisit.isEmpty()) {
      GraphNode start = toVisit.poll();
      if (end != null && start.getLevel() >= end.getLevel()) {
        continue;
      }
      start.setInEdges(new HashSet<>());
      start.setOutEdges(new HashSet<>());
      poppedNodes.add(start);
      visited.add(start);
      Bubble newBubble = createBubble(start, end);
      if (newBubble == null) {
        for (GraphNode out : getOriginalOutEdges().get(start.getId())) {
          FilterHelpers.addToVisit(out, toVisit, visited);
        }
      } else {
        if (!newBubble.getChildren().isEmpty()) {
          poppedNodes.add(newBubble);
          newBubbles.add(newBubble);
        }
        FilterHelpers.addToVisit(newBubble.getOutEdges().iterator().next(), toVisit, visited);
      }
    }
    return poppedNodes;
  }
  
  /**
   * Creates a bubble.
   * 
   * @param start : start of the bubble.
   * @param currentEnd : the end of the bubble should be before this node.
   * @return : a bubble (or null).
   */
  private Bubble createBubble(GraphNode start, GraphNode currentEnd) {
    Set<Integer> genomes = new HashSet<>(start.getGenomes());
    Set<GraphNode> visited = new HashSet<>();
    Queue<GraphNode> toVisit = new LinkedList<>();
    toVisit.addAll(getOriginalOutEdges().get(start.getId()));
    Set<GraphNode> nestedNodes = new HashSet<>();
    
    Bubble bubble = makeBubble(toVisit, visited, start, currentEnd, nestedNodes, genomes);
    if (bubble != null) {
      for (GraphNode node : nestedNodes) {
        bubble.addChild(node);
      }
    }
    
    return bubble;
  }
  
  /**
   * Makes a bubble.
   * 
   * @param toVisit : nodes to visit.
   * @param visited : nodes already visited.
   * @param start : start of bubble.
   * @param endNode : end of the bubble should be before this node.
   * @param nestedNodes : nestednodes of the bubble.
   * @param genomes : genomes of the bubble.
   * @return : a bubble (or null).
   */
  private Bubble makeBubble(Queue<GraphNode> toVisit, Set<GraphNode> visited, GraphNode start, 
      GraphNode endNode, Set<GraphNode> nestedNodes, Set<Integer> genomes) {
    GraphBubble bubble = null;
    while (!toVisit.isEmpty()) {
      GraphNode next = toVisit.poll();
      visited.add(next);
      if (endNode != null && next.getLevel() >= endNode.getLevel()) {
        continue;
      }
      Set<Integer> nextGenomes = new HashSet<>(next.getGenomes());
      if (nextGenomes.size() == genomes.size() && nextGenomes.containsAll(genomes)) {
        endNode = next;
        bubble = new GraphBubble(mutationId, this, 
            Collections.singletonList(start), Collections.singletonList(endNode));
        mutationId--;
      } else if (nextGenomes.size() < genomes.size()) {
        nestedNodes.add(next);
        for (GraphNode out : getOriginalOutEdges().get(next.getId())) {
          if (endNode == null || out.getLevel() < endNode.getLevel()) {
            FilterHelpers.addToVisit(out, toVisit, visited);
          }
        }
      }
    }
    return bubble;
  }
}
