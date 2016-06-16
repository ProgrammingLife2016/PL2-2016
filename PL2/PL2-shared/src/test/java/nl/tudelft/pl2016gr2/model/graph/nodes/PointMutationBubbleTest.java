package nl.tudelft.pl2016gr2.model.graph.nodes;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PointMutationBubbleTest extends BubbleTest {

  private PointMutationBubble pointBubble;

  @Override
  public Bubble getBubbleInstance() {
    Collection<GraphNode> col = new ArrayList<GraphNode>();
    List<GraphNode> list = new ArrayList<GraphNode>();

    PointMutationBubble point = new PointMutationBubble(5, col, col, list, null);
    this.pointBubble = point;
    return pointBubble;
  }

  @Override
  public void testConstructorId() {
    GraphNode straight = pointBubble.copy();
    assertEquals(5, straight.getId());
  }
  

  @Override
  public void testConstructorIdInOut() {
    // Not needed.
  }

  @Override
  public void testConstructorIdInOutNested() {
    // Not needed.
  }

  @Override
  public void testConstructorBubble() {
    // Not needed.
  }

  @Override
  public void toStringTest() {
    assertEquals("PointMutation:\n", pointBubble.toString());
  }
  
  public void setPoppedEdgesTest() {
    
  }
  
  public void setUnPoppedEdgesTest() {
    
  }

}
