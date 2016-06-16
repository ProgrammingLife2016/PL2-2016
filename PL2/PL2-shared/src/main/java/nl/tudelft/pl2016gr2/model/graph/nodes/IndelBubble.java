package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * This kind of bubble contains an insertion/deletion mutation.
 *
 * @author Faris
 */
public class IndelBubble extends Bubble {

  private final IVerticalAligner aligner;
  private boolean isPopped;
  private boolean verticallyAligned;
  private Collection<Integer> genomes;

  /**
   * Constructs an indelBubble. In the constructor, the genomes of this indelBubble are set to
   * the genomes of its inedge.
   * 
   * @param id          the id of the bubble.
   * @param inEdges     the in edges of the bubble.
   * @param outEdges    the out edges of the bubble.
   * @param nestedNodes the nested nodes of the bubble.
   * @param aligner     aligner to align the nodes vertically.
   */
  public IndelBubble(int id, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, List<GraphNode> nestedNodes, IVerticalAligner aligner) {
    super(id, inEdges, outEdges, nestedNodes);
    this.genomes = inEdges.iterator().next().getGenomes();
    this.aligner = aligner;
  }

  private IndelBubble(Bubble bubble, IVerticalAligner aligner) {
    super(bubble);
    this.aligner = aligner;
  }

  @Override
  public int getGenomeSize() {
    return getInEdges().iterator().next().getGenomeSize();
  }
  
  @Override
  public Collection<Integer> getGenomes() {
    return genomes;
  }

  @Override
  public GraphNode copy() {
    return new IndelBubble(this, aligner);
  }

  @Override
  public GraphNode copyAll() {
    return new IndelBubble(this, aligner);
  }

  @Override
  public Collection<GraphNode> pop() {
    if (!isPopped) {
      setPoppedEdges();
      isPopped = true;
    }
    if (!verticallyAligned) {
      aligner.align(getChildren(), getInEdges());
      verticallyAligned = true;
    }
    return this.getChildren();
  }

  @Override
  public void unpop() {
    for (GraphNode child : getChildren()) {
      child.unpop();
    }
    if (isPopped) {
      setUnpoppedEdges();
      isPopped = false;
    }
  }

  /**
   * Remove the edges to this bubble and set the edges to the nested nodes.
   */
  private void setPoppedEdges() {
    GraphNode inEdge = getInEdges().iterator().next();
    GraphNode outEdge = getOutEdges().iterator().next();

    Iterator<GraphNode> it = getChildren().iterator();
    GraphNode firstNode = it.next();
    inEdge.removeOutEdge(this);
    inEdge.addOutEdge(firstNode);
    inEdge.addOutEdge(outEdge);

    GraphNode lastNode = firstNode;
    while (it.hasNext()) {
      lastNode = it.next();
    }
    outEdge.removeInEdge(this);
    outEdge.addInEdge(lastNode);
    outEdge.addInEdge(inEdge);

    firstNode.setInEdges(getInEdges());
    lastNode.setOutEdges(getOutEdges());
  }

  /**
   * Remove the edges to the nodes in this bubble and set the edges to this bubble.
   */
  private void setUnpoppedEdges() {
    GraphNode inEdge = getInEdges().iterator().next();
    GraphNode outEdge = getOutEdges().iterator().next();

    Iterator<GraphNode> it = getChildren().iterator();
    GraphNode firstNode = it.next();
    inEdge.removeOutEdge(firstNode);
    inEdge.removeOutEdge(outEdge);
    inEdge.addOutEdge(this);

    GraphNode lastNode = firstNode;
    while (it.hasNext()) {
      lastNode = it.next();
    }
    outEdge.removeInEdge(lastNode);
    outEdge.removeInEdge(inEdge);
    outEdge.addInEdge(this);
  }

  @Override
  public boolean needsVerticalAligning() {
    return !verticallyAligned;
  }

  @Override
  public boolean isPopped() {
    return isPopped;
  }

  @Override
  public void accept(NodeVisitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return String.format("%s:\n%s", "InDel", super.toString());
  }
}
