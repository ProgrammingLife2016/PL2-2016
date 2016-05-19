package nl.tudelft.pl2016gr2.parser.controller;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * This class tests the {@link GfaReader} class.
 *
 * @author Cas
 */
public class GfaReaderTest {

  private static final String filename = "SMALL.gfa";

  private File file;

  /**
   * Since the GfaReader uses files, we need to copy the resource to a file.
   *
   * @throws IOException When creation of tempfile fails.
   */
  @Before
  public void setup() throws IOException {
    file = File.createTempFile("GfaReaderTest", filename);

    FileUtils.copyInputStreamToFile(
        GfaReader.class.getClassLoader().getResourceAsStream(filename),
        file);

  }

  @After
  public void tearDown() {
    file.delete();
  }

  @Test
  public void integrationTest() {
    GfaReader reader = new GfaReader(file);
    OriginalGraph og = reader.read();
    assertEquals(og.getGenoms().size(), 11);
    assertEquals(og.getNode(3).getGenomes().size(), 1);
  }

  /**
   * Test of read method, of class GfaReader.
   */
  @Test
  public void testRead() {
    GfaReader reader = new GfaReader(file);
    OriginalGraph actual = reader.read();
    OriginalGraph expected = AccessPrivate.getFieldValue("originalGraph", GfaReader.class, reader);
    assertEquals(expected, actual);
  }

}
