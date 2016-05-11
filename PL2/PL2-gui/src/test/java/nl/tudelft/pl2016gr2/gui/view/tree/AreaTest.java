package nl.tudelft.pl2016gr2.gui.view.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the {@link Area} class.
 *
 * @author Faris
 */
public class AreaTest {

  private static final double DELTA = 0.0001;
  private Area area;

  @Before
  public void initialize() {
    area = new Area(100, 200, 0, 50);
  }

  /**
   * Test of getStartX method, of class Area.
   */
  @Test
  public void testGetStartX() {
    assertEquals(100, area.getStartX(), DELTA);
  }

  /**
   * Test of getEndX method, of class Area.
   */
  @Test
  public void testGetEndX() {
    assertEquals(200, area.getEndX(), DELTA);
  }

  /**
   * Test of getStartY method, of class Area.
   */
  @Test
  public void testGetStartY() {
    assertEquals(0, area.getStartY(), DELTA);
  }

  /**
   * Test of getEndY method, of class Area.
   */
  @Test
  public void testGetEndY() {
    assertEquals(50, area.getEndY(), DELTA);
  }

  /**
   * Test of getWidth method, of class Area.
   */
  @Test
  public void testGetWidth() {
    assertEquals(100, area.getWidth(), DELTA);
  }

  /**
   * Test of getHeight method, of class Area.
   */
  @Test
  public void testGetHeight() {
    assertEquals(50, area.getHeight(), DELTA);
  }

  /**
   * Test of getCenterX method, of class Area.
   */
  @Test
  public void testGetCenterX() {
    assertEquals(150, area.getCenterX(), DELTA);
  }

  /**
   * Test of getCenterY method, of class Area.
   */
  @Test
  public void testGetCenterY() {
    assertEquals(25, area.getCenterY(), DELTA);
  }

  /**
   * Test of contains method, of class Area.
   */
  @Test
  public void testContains() {
    assertTrue(area.contains(150, 25));
  }

  /**
   * Test of contains method, of class Area.
   */
  @Test
  public void testContainsFalse() {
    assertFalse(area.contains(100 - DELTA, 25));
    assertFalse(area.contains(150, 50 + DELTA));
  }

  /**
   * Test of contains method, of class Area. Tests the boundary cases for the x coordinate.
   */
  @Test
  public void testContainsBoundaryX() {
    assertTrue(area.contains(100 + DELTA, 25));
    assertTrue(area.contains(200 - DELTA, 25));
  }

  /**
   * Test of contains method, of class Area. Tests the boundary cases for the y coordinate.
   */
  @Test
  public void testContainsBoundaryY() {
    assertTrue(area.contains(150, 0 + DELTA));
    assertTrue(area.contains(150, 50 - DELTA));
  }

}
