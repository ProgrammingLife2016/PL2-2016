package nl.tudelft.pl2016gr2.gui.view.graph;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxJUnit4ClassRunner;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

/**
 * This class tests the {@link DrawGraph} class.
 *
 * @author Faris
 */
@RunWith(JavaFxJUnit4ClassRunner.class)
public class DrawGraphTest {

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

  /**
   * Test of drawGraph method, of class DrawGraph.
   */
  @Test
  public void testDrawGraph() {
    Pane pane = spy(new Pane());
    OriginalGraph graph = spy(new GfaReader(file).read());

    new DrawGraph().drawGraph(pane, graph);
    verify(pane, Mockito.atLeast(1)).getChildren();
  }

}
