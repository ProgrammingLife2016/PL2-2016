package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.visitor.BubblePhyloVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class ZoomIn {
  
  private final FilterBubbles filter;
  
  protected ZoomIn(FilterBubbles filter) {
    this.filter = filter;
  }
  
  /**
   * Zoom in on a given bubble. This method makes new bubbles within 
   * the bubble by going a level deeper in the phylogenetic tree,
   * and returns a list of graphnodes (including both nodes and bubbles).
   * 
   * @param bubble the bubble to zoom in on.
   * @return a list of graphnodes that are in this bubble.
   */
  public List<GraphNode> zoom(Bubble bubble) {
    Map<Integer, Collection<GraphNode>> originalInEdges = new HashMap<>();
    Map<Integer, Collection<GraphNode>> originalOutEdges = new HashMap<>();
    setOriginalEdges(originalInEdges, originalOutEdges, bubble);
    IPhylogeneticTreeNode curTreeNode = getTreeNode(bubble);
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
    Set<GraphNode> poppedNodes = new HashSet<>();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(poppedNodes, childOne, bubble, newBubbles);
    debubble(poppedNodes, childTwo, bubble, newBubbles);
    pruneStart(bubble.getInEdges(), originalInEdges, originalOutEdges, bubble.getId());
    pruneEnd(bubble.getOutEdges(), originalOutEdges, originalInEdges, bubble.getId());
    ArrayList<GraphNode> graphNodes = new ArrayList<>(poppedNodes);
    filter.pruneNodes(graphNodes, newBubbles);
    return graphNodes;
  }
  
  private IPhylogeneticTreeNode getTreeNode(Bubble bubble) {
    BubblePhyloVisitor visitor = new BubblePhyloVisitor();
    bubble.accept(visitor);
    return visitor.getTreeNode();
  }
  
  private void setOriginalEdges(Map<Integer, Collection<GraphNode>> originalInEdges, 
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
  
  private void pruneStart(Collection<GraphNode> bubbleInEdges, 
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
  
  private void pruneEnd(Collection<GraphNode> bubbleOutEdges, 
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
  
  private List<Bubble> debubble(Set<GraphNode> poppedNodes, IPhylogeneticTreeNode treeNode, 
      Bubble bubble, ArrayList<Bubble> newBubbles) {
    Collection<GraphNode> startNodes = bubble.getInEdges();
    ArrayList<Integer> leaves = treeNode.getGenomes();
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();
    toVisit.addAll(startNodes);
    filter.filterBubbles(toVisit, visited, poppedNodes, leaves, bubble, treeNode, newBubbles);
    Iterator<GraphNode> iterator = bubble.getOutEdges().iterator();
    while (iterator.hasNext()) {
      GraphNode next = iterator.next();
      next.setInEdges(new HashSet<>());
      next.setOutEdges(new HashSet<>());
      poppedNodes.add(next);
    }
    return newBubbles;
  }
}
