package nl.tudelft.pl2016gr2.gui.view.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.javafxrunner.JavaFxJUnit4ClassRunner;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.test.utility.AccessPrivate;
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
@RunWith(JavaFxJUnit4ClassRunner.class)
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
  }

  /**
   * Mock the right child node of the root node.
   */
  private void mockLeafR() {
    when(leafR.hasParent()).thenReturn(true);
    when(leafR.getParent()).thenReturn(root);
    when(leafR.getChildCount()).thenReturn(0);
    when(leafR.getDirectChildCount()).thenReturn(0);
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
  }

  /**
   * Mock the right child node of the left child node of the root node.
   */
  private void mockLeafLr() {
    when(leafLr.hasParent()).thenReturn(true);
    when(leafLr.getParent()).thenReturn(leafL);
    when(leafLr.getChildCount()).thenReturn(0);
    when(leafLr.getDirectChildCount()).thenReturn(0);
  }

  /**
   * Mock the left child node of the left child node of the root node.
   */
  private void mockLeafLl() {
    when(leafLl.hasParent()).thenReturn(true);
    when(leafLl.getParent()).thenReturn(leafL);
    when(leafLl.getChildCount()).thenReturn(0);
    when(leafLl.getDirectChildCount()).thenReturn(0);
  }

  /**
   * Test of setOnLeavesChanged method, of class TreeManager.
   */
  @Test
  public void testSetOnLeavesChanged() {
    SelectionManager selectionManager = new SelectionManager(new Pane(), new Pane());
    Button zoomOutButton = new Button();
    Pane graphPane = new Pane();
    Scene scene = new Scene(graphPane, 500, 500);
    TreeManager treeManager = new TreeManager(graphPane, root, zoomOutButton, selectionManager);

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
    SelectionManager selectionManager = new SelectionManager(new Pane(), new Pane());
    Button zoomOutButton = new Button();
    Pane graphPane = new Pane();
    Scene scene = new Scene(graphPane, 500, 500);
    TreeManager treeManager = new TreeManager(graphPane, root, zoomOutButton, selectionManager);
    ArrayList<ViewNode> leaves = treeManager.getCurrentLeaves();

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
