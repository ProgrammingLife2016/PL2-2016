package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public abstract class GraphTest {
  
  public GraphInterface gra;
  
  @Before
  public void setup() {
    gra = getInstance();
  }
  
  public abstract GraphInterface getInstance();

  @Test
  public void testSize() {
    assertEquals(gra.getSize(), 2);
  }
  
  @Test
  public void testGetNode() {
    assertEquals(gra.getNode(5).getSequenceLength(), 8);
  }
  
  @Test
  public void getRootTest() {
    assertEquals(gra.getRoot().getId(), 2);
  }

}
