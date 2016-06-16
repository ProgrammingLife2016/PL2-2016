package nl.tudelft.pl2016gr2.model.graph.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractGraphBubbleTest extends BubbleTest {
  
  private AbstractGraphBubble abstractBubble;
  
  @Before
  public void setup() {
    this.abstractBubble = getAbstractGraphBubbleInstance();
  }
  
  public abstract AbstractGraphBubble getAbstractGraphBubbleInstance();
  
  @Test
  public abstract void constructorIdTest();
  
  @Test
  public abstract void constructorIdEdgesTest();

  @Test
  public abstract void constructorIdEdgesNestedTest();

  @Test
  public abstract void constructorBubbleTest();

  @Test
  public void getGenomeSizeTest() {
    assertEquals(0, abstractBubble.getGenomeSize());
  }
  
  @Test
  public void getFilterTest() {
    assertEquals(null, abstractBubble.getFilter());
  }
  
  @Test
  public void needsVerticalAligningTest() {
    assertTrue(abstractBubble.needsVerticalAligning());
  }
  
  @Test
  public void isPoppedTest() {
    assertFalse(abstractBubble.isPopped());
  }


}