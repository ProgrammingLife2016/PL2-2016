package nl.tudelft.pl2016gr2.gui.view.tree;

/**
 * This class can be used to store an area of for a part of the user interface. An area consists out
 * of a pair of start coordinates: (startX, startY) and a pair of end coordinates: (endX, endY).
 *
 * @author Faris
 */
public class Area {

  private final double startX;
  private final double endX;
  private final double startY;
  private final double endY;

  /**
   * Create a graph area.
   *
   * @param startX the x coordinate of the left border of the graph area.
   * @param endX   the x coordinate of the right border of the graph area.
   * @param startY the y coordinate of the top border of the graph area.
   * @param endY   the y coordinate of the bottom border of the graph area.
   */
  public Area(double startX, double endX, double startY, double endY) {
    this.startX = startX;
    this.endX = endX;
    this.startY = startY;
    this.endY = endY;
  }

  /**
   * Get the start position of the x coordinate.
   *
   * @return the start position of the x coordinate.
   */
  public double getStartX() {
    return startX;
  }

  /**
   * Get the end position of the x coordinate.
   *
   * @return the end position of the x coordinate.
   */
  public double getEndX() {
    return endX;
  }

  /**
   * Get the start position of the y coordinate.
   *
   * @return the start position of the y coordinate.
   */
  public double getStartY() {
    return startY;
  }

  /**
   * Get the end position of the y coordinate.
   *
   * @return the end position of the y coordinate.
   */
  public double getEndY() {
    return endY;
  }

  /**
   * Get the width of the graph area.
   *
   * @return the width of the graph area.
   */
  public double getWidth() {
    return endX - startX;
  }

  /**
   * Get the height of the graph area.
   *
   * @return the height of the graph area.
   */
  public double getHeight() {
    return endY - startY;
  }

  /**
   * Get the center x coordinate of the graph area.
   *
   * @return the center x coordinate of the graph area.
   */
  public double getCenterX() {
    return (endX - startX) / 2.0 + startX;
  }

  /**
   * Get the center y coordinate of the graph area.
   *
   * @return the center y coordinate of the graph area.
   */
  public double getCenterY() {
    return (endY - startY) / 2.0 + startY;
  }

  /**
   * Check if the given coordinates are within the graph area.
   *
   * @param xCoord the x coordinate.
   * @param yCoord the y coordinate.
   * @return if the given coordinates are within the graph area.
   */
  public boolean contains(double xCoord, double yCoord) {
    return xCoord >= startX && xCoord <= endX
        && yCoord >= startY && yCoord <= endY;
  }
}
