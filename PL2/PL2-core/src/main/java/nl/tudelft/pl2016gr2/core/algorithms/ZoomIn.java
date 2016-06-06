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
  
  public List<GraphNode> zoom(Bubble bubble) {
    BubblePhyloVisitor visitor = new BubblePhyloVisitor();
    bubble.accept(visitor);
    IPhylogeneticTreeNode curTreeNode = visitor.getTreeNode();
    
    // THIS MIGHT NOT WORK WITH THE FIRST/LAST NODE
    //zoomOut.addOldView(getNode(inlinks), getNode(outlinks));
    
    Map<Integer, Collection<GraphNode>> originalInEdges = new HashMap<>();
    Map<Integer, Collection<GraphNode>> originalOutEdges = new HashMap<>();
    bubble.getInEdges().forEach(node -> {
      originalInEdges.put(node.getId(), new ArrayList<>(node.getInEdges()));
      originalOutEdges.put(node.getId(), new ArrayList<>(node.getOutEdges()));
    });
    bubble.getOutEdges().forEach(node -> {
      originalInEdges.put(node.getId(), new ArrayList<>(node.getInEdges()));
      originalOutEdges.put(node.getId(), new ArrayList<>(node.getOutEdges()));
    });
    
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
  
  private GraphNode getNode(int id, boolean startEnd, List<GraphNode> nodes) {
    if (!startEnd) {
      for (int i = 0; i < nodes.size(); i++) {
        if (nodes.get(i).getId() == id) {
          return nodes.get(i);
        }
      }
    } else {
      for (int i = nodes.size() - 1; i >= 0; i--) {
        if (nodes.get(i).getId() == id) {
          return nodes.get(i);
        }
      }
    }
    
    return null;
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
