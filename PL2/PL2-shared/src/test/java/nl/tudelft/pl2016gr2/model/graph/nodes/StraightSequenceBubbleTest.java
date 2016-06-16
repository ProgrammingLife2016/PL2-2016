package nl.tudelft.pl2016gr2.model.graph.nodes;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StraightSequenceBubbleTest extends BubbleTest {

  private StraightSequenceBubble straightBubble;

  @Override
  public Bubble getBubbleInstance() {
    Collection<GraphNode> col = new ArrayList<GraphNode>();
    List<GraphNode> list = new ArrayList<GraphNode>();

    StraightSequenceBubble straight = new StraightSequenceBubble(5, col, col, list, null);
    this.straightBubble = straight;
    return straight;
  }

  @Override
  public void testConstructorId() {
    GraphNode straight = straightBubble.copy();
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
    assertEquals("%s\n%s", "Straight sequence Bubble", straightBubble.toString());
  }
  
  public void setPoppedEdgesTest() {
    
  }
  
  public void setUnPoppedEdgesTest() {
    
  }

}
