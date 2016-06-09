package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
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
      Collection<GraphNode> outEdges, List<GraphNode> nestedNodes, IVerticalAligner aligner) {
    super(id, inEdges, outEdges, nestedNodes);
    this.aligner = aligner;
  }

  private StraightSequenceBubble(Bubble bubble, IVerticalAligner aligner) {
    super(bubble);
    this.aligner = aligner;
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
  public Collection<Integer> getGenomes() {
    HashSet<Integer> genomeSet = new HashSet<>();
    for (GraphNode inEdge : getInEdges()) {
      genomeSet.addAll(inEdge.getGenomes());
    }
    return genomeSet;
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
      setUnpoppedEdges();
      isPopped = false;
    }
  }

  /**
   * Remove the edges to this bubble and set the edges to the nested nodes.
   */
  private void setPoppedEdges() {
    Iterator<GraphNode> it = getChildren().iterator();
    GraphNode firstNode = it.next();
    firstNode.setInEdges(getInEdges());

    for (GraphNode inEdge : getInEdges()) {
      inEdge.removeOutEdge(this);
      inEdge.addOutEdge(firstNode);
    }
    GraphNode lastNode = firstNode;
    while (it.hasNext()) {
      lastNode = it.next();
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
    Iterator<GraphNode> it = getChildren().iterator();
    GraphNode firstNode = it.next();
    for (GraphNode inEdge : firstNode.getInEdges()) {
      inEdge.removeOutEdge(firstNode);
      inEdge.addOutEdge(this);
    }
    GraphNode lastNode = firstNode;
    while (it.hasNext()) {
      lastNode = it.next();
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
