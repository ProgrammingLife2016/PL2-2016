package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.visitor.BubblePhyloVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * Class to zoom out from a bubble. It will then show the previous view.
 * It is not public, and should only be used in the FilterBubbles class.
 * 
 * @author Casper
 *
 */
public class ZoomOut {
  
  private final SequenceGraph originalGraph;
  private Map<GraphNode, Map<GraphNode, Stack<Set<GraphNode>>>> oldGraphs = new HashMap<>();
  
  /**
   * Constructs an object of this class, with the originalgraph. 
   * @param originalGraph : the originalGraph as it was before filtering.
   */
  protected ZoomOut(SequenceGraph originalGraph) {
    this.originalGraph = originalGraph;
    this.oldGraphs = new HashMap<>();
  }
  
  /**
   * Method to add an old view. This method should be called when zooming in on a bubble,
   * to save the view before the zooming in on the bubble was done.
   * @param start the id of the start node from where the view should be saved.
   * @param end the id of the end node from where the view should be saved.
   * @param graph the graph
   */
  protected void addOldView(GraphNode start, GraphNode end) {
    Set<GraphNode> oldView = NodePathFinder.getNodesOnPath(start, end, true);
    
    if (oldGraphs.containsKey(start)) {
      if (oldGraphs.get(start).containsKey(end)) {
        oldGraphs.get(start).get(end).push(oldView);
      } else {
        Stack<Set<GraphNode>> stack = new Stack<>();
        stack.push(oldView);
        oldGraphs.get(start).put(end, stack);
      }
    } else {
      Map<GraphNode, Stack<Set<GraphNode>>> endMap = new HashMap<>();
      Stack<Set<GraphNode>> stack = new Stack<>();
      stack.push(oldView);
      endMap.put(end, stack);
      oldGraphs.put(start, endMap);
    }
  }
  
  /**
   * This method zooms out on a bubble, as follows: it takes the parent of
   * the current phylonode belonging to this bubble, and finds the closest nodes
   * before and after the bubble that are shared by all the leaves of this parent
   * phylo node (start and end). It then removes all nodes on the path from this 
   * start and end, and then gets the previous view belonging to this start and end
   * from memory.
   * 
   * @param bubble the bubble on which is zoomed out.
   * @param graph the graph
   * @return a new graph zoomed out on the bubble.
   */
  protected SequenceGraph zoomOut(Bubble bubble, SequenceGraph graph) {
    BubblePhyloVisitor visitor = new BubblePhyloVisitor();
    bubble.accept(visitor);
    IPhylogeneticTreeNode treeNode = visitor.getTreeNode();
    // find start
    if (!treeNode.hasParent()) {
      return graph;
    }
    
    IPhylogeneticTreeNode parent = treeNode.getParent();
    ArrayList<String> leaves = parent.getGenomes();
    GraphNode start = findStart(bubble, graph, leaves);
    GraphNode end = findEnd(bubble.getOutEdges(), graph, leaves);
    
    graph = getPreviousView(start, end, graph);
    return graph;
  }
  
  private GraphNode findStart(GraphNode bubble, SequenceGraph graph, ArrayList<String> leaves) {
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();
    toVisit.add(bubble);
    GraphNode start = null;
    
    while (!toVisit.isEmpty()) {
      GraphNode next = toVisit.poll();
      if (!originalGraph.contains(next)) {
        for (GraphNode inlink : graph.getNode(next.getId()).getInEdges()) {
          FilterHelpers.addToVisit(inlink, toVisit, visited);
        }
      } else {
        GraphNode curNode = originalGraph.getNode(next.getId());
        if (FilterHelpers.isShared(curNode, leaves)) {
          return curNode;
        }
        
        for (GraphNode inlink : graph.getNode(next.getId()).getInEdges()) {
          FilterHelpers.addToVisit(inlink, toVisit, visited);
        }
      }
    }
    
    return start;
  }
  
  private GraphNode findEnd(Collection<GraphNode> outEdges, SequenceGraph graph, ArrayList<String> leaves) {
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();
    toVisit.addAll(outEdges);
    GraphNode end = null;
    
    while (!toVisit.isEmpty()) {
      GraphNode next = toVisit.poll();
      if (!originalGraph.contains(next)) {
        for (GraphNode outlink : graph.getNode(next.getId()).getOutEdges()) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        }
      } else {
        if (FilterHelpers.isShared(originalGraph.getNode(next.getId()), leaves)) {
          return next;
        }
        
        for (GraphNode outlink : graph.getNode(next.getId()).getOutEdges()) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        }
      }
    }
    
    return end;
  }
  
  private SequenceGraph getPreviousView(GraphNode start, GraphNode end, SequenceGraph graph) {
    if (!oldGraphs.containsKey(start) || !oldGraphs.get(start).containsKey(end) 
        || oldGraphs.get(start).get(end).isEmpty()) {
      return graph;
    }
    
    Set<GraphNode> nodesToRemove = NodePathFinder.getNodesOnPath(start, end, false);
    // get current in and outlinks of start and end node
    Collection<GraphNode> startInEdges = graph.getNode(start.getId()).getInEdges();
    Collection<GraphNode> endOutEdges = graph.getNode(end.getId()).getOutEdges();
    
    nodesToRemove.forEach(node -> {
      if (node.getId() != start.getId() && node.getId() != end.getId()) {
        graph.remove(node, true, true);
      } else {
        graph.remove(node, false, false);
      }
    });
    
    Set<GraphNode> previousView = oldGraphs.get(start).get(end).pop();
    previousView.forEach(node -> {
      if (node.getId() == start.getId()) {
        node.setInEdges(startInEdges);
      } else if (node.getId() == end.getId()) {
        node.setOutEdges(endOutEdges);
      }
      graph.add(node);
    });
    
    return graph;
  }
  
  private void printList(String header, Collection<GraphNode> nodes) {
    System.out.print(header + ": [");
    nodes.forEach(node -> {
      System.out.print(node.getId() + ", ");
    });
    System.out.println("]");
  }
}
