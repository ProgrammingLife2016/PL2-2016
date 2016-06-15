package nl.tudelft.pl2016gr2.core.algorithms.bubbles;

import nl.tudelft.pl2016gr2.core.algorithms.subgraph.CompareSubgraphs;
import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractZoom {
  
  /**
  * Zoom in on a given bubble. This method makes new bubbles within 
  * the bubble.
  * 
  * @param bubble the bubble to zoom in on.
  * @return a list of graphnodes that are in this bubble.
  */
  public abstract List<GraphNode> zoom(Bubble bubble);
  
  /**
   * This method removes the in and outedges of the bubble from a list, and then
   * aligns the nodes in this list vertically.
   * 
   * @param graphNodes the list of graphnodes to align.
   * @param bubble the bubble in which these graphnodes were contained.
   * @return the list of graphnodes without the in and outedges of the bubble aligned.
   */
  public List<GraphNode> alignNodes(List<GraphNode> graphNodes, Bubble bubble) {
    graphNodes.sort((GraphNode left, GraphNode right) -> left.getLevel() - right.getLevel());
    graphNodes.removeAll(bubble.getInEdges());
    graphNodes.removeAll(bubble.getOutEdges());
    CompareSubgraphs.alignVertically(graphNodes, bubble.getInEdges());
    return graphNodes;
  }
  
  /**
   * Maps for each in and out edge of a bubble its original in and out edges.
   * 
   * @param originalInEdges Mapping of ids to inedges.
   * @param originalOutEdges Mapping of ids to outedges.
   * @param bubble the bubble for which the in and out edges need to be mapped.
   */
  public void setOriginalEdges(Map<Integer, Collection<GraphNode>> originalInEdges, 
      Map<Integer, Collection<GraphNode>> originalOutEdges, Bubble bubble) {
    bubble.getInEdges().forEach(node -> {
      originalInEdges.put(node.getId(), new ArrayList<>(node.getInEdges()));
      originalOutEdges.put(node.getId(), new ArrayList<>(node.getOutEdges()));
    });
    bubble.getOutEdges().forEach(node -> {
      originalInEdges.put(node.getId(), new ArrayList<>(node.getInEdges()));
      originalOutEdges.put(node.getId(), new ArrayList<>(node.getOutEdges()));
    });
  }
  
  /**
   * This method prunes the start nodes of a bubble (which are its inedges). 
   * 
   * @param bubbleInEdges the inedges of the bubble.
   * @param inEdges a mapping of ids to the original inedges of nodes.
   * @param outEdges a mapping of ids to the original outedges of nodes.
   * @param bubbleId the id of the bubble.
   */
  public void pruneStart(Collection<GraphNode> bubbleInEdges, 
      Map<Integer, Collection<GraphNode>> inEdges, Map<Integer, Collection<GraphNode>> outEdges,
      int bubbleId) {
    Iterator<GraphNode> inlinkIterator = bubbleInEdges.iterator();
    while (inlinkIterator.hasNext()) {
      GraphNode startNode = inlinkIterator.next();
      startNode.setInEdges(inEdges.get(startNode.getId()));
      for (GraphNode curOutlink : outEdges.get(startNode.getId())) {
        if (curOutlink.getId() != bubbleId && !startNode.getOutEdges().contains(curOutlink)) {
          startNode.addOutEdge(curOutlink);
        }
      }
    }
  }
  
  /**
   * This method prunes the end nodes of a bubble (which are its outedges). 
   * 
   * @param bubbleOutEdges the outedges of the bubble.
   * @param inEdges a mapping of ids to the original inedges of nodes.
   * @param outEdges a mapping of ids to the original outedges of nodes.
   * @param bubbleId the id of the bubble.
   */
  public void pruneEnd(Collection<GraphNode> bubbleOutEdges, 
      Map<Integer, Collection<GraphNode>> outEdges, Map<Integer, Collection<GraphNode>> inEdges,
      int bubbleId) {
    Iterator<GraphNode> outlinkIterator = bubbleOutEdges.iterator();
    while (outlinkIterator.hasNext()) {
      GraphNode endNode = outlinkIterator.next();
      endNode.setOutEdges(outEdges.get(endNode.getId()));
      for (GraphNode curOutlink : inEdges.get(endNode.getId())) {
        if (curOutlink.getId() != bubbleId && !endNode.getInEdges().contains(curOutlink)) {
          endNode.addInEdge(curOutlink);
        }
      }
    }
  }
}
