package nl.tudelft.pl2016gr2.gui.view.graph.heatmap;

import static nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController.HALF_HEATMAP_HEIGHT;
import static nl.tudelft.pl2016gr2.gui.view.graph.heatmap.IHeatmapColorer.DEFAULT_COLOR;

import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.StraightSequenceBubble;
import nl.tudelft.pl2016gr2.visitor.NodeVisitor;

/**
 * This is the general heatmap indication super class.
 *
 * @author Faris
 */
public abstract class AbstractHeatmap implements IHeatmapColorer, NodeVisitor {

  private final GraphicsContext heatmapGraphics;
  private final ObservableDoubleValue zoomFactor;

  private Color color = DEFAULT_COLOR;
  private double startLevel;

  /**
   * Construct an abstract heatmap.
   *
   * @param heatmapGraphics the canvas to draw the heatmap op.
   * @param zoomFactor      the zoom factor.
   */
  public AbstractHeatmap(GraphicsContext heatmapGraphics, ObservableDoubleValue zoomFactor) {
    this.heatmapGraphics = heatmapGraphics;
    this.zoomFactor = zoomFactor;
  }

  /**
   * Get the width multipler of this heatmap. All of the heatmap indication rectangles will be
   * multiplied by this value.
   *
   * @return the width multipler.
   */
  protected abstract double getWidthMultiplier();

  @Override
  public void drawHeatmap(GraphNode node, double startLevel) {
    this.startLevel = startLevel;
    node.accept(this);

    if (color.equals(DEFAULT_COLOR)) {
      return;
    }
    double nodeWidth = GraphPaneController.calculateNodeWidth(node, zoomFactor.get())
        * getWidthMultiplier();
    double endX = zoomFactor.get() * (node.getLevel() - startLevel - node.size() / 2.0)
        + nodeWidth / 2.0;
    double startX = endX - nodeWidth;
    heatmapGraphics.setFill(color);
    heatmapGraphics.fillRect(startX, 0, endX - startX, HALF_HEATMAP_HEIGHT * 2.0);

    color = DEFAULT_COLOR;
  }

  @Override
  public void visit(GraphNode node) {
  }

  @Override
  public void visit(PhyloBubble bubble) {
    colorChildren(bubble);
  }

  @Override
  public void visit(PointMutationBubble bubble) {
    colorChildren(bubble);
  }

  @Override
  public void visit(StraightSequenceBubble bubble) {
    colorChildren(bubble);
  }

  @Override
  public void visit(IndelBubble bubble) {
    colorChildren(bubble);
  }

  @Override
  public void visit(GraphBubble bubble) {
    colorChildren(bubble);
  }

  @Override
  public void visit(SequenceNode node) {
  }

  /**
   * Color the children of this bubble.
   *
   * @param bubble the bubble.
   */
  private void colorChildren(Bubble bubble) {
    if (!bubble.isPopped()) {
      for (GraphNode child : bubble.getChildren()) {
        drawHeatmap(child, startLevel);
      }
    }
  }

  protected void setColor(Color color) {
    this.color = color;
  }
}
