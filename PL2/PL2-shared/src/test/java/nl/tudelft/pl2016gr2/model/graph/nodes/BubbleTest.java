package nl.tudelft.pl2016gr2.model.graph.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

public abstract class BubbleTest {
  
  private Bubble bubble;
  
  @Before
  public void setup() {
    this.bubble = getBubbleInstance();
  }
  
  public abstract Bubble getBubbleInstance();
  
  @Test
  public abstract void testConstructorId();
  
  @Test
  public abstract void testConstructorIdInOut();
  
  @Test
  public abstract void testConstructorIdInOutNested();
  
  @Test
  public abstract void testConstructorBubble();
  
  @Test
  public void testAddChild() {
    GraphNode child = new SequenceNode(5);
    bubble.addChild(child);
    assertTrue(bubble.getChildren().contains(child));
    assertTrue(bubble.hasChildren());
  }
  
  @Test
  public void testChildren() {
    GraphNode child = new SequenceNode(3);
    bubble.addChild(child);
    assertTrue(bubble.hasChild(child));
  }
  
  @Test
  public void sizeTest() {
    assertEquals(-1, bubble.size());
  }
  
  @Test
  public void getGenomesTest() {
    Collection<Integer> genomes = bubble.getGenomes();
    assertEquals(0, genomes.size());
  }
  
  @Test (expected = UnsupportedOperationException.class)
  public void addGenomeTest() {
    bubble.addGenome(3);
  }
  
  @Test (expected = UnsupportedOperationException.class)
  public void removeGenomeTest() {
    bubble.removeGenome(3);
  }
  
  @Test (expected = UnsupportedOperationException.class)
  public void addAllGenomeTest() {
    bubble.addAllGenomes(Arrays.asList(3));
  }
  
  @Test (expected = UnsupportedOperationException.class)
  public void setLevelTest() {
    bubble.setLevel(3);
  }
  
  @Test
  public void getLevelTest() {
    assertEquals(-1, bubble.getLevel());
  }
  
  public abstract void toStringTest();


}