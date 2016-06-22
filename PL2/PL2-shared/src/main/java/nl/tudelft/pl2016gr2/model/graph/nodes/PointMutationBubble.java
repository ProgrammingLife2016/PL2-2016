package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * This kind of bubble contains a point mutation.
 *
 * @author Faris
 */
public class PointMutationBubble extends Bubble {

  private final IVerticalAligner aligner;
  private boolean isPopped;
  private boolean verticallyAligned;

  public PointMutationBubble(int id, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges, HashSet<GraphNode> nestedNodes, IVerticalAligner aligner) {
    super(id, inEdges, outEdges, nestedNodes);
    this.aligner = aligner;
  }

  private PointMutationBubble(Bubble bubble, IVerticalAligner aligner) {
    super(bubble);
    this.aligner = aligner;
  }

  @Override
  public int getGenomeSize() {
    int count = 0;
    for (GraphNode graphNode : getChildren()) {
      count += graphNode.getGenomeSize();
    }
    return count;
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
  public GraphNode copy() {
    return new PointMutationBubble(this, aligner);
  }

  @Override
  public GraphNode copyAll() {
    return new PointMutationBubble(this, aligner);
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
    GraphNode firstChild = it.next();
    GraphNode secondChild = it.next();

    GraphNode inNode = getInEdges().iterator().next();
    inNode.removeOutEdge(this);
    inNode.addOutEdge(firstChild);
    inNode.addOutEdge(secondChild);

    GraphNode outNode = getOutEdges().iterator().next();
    outNode.removeInEdge(this);
    outNode.addInEdge(firstChild);
    outNode.addInEdge(secondChild);

    firstChild.setInEdges(getInEdges());
    firstChild.setOutEdges(getOutEdges());
    secondChild.setInEdges(getInEdges());
    secondChild.setOutEdges(getOutEdges());
  }

  /**
   * Remove the edges to the nodes in this bubble and set the edges to this bubble.
   */
  private void setUnpoppedEdges() {
    Iterator<GraphNode> it = getChildren().iterator();
    GraphNode firstChild = it.next();
    GraphNode secondChild = it.next();

    GraphNode inNode = getInEdges().iterator().next();
    inNode.removeOutEdge(firstChild);
    inNode.removeOutEdge(secondChild);
    inNode.addOutEdge(this);

    GraphNode outNode = getOutEdges().iterator().next();
    outNode.removeInEdge(firstChild);
    outNode.removeInEdge(secondChild);
    outNode.addInEdge(this);

    setInEdges(firstChild.getInEdges());
    setOutEdges(firstChild.getOutEdges());
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
    return String.format("%s:\n%s", "PointMutation", super.toString());
  }
}
