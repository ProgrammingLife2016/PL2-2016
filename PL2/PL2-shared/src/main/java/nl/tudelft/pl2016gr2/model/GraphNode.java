package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.util.Copyable;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;
import nl.tudelft.pl2016gr2.visitor.Visitable;

import java.util.Collection;

/**
 * Represents a node in a <code>SequenceGraph</code> and provides basic operations on it.
 * <p>
 * This interface describes what functionality elements of a {@link SequenceGraph} should provide.
 * Note that the <code>SequenceGraph</code> is a directed graph, and thus only contains directed
 * edges.
 * </p>
 * <p>
 * This interface uses the <i>composite design pattern</i> to (not) distinguish between
 * {@link Bubble}s and {@link Node}s
 * </p>
 * <p>
 * This interface uses the <i>visitor design pattern</i> to allow external actors to target specific
 * implementations of this interface. This is useful when these are stored in a
 * {@link SequenceGraph}.
 * </p>
 * <p>
 * This interface uses the <i>factory method design pattern</i> to allow external actors to copy the
 * concrete instance of a GraphNode.
 * </p>
 *
 * @author Wouter Smit
 */
public interface GraphNode extends Visitable, Copyable<GraphNode> {

  /**
   * Provides a unique integer value to reference the element in the graph.
   * <p>
   * Since the integer value should be unique, it might be advisable to incorporate it in a
   * {@link Object#hashCode()} or {@link Object#equals(Object)}.
   * </p>
   *
   * @return An integer value that represents the <code>GraphNode</code>'s id.
   */
  int getId();

  /**
   * Returns true if this node is an aggregate (a bubble) of more nodes, which are its children.
   *
   * @return <code>true</code> if the node is an aggregate
   */
  boolean hasChildren();

  /**
   * Returns all child nodes of this node, that are contained in this node.
   * <p>
   * This methods returns <code>null</code> if it is called on an instance that is not an aggregate
   * of nodes.
   * </p>
   *
   * @return The set of child nodes, or an <code>null</code> if the node does not have children
   */
  Collection<GraphNode> getChildren();

  /**
   * Checks if this node has a child with the specified child.
   *
   * @param child the child to check for.
   * @return true if this node has the child.
   */
  boolean hasChild(GraphNode child);

  /**
   * Returns the size of this node.
   * <p>
   * The implementing classes must define what <i>size</i> means to that class.
   * </p>
   *
   * @return A value representing the size of this node.
   */
  int size();

  /**
   * Returns true if this node is a root node, that is, has no in-edges.
   *
   * @return <code>true</code> iff <code>this.getInEdges().isEmpty</code>
   */
  default boolean isRoot() {
    return this.getInEdges().isEmpty();
  }

  /**
   * Returns the identifiers of the nodes of all in-edges of <code>GraphNode</code>.
   *
   * @return All in-edges of this <code>GraphNode</code>
   */
  Collection<GraphNode> getInEdges();

  /**
   * Sets the set of in-edges to be equal to the provided collection.
   * <p>
   * This method is a better performing abstraction of edge addition. This method should be used
   * over {@link #addInEdge(int)} and {@link #removeInEdge(int)} wherever possible.
   * </p>
   * <p>
   * The provided collection <b>may not</b> contain duplicates. These will not be removed by the
   * method.
   * </p>
   *
   * @param edges The collection of edges to make the in-edges of this node
   */
  void setInEdges(Collection<GraphNode> edges);

  /**
   * Adds the specified ID to the set of in-edges of this <code>GraphNode</code>.
   *
   * @param node The node to add to the in-edges of this <code>GraphNode</code>.
   * @deprecated This method delivers suboptimal performance. Use {@link #setInEdges(Collection)}
   */
  void addInEdge(GraphNode node);

  /**
   * Removes the specified ID to the set of in-edges of this <code>GraphNode</code>.
   *
   * @param node The node to remove from the in-edges of this <code>GraphNode</code>.
   * @deprecated This method delivers suboptimal performance. Use {@link #setInEdges(Collection)}
   */
  void removeInEdge(GraphNode node);

  /**
   * Returns the identifiers of the nodes of all out-edges of <code>GraphNode</code>.
   * <p>
   * A <code>GraphNode</code> is a child when it has an edge leading out of this element.
   * </p>
   *
   * @return All children of this <code>GraphNode</code>
   */
  Collection<GraphNode> getOutEdges();

  /**
   * Sets the set of out-edges to be equal to the provided collection.
   * <p>
   * This method is a better performing abstraction of edge addition. This method should be used
   * over {@link #addOutEdge(int)} and {@link #removeOutEdge(int)} wherever possible.
   * </p>
   * <p>
   * The provided collection <b>may not</b> contain duplicates. These will not be removed by the
   * method.
   * </p>
   *
   * @param edges The collection of edges to make the out-edges of this node
   */
  void setOutEdges(Collection<GraphNode> edges);

  /**
   * Adds the specified ID to the set of out-edges of this <code>GraphNode</code>.
   *
   * @param node The node to add to the out-edges of this <code>GraphNode</code>.
   * @deprecated This method delivers suboptimal performance. Use {@link #setOutEdges(Collection)}
   */
  void addOutEdge(GraphNode node);

  /**
   * Removes the specified ID to the set of out-edges of this <code>GraphNode</code>.
   *
   * @param node The node to remove from the out-edges of this <code>GraphNode</code>.
   * @deprecated This method delivers suboptimal performance. Use {@link #setOutEdges(Collection)}
   */
  void removeOutEdge(GraphNode node);

  /**
   * Returns the names of all genomes that go through this <code>GraphNode</code>.
   * <p>
   * Paths through the <code>SequenceGraph</code> describe certain genomes. Every
   * <code>GraphNode</code> is annotated with information about its genomes.
   * </p>
   *
   * @return The genomes of this <code>GraphNode</code>
   */
  Collection<String> getGenomes();

  /**
   * Adds the genome name to the set of genomes of this <code>GraphNode</code>.
   *
   * @param genome The name of the genome to add to this <code>GraphNode</code>.
   */
  void addGenome(String genome);

  /**
   * Removes the genome name from the set of genomes of this <code>GraphNode</code>.
   *
   * @param genome The name of the genome to remove from this <code>GraphNode</code>.
   */
  void removeGenome(String genome);

  /**
   * Calculates the genomes that pass over the edge between this node and the specified node.
   * <p>
   * The node must be the destination of an out-edge of this node.
   * </p>
   *
   * @param node The node that the edge leads to.
   * @return The set of genomes that pass over the common edge.
   */
  Collection<String> getGenomesOverEdge(GraphNode node);

  @Override
  default void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  /**
   * Provides a copy of the node with at least equal IDs.
   * <p>
   * Furthermore, the following fields should remain empty.
   * </p>
   * <ul>
   * <li>Set of genomes</li>
   * <li>Set of in edges</li>
   * <li>Set of out edges</li>
   * </ul>
   *
   * @return An exact copy of the class
   */
  @Override
  GraphNode copy();

  /**
   * Copies all of the elements of this node, including its inedges, outedges, genomes/treenode.
   *
   * @return A copy of the object.
   */
  GraphNode copyAll();

  boolean isOverlapping();

  void setOverlapping(boolean overlapping);

  /**
   * Add an offset to the current level of the node.
   *
   * @param offset the offset.
   */
  void addPositionOffset(int offset);

  void setLevel(int level);

  int getLevel();
  
  double getRelativeYPos();
  
  void setRelativeYPos(double relativeYPos);
  
  void setMaxHeight(double maxHeight);
  double getMaxHeightPercentage();
}
