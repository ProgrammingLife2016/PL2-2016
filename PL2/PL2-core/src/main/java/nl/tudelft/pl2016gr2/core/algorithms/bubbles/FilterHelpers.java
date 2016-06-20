package nl.tudelft.pl2016gr2.core.algorithms.bubbles;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.Collections;
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

  private FilterHelpers() {
  }

  /**
   * Adds an int to a tovisit queue if it hasn't been visited yet and is not already in the queue.
   *
   * @param visitor the int to visit
   * @param toVisit the current queue
   * @param visited the set of visited ints
   */
  public static void addToVisit(GraphNode visitor, Queue<GraphNode> toVisit,
      Set<GraphNode> visited) {
    if (!visited.contains(visitor) && !toVisit.contains(visitor)) {
      toVisit.add(visitor);
    }
  }

  /**
   * This method checks if a node is shared among the leaves of a phylo genetic tree node. What that
   * means is that the nodes either contains all the genomes of the leaves of the phylo node, or
   * that it has some genomes which are in other branches of the phylo tree.
   *
   * @param node   the node to check
   * @param leaves the labels of the leaves of the phylo node
   * @return true if the node is shared
   */
  public static boolean isShared(GraphNode node, List<Integer> leaves) {
    sortIfUnsorted(leaves);
    if (node.containsAllGenomes(leaves)) {
      return true;
    }

    for (Integer genome : node.getGenomes()) {
      if (genome != 0 && Collections.binarySearch(leaves, genome) < 0) {
        return true;
      }
    }
    return false;
  }

  private static void sortIfUnsorted(List<Integer> genomes) {
    int previous = Integer.MIN_VALUE;
    for (int i = 0; i < genomes.size(); i++) {
      if (genomes.get(i) < previous) {
        genomes.sort(null);
        return;
      }
      previous = genomes.get(i);
    }
  }
}
