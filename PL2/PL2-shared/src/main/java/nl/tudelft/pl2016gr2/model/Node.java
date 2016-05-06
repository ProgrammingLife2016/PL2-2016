package nl.tudelft.pl2016gr2.model;

/**
 * Mainly a dataholder class which represents a Node.
 *
 * @author Cas
 *
 */
public class Node extends Bubble {

  private double flow;

  /**
   * Construct a new node.
   *
   * @param id the id (index) of the node.
   */
  public Node(int id) {
    super(id);
    this.flow = 0;
    setLevel(0);
  }

  @Override
  public String toString() {
    return getId() + " flow: " + this.flow;
  }

  public double getFlow() {
    return flow;
  }

  public void setFlow(double flow) {
    this.flow = flow;
  }

  public void addFlow(double flow) {
    this.flow += flow;
  }

}
