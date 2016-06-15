package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.StraightSequenceBubble;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

/**
 * This class can be used to build {@link ISelectionInfo} for a {@link GraphNode}.
 *
 * <p>
 * The internal logic uses the visitor-pattern to provide a fitting result for each
 * given {@link GraphNode}.
 * </p>
 * <p>
 * Please see {@link #buildInfo(GraphNode)} when you want to use this class.
 * </p>
 *
 */
public class GraphBubbleDescriptionBuilder implements NodeVisitor {

  /**
   * Builds an {@link ISelectionInfo} object for a given {@link GraphNode}.
   *
   * <p>
   * This is actually a helper method for constructing the outer class and calling a method,
   * but since this is basically the only use-case of this class that is fine.
   * </p>
   *
   * @param node The {@link GraphNode} you want to get a {@link ISelectionInfo} for.
   * @return a fitting {@link GraphNode} for the <tt>node</tt>.
   */
  public static ISelectionInfo buildInfo(GraphNode node) {
    GraphBubbleDescriptionBuilder builder = new GraphBubbleDescriptionBuilder();
    node.accept(builder);
    return builder.selectionInfo;
  }

  private ISelectionInfo selectionInfo = Pane::new;

  private GraphBubbleDescriptionBuilder() {

  }

  @Override
  public void visit(GraphNode node) {
    throw new UnsupportedOperationException("Unknown node, this node doesn't have a graphical"
        + "representation of its selection.");
  }

  @Override
  public void visit(PhyloBubble bubble) {

  }

  @Override
  public void visit(StraightSequenceBubble bubble) {

  }

  @Override
  public void visit(IndelBubble bubble) {
    selectionInfo = () -> {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
          .getResource("pages/selection_descriptions/IndelBubbleDescription.fxml"));
      Node out = loader.load();
      loader.<IndelBubbleDescriptionController>getController().setup(bubble);
      return out;
    };
  }

  @Override
  public void visit(PointMutationBubble bubble) {
    selectionInfo = () -> {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
          .getResource("pages/selection_descriptions/PointMutationBubbleDescription.fxml"));
      Node out = loader.load();
      loader.<PointMutationBubbleDescriptionController>getController().setup(bubble);
      return out;
    };
  }

  @Override
  public void visit(SequenceNode node) {
    selectionInfo = () -> {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader()
          .getResource("pages/selection_descriptions/SequenceNodeDescription.fxml"));
      Node out = loader.load();
      loader.<SequenceNodeDescriptionController>getController().setup(node);
      return out;
    };
  }

}
