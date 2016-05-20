package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class NodePositionTest {
  
  private NodePosition nodePosition;
  private AbstractNode node;

  @Before
  public void setup() {
    this.node = new Node(5, 3, new ArrayList<String>(), 2);
    this.nodePosition = new NodePosition(node, 6);    
  }
  
  @Test
  public void offsetTest() {
    nodePosition.addPositionOffset(4);
    assertEquals(10, nodePosition.getLevel());
  }
  
  @Test
  public void getNodeTest() {
    assertEquals(node, nodePosition.getNode());
  }
  
  @Test
  public void overlapTest() {
    nodePosition.setOverlapping(true);
    assertTrue(nodePosition.isOverlapping());
  }
  
  @Test
  public void compareTest() {
    AbstractNode other = new Node(5, 3, new ArrayList<String>(), 2);
    NodePosition otherPosition = new NodePosition(other, 2); 
    assertEquals(4, nodePosition.compareTo(otherPosition));
  }

}
