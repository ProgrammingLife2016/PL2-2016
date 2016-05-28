package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.SequenceGraph;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Class to find the set of nodes that have a path from a given node
 * to another given node on a given graph.
 * 
 * @author Casper
 *
 */
public class NodePathFinder {
  
  private NodePathFinder() {
  }
  
  /**
   * Gets all the nodes that are on a path between start and end in
   * the given graph.
   * 
   * @param start the starting node
   * @param end the ending node
   * @param graph the graph on which the nodes have a path
   * @param copy if true, every node is copied into a new list (instead
   *     of a reference)
   * @return a set with all nodes on the path between start and end.
   */
  protected static Set<GraphNode> getNodesOnPath(GraphNode start, GraphNode end, 
      SequenceGraph graph, boolean copy) {
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();
    
    Set<GraphNode> nodesOnPath = new HashSet<>();
    Set<GraphNode> seenPaths = new HashSet<>();
    Set<GraphNode> noPath = new HashSet<>();
    toVisit.add(start);
    
    while (!toVisit.isEmpty()) {
      GraphNode next = graph.getNode(toVisit.poll().getId());
      if (copy) {
        nodesOnPath.add(next.copyAll());
      } else {
        nodesOnPath.add(next);
      }
      
      for (GraphNode outlink : next.getOutEdges()) {
        if (hasPath(outlink, end, graph, seenPaths, noPath)) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        }
      }
    }
    
    return nodesOnPath;
  }
  
  /**
   * Returns true if there is a path.
   * @param from the start node
   * @param to the target node
   * @param graph the graph
   * @param seenPaths a set of nodes that are known to be on the path
   * @param noPath a set of nodes that are know to not be on the path
   * @return true if there is a path
   */
  private static boolean hasPath(GraphNode from, GraphNode to, SequenceGraph graph, 
      Set<GraphNode> seenPaths, Set<GraphNode> noPath) {
    if (from == to || seenPaths.contains(from)) {
      return true;
    } else if (noPath.contains(from)) {
      return false;
    }

    GraphNode node = graph.getNode(from.getId());
    if (!node.hasChildren() && from.getId() > to.getId()) { // is this really correct? comparing IDs <<<<<<<
      noPath.add(from);
      return false;
    } else {
      boolean hasPath = false;
      for (GraphNode outEdge : node.getOutEdges()) {
        if (outEdge == to) {
          seenPaths.add(from);
          return true;
        }
        hasPath = hasPath || hasPath(outEdge, to, graph, seenPaths, noPath);
      }
      
      if (hasPath) {
        seenPaths.add(from);
      } else {
        noPath.add(from);
      }
      return hasPath;
    }
  }
}
