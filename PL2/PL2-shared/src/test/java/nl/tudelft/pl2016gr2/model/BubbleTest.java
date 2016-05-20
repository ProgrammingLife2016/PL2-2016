package nl.tudelft.pl2016gr2.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BubbleTest {

  @Test
  public void testConstructor() {
    Bubble bubble = new Bubble(123, 5);
    assertEquals(123, bubble.getId());
    assertEquals(5, bubble.getSequenceLength());
  }

  @Test
  public void testLevel() {
    Bubble bubble = new Bubble(123, 5);
    bubble.setLevel(12);

    assertEquals(12, bubble.getLevel());
  }

  @Test
  public void testStartBubble() {
    Bubble bubble = new Bubble(123, 5);
    Bubble startBubble = new Bubble(321, 3);
    bubble.setStartBubble(startBubble);

    assertEquals(startBubble, bubble.getStartBubble());
  }

  @Test
  public void testEndBubble() {
    Bubble bubble = new Bubble(123, 5);
    Bubble endBubble = new Bubble(321, 3);
    bubble.setEndBubble(endBubble);

    assertEquals(endBubble, bubble.getEndBubble());
  }

  @Test
  public void testNestedBubbles() {
    Bubble bubble = new Bubble(123, 5);
    List<Integer> nestedBubbleIds = new ArrayList<>();
    nestedBubbleIds.add(1);
    nestedBubbleIds.add(2);
    nestedBubbleIds.add(3);
    nestedBubbleIds.add(4);
    nestedBubbleIds.add(5);
    nestedBubbleIds.add(6);
    nestedBubbleIds.forEach(bubble::addNestedBubble);

    assertEquals(nestedBubbleIds, bubble.getNestedBubbles());
  }

  @Test
  public void testEquals1() {
    Bubble bubble1 = new Bubble(123, 5);
    Bubble bubble2 = new Bubble(123, 5);

    assertTrue(bubble1.equals(bubble2));
  }

  @Test
  public void testEquals2() {
    Bubble bubble1 = new Bubble(123, 5);
    Bubble bubble2 = new Bubble(122, 7);

    assertFalse(bubble1.equals(bubble2));
  }

  @Test
  public void testEquals3() {
    Bubble bubble = new Bubble(123, 5);
    Object obj = new Object();

    assertFalse(bubble.equals(obj));
  }

  @Test
  public void testHashCode() {
    Bubble bubble1 = new Bubble(123, 5);
    Bubble bubble2 = new Bubble(123, 5);

    assertEquals(bubble1.hashCode(), bubble2.hashCode());

  }

  /**
   * Code that this test tests, is not implemented!!
   * Dont forget to amend the test, this test SHOULD fail when
   * the this feature is implemented.
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testGetGenomesOverEdge() {
    Bubble bubble = new Bubble(123, 5);

    bubble.getGenomesOverEdge(new Bubble(1, 2));
  }

}