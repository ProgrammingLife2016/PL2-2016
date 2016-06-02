package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.visitor.BubblePhyloVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class ZoomIn {
  
  private final SequenceGraph originalGraph; // unused?
  private final ZoomOut zoomOut;
  private final FilterBubbles filter;
  
  protected ZoomIn(SequenceGraph originalGraph, ZoomOut zoomOut, FilterBubbles filter) {
    this.originalGraph = originalGraph;
    this.zoomOut = zoomOut;
    this.filter = filter;
  }
  
  public List<GraphNode> zoom(Bubble bubble) {
    BubblePhyloVisitor visitor = new BubblePhyloVisitor();
    bubble.accept(visitor);
    IPhylogeneticTreeNode curTreeNode = visitor.getTreeNode();
    
    Collection<GraphNode> inlinks = bubble.getInEdges();
    Collection<GraphNode> outlinks = bubble.getOutEdges();
    // THIS MIGHT NOT WORK WITH THE FIRST/LAST NODE
    zoomOut.addOldView(getNode(inlinks), getNode(outlinks));
    
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
 
    List<GraphNode> poppedNodes = new ArrayList<>();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(poppedNodes, childOne, bubble, newBubbles);
    debubble(poppedNodes, childTwo, bubble, newBubbles);
    
    // Add outlink to other bubbles that are not affected
    Iterator<GraphNode> inlinkIterator = inlinks.iterator();
    while (inlinkIterator.hasNext()) {
      GraphNode oldStartNode = inlinkIterator.next();
      GraphNode startNode = getNode(oldStartNode.getId(), false, poppedNodes);
      //GraphNode oldStartNode = graph.getNode(next.getId());
      startNode.setInEdges(oldStartNode.getInEdges());
      for (GraphNode curOutlink : oldStartNode.getOutEdges()) {
        if (curOutlink.getId() != bubble.getId() && !startNode.getOutEdges().contains(curOutlink)) {
          startNode.addOutEdge(curOutlink);
        }
      }
    }
    
    // Add inlink to other bubbles that are not affected
    Iterator<GraphNode> outlinkIterator = outlinks.iterator();
    while (outlinkIterator.hasNext()) {
      GraphNode oldEndNode = outlinkIterator.next();
      GraphNode endNode = getNode(oldEndNode.getId(), true, poppedNodes);
      endNode.setOutEdges(oldEndNode.getOutEdges());
      for (GraphNode curInlink : oldEndNode.getInEdges()) {
        if (curInlink.getId() != bubble.getId() && !endNode.getInEdges().contains(curInlink)) {
          endNode.addInEdge(curInlink);
        }
      }
    }

    filter.pruneNodes(poppedNodes, newBubbles);
    //replace(bubble, graph, zoomedGraph);
    return poppedNodes;
  }
  
  private GraphNode getNode(Collection<GraphNode> links) {
    GraphNode node = null;
    if (!links.isEmpty()) {
      node = links.iterator().next();
    }
    
    return node;
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
  
  private List<Bubble> debubble(List<GraphNode> poppedNodes, IPhylogeneticTreeNode treeNode, 
      Bubble bubble, ArrayList<Bubble> newBubbles) {
    Collection<GraphNode> startNodes = bubble.getInEdges();
    
    ArrayList<String> leaves = treeNode.getGenomes();
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();
    toVisit.addAll(startNodes);
    
    filter.filterBubbles(toVisit, visited, poppedNodes, leaves, bubble, treeNode, newBubbles);
    
    Iterator<GraphNode> iterator = bubble.getOutEdges().iterator();
    while (iterator.hasNext()) {
      poppedNodes.add(iterator.next().copy());
    }
    
    return newBubbles;
  }
  
  private void replace(Bubble bubble, SequenceGraph completeGraph, SequenceGraph partGraph) {
    completeGraph.remove(bubble, false, false);
    
    Iterator<GraphNode> iterator = partGraph.iterator();
    while (iterator.hasNext()) {
      GraphNode next = iterator.next();
      completeGraph.add(next);
    }
  }
}
