package nl.tudelft.pl2016gr2.collectioninterfaces;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * This will provide an Iterator that can iterate over a single genome's path through the sequence
 * graph.
 * <p>
 * Because finding the next element is more complex than in a generic <code>Collection</code>, this
 * class is optimized to only search for the the next element once, in the {@link #hasNext()}
 * method.
 * Unlike the {@link Iterator} interface, it does not find new elements every time {@link #next()}
 * is called.
 * </p>
 *
 * @author Wouter Smit
 */
public class GenomeIterator implements Iterator<Node> {

  private OriginalGraph graph;
  private String genome;
  private Node next;

  /**
   * Creates an iterator over the <code>graph</code> for <code>genome</code>.
   * <p>
   * Note that the genome must be present in the graph, otherwise a {@link
   * NoSuchElementException} will be thrown.
   * </p>
   *
   * @param graph  The graph to traverse through
   * @param genome The genome to find and iterate over
   */
  public GenomeIterator(OriginalGraph graph, String genome) {
    this.genome = genome;
    this.graph = graph;
    this.next = findFirstGenomeOccurrence();
  }

  /**
   * Searches the children of the previous node for a node that follows the path for the genome.
   * <p>
   * This method must be used to expose the next element with {@link #next()}. Repeatedly calling
   * <code>next()</code> without calling this method will not produce new elements.
   * </p>
   *
   * @return The next node in the genome path
   */
  @Override
  public boolean hasNext() {
    ArrayList<Node> children = graph.getTargets(next.getId());
    for (Node child : children) {
      if (child.getGenomes().contains(genome)) {
        next = child;
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the next element as retrieved by <code>hasNext()</code>.
   * <p>
   * This method will return the same element when called multiple times. The element is found with
   * {@link #hasNext()}. To ensure actual iteration, use the object in conjunction with
   * <code>hasNext</code>.
   * </p>
   *
   * @return The element as found with <code>hasNext()</code>
   */
  @Override
  public Node next() {
    return next;
  }

  /**
   * Searches {@link #graph} for the first <code>Node</code> that contains the specified genome.
   * <p>
   * The {@link nl.tudelft.pl2016gr2.model.Node} is found using a breadth-first search
   * </p>
   *
   * @return The first node
   */
  private Node findFirstGenomeOccurrence() {
    Queue<Node> queue = new LinkedList<>();
    queue.add(graph.getRoot());

    while (!queue.isEmpty()) {
      Node current = queue.remove();
      if (current.getGenomes().contains(genome)) {
        return current;
      }

      queue.addAll(graph.getTargets(current.getId()));
    }
    throw new NoSuchElementException("The genome was not found in the data set");
  }
}
