package nl.tudelft.pl2016gr2.model.graph.data;

/**
 * This class stores the data of a node which is needed by the GUI.
 *
 * @author Faris
 */
public class GraphNodeGuiData {

  /**
   * If this node is overlapping with a node from the other graph.
   */
  private boolean overlapping;

  /**
   * The relative y-position of the node.
   */
  private double relativeYPos;
  private double maxHeight;

  public void setOverlapping(boolean overlapping) {
    this.overlapping = overlapping;
  }

  public boolean isOverlapping() {
    return overlapping;
  }

  public void setRelativeYPos(double relativeYPos) {
    this.relativeYPos = relativeYPos;
  }

  public double getRelativeYPos() {
    return relativeYPos;
  }

  public void setMaxHeight(double maxHeight) {
    this.maxHeight = maxHeight;
  }

  public double getMaxHeightPercentage() {
    return maxHeight;
  }
}
