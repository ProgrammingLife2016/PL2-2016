package nl.tudelft.pl2016gr2.gui.view.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxIntegrationTestRunner;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Observable;

/**
 * This class tests the {@link TreeManager} class.
 *
 * @author Faris
 */
@RunWith(JavaFxIntegrationTestRunner.class)
public class TreeManagerTest {

  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  /* The tree is initialized in the @Before method as follows: */
  /*      - - - leafR                                          */
  /*      -                                                    */
  /* root -                                                    */
  /*      -           - - - leafLr                             */
  /*      - - - leafL -                                        */
  /*                  - - - leafLl                             */
  /* Note: in @Before where the tree is drawn, there is not    */
  /* enough space for the leafLr and leafLl nodes, so they     */
  /* aren't drawn.                                             */
  /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  private IPhylogeneticTreeNode root;
  private IPhylogeneticTreeNode leafR;
  private IPhylogeneticTreeNode leafL;
  private IPhylogeneticTreeNode leafLr;
  private IPhylogeneticTreeNode leafLl;

  private TreeManager treeManager;
  private boolean lambdaExecuted = false;

  /**
   * Initialize the phylogenetic tree.
   */
  @Before
  public void loadTree() {
    root = Mockito.mock(IPhylogeneticTreeNode.class);
    leafR = Mockito.mock(IPhylogeneticTreeNode.class);
    leafL = Mockito.mock(IPhylogeneticTreeNode.class);
    leafLr = Mockito.mock(IPhylogeneticTreeNode.class);
    leafLl = Mockito.mock(IPhylogeneticTreeNode.class);
    mockRoot();
    mockLeafR();
    mockLeafL();
    mockLeafLr();
    mockLeafLl();
    initializeTreeManager();
  }

  /**
   * Initialize a tree manager.
   */
  private void initializeTreeManager() {
    Pane graphPane = new Pane();
    Scene scene = new Scene(graphPane, 500, 500);
    treeManager = new TreeManager();
    Pane largePane = new Pane();
    largePane.setMinHeight(100.0);
    largePane.setMinWidth(100.0);
    AccessPrivate.setFieldValue("treePane", TreeManager.class, treeManager, largePane);
    AccessPrivate.setFieldValue("heatmapPane", TreeManager.class, treeManager, largePane);
    AccessPrivate.setFieldValue("mainPane", TreeManager.class, treeManager, new AnchorPane());

    SelectionManager mockedSelectionManager = Mockito.spy(new SelectionManager(null, new Pane()));
    mockedSelectionManager.getBottomGraphGenomes().addAll(root.getGenomes());
    mockedSelectionManager.getTopGraphGenomes().addAll(root.getGenomes());
    AccessPrivate.callMethod("setSelectionManager", TreeManager.class, treeManager,
        mockedSelectionManager);
    AccessPrivate.callMethod("setRoot", TreeManager.class, treeManager, root);
  }

  /**
   * Mock the root node.
   */
  private void mockRoot() {
    when(root.hasParent()).thenReturn(false);
    when(root.getChildCount()).thenReturn(4);
    when(root.getDirectChildCount()).thenReturn(2);
    when(root.getChild(0)).thenReturn(leafR);
    when(root.getChild(1)).thenReturn(leafL);
    when(root.getChildIndex(leafR)).thenReturn(0);
    when(root.getChildIndex(leafL)).thenReturn(1);
    when(root.isLeaf()).thenReturn(false);
    when(root.getDrawnInBottomProperty()).thenReturn(new SimpleBooleanProperty(false));
    when(root.getDrawnInTopProperty()).thenReturn(new SimpleBooleanProperty(false));
    when(root.getInHighlightedPathProperty()).thenReturn(new SimpleBooleanProperty(false));
  }

  /**
   * Mock the right child node of the root node.
   */
  private void mockLeafR() {
    when(leafR.hasParent()).thenReturn(true);
    when(leafR.getParent()).thenReturn(root);
    when(leafR.getChildCount()).thenReturn(0);
    when(leafR.getDirectChildCount()).thenReturn(0);
    when(leafR.isLeaf()).thenReturn(true);
    when(leafR.getDrawnInBottomProperty()).thenReturn(new SimpleBooleanProperty(true));
    when(leafR.getDrawnInTopProperty()).thenReturn(new SimpleBooleanProperty(false));
    when(leafR.getInHighlightedPathProperty()).thenReturn(new SimpleBooleanProperty(false));
  }

  /**
   * Mock the left child node of the root node.
   */
  private void mockLeafL() {
    when(leafL.hasParent()).thenReturn(true);
    when(leafL.getParent()).thenReturn(root);
    when(leafL.getChildCount()).thenReturn(2);
    when(leafL.getDirectChildCount()).thenReturn(2);
    when(leafL.getChild(0)).thenReturn(leafLr);
    when(leafL.getChild(1)).thenReturn(leafLl);
    when(leafL.getChildIndex(leafLr)).thenReturn(0);
    when(leafL.getChildIndex(leafLl)).thenReturn(1);
    when(leafL.isLeaf()).thenReturn(false);
    when(leafL.getDrawnInBottomProperty()).thenReturn(new SimpleBooleanProperty(false));
    when(leafL.getDrawnInTopProperty()).thenReturn(new SimpleBooleanProperty(true));
    when(leafL.getInHighlightedPathProperty()).thenReturn(new SimpleBooleanProperty(false));
  }

  /**
   * Mock the right child node of the left child node of the root node.
   */
  private void mockLeafLr() {
    when(leafLr.hasParent()).thenReturn(true);
    when(leafLr.getParent()).thenReturn(leafL);
    when(leafLr.getChildCount()).thenReturn(0);
    when(leafLr.getDirectChildCount()).thenReturn(0);
    when(leafLr.isLeaf()).thenReturn(true);
    when(leafLr.getDrawnInBottomProperty()).thenReturn(new SimpleBooleanProperty(true));
    when(leafLr.getDrawnInTopProperty()).thenReturn(new SimpleBooleanProperty(true));
    when(leafLr.getInHighlightedPathProperty()).thenReturn(new SimpleBooleanProperty(false));
  }

  /**
   * Mock the left child node of the left child node of the root node.
   */
  private void mockLeafLl() {
    when(leafLl.hasParent()).thenReturn(true);
    when(leafLl.getParent()).thenReturn(leafL);
    when(leafLl.getChildCount()).thenReturn(0);
    when(leafLl.getDirectChildCount()).thenReturn(0);
    when(leafLl.isLeaf()).thenReturn(true);
    when(leafLl.getDrawnInBottomProperty()).thenReturn(new SimpleBooleanProperty(true));
    when(leafLl.getDrawnInTopProperty()).thenReturn(new SimpleBooleanProperty(true));
    when(leafLl.getInHighlightedPathProperty()).thenReturn(new SimpleBooleanProperty(false));
  }

  /**
   * Test of setOnLeavesChanged method, of class TreeManager.
   */
  @Test
  public void testSetOnLeavesChanged() {
    treeManager.setOnLeavesChanged((Observable obs, Object arg) -> {
      lambdaExecuted = true;
    });
    assertFalse(lambdaExecuted);
    AccessPrivate.callMethod("setRoot", TreeManager.class, treeManager, leafL);
    assertTrue(lambdaExecuted);
  }

  /**
   * Test of getCurrentLeaves method, of class TreeManager.
   */
  @Test
  public void testGetCurrentLeaves() {
    ArrayList<TreeNodeCircle> leaves = AccessPrivate.callMethod("getCurrentLeaves()",
        TreeManager.class, treeManager);
    assertEquals(3, leaves.size());
    ArrayList<IPhylogeneticTreeNode> actualLeaves = new ArrayList<>();
    actualLeaves.add(leafR);
    actualLeaves.add(leafLl);
    actualLeaves.add(leafLr);
    for (int i = 0; i < 3; i++) {
      assertTrue(actualLeaves.remove(leaves.get(i).getDataNode()));
    }
  }
}
