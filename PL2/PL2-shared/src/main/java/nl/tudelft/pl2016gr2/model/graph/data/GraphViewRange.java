package nl.tudelft.pl2016gr2.model.graph.data;

/**
 * This class stores a range. It contains a start y coordinate and height.
 *
 * @author Faris
 */
public class GraphViewRange {

  public final double rangeStartY;
  public final double rangeHeight;

  /**
   * Construct a range.
   *
   * @param rangeStartY the start value.
   * @param rangeHeight the height.
   */
  public GraphViewRange(double rangeStartY, double rangeHeight) {
    this.rangeStartY = rangeStartY;
    this.rangeHeight = rangeHeight;
  }
}
