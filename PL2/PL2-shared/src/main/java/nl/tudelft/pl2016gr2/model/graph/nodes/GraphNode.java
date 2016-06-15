package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.data.GraphNodeGuiData;
import nl.tudelft.pl2016gr2.model.metadata.LineageColor;
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
   * <p>
   * The collection that is returned is not meant for editing. Depending on the implementing class,
   * it might be unmodifiable.
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
   * Set the in edges.
   *
   * @param edges the in edges.
   */
  void setInEdges(Collection<GraphNode> edges);

  /**
   * Returns the identifiers of the nodes of all in-edges of <code>GraphNode</code>.
   * <p>
   * The collection that is returned is not meant for editing. Depending on the implementing class,
   * it might be unmodifiable.
   * </p>
   *
   * @return All in-edges of this <code>GraphNode</code>
   */
  Collection<GraphNode> getInEdges();

  /**
   * Adds the specified ID to the set of in-edges of this <code>GraphNode</code>.
   * <p>
   * After calling this method, the caller must make sure to also call {@link #trimToSize()}.
   * </p>
   * <p>
   * This method will have to expand the underlying data structure. Repeatedly adding edges should
   * be done with {@link #addAllInEdges(Collection)}.
   * </p>
   *
   * @param node The node to add to the in-edges of this <code>GraphNode</code>.
   */
  void addInEdge(GraphNode node);

  /**
   * Adds all edges to the current in edges, leaving the old in edges untouched.
   * <p>
   * This method ensures that storage is minimized after the additions.
   * </p>
   *
   * @param nodes The nodes to add as in edges
   */
  void addAllInEdges(Collection<GraphNode> nodes);

  /**
   * Removes the specified ID to the set of in-edges of this <code>GraphNode</code>.
   * <p>
   * After calling this method, the caller must make sure to also call {@link #trimToSize()}.
   * </p>
   *
   * @param node The node to remove from the in-edges of this <code>GraphNode</code>.
   */
  void removeInEdge(GraphNode node);

  /**
   * Set the out edges.
   *
   * @param edges the out edges.
   */
  void setOutEdges(Collection<GraphNode> edges);

  /**
   * Returns the identifiers of the nodes of all out-edges of <code>GraphNode</code>.
   * <p>
   * The collection that is returned is not meant for editing. Depending on the implementing class,
   * it might be unmodifiable.
   * </p>
   *
   * @return All children of this <code>GraphNode</code>
   */
  Collection<GraphNode> getOutEdges();

  /**
   * Adds the specified ID to the set of out-edges of this <code>GraphNode</code>.
   * <p>
   * After calling this method, the caller must make sure to also call {@link #trimToSize()}.
   * </p>
   * <p>
   * This method will have to expand the underlying data structure. Repeatedly adding edges should
   * be done with {@link #addAllOutEdges(Collection)}.
   * </p>
   *
   * @param node The node to add to the out-edges of this <code>GraphNode</code>.
   */
  void addOutEdge(GraphNode node);

  /**
   * Adds all edges to the current out edges, leaving the old out edges untouched.
   * <p>
   * This method ensures that storage is minimized after the additions.
   * </p>
   *
   * @param nodes The nodes to add as out edges
   */
  void addAllOutEdges(Collection<GraphNode> nodes);

  /**
   * Removes the specified ID to the set of out-edges of this <code>GraphNode</code>.
   * <p>
   * After calling this method, the caller must make sure to also call {@link #trimToSize()}.
   * </p>
   *
   * @param node The node to remove from the out-edges of this <code>GraphNode</code>.
   */
  void removeOutEdge(GraphNode node);

  /**
   * Returns the names of all genomes that go through this <code>GraphNode</code>.
   * <p>
   * Paths through the <code>SequenceGraph</code> describe certain genomes. Every
   * <code>GraphNode</code> is annotated with information about its genomes.
   * </p>
   * <p>
   * The collection that is returned is not meant for editing. Depending on the implementing class,
   * it might be unmodifiable.
   * </p>
   *
   * @return The genomes of this <code>GraphNode</code>
   */
  Collection<Integer> getGenomes();

  /**
   * Adds the genome name to the set of genomes of this <code>GraphNode</code>.
   * <p>
   * After calling this method, the caller must make sure to also call {@link #trimToSize()}.
   * </p>
   * <p>
   * This method will have to expand the underlying data structure. Repeatedly adding genomes should
   * be done with {@link #addAllGenomes(Collection)}.
   * </p>
   *
   * @param genome The name of the genome to add to this <code>GraphNode</code>.
   */
  void addGenome(int genome);

  /**
   * Adds all genomes to the current genomes, leaving the old genomes untouched.
   * <p>
   * This method ensures that storage is minimized after the additions.
   * </p>
   *
   * @param genomes The genomes to add to this node
   */
  void addAllGenomes(Collection<Integer> genomes);

  /**
   * Removes the genome name from the set of genomes of this <code>GraphNode</code>.
   * <p>
   * After calling this method, the caller must make sure to also call {@link #trimToSize()}.
   * </p>
   *
   * @param genome The name of the genome to remove from this <code>GraphNode</code>.
   */
  void removeGenome(int genome);

  /**
   * Calculates the genomes that pass over the edge between this node and the specified node.
   * <p>
   * The node must be the destination of an out-edge of this node.
   * </p>
   *
   * @param node The node that the edge leads to.
   * @return The set of genomes that pass over the common edge.
   */
  Collection<Integer> getGenomesOverEdge(GraphNode node);

  /**
   * Get the amount of genomes which are present in this node.
   *
   * @return the amount of genomes which are present in this node.
   */
  int getGenomeSize();

  /**
   * Approximate the amount of genomes which go over the edge to the given node.
   *
   * @param node the node to which the edge goes.
   * @return an approximation of the amount of genomes which go over the given edge.
   */
  int approximateGenomesOverEdge(GraphNode node);

  /**
   * Trims the capacity of this <code>GraphNode</code> instance to the currently used size.
   * <p>
   * This method can be used to minimize the storage of this <code>GraphNode</code>.
   * </p>
   */
  void trimToSize();

  @Override
  void accept(NodeVisitor visitor);

  /**
   * Copies all of the elements of this node, including its inedges, outedges, genomes/treenode.
   *
   * @return A copy of the object.
   */
  GraphNode copyAll();

  /**
   * Add an offset to the current level of the node.
   *
   * @param offset the offset.
   */
  void addPositionOffset(int offset);

  /**
   * Set the level of this node (the depth in the tree).
   *
   * @param level the level of this node.
   */
  void setLevel(int level);

  /**
   * Get the level of this node (the depth in the tree).
   *
   * @return the level of this node.
   */
  int getLevel();

  /**
   * Get the GUI data of this node.
   *
   * @return the GUI data of this node.
   */
  GraphNodeGuiData getGuiData();

  /**
   * Pops this bubble.
   * <p>
   * Popping is a synonym for zooming in on the bubble, thus revealing the nodes that it stores.
   * </p>
   *
   * @return the sorted and aligned list of nodes which was nested in this bubble.
   */
  Collection<GraphNode> pop();

  /**
   * Unpops a bubble.
   */
  void unpop();

  /**
   * Checks if a bubble is popped.
   *
   * @return true is the bubble is popped
   */
  boolean isPopped();

  void setAnnotation(Annotation annotation);

  boolean hasAnnotation();

  Annotation getAnnotation();

  LineageColor getMostFrequentLineage();
}
