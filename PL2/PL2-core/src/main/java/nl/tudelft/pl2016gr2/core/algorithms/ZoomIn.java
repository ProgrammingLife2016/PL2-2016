package nl.tudelft.pl2016gr2.core.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.HashGraph;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.visitor.BubblePhyloVisitor;

public class ZoomIn {
  
  private SequenceGraph originalGraph;
  private ZoomOut zoomOut;
  private int mutationId;
  private FilterBubbles filter;
  
  protected ZoomIn(SequenceGraph originalGraph, ZoomOut zoomOut, int mutationId,
      FilterBubbles filter) {
    this.originalGraph = originalGraph;
    this.zoomOut = zoomOut;
    this.mutationId = mutationId;
    this.filter = filter;
  }
  
  public SequenceGraph zoom(Bubble bubble, SequenceGraph graph) {
    BubblePhyloVisitor visitor = new BubblePhyloVisitor();
    bubble.accept(visitor);
    IPhylogeneticTreeNode curTreeNode = visitor.getTreeNode();
    
    int inlink = bubble.getInEdges().iterator().next();
    int outlink = bubble.getOutEdges().iterator().next();
    zoomOut.addOldView(inlink, outlink, graph);
    
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
 
    SequenceGraph zoomedGraph = new HashGraph();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(zoomedGraph, childOne, bubble, newBubbles);
    debubble(zoomedGraph, childTwo, bubble, newBubbles);
    
 // Add outlink to other bubbles that are not affected
    GraphNode startNode = zoomedGraph.getNode(inlink);
    GraphNode oldStartNode = graph.getNode(inlink);
    startNode.setInEdges(oldStartNode.getInEdges());
    for (Integer curOutlink : oldStartNode.getOutEdges()) {
      if (curOutlink != bubble.getId() && !startNode.getOutEdges().contains(curOutlink)) {
        startNode.addOutEdge(curOutlink);
      }
    }
    
    // Add inlink to other bubbles that are not affected
    GraphNode endNode = zoomedGraph.getNode(outlink);
    GraphNode oldEndNode = graph.getNode(outlink);
    endNode.setOutEdges(oldEndNode.getOutEdges());
    for (Integer curInlink : oldEndNode.getInEdges()) {
      if (curInlink != bubble.getId() && !endNode.getInEdges().contains(curInlink)) {
        endNode.addInEdge(curInlink);
      }
    }
    
    filter.pruneNodes(zoomedGraph, graph, newBubbles);
    replace(bubble, graph, zoomedGraph);
    return graph;
  }
  
  private List<Bubble> debubble(SequenceGraph filteredGraph, IPhylogeneticTreeNode treeNode, 
      Bubble bubble, ArrayList<Bubble> newBubbles) {
    GraphNode start = originalGraph.getNode(bubble.getInEdges().iterator().next());
    
    ArrayList<String> leaves = treeNode.getGenomes();
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    toVisit.add(start.getId());
    
    filter.filterBubbles(toVisit, visited, filteredGraph, leaves, bubble, treeNode, newBubbles);
    
    GraphNode end = originalGraph.getNode(bubble.getOutEdges().iterator().next());
    filteredGraph.add(end.copy());
    
    return newBubbles;
  }
  
  private void replace(Bubble bubble, SequenceGraph completeGraph, SequenceGraph partGraph) {
    completeGraph.remove(bubble.getId(), false, false);
    
    Iterator<GraphNode> iterator = partGraph.iterator();
    while (iterator.hasNext()) {
      GraphNode next = iterator.next();
      completeGraph.add(next);
    }
  }
}
