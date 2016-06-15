package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Class to zoom in on random bubbles.
 * 
 * @author Casper
 *
 */
public class GraphBubbleZoom extends AbstractZoom {
  
  private GraphBubbleFilter filter;
  
  /**
   * Creates an object to zoom in on a random bubble with.
   * 
   * @param filter an instance of FilterRandomBubbles.
   */
  public GraphBubbleZoom(GraphBubbleFilter filter) {
    this.filter = filter;
  }
  
  @Override
  public List<GraphNode> zoom(Bubble bubble) {
    Map<Integer, Collection<GraphNode>> originalInEdges = new HashMap<>();
    Map<Integer, Collection<GraphNode>> originalOutEdges = new HashMap<>();
    setOriginalEdges(originalInEdges, originalOutEdges, bubble);
    
    GraphNode startNode = bubble.getInEdges().iterator().next();
    startNode.setOutEdges(new HashSet<>());
    GraphNode end = bubble.getOutEdges().iterator().next();
    end.setInEdges(new HashSet<>());
    
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    ArrayList<GraphNode> graphNodes = new ArrayList<>(
        filter.findBubbles(Collections.singletonList(startNode), end, newBubbles));
    graphNodes.add(end);
    pruneStart(bubble.getInEdges(), originalInEdges, originalOutEdges, bubble.getId());
    pruneEnd(bubble.getOutEdges(), originalOutEdges, originalInEdges, bubble.getId());
    filter.pruneNodes(graphNodes, newBubbles);
    
    return alignNodes(graphNodes, bubble);
  }
}
