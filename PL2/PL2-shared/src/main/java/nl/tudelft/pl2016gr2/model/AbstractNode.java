package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Aides in implementing the <code>Node</code> interface by implementing methods that should have
 * the same behaviour for all nodes.
 * <p>
 * This node only has an ID. it defers all other fields, such as in edges and out edges to its
 * implementations.
 * </p>
 *
 * @author Wouter Smit
 */
public abstract class AbstractNode implements Node {

  @TestId(id = "id_field")
  private int identifier;

  /**
   * Construct a bare abstract node with an ID.
   *
   * @param identifier the id of the node.
   */
  public AbstractNode(int identifier) {
    this.identifier = identifier;
  }

  @Override
  public int getId() {
    return identifier;
  }

  @Override
  public boolean hasChildren() {
    return false;
  }

  @Override
  public Collection<GraphNode> getChildren() {
    return null;
  }

  /**
   * Returns the length of the sequence in this node.
   *
   * @return The sequence length
   */
  @Override
  public int size() {
    return getSequence().length();
  }

  @Override
  public Collection<Integer> getGenomesOverEdge(GraphNode node) {
    assert getOutEdges().contains(
        node) : "Tried to get genomes over edge for node " + node.getId() + "but it is " + "not "
        + "a" + " direct successor. This = " + this.getId();

    Collection<Integer> genomes = new ArrayList<>();
    Collection<Integer> otherGenomes = new ArrayList<>();
    // Mark genomes that are seen in other out edges which appear before the node.
    this.getOutEdges().forEach(outEdge -> {
      if (!outEdge.equals(node) && outEdge.getId() < node.getId()) {
        otherGenomes.addAll(outEdge.getGenomes());
      }
    });
    getGenomes().stream().filter(
        genome -> node.getGenomes().contains(genome) && !otherGenomes.contains(genome)).forEach(
        genomes::add);

    return genomes;
  }

  @Override
  public String toString() {
    return "id: " + getId();
  }

  @Override
  public int hashCode() {
    return identifier;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !getClass().equals(obj.getClass())) {
      return false;
    }
    return identifier == ((AbstractNode) obj).identifier;
  }
}
