package nl.tudelft.pl2016gr2.gui.view.graph;

import static org.mockito.Mockito.verify;

import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

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
    Pane pane = Mockito.spy(new Pane());
    new DrawGraph().drawGraph(pane);
    verify(pane, Mockito.atLeast(1)).getChildren();
  }

}
