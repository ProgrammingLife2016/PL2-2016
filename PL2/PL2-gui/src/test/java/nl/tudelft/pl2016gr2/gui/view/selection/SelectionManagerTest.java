package nl.tudelft.pl2016gr2.gui.view.selection;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxIntegrationTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * This class tests the {@link SelectionManager} class.
 *
 * @author Faris
 */
@RunWith(JavaFxIntegrationTestRunner.class)
public class SelectionManagerTest {

  private SelectionManager selectionManager;
  private ISelectable selectable;

  /**
   * Initialize and mock the variables which are used by the tests.
   */
  @Before
  public void initializeVariables() {
    Pane pane = new Pane();
    Scene scene = new Scene(pane);
    selectionManager = new SelectionManager(null, pane);
    selectable = Mockito.mock(ISelectable.class);
    ISelectionInfo selectionInfo = Mockito.mock(ISelectionInfo.class);

    when(selectable.getSelectionInfo(selectionManager)).thenReturn(selectionInfo);
    when(selectionInfo.getNode()).thenReturn(new Pane());
  }

  /**
   * Test of select method, of class SelectionManager.
   */
  @Test
  public void testSelect() {
    selectionManager.select(selectable);
    verify(selectable, times(1)).select();
  }

  /**
   * Test of select method, of class SelectionManager.
   */
  @Test
  public void testSelectTwice() {
    selectionManager.select(selectable);
    selectionManager.select(selectable);
    verify(selectable, times(1)).select();
  }

  /**
   * Test of deselect method, of class SelectionManager.
   */
  @Test
  public void testDeselect() {
    selectionManager.select(selectable);
    selectionManager.deselect();
    verify(selectable, times(1)).deselect();
  }

  /**
   * Test of deselect method, of class SelectionManager.
   */
  @Test
  public void testDeselectTwice() {
    selectionManager.select(selectable);
    selectionManager.deselect();
    selectionManager.deselect();
    verify(selectable, times(1)).deselect();
  }

}
