package nl.tudelft.pl2016gr2.model.graph;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Describes the data structure of a DNA sequence (alignment) graph.
 * <p>
 * A sequence graph is a <i>directed</i> graph.
 * Every node represents some sequence of DNA.
 * An edge between two nodes represents the the concatenated sequence of both nodes appearing in
 * one or more genomes, which are exactly the genomes going over that edge.
 * </p>
 * <p>
 * A genome's sequence can be constructed by following all edges that the genome traverses and
 * concatenating the sequences in the encountered nodes.
 * Note that not <i>all</i> paths through the graph construct a valid genome.
 * </p>
 * <p>
 * Genomes can have parts of their sequence aligned, which means
 * that they share nodes and edges (the path).
 * When parts differ, the genomes traverse different edges to nodes which contain their respective
 * sequence.
 * They can later 'merge' by traversing to a common node again.
 * </p>
 * <p>
 * This data structure behaves like a <code>Map</code>, with some slight alterations.
 * This is an efficient data structure, because element retrieval can be done in (amortized) O(1).
 * The graph contains potentially millions of elements, making it otherwise unfeasible to inspect
 * any single element.
 * </p>
 * <p>
 * Modification of the entries in the graph is permitted, but not advisable.
 * In almost all cases, it is preferable to create a new graph based on the original.
 * E.g. One must manually add nodes as <code>root node</code> when all in edges of a node are
 * removed.
 * </p>
 *
 * @author Wouter Smit
 */
public interface SequenceGraph extends Iterable<GraphNode> {

  /**
   * Returns the identifiers of all root nodes in this graph.
   * <p>
   * A root node is a node without parents.
   * </p>
   * <p>
   * The collection that is returned is not meant for editing.
   * Depending on the implementing class, it might be unmodifiable.
   * </p>
   *
   * @return the identifiers of all root nodes in this graph.
   */
  Collection<GraphNode> getRootNodes();

  /**
   * Returns the IDs of all genomes that exist in this graph.
   * <p>
   * The collection that is returned is not meant for editing.
   * Depending on the implementing class, it might be unmodifiable.
   * </p>
   *
   * @return The IDs of all genomes in this graph
   */
  Collection<Integer> getGenomes();

  /**
   * Add the node as a root node of this graph.
   * <p>
   * This does not add the node itself to the graph.
   * In general, there is no need to add root nodes manually, nor is it advisable.
   * Nodes are automatically marked as root when they are added.
   * </p>
   * <p>
   * Adding root nodes manually is only useful when modifying existing nodes.
   * In such a case, one should construct a new instantiation of the graph with new nodes.
   * </p>
   *
   * @param rootNode The node in this graph to manually add as root
   */
  void addAsRootNode(GraphNode rootNode);

  /**
   * Add the <code>genome</code> to the genomes in this graph.
   *
   * @param genome The ID of the genome that is to be added.
   */
  void addGenome(int genome);

  /**
   * Remove the <code>genome</code> from the genomes in this graph.
   *
   * @param genome The ID of the genome that is to be removed.
   */
  void removeGenome(int genome);

  /**
   * The size of the graph is defined as the amount of nodes in the graph.
   *
   * @return the amount of nodes in the graph
   */
  int size();

  /**
   * Returns true if this graph contains no nodes.
   *
   * @return <code>true</code> if this graph contains no nodes
   */
  boolean isEmpty();

  /**
   * Returns true if this graph contains the element.
   *
   * @param node the element whose presence in this graph is to be tested
   * @return <code>true</code> if this graph contains an element with the specified identifier.
   */
  boolean contains(GraphNode node);

  /**
   * Returns the <code>GraphNode</code> with the specified ID.
   *
   * @param id The unique identifier of the desired element
   * @return The desired <code>GraphNode</code> (node), or null if the element was not found
   */
  GraphNode getNode(int id);

  /**
   * Adds the <code>node</code> to the graph.
   * <p>
   * If the node is defined as a <i>root node</i>, that is, the node has no parents,
   * then the node is also added as a root node to the graph.
   * It can be reached by calling {@link #getRootNodes()} on the graph.
   * </p>
   * <p>
   * This method makes no promises on the behaviour if an equal node already exists.
   * Mostly, this will mean that the node has the same {@link GraphNode#getId()} value.
   * Mostly, also, the old node will be overwritten and lost.
   * </p>
   *
   * @param node The element to add to the graph
   */
  void add(GraphNode node);

  /**
   * Removes the node in the graph saved under the specified <code>identifier</code>.
   * <p>
   * The old element is returned, or <code>null</code> if the graph contained no element with the
   * identifier.
   * </p>
   *
   * @param node The element which is to be removed from the graph
   * @param updateInlinks if the inlinks must be updated.
   * @param updatOutlinks if the outlinks must be updated.
   */
  void remove(GraphNode node, boolean updateInlinks, boolean updatOutlinks);

  /**
   * Returns an iterator over the elements in the graph.
   *
   * @return An iterator over all <code>GraphNode</code>s
   */
  @Override
  Iterator<GraphNode> iterator();
  
  /**
   * Get the nodes of the graph ordered by level.
   * @return the nodes of the graph ordered by level.
   */
  ArrayList<GraphNode> getOrderedGraph();
}
