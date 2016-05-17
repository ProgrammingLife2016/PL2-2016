package nl.tudelft.pl2016gr2.parser.controller;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Test;

import java.io.File;

/**
 * This class tests the {@link GfaReader} class.
 *
 * @author Cas
 */
public class GfaReaderTest {

  private static final String filename = "SMALL.gfa";

  @Test
  public void integrationTest() {
    GfaReader reader = new GfaReader(new File(filename));
    OriginalGraph og = reader.read();
    assertEquals(og.getGenoms().size(), 11);
    assertEquals(og.getNode(3).getGenomes().size(), 1);
  }

  /**
   * Test of read method, of class GfaReader.
   */
  @Test
  public void testRead() {
    GfaReader reader = new GfaReader(new File(filename));
    OriginalGraph actual = reader.read();
    OriginalGraph expected = AccessPrivate.getFieldValue("originalGraph", GfaReader.class, reader);
    assertEquals(expected, actual);
  }

}
