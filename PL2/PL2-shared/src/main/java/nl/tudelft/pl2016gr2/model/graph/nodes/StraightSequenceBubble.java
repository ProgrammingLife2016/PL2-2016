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
  private boolean isPopped;
  private boolean verticallyAligned;

  public StraightSequenceBubble(int id, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, HashSet<GraphNode> nestedNodes, IVerticalAligner aligner) {
    super(id, inEdges, outEdges, nestedNodes);
    this.aligner = aligner;
  }

  private StraightSequenceBubble(StraightSequenceBubble bubble, IVerticalAligner aligner) {
    super(bubble);
    this.aligner = aligner;
  }

  @Override
  public int getGenomeSize() {
    return getFirstNode().getGenomeSize();
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
    if (isPopped) {
      for (GraphNode child : getChildren()) {
        child.unpop();
      }
      setUnpoppedEdges();
      isPopped = false;
    }
  }

  /**
   * Remove the edges to this bubble and set the edges to the nested nodes.
   */
  private void setPoppedEdges() {
    GraphNode firstNode = getFirstNode();
    firstNode.setInEdges(getInEdges());

    for (GraphNode inEdge : getInEdges()) {
      inEdge.removeOutEdge(this);
      inEdge.addOutEdge(firstNode);
    }
    GraphNode lastNode = getLastNode();
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
    GraphNode firstNode = getFirstNode();
    GraphNode lastNode = getLastNode();
    
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
