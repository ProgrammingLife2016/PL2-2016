package nl.tudelft.pl2016gr2.model.graph.nodes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IndelBubbleTest extends BubbleTest {

  private IndelBubble indelBubble;

  @Override
  public Bubble getBubbleInstance() {
    Collection<GraphNode> col = new ArrayList<GraphNode>();
    List<GraphNode> list = new ArrayList<GraphNode>();

    IndelBubble point = new IndelBubble(5, col, col, list, null);
    this.indelBubble = point;
    return indelBubble;
  }

  @Override
  public void testConstructorId() {
    GraphNode straight = indelBubble.copy();
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

  @Test
  public void toStringTest() {
    assertEquals("InDel:\n", indelBubble.toString());
  }
  
  public void setPoppedEdgesTest() {
    
  }
  
  public void setUnPoppedEdgesTest() {
    
  }

}
