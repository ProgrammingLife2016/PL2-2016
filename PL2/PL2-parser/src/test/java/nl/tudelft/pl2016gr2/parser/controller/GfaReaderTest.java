package nl.tudelft.pl2016gr2.parser.controller;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.OriginalGraph;
import org.junit.Test;

/**
 * This class tests the {@link GfaReader} class.
 *
 * @author Cas
 */
public class GfaReaderTest {

  private static final String filename = "SMALL.gfa";

  @Test
  public void integrationTest() {
    GfaReader reader = new GfaReader(filename);
    OriginalGraph og = reader.read();
    assertEquals(og.getGenoms().size(), 11);
    assertEquals(og.getNode(3).getGenomes().size(), 1);
  }

  /**
   * Test of read method, of class GfaReader.
   */
  @Test
  public void testRead() {
  }

}
