package nl.tudelft.pl2016gr2.model;

public class BubbledGraphTest extends GraphTest {
  
  @Override
  public GraphInterface getInstance() {
    Bubble b2 = new Bubble(2, 4);
    Bubble b1 = new Bubble(5, 8);
    GraphInterface graph = new BubbledGraph();
    graph.addNode(b2);
    graph.addNode(b1);
    return graph;
  }

}
