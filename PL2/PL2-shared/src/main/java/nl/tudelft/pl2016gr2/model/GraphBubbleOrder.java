package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

public class GraphBubbleOrder extends GraphNodeOrder {
  
  private ArrayList<AbstractNode> nodes;

  /**
   * Create an object used to display bubbles.
   * @param node the first node in the bubble
   * @param level The original level of the bubble.
   */
  public GraphBubbleOrder(AbstractNode node, int level) {
    super(node, level);
    nodes = new ArrayList<AbstractNode>();
    nodes.add(node);
  }
  
  public void addNode(AbstractNode node) {
    nodes.add(node);
  }  
  
  public int size() {
    return nodes.size();
  }
  
  public ArrayList<AbstractNode> getNodes() {
    return this.nodes;
  }

}
