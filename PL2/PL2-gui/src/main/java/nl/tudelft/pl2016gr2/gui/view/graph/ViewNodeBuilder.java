package nl.tudelft.pl2016gr2.gui.view.graph;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.StraightSequenceBubble;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

/**
 * Constructs a node based on the kind of the node. For example a regular {@link SequenceNode} will
 * have a different visual representations than an {@link PhyloBubble}.
 *
 * @author Faris
 */
public class ViewNodeBuilder implements NodeVisitor {

  private final double width;
  private final double height;
  private IViewGraphNode viewNode;

  /**
   * Construct a view node builder. This constructor is private, because only the static
   * {@code buildNode} method may create an instance of this class.
   *
   * @param width       the width of the node.
   * @param height      the height of the node.
   */
  private ViewNodeBuilder(double width, double height) {
    this.width = width;
    this.height = height;
  }

  /**
   * Build the visual representation of this node.
   *
   * @param node        the node of which to create a visual representation.
   * @param width       the width of the node.
   * @param height      the height of the node.
   * @return the visual representation of the node.
   */
  public static IViewGraphNode buildNode(GraphNode node, double width, double height) {
    ViewNodeBuilder builder = new ViewNodeBuilder(width, height);
    node.accept(builder);
    return builder.viewNode;
  }

  @Override
  public void visit(GraphNode node) {
    throw new UnsupportedOperationException("Unknown node, this node doesn't have a graphical"
        + "representation.");
  }

  @Override
  public void visit(PhyloBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height, bubble);
    rect.getStyleClass().addAll("graphBubblePhylo", "graphUnselectedNode");
    viewNode = rect;
  }

  @Override
  public void visit(StraightSequenceBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height, bubble);
    rect.getStyleClass().addAll("graphBubbleStraight", "graphUnselectedNode");
    viewNode = rect;
  }

  @Override
  public void visit(IndelBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height, bubble);
    rect.getStyleClass().addAll("graphBubbleIndel", "graphUnselectedNode");
    viewNode = rect;
  }

  @Override
  public void visit(PointMutationBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height, bubble);
    rect.getStyleClass().addAll("graphBubblePoint", "graphUnselectedNode");
    viewNode = rect;
  }

  @Override
  public void visit(SequenceNode node) {
    ViewGraphNodeEllipse circle = new ViewGraphNodeEllipse(width, height, node);
    if (node.getGuiData().overlapping) {
      circle.getStyleClass().addAll("graphNodeOverlap", "graphUnselectedNode");
    } else {
      circle.getStyleClass().addAll("graphNodeNoOverlap", "graphUnselectedNode");
    }
    viewNode = circle;
  }
}
