package nl.tudelft.pl2016gr2.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.OrderedGraph;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxIntegrationTestRunner;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxRealApplication;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionPaneController;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;
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
   * Test if a tree node is correctly selected when it is clicked.
   */
  @Test
  public void selectTreeNodeTest() {
    loadFiles();
    TreeNodeCircle root = AccessPrivate.getFieldValue("currentRoot", TreePaneController.class,
        getTreeManager());
    root.getOnMouseClicked().handle(SOME_MOUSE_EVENT);
    SelectionManager selectionManager = getSelectionManager();
    ISelectable selected = AccessPrivate.getFieldValue("selected", SelectionManager.class,
        selectionManager);
    assertEquals(selected, root);
  }

  /**
   * Check if two graphs are drawn when the compare children button is clicked.
   */
  @Test
  public void compareChildrenButtonTest() {
    drawGraph();
    SimpleIntegerProperty drawnLevels
        = AccessPrivate.getFieldValue("amountOfLevels", GraphPaneController.class,
            getDrawComparedGraphs());
    assertTrue(drawnLevels.get() > 0);
    OrderedGraph topGraph = AccessPrivate.getFieldValue("topGraph", GraphPaneController.class,
        getDrawComparedGraphs());
    OrderedGraph bottomGraph = AccessPrivate.getFieldValue("bottomGraph", GraphPaneController.class,
        getDrawComparedGraphs());
    assertTrue(topGraph.getGraphOrder().size() > 0);
    assertTrue(bottomGraph.getGraphOrder().size() > 0);
  }

  /**
   * Click through the application, select a tree node and click the "compare children" button.
   */
  private void drawGraph() {
    loadFiles();
    TreeNodeCircle root = AccessPrivate.getFieldValue("currentRoot", TreePaneController.class,
        getTreeManager());
    root.getOnMouseClicked().handle(
        new MouseEvent(null, 0, 0, 0, 0, MouseButton.NONE, 0, true, true, true, true, true, true,
            true, true, true, true, null));
    AnchorPane rootPane = getSelectionController().rootPane;
    Button compareButton = null;
    for (Node node : rootPane.getChildren()) {
      if (node instanceof Pane) {
        compareButton = (Button) ((Pane) node).getChildren().get(0);
      }
    }
    assertNotEquals(null, compareButton);
    compareButton.fire();
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
