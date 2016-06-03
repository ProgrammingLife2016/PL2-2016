package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.GraphNode;

import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Class with helper methods for the filter bubbles algorithm.
 * 
 * @author Casper
 *
 */
public class FilterHelpers {
  
  private FilterHelpers(){
  }
  
  /**
   * Adds an int to a tovisit queue if it hasn't been visited yet and is not already in 
   * the queue.
   * @param visitor the int to visit
   * @param toVisit the current queue
   * @param visited the set of visited ints
   */
  protected static void addToVisit(GraphNode visitor, Queue<GraphNode> toVisit, 
      Set<GraphNode> visited) {
    if (!visited.contains(visitor) && !toVisit.contains(visitor)) {
      toVisit.add(visitor);
    }
  }
  
  /**
   * This method checks if a node is shared among the leaves of a phylo genetic tree node.
   * What that means is that the node either contains all the genomes of the leaves of the 
   * phylo node, or that it has some genomes which are in other branches of the phylo tree.
   * 
   * @param node the node to check
   * @param leaves the labels of the leaves of the phylo node
   * @return true if the node is shared
   */
  protected static boolean isShared(GraphNode node, List<Integer> leaves) {
    if (node.getGenomes().containsAll(leaves)) {
      return true;
    }
    
    for (Integer genome : node.getGenomes()) {
      if (genome != 0 && !leaves.contains(genome)) {
        return true;
      }
    }
    return false;
  }
}
