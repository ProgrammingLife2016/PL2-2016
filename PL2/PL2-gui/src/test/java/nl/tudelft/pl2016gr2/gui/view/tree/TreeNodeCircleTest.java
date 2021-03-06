package nl.tudelft.pl2016gr2.gui.view.tree;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

/**
 * This class tests the {@link TreeNodeCircle} class.
 *
 * @author Faris
 */
@RunWith(MockitoJUnitRunner.class)
public class TreeNodeCircleTest {

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
  @Mock
  private IPhylogeneticTreeNode root;
  @Mock
  private IPhylogeneticTreeNode leafR;
  @Mock
  private IPhylogeneticTreeNode leafL;
  @Mock
  private IPhylogeneticTreeNode leafLr;
  @Mock
  private IPhylogeneticTreeNode leafLl;

  @Mock
  private SelectionManager selectionManager;
  @Mock
  private TreePaneController treePaneController;
  private TreeNodeCircle viewNode;
  private final Area area = new Area(0, 50, 100, 200);
  private Pane graphPaneSpy;

  /**
   * Initialize the phylogenetic tree.
   */
  @Before
  public void loadTree() {
    mockRoot();
    mockLeafR();
    mockLeafL();
    mockLeafLr();
    mockLeafLl();
    graphPaneSpy = Mockito.spy(new Pane());
    when(treePaneController.getSelectionManager()).thenReturn(selectionManager);
    viewNode = TreeNodeCircle.drawNode(root, area, graphPaneSpy, treePaneController);
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
    when(leafR.getEdgeLength()).thenReturn(0.);
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
    when(leafL.getEdgeLength()).thenReturn(0.);
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
    when(leafLr.getEdgeLength()).thenReturn(0.);
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
    when(leafLl.getEdgeLength()).thenReturn(0.);
    when(leafLl.isLeaf()).thenReturn(true);
    when(leafLl.getDrawnInBottomProperty()).thenReturn(new SimpleBooleanProperty(true));
    when(leafLl.getDrawnInTopProperty()).thenReturn(new SimpleBooleanProperty(true));
    when(leafLl.getInHighlightedPathProperty()).thenReturn(new SimpleBooleanProperty(false));
  }

  /**
   * Test of getDataNode method, of class TreeNodeCircle.
   */
  @Test
  public void testGetDataNode() {
    assertEquals(root, viewNode.getDataNode());
  }

  /**
   * Test of drawRootNode method, of class TreeNodeCircle. This method is executed in the @Before,
   * here we will only test if it was done correctly. Tests if the 3 nodes, 2 edges and 1 elipsis
   * are drawn in the given pane.
   */
  @Test
  public void testDrawNode() {
    verify(graphPaneSpy, times(6)).getChildren();
  }

  /**
   * Test of drawRootNode method, of class TreeNodeCircle. This method is executed in the @Before,
   * here we will only test if it was done correctly. Tests if the drawn tree contains the correct
   * nodes.
   */
  @Test
  public void testDrawNodeTreeStructureFirstLevel() {
    ArrayList<TreeNodeCircle> rootChildren = AccessPrivate.getFieldValue("children",
        TreeNodeCircle.class,
        viewNode);
    assertEquals(2, rootChildren.size());
    assertEquals(leafR, rootChildren.get(0).getDataNode());
    assertEquals(leafL, rootChildren.get(1).getDataNode());
  }

  /**
   * Test of drawRootNode method, of class TreeNodeCircle. This method is executed in the @Before,
   * here we will only test if it was done correctly. Tests if the drawn tree contains the correct
   * nodes.
   */
  @Test
  public void testDrawNodeTreeStructureSecondLevel() {
    ArrayList<TreeNodeCircle> rootChildren = AccessPrivate.getFieldValue("children",
        TreeNodeCircle.class, viewNode);
    ArrayList<TreeNodeCircle> rightChildren = AccessPrivate.getFieldValue("children",
        TreeNodeCircle.class,
        rootChildren.get(0));
    ArrayList<TreeNodeCircle> leftChildren = AccessPrivate.getFieldValue("children",
        TreeNodeCircle.class,
        rootChildren.get(1));
    assertEquals(0, rightChildren.size());
    assertEquals(0, leftChildren.size());
  }

  @Test
  public void clickNode() {
    MouseEvent testMouseEvent = new MouseEvent(viewNode, viewNode, MouseEvent.MOUSE_CLICKED, 0, 0,
        0, 0, MouseButton.PRIMARY, 0, false, false, false, false, true, false, false, false, false,
        true, null);
    viewNode.fireEvent(testMouseEvent);
    verify(selectionManager, times(1)).select(viewNode);
  }

  /**
   * Test of zoomIn method, of class TreeNodeCircle. This is mostly based on animation, so we will
   * only verify that the correct amount of circles is animated.See the testing document in the
   * documentation folder for an explanation about this and other special cases.
   */
  @Test
  public void testZoomIn() {
    ArrayList<TreeNodeCircle> rootChildren = AccessPrivate.getFieldValue("children",
        TreeNodeCircle.class,
        viewNode);
    Timeline timeline = new Timeline();
    viewNode.zoomIn(rootChildren.get(1), timeline);
    assertEquals(3, timeline.getKeyFrames().size());
  }

  /**
   * Test of zoomOut method, of class TreeNodeCircle. This is mostly based on animation, so we will
   * only verify that the correct amount of circles is animated. See the testing document in the
   * documentation folder for an explanation about this and other special cases.
   */
  @Test
  public void testZoomOut() {
    Timeline timeline = new Timeline();
    TreeNodeCircle zoomLeafL =
        TreeNodeCircle.drawNode(leafL, area,graphPaneSpy, treePaneController);
    zoomLeafL.zoomOut(timeline);
    assertEquals(3, timeline.getKeyFrames().size());
  }

  /**
   * Test of getGraphArea method, of class TreeNodeCircle.
   */
  @Test
  public void testGetGraphArea() {
    assertEquals(area, viewNode.getGraphArea());
  }

  /**
   * Test of getCurrentLeaves method, of class TreeNodeCircle.
   */
  @Test
  public void testGetCurrentLeaves() {
    ArrayList<TreeNodeCircle> viewLeaves = viewNode.getCurrentLeaves();
    assertEquals(2, viewLeaves.size());
    assertEquals(leafR, viewLeaves.get(0).getDataNode());
    assertEquals(leafL, viewLeaves.get(1).getDataNode());
  }

  /**
   * Test of getClosestParentNode method, of class TreeNodeCircle.
   */
  @Test
  public void testGetClosestParentNode() {
    assertEquals(leafL, viewNode.getClosestParentNode(10, 180).getDataNode());
  }

  /**
   * Test of select method, of class TreeNodeCircle.
   */
  @Test
  public void testSelect() {
    // NOT IMPLEMENTED YET
  }

  /**
   * Test of deselect method, of class TreeNodeCircle.
   */
  @Test
  public void testDeselect() {
    // NOT IMPLEMENTED YET
  }

  /**
   * Test of getSelectionInfo method, of class TreeNodeCircle.
   */
  @Test
  public void testGetSelectionInfo() {
    // NOT IMPLEMENTED YET
  }

}
