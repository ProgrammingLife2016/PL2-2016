package nl.tudelft.pl2016gr2.model;

/**
 * An aggregate <code>GraphNode</code>, containing other <code>GraphNodes</code>.
 *
 * @author Wouter Smit
 */
public interface Bubble extends GraphNode {

  /**
   * Pops this bubble.
   * <p>
   * Popping is a synonym for zooming in on the bubble, thus revealing the nodes that it stores.
   * </p>
   */
  void pop();

}
