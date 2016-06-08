package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;

/**
 * An ellipse representation of a node, which can be drawn in the user interface.
 *
 * @author Faris
 */
public class ViewGraphNodeEllipse extends Ellipse implements IViewGraphNode {

  private final ISelectionInfo selectionInfo;

  /**
   * Construct a node circle.
   *
   * @param width  the width of the ellipse.
   * @param height the height of the ellipse.
   */
  public ViewGraphNodeEllipse(double width, double height, ISelectionInfo selectionInfo) {
    super(width * DrawComparedGraphs.NODE_MARGIN / 2.0, height / 2.0);
    setStrokeWidth(height / 20.0d);
    this.selectionInfo = selectionInfo;
  }

  @Override
  public double getWidth() {
    return getRadiusX() * 2.0;
  }

  @Override
  public double getHeight() {
    return getRadiusY() * 2.0;
  }

  @Override
  public Node get() {
    return this;
  }

  @Override
  public void select() {
    setStroke(Color.BLACK);
  }

  @Override
  public void deselect() {
    setStroke(null);
  }

  @Override
  public ISelectionInfo getSelectionInfo(SelectionManager selectionManager) {
    return selectionInfo;
  }
}
