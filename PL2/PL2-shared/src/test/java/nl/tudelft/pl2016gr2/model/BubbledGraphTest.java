package nl.tudelft.pl2016gr2.model;

public class BubbledGraphTest extends GraphTest {
  
  /**
   * Setup, creates a new graph.
   */
  @Override
  public GraphInterface getInstance() {
    //ArrayList<Bubble> bubbles = new ArrayList<Bubble>();
    Bubble b2 = new Bubble(2, 4);
    Bubble b1 = new Bubble(5, 8);
    //bubbles.add(b1);
    //bubbles.add(b2);    
    GraphInterface gra = new BubbledGraph();
    gra.addNode(b2);
    gra.addNode(b1);
    return gra;
  }

}
