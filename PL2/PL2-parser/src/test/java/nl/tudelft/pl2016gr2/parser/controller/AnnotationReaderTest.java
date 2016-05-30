package nl.tudelft.pl2016gr2.parser.controller;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.Annotation;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class AnnotationReaderTest {

  private File tempFile;

  /**
   * Creates a temp file for this class to use.
   *
   * @throws IOException when the file couldn't be created.
   */
  @Before
  public void setup() throws IOException {
    tempFile = File.createTempFile(getClass().getName(), "tempmetadatafile");
  }

  @After
  public void teardown() {
    tempFile.delete();
  }

  @Test
  public void testRead() throws Exception {

    FileUtils.copyInputStreamToFile(
        getClass().getClassLoader().getResourceAsStream("TESTMETADATA1.xlsx"),
        tempFile);

    AnnotationReader reader = new AnnotationReader(new FileInputStream(tempFile));

    List<Annotation> annotations = reader.read();

    assertEquals(4, annotations.size());
  }
}
