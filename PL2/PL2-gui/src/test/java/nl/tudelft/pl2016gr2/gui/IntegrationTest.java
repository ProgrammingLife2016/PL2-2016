package nl.tudelft.pl2016gr2.gui;

import static org.junit.Assert.assertNotEquals;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxIntegrationTestRunner;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxRealApplication;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionPaneController;
import nl.tudelft.pl2016gr2.gui.view.tree.TreePaneController;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class performs integration tests on the user interface. It clicks through windows and checks
 * for the correct behavior.
 *
 * @author Faris
 */
@RunWith(JavaFxIntegrationTestRunner.class)
public class IntegrationTest {

  private static final MouseEvent SOME_MOUSE_EVENT = new MouseEvent(null, 0, 0, 0, 0,
      MouseButton.NONE, 0, true, true, true, true, true, true, true, true, true, true, null);

  @Before
  public void setUp() {
    AccessPrivate.setFieldValue("singleton_instance", GenomeMap.class, null, null);
  }

  /**
   * Test if the scene is correctly set when the application is launcher.
   */
  @Test
  public void launchApplicationTest() {
    assertNotEquals(null, getPrimaryStage().getScene());
  }

  /**
   * Load the tree, graph and metadata files.
   */
  private void loadFiles() {
    getRootLayoutController().filesLoaded(
        GfaReader.class.getClassLoader().getResourceAsStream("10tree_custom.rooted.TKK.nwk"),
        GfaReader.class.getClassLoader().getResourceAsStream("TB10.gfa"),
        GfaReader.class.getClassLoader().getResourceAsStream("metadata.xlsx"),
        GfaReader.class.getClassLoader().getResourceAsStream("decorationV5_20130412.gff"));
  }

  private static Stage getPrimaryStage() {
    return JavaFxRealApplication.primaryStage;
  }

  private static RootLayoutController getRootLayoutController() {
    return JavaFxRealApplication.rootLayout;
  }

  private static SelectionManager getSelectionManager() {
    return AccessPrivate.getFieldValue("selectionManager", RootLayoutController.class,
        getRootLayoutController());
  }

  private static SelectionPaneController getSelectionController() {
    return AccessPrivate.getFieldValue("selectionPaneController", SelectionManager.class,
        getSelectionManager());
  }

  private static TreePaneController getTreeManager() {
    return AccessPrivate.getFieldValue("treeManager", RootLayoutController.class,
        getRootLayoutController());
  }

  private static GraphPaneController getDrawComparedGraphs() {
    return AccessPrivate.getFieldValue("drawGraphs", RootLayoutController.class,
        getRootLayoutController());
  }
}
