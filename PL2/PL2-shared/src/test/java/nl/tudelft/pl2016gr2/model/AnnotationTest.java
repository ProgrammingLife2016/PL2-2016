package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import nl.tudelft.pl2016gr2.util.Pair;

import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the {@link Annotation} class.
 *
 * @author Faris
 */
public class AnnotationTest {

  private Annotation annotation;

  /**
   * Initialize the readResult.
   */
  @Before
  public void setup() {
    annotation = new Annotation("id", "source", "type", 1, 2, 0.1, "strand", "phase");
    annotation.addAttribute(new Pair<String, String>("a", "1"));
    annotation.addAttribute(new Pair<String, String>("bb", "22"));
  }

  /**
   * Test of the constructor, of class Annotation.
   */
  @Test
  public void testConstructorId() {
    assertEquals("id", annotation.sequenceId);
  }

  /**
   * Test of the constructor, of class Annotation.
   */
  @Test
  public void testConstructorSource() {
    assertEquals("source", annotation.source);
  }

  /**
   * Test of the constructor, of class Annotation.
   */
  @Test
  public void testConstructorType() {
    assertEquals("type", annotation.type);
  }

  /**
   * Test of the constructor, of class Annotation.
   */
  @Test
  public void testConstructorStart() {
    assertEquals(1, annotation.start);
  }

  /**
   * Test of the constructor, of class Annotation.
   */
  @Test
  public void testConstructorEnd() {
    assertEquals(2, annotation.end);
  }

  /**
   * Test of the constructor, of class Annotation.
   */
  @Test
  public void testConstructorScore() {
    assertEquals(0.1, annotation.score, 0.001);
  }

  /**
   * Test of the constructor, of class Annotation.
   */
  @Test
  public void testConstructorStrand() {
    assertEquals("strand", annotation.strand);
  }

  /**
   * Test of the constructor, of class Annotation.
   */
  @Test
  public void testConstructorPhase() {
    assertEquals("phase", annotation.phase);
  }

  /**
   * Test of getAttribute method, of class Annotation.
   */
  @Test
  public void testGetAttribute() {
    assertEquals("1", annotation.getAttribute("a"));
    assertEquals("1", annotation.getAttribute("A"));
    assertEquals("22", annotation.getAttribute("bb"));
  }

  /**
   * Test of containsAttribute method, of class Annotation.
   */
  @Test
  public void testContainsAttribute() {
    assertTrue(annotation.containsAttribute("a"));
    assertTrue(annotation.containsAttribute("A"));
    assertTrue(annotation.containsAttribute("bB"));
  }
  
  @Test
  public void getAttributesTest() {
    assertEquals(2, annotation.getAttributes().size());
    assertTrue(annotation.getAttributes().containsKey("a"));
  }
}
