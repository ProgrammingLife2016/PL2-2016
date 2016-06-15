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
  public boolean overlapping;

  /**
   * The relative y-position of the node.
   */
  public double relativeYPos;
  public double maxHeight;
  
  public double startY;
  public double height;
  
  public GraphViewRange range;
}
