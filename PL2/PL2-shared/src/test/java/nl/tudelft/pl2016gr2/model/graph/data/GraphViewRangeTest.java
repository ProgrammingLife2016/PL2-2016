
package nl.tudelft.pl2016gr2.model.graph.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * This class tests the {@link GraphViewRange} class.
 *
 * @author Faris
 */
public class GraphViewRangeTest {

  @Test
  public void testConstructor() {
    GraphViewRange range = new GraphViewRange(5.0, 6.0);
    assertEquals(5.0, range.rangeStartY, 0.001);
    assertEquals(6.0, range.rangeHeight, 0.001);
  }

}