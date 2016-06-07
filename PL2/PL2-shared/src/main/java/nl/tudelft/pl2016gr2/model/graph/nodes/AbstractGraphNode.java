package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.graph.data.GraphNodeGuiData;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Aides in implementing the <code>GraphNode</code> interface by implementing methods that should
 * have the same behaviour for all nodes.
 *
 * @author Wouter Smit
 */
public abstract class AbstractGraphNode implements GraphNode {

  @TestId(id = "id_field")
  private int identifier;
  private final GraphNodeGuiData guiData = new GraphNodeGuiData();
  
  private HashSet<GraphNode> inEdges;
  private HashSet<GraphNode> outEdges;

  /**
   * Construct a bare abstract node with an ID.
   *
   * @param identifier the id of the node.
   */
  public AbstractGraphNode(int identifier) {
    this.identifier = identifier;
    this.inEdges = new HashSet<>();
    this.outEdges = new HashSet<>();
  }

  /**
   * Construct an abstract node with an ID and the in/out edges.
   *
   * @param identifier the id of the node.
   * @param inEdges    the in edges of the node.
   * @param outEdges   the out edges of the node.
   */
  public AbstractGraphNode(int identifier, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges) {
    this.identifier = identifier;
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
  }

  /**
   * Constructor to copy an AbstractGraphNode.
   *
   * @param abstractGraphNode the abstractGraphNode to copy.
   */
  protected AbstractGraphNode(AbstractGraphNode abstractGraphNode) {
    this.identifier = abstractGraphNode.identifier;
    this.inEdges = abstractGraphNode.inEdges;
    this.outEdges = abstractGraphNode.outEdges;
  }

  @Override
  public int getId() {
    return identifier;
  }

  @Override
  public Collection<GraphNode> getChildren() {
    return null;
  }

  @Override
  public Collection<Integer> getGenomesOverEdge(GraphNode node) {
    //assert getOutEdges().contains(
    //    node) : "Tried to get genomes over edge for node " + node.getId() + "but it is "
    //    + "not a direct successor. This = " + this.getId();
    Collection<Integer> genomes = new ArrayList<>();
    getGenomes().stream().filter(genome -> node.getGenomes().contains(genome))
        .forEach(genomes::add);
    return genomes;
  }

  @Override
  public Collection<GraphNode> getInEdges() {
    return inEdges;
  }

  @Override
  public void setInEdges(Collection<GraphNode> edges) {
    inEdges = new HashSet<>(edges);
    //inEdges.trimToSize();
  }

  @Override
  public void addAllInEdges(Collection<GraphNode> nodes) {
    inEdges.addAll(nodes);
  }

  @Override
  public void addInEdge(GraphNode node) {
    inEdges.add(node);
  }

  @Override
  public void removeInEdge(GraphNode node) {
    inEdges.remove(node);
  }

  @Override
  public Collection<GraphNode> getOutEdges() {
    return outEdges;
  }

  @Override
  public void setOutEdges(Collection<GraphNode> edges) {
    outEdges = new HashSet<>(edges);
    //outEdges.trimToSize();
  }

  @Override
  public void addAllOutEdges(Collection<GraphNode> nodes) {
    outEdges.addAll(nodes);
  }

  @Override
  public void addOutEdge(GraphNode node) {
    outEdges.add(node);
  }

  @Override
  public void removeOutEdge(GraphNode node) {
    outEdges.remove(node);
  }
  
  @Override
  public void trimToSize() {
    //inEdges.trimToSize();
    //outEdges.trimToSize();
  }

  @Override
  public String toString() {
    return "id: " + getId();
  }

  @Override
  public int hashCode() {
    return identifier * 37;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !getClass().equals(obj.getClass())) {
      return false;
    }
    return identifier == ((AbstractGraphNode) obj).identifier;
  }

  @Override
  public GraphNodeGuiData getGuiData() {
    return guiData;
  }
}
