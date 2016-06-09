package nl.tudelft.pl2016gr2.gui.view.graph;

import static nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController.NO_OVERLAP_COLOR;
import static nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController.OVERLAP_COLOR;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.gui.view.selection.GraphBubbleDescription;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
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
  private final int nestedDepth;
  private final SelectionManager selectionManager;
  private IViewGraphNode viewNode;

  /**
   * Construct a view node builder. This constructor is private, because only the static
   * {@code buildNode} method may create an instance of this class.
   *
   * @param width       the width of the node.
   * @param height      the height of the node.
   * @param nestedDepth the amount of times this node is nested in a bubble.
   * @param selectionManager The selection manager, used to select things.
   */
  private ViewNodeBuilder(double width, double height, int nestedDepth,
                          SelectionManager selectionManager) {
    this.width = width;
    this.height = height;
    this.nestedDepth = nestedDepth;
    this.selectionManager = selectionManager;
  }

  /**
   * Build the visual representation of this node.
   *
   * @param node        the node of which to create a visual representation.
   * @param width       the width of the node.
   * @param height      the height of the node.
   * @param nestedDepth the amount of times this node is nested in a bubble.
   * @param selectionManager The selection manager. Needed to select things.
   * @return the visual representation of the node.
   */
  public static IViewGraphNode buildNode(GraphNode node, double width, double height,
      int nestedDepth, SelectionManager selectionManager) {
    ViewNodeBuilder builder = new ViewNodeBuilder(width, height, nestedDepth, selectionManager);
    node.accept(builder);

    // select when previously selected node is equal to this new one
    if (selectionManager.getSelectedGraphNodes().contains(node.getId())) {
      selectionManager.select(builder.viewNode);
    }

    return builder.viewNode;
  }

  /**
   * Builds a mouse click event handler that selects the graphnode for the given id.
   *
   * @param id The id of the graph node to be selected.
   * @return The event handler.
   */
  private EventHandler<? super MouseEvent> buildOnMouseClickedHandler(int id) {
    return mouseEvent -> {
      selectionManager.getSelectedGraphNodes().clear();
      selectionManager.getSelectedGraphNodes().add(id);
      selectionManager.select(viewNode);
      mouseEvent.consume();
    };
  }

  @Override
  public void visit(GraphNode node) {
    throw new UnsupportedOperationException("Unknown node, this node doesn't have a graphical"
        + "representation.");
  }

  @Override
  public void visit(PhyloBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height,
        new GraphBubbleDescription() {
          @Override
          public String getText() {
            return bubble.toString();
          }
        }
    );
    Color fill = Color.ALICEBLUE;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    rect.setFill(fill);
    rect.setOnMouseClicked(buildOnMouseClickedHandler(bubble.getId()));
    viewNode = rect;
  }

  @Override
  public void visit(StraightSequenceBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height,
        new GraphBubbleDescription() {
          @Override
          public String getText() {
            return bubble.toString();
          }
        }
    );
    Color fill = Color.LIGHTCORAL;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    rect.setFill(fill);
    rect.setOnMouseClicked(buildOnMouseClickedHandler(bubble.getId()));
    viewNode = rect;
  }

  @Override
  public void visit(IndelBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height,
        new GraphBubbleDescription() {
          @Override
          public String getText() {
            return bubble.toString();
          }
        }
    );
    Color fill = Color.LIGHTSKYBLUE;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    rect.setFill(fill);
    rect.setOnMouseClicked(buildOnMouseClickedHandler(bubble.getId()));
    viewNode = rect;
  }

  @Override
  public void visit(PointMutationBubble bubble) {
    ViewGraphNodeRectangle rect = new ViewGraphNodeRectangle(width, height,
        new GraphBubbleDescription() {
          @Override
          public String getText() {
            return bubble.toString();
          }
        }
    );
    Color fill = Color.PLUM;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    rect.setFill(fill);
    rect.setOnMouseClicked(buildOnMouseClickedHandler(bubble.getId()));
    viewNode = rect;
  }

  @Override
  public void visit(SequenceNode node) {
    ViewGraphNodeEllipse circle = new ViewGraphNodeEllipse(width, height,
        new GraphBubbleDescription() {
          @Override
          public String getText() {
            return node.toString();
          }
        }
    );
    if (node.getGuiData().overlapping) {
      circle.setFill(OVERLAP_COLOR);
    } else {
      circle.setFill(NO_OVERLAP_COLOR);
    }
    circle.setOnMouseClicked(buildOnMouseClickedHandler(node.getId()));
    viewNode = circle;
  }
}
