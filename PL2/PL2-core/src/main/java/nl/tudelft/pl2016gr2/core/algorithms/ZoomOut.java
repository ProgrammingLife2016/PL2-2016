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
  
  private SequenceGraph originalGraph;
  private Map<Integer, Map<Integer, Stack<Set<GraphNode>>>> oldGraphs = new HashMap<>();
  
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
  protected void addOldView(int start, int end, SequenceGraph graph) {
    Set<GraphNode> oldView = NodePathFinder.getNodesOnPath(start, end, graph, true);
    
    if (oldGraphs.containsKey(start)) {
      if (oldGraphs.get(start).containsKey(end)) {
        oldGraphs.get(start).get(end).push(oldView);
      } else {
        Stack<Set<GraphNode>> stack = new Stack<>();
        stack.push(oldView);
        oldGraphs.get(start).put(end, stack);
      }
    } else {
      Map<Integer, Stack<Set<GraphNode>>> endMap = new HashMap<>();
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
    int start = findStart(bubble.getId(), graph, leaves);
    int end = findEnd(bubble.getOutEdges(), graph, leaves);
    
    graph = getPreviousView(start, end, graph);
    return graph;
  }
  
  private int findStart(int bubbleId, SequenceGraph graph, ArrayList<String> leaves) {
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    toVisit.add(bubbleId);
    int start = -1;
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      if (!originalGraph.contains(next)) {
        for (Integer inlink : graph.getNode(next).getInEdges()) {
          FilterHelpers.addToVisit(inlink, toVisit, visited);
        }
      } else {
        GraphNode curNode = originalGraph.getNode(next);
        if (FilterHelpers.isShared(curNode, leaves)) {
          return curNode.getId();
        }
        
        for (Integer inlink : graph.getNode(next).getInEdges()) {
          FilterHelpers.addToVisit(inlink, toVisit, visited);
        }
      }
    }
    
    return start;
  }
  
  private int findEnd(Collection<Integer> outEdges, SequenceGraph graph, ArrayList<String> leaves) {
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    toVisit.addAll(outEdges);
    int end = -1;
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      if (!originalGraph.contains(next)) {
        for (Integer outlink : graph.getNode(next).getOutEdges()) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        }
      } else {
        GraphNode curNode = originalGraph.getNode(next);
        if (FilterHelpers.isShared(curNode, leaves)) {
          return curNode.getId();
        }
        
        for (Integer outlink : graph.getNode(next).getOutEdges()) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        }
      }
    }
    
    return end;
  }
  
  private SequenceGraph getPreviousView(int start, int end, SequenceGraph graph) {
    if (!oldGraphs.containsKey(start) || !oldGraphs.get(start).containsKey(end) 
        || oldGraphs.get(start).get(end).isEmpty()) {
      return graph;
    }
    
    Set<GraphNode> nodesToRemove = NodePathFinder.getNodesOnPath(start, end, graph, false);
    // get current in and outlinks of start and end node
    Collection<Integer> startInEdges = graph.getNode(start).getInEdges();
    Collection<Integer> endOutEdges = graph.getNode(end).getOutEdges();
    
    nodesToRemove.forEach(node -> {
      if (node.getId() != start && node.getId() != end) {
        graph.remove(node.getId(), true, true);
      } else {
        graph.remove(node.getId(), false, false);
      }
    });
    
    Set<GraphNode> previousView = oldGraphs.get(start).get(end).pop();
    previousView.forEach(node -> {
      if (node.getId() == start) {
        node.setInEdges(startInEdges);
      } else if (node.getId() == end) {
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
