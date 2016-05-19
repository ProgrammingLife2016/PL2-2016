package nl.tudelft.pl2016gr2.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class FileGraphFactoryTest {

  private static final String filename = "SMALL.gfa";

  private File file;

  /**
   * Since the GfaReader uses files, we need to copy the resource to a file.
   *
   * @throws IOException When creation of tempfile fails.
   */
  @Before
  public void setup() throws IOException {
    file = File.createTempFile("FileGraphFactoryTest", filename);

    FileUtils.copyInputStreamToFile(
        GfaReader.class.getClassLoader().getResourceAsStream(filename),
        file);

  }

  @After
  public void tearDown() {
    file.delete();
  }

  @Test
  public void testGet() {
    FileGraphFactory factory = spy(new FileGraphFactory(file));
    when(factory.getGraph()).thenCallRealMethod();
    GfaReader reader = new GfaReader(file);
    when(factory.getGfaReader()).thenReturn(reader);
    OriginalGraph actual = factory.getGraph();
    OriginalGraph expected = AccessPrivate.getFieldValue("originalGraph", GfaReader.class, reader);
    assertEquals(expected, actual);
  }

}
