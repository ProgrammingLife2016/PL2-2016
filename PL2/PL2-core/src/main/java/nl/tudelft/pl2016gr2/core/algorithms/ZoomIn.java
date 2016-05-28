package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.HashGraph;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.visitor.BubblePhyloVisitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class ZoomIn {
  
  private final SequenceGraph originalGraph;
  private final ZoomOut zoomOut;
  private final FilterBubbles filter;
  
  protected ZoomIn(SequenceGraph originalGraph, ZoomOut zoomOut, FilterBubbles filter) {
    this.originalGraph = originalGraph;
    this.zoomOut = zoomOut;
    this.filter = filter;
  }
  
  public SequenceGraph zoom(Bubble bubble, SequenceGraph graph) {
    BubblePhyloVisitor visitor = new BubblePhyloVisitor();
    bubble.accept(visitor);
    IPhylogeneticTreeNode curTreeNode = visitor.getTreeNode();
    
    GraphNode inlink = bubble.getInEdges().iterator().next();
    GraphNode outlink = bubble.getOutEdges().iterator().next();
    zoomOut.addOldView(inlink, outlink, graph);
    
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
 
    SequenceGraph zoomedGraph = new HashGraph();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(zoomedGraph, childOne, bubble, newBubbles);
    debubble(zoomedGraph, childTwo, bubble, newBubbles);
    
 // Add outlink to other bubbles that are not affected
    GraphNode startNode = zoomedGraph.getNode(inlink.getId());
    GraphNode oldStartNode = graph.getNode(inlink.getId());
    startNode.setInEdges(oldStartNode.getInEdges());
    for (GraphNode curOutlink : oldStartNode.getOutEdges()) {
      if (!curOutlink.equals(bubble) && !startNode.getOutEdges().contains(curOutlink)) {
        startNode.addOutEdge(curOutlink);
      }
    }
    
    // Add inlink to other bubbles that are not affected
    GraphNode endNode = zoomedGraph.getNode(outlink.getId());
    GraphNode oldEndNode = graph.getNode(outlink.getId());
    endNode.setOutEdges(oldEndNode.getOutEdges());
    for (GraphNode curInlink : oldEndNode.getInEdges()) {
      if (!curInlink.equals(bubble) && !endNode.getInEdges().contains(curInlink)) {
        endNode.addInEdge(curInlink);
      }
    }

    filter.pruneNodes(zoomedGraph, graph, newBubbles);
    replace(bubble, graph, zoomedGraph);
    return graph;
  }
  
  private List<Bubble> debubble(SequenceGraph filteredGraph, IPhylogeneticTreeNode treeNode, 
      Bubble bubble, ArrayList<Bubble> newBubbles) {
    GraphNode start = originalGraph.getNode(bubble.getInEdges().iterator().next().getId());
    
    ArrayList<String> leaves = treeNode.getGenomes();
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();
    toVisit.add(start);
    
    filter.filterBubbles(toVisit, visited, filteredGraph, leaves, bubble, treeNode, newBubbles);
    
    GraphNode end = originalGraph.getNode(bubble.getOutEdges().iterator().next().getId());
    filteredGraph.add(end.copy());
    
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
