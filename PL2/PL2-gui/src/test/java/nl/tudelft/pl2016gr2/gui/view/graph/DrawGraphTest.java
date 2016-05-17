package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxJUnit4ClassRunner;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.File;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * This class tests the {@link DrawGraph} class.
 *
 * @author Faris
 */
@RunWith(JavaFxJUnit4ClassRunner.class)
public class DrawGraphTest {

  /**
   * Test of drawGraph method, of class DrawGraph.
   */
  @Test
  public void testDrawGraph() {
    Pane pane = spy(new Pane());
    OriginalGraph graph = spy(new GfaReader(new File("SMALL.gfa")).read());

    new DrawGraph().drawGraph(pane, graph);
    verify(pane, Mockito.atLeast(1)).getChildren();
  }

}
