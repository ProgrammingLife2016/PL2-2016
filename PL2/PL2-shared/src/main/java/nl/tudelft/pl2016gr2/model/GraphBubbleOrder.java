package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

public class GraphBubbleOrder extends NodePosition {
  
  private ArrayList<GraphNode> nodes;

  /**
   * Create an object used to display bubbles.
   * @param node the first node in the bubble
   * @param level The original level of the bubble.
   */
  public GraphBubbleOrder(GraphNode node, int level) {
    super(node, level);
    nodes = new ArrayList<GraphNode>();
    nodes.add(node);
  }
  
  public void addNode(GraphNode node) {
    nodes.add(node);
  }  
  
  public int size() {
    return nodes.size();
  }
  
  public ArrayList<GraphNode> getNodes() {
    return this.nodes;
  }

}
