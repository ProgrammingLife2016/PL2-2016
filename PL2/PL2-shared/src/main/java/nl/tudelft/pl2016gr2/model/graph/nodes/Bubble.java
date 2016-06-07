package nl.tudelft.pl2016gr2.model.graph.nodes;

/**
 * An aggregate <code>GraphNode</code>, containing other <code>GraphNodes</code>.
 *
 * @author Wouter Smit
 */
public abstract class Bubble implements GraphNode {

  /**
   * If the nodes in the bubble have to be vertically aligned.
   *
   * @return if the nodes in the bubble have to be vertically aligned.
   */
  public abstract boolean needsVerticalAligning();
}
