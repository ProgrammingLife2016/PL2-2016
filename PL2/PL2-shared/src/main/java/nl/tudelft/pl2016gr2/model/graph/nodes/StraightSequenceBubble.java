package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * This kind of bubble contains a straight sequence of nodes.
 *
 * @author Faris
 */
public class StraightSequenceBubble extends Bubble {

  private final IVerticalAligner aligner;
  private final GraphNode firstNode;
  private final GraphNode lastNode;
  private boolean isPopped;
  private boolean verticallyAligned;

  /**
   * Construct a sequence bubble.
   *
   * @param id          .
   * @param inEdges     .
   * @param outEdges    .
   * @param nestedNodes .
   * @param aligner     .
   * @param firstNode   .
   * @param lastNode    .
   */
  public StraightSequenceBubble(int id, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, HashSet<GraphNode> nestedNodes, IVerticalAligner aligner,
      GraphNode firstNode, GraphNode lastNode) {
    super(id, inEdges, outEdges, nestedNodes);
    this.aligner = aligner;
    this.firstNode = firstNode;
    this.lastNode = lastNode;
  }

  private StraightSequenceBubble(StraightSequenceBubble bubble, IVerticalAligner aligner) {
    super(bubble);
    this.aligner = aligner;
    this.firstNode = bubble.firstNode;
    this.lastNode = bubble.lastNode;
  }

  @Override
  public int getGenomeSize() {
    return firstNode.getGenomeSize();
  }

  @Override
  public GraphNode copy() {
    return new StraightSequenceBubble(this, aligner);
  }

  @Override
  public GraphNode copyAll() {
    return new StraightSequenceBubble(this, aligner);
  }

  @Override
  public List<Integer> getGenomes() {
    List<Integer> genomes = new ArrayList<>();
    for (GraphNode inEdge : getInEdges()) {
      genomes.addAll(inEdge.getGenomes()); // only 1 in edge, so no duplicates
    }
    genomes.sort(null);
    return genomes;
  }

  @Override
  public boolean containsGenome(Integer genome) {
    for (GraphNode inEdge : getInEdges()) {
      if (inEdge.containsGenome(genome)) {
        return true;
      }
    }
    return false;
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
    firstNode.setInEdges(getInEdges());

    for (GraphNode inEdge : getInEdges()) {
      inEdge.removeOutEdge(this);
      inEdge.addOutEdge(firstNode);
    }
    lastNode.setOutEdges(getOutEdges());
    for (GraphNode outEdge : getOutEdges()) {
      outEdge.removeInEdge(this);
      outEdge.addInEdge(lastNode);
    }
  }

  /**
   * Remove the edges to the nodes in this bubble and set the edges to this bubble.
   */
  private void setUnpoppedEdges() {
    for (GraphNode inEdge : firstNode.getInEdges()) {
      inEdge.removeOutEdge(firstNode);
      inEdge.addOutEdge(this);
    }
    for (GraphNode outEdge : lastNode.getOutEdges()) {
      outEdge.removeInEdge(lastNode);
      outEdge.addInEdge(this);
    }
    setInEdges(firstNode.getInEdges());
    setOutEdges(lastNode.getOutEdges());
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
    return String.format("%s\n%s", "Straight sequence Bubble", super.toString());
  }
}
