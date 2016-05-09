package nl.tudelft.pl2016gr2.gui.view.selection;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

/**
 * This class tests the {@link SelectionManager} class.
 *
 * @author Faris
 */
@RunWith(JavaFxJUnit4ClassRunner.class)
public class SelectionManagerTest {

  /**
   * Test of select method, of class SelectionManager.
   */
  @Test
  public void testSelect() {
    Pane pane = new Pane();
    Scene scene = new Scene(pane);
    SelectionManager selectionManager = new SelectionManager(pane, pane);
    ISelectable selectable = Mockito.mock(ISelectable.class);
    ISelectionInfo selectionInfo = Mockito.mock(ISelectionInfo.class);
    
    when(selectable.getSelectionInfo()).thenReturn(selectionInfo);
    when(selectionInfo.getNode()).thenReturn(new Pane());
    
    selectionManager.select(selectable);
    verify(selectable, times(1)).select();
  }

  /**
   * Test of select method, of class SelectionManager.
   */
  @Test
  public void testSelectTwice() {
    Pane pane = new Pane();
    Scene scene = new Scene(pane);
    SelectionManager selectionManager = new SelectionManager(pane, pane);
    ISelectable selectable = Mockito.mock(ISelectable.class);
    ISelectionInfo selectionInfo = Mockito.mock(ISelectionInfo.class);
    
    when(selectable.getSelectionInfo()).thenReturn(selectionInfo);
    when(selectionInfo.getNode()).thenReturn(new Pane());
    
    selectionManager.select(selectable);
    selectionManager.select(selectable);
    verify(selectable, times(1)).select();
  }

  /**
   * Test of deselect method, of class SelectionManager.
   */
  @Test
  public void testDeselect() {
    Pane pane = new Pane();
    Scene scene = new Scene(pane);
    SelectionManager selectionManager = new SelectionManager(pane, pane);
    ISelectable selectable = Mockito.mock(ISelectable.class);
    ISelectionInfo selectionInfo = Mockito.mock(ISelectionInfo.class);
    
    when(selectable.getSelectionInfo()).thenReturn(selectionInfo);
    when(selectionInfo.getNode()).thenReturn(new Pane());
    
    selectionManager.select(selectable);
    selectionManager.deselect();
    verify(selectable, times(1)).deselect();
  }

  /**
   * Test of deselect method, of class SelectionManager.
   */
  @Test
  public void testDeselectTwice() {
    Pane pane = new Pane();
    Scene scene = new Scene(pane);
    SelectionManager selectionManager = new SelectionManager(pane, pane);
    ISelectable selectable = Mockito.mock(ISelectable.class);
    ISelectionInfo selectionInfo = Mockito.mock(ISelectionInfo.class);
    
    when(selectable.getSelectionInfo()).thenReturn(selectionInfo);
    when(selectionInfo.getNode()).thenReturn(new Pane());
    
    selectionManager.select(selectable);
    selectionManager.deselect();
    selectionManager.deselect();
    verify(selectable, times(1)).deselect();
  }

}
