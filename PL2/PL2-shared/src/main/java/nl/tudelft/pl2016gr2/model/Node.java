package nl.tudelft.pl2016gr2.model;

/**
 * The most low level <code>GraphNode</code>, containing the individual bases of the DNA sequence.
 *
 * @author Wouter Smit
 */
public interface Node extends GraphNode {

  /**
   * Sets the sequence stored in this node to the specified sequence.
   *
   * @param sequence The sequence to set this node to
   */
  void setSequence(String sequence);

  /**
   * Returns a string representation of the sequence that this node contains.
   *
   * @return The sequence of this node
   */
  String getSequence();
}
