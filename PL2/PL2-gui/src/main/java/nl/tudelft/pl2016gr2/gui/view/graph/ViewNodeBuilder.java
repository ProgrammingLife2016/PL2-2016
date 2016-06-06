package nl.tudelft.pl2016gr2.gui.view.graph;

import static nl.tudelft.pl2016gr2.gui.view.graph.DrawComparedGraphs.NO_OVERLAP_COLOR;
import static nl.tudelft.pl2016gr2.gui.view.graph.DrawComparedGraphs.OVERLAP_COLOR;

import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IndelBubble;
import nl.tudelft.pl2016gr2.model.PhyloBubble;
import nl.tudelft.pl2016gr2.model.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.SequenceNode;
import nl.tudelft.pl2016gr2.model.StraightSequenceBubble;
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
  private final int nestedDepth;
  private IViewGraphNode viewNode;

  /**
   * Construct a view node builder. This constructor is private, because only the static
   * {@code buildNode} method may create an instance of this class.
   *
   * @param width       the width of the node.
   * @param height      the height of the node.
   * @param nestedDepth the amount of times this node is nested in a bubble.
   */
  private ViewNodeBuilder(double width, double height, int nestedDepth) {
    this.width = width;
    this.height = height;
    this.nestedDepth = nestedDepth;
  }

  /**
   * Build the visual representation of this node.
   *
   * @param node        the node of which to create a visual representation.
   * @param width       the width of the node.
   * @param height      the height of the node.
   * @param nestedDepth the amount of times this node is nested in a bubble.
   * @return the visual representation of the node.
   */
  public static IViewGraphNode buildNode(GraphNode node, double width, double height,
      int nestedDepth) {
    ViewNodeBuilder builder = new ViewNodeBuilder(width, height, nestedDepth);
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
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height);
    Color fill = Color.ALICEBLUE;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    rect.setFill(fill);
    viewNode = rect;
  }

  @Override
  public void visit(StraightSequenceBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height);
    Color fill = Color.LIGHTCORAL;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    rect.setFill(fill);
    viewNode = rect;
  }

  @Override
  public void visit(IndelBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height);
    Color fill = Color.LIGHTSKYBLUE;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    rect.setFill(fill);
    viewNode = rect;
  }

  @Override
  public void visit(PointMutationBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height);
    Color fill = Color.PLUM;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    rect.setFill(fill);
    viewNode = rect;
  }

  @Override
  public void visit(SequenceNode node) {
    ViewGraphNodeEllipse circle = new ViewGraphNodeEllipse(width, height);
    if (node.isOverlapping()) {
      circle.setFill(OVERLAP_COLOR);
    } else {
      circle.setFill(NO_OVERLAP_COLOR);
    }
    viewNode = circle;
  }
}
