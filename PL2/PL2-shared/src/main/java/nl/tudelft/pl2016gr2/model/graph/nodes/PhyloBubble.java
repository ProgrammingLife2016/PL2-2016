package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.graph.data.GraphNodeGuiData;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class PhyloBubble extends Bubble {

  private final int id;
  private final IPhylogeneticTreeNode treeNode;
  private final HashSet<GraphNode> nestedNodes;

  private HashSet<GraphNode> inEdges;
  private HashSet<GraphNode> outEdges;

  private final HashMap<Integer, Collection<GraphNode>> originalOutEdges = new HashMap<>();
  private final HashMap<Integer, Collection<GraphNode>> originalInEdges = new HashMap<>();

  private final PhyloFilter filter;
  private Collection<GraphNode> poppedNodes;
  private boolean isPopped;

  private int size = -1;
  private int level = -1;
  private final GraphNodeGuiData guiData;

  /**
   * Construct a phylo bubble.
   *
   * @param id       the id of the bubble.
   * @param treeNode the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter   the filter object to call when zooming in.
   */
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter) {
    this.id = id;
    this.treeNode = treeNode;
    this.filter = filter;
    this.inEdges = new HashSet<>();
    this.outEdges = new HashSet<>();
    this.nestedNodes = new HashSet<>();
    guiData = new GraphNodeGuiData();
  }

  /**
   * Construct a phylo bubble.
   *
   * @param id       the id of the bubble.
   * @param treeNode the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter   the filter object to call when zooming in.
   * @param inEdges  the in edges of the bubble.
   * @param outEdges the out edges of the bubble.
   */
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    this.id = id;
    this.treeNode = treeNode;
    this.filter = filter;
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
    //this.inEdges.trimToSize();
    //this.outEdges.trimToSize();
    this.nestedNodes = new HashSet<>();
    guiData = new GraphNodeGuiData();
  }

  /**
   * Construct a phylo bubble.
   *
   * @param id          the id of the bubble.
   * @param treeNode    the phylogenetic tree node where the mutation of this bubble occurs.
   * @param filter      the filter object to call when zooming in.
   * @param inEdges     the in edges of the bubble.
   * @param outEdges    the out edges of the bubble.
   * @param nestedNodes the nested nodes of the bubble.
   */
  public PhyloBubble(int id, IPhylogeneticTreeNode treeNode, PhyloFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges,
      Collection<GraphNode> nestedNodes) {
    this.id = id;
    this.treeNode = treeNode;
    this.filter = filter;
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
    //this.inEdges.trimToSize();
    //this.outEdges.trimToSize();
    this.nestedNodes = new HashSet<>(nestedNodes);
    guiData = new GraphNodeGuiData();
    initOverlap();
  }

  /**
   * Initialize the overlap value, based on the nested nodes.
   */
  private void initOverlap() {
    for (GraphNode nestedNode : nestedNodes) {
      if (nestedNode.getGuiData().overlapping) {
        guiData.overlapping = true;
        return;
      }
    }
    guiData.overlapping = false;
  }

  @Override
  public String toString() {
    return "id: " + id + ", in: " + getIds(inEdges) + ", out: " + getIds(outEdges)
        + ", nested: " + getIds(nestedNodes) + ", tree leaves: " + treeNode.getGenomes();
  }

  @Override
  public boolean needsVerticalAligning() {
    return poppedNodes == null;
  }

  /**
   * Get a string representation of the IDs of the nodes.
   *
   * @param nodes the nodes.
   * @return the string containing all of the IDs.
   */
  private static String getIds(Collection<GraphNode> nodes) {
    StringBuilder builder = new StringBuilder("[");
    for (GraphNode node : nodes) {
      builder.append(node.getId()).append(", ");
    }
    builder.append("]");
    return builder.toString();
  }

  @Override
  public int getId() {
    return id;
  }

  /**
   * Get the phylogenetic tree node.
   *
   * @return the phylogenetic tree node.
   */
  public IPhylogeneticTreeNode getTreeNode() {
    return treeNode;
  }

  /**
   * Add another child node to the nested nodes.
   *
   * @param child the child node.
   */
  public void addChild(GraphNode child) {
    nestedNodes.add(child);
    if (child.getGuiData().overlapping) {
      guiData.overlapping = true;
    }
  }

  @Override
  public boolean hasChildren() {
    return true;
  }

  @Override
  public boolean hasChild(GraphNode child) {
    return nestedNodes.contains(child);
  }

  @Override
  public Collection<GraphNode> getChildren() {
    return nestedNodes;
  }

  @Override
  public int size() {
    if (size == -1) {
      size = 0;
      int highestInEdgeLevel = 0;
      for (GraphNode inEdge : inEdges) {
        if (inEdge.getLevel() > highestInEdgeLevel) {
          highestInEdgeLevel = inEdge.getLevel();
        }
      }
      size = getLevel() - highestInEdgeLevel;
    }
    return size;
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
  public void addOutEdge(GraphNode node) {
    outEdges.add(node);
  }

  @Override
  public void removeOutEdge(GraphNode node) {
    outEdges.remove(node);
  }

  @Override
  public Collection<Integer> getGenomes() {
    HashSet<Integer> genomeSet = new HashSet<>();
    for (GraphNode nestedNode : nestedNodes) {
      genomeSet.addAll(nestedNode.getGenomes());
    }
    return genomeSet;
  }

  @Override
  public void addGenome(int genome) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public void removeGenome(int genome) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public Collection<Integer> getGenomesOverEdge(GraphNode node) {
    assert getOutEdges().contains(
        node) : "Tried to get genomes over edge for node " + node.getId() + "but it is "
        + "not a direct successor. This = " + this.getId();

    Collection<Integer> genomes = new ArrayList<>();
    getGenomes().stream().filter(genome -> node.getGenomes().contains(genome))
        .forEach(genomes::add);
    return genomes;
  }

  @Override
  public GraphNode copy() {
    return new PhyloBubble(getId(), treeNode, filter);
  }

  @Override
  public GraphNode copyAll() {
    return new PhyloBubble(getId(), treeNode, filter, inEdges, outEdges, nestedNodes);
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public Collection<GraphNode> pop() {
    if (!isPopped) {
      isPopped = true;
      for (GraphNode node : inEdges) {
        originalOutEdges.put(node.getId(), new HashSet<>(node.getOutEdges()));
      }
      for (GraphNode node : outEdges) {
        originalInEdges.put(node.getId(), new HashSet<>(node.getInEdges()));
      }
      poppedNodes = filter.zoomIn(this);
    }
    return poppedNodes;
  }

  @Override
  public void unpop() {
    if (isPopped) {
      isPopped = false;
      for (GraphNode node : inEdges) {
        node.setOutEdges(originalOutEdges.get(node.getId()));
      }

      for (GraphNode node : outEdges) {
        node.setInEdges(originalInEdges.get(node.getId()));
      }
    }
  }

  @Override
  public boolean isPopped() {
    return isPopped;
  }

  @Override
  public int hashCode() {
    return id * 37;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !getClass().equals(obj.getClass())) {
      return false;
    }
    return id == ((PhyloBubble) obj).id;
  }

  @Override
  public void addPositionOffset(int offset) {
    for (GraphNode nestedNode : nestedNodes) {
      nestedNode.addPositionOffset(offset);
    }
  }

  @Override
  public void setLevel(int level) {
    throw new UnsupportedOperationException("This must be performed on the nodes inside of the "
        + "bubbles before the bubbles are made.");
  }

  @Override
  public int getLevel() {
    if (level == -1) {
      for (GraphNode nestedNode : nestedNodes) {
        if (nestedNode.getLevel() > level) {
          level = nestedNode.getLevel();
        }
      }
    }
    return level;
  }

  @Override
  public GraphNodeGuiData getGuiData() {
    return guiData;
  }
}
