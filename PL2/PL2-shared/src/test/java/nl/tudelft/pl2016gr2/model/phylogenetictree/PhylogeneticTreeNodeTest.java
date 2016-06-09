package nl.tudelft.pl2016gr2.model.phylogenetictree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.model.MetaData;
import nl.tudelft.pl2016gr2.model.metadata.LineageColor;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class tests the {@link PhylogeneticTreeNode} class.
 *
 * @author Faris
 */
public class PhylogeneticTreeNodeTest {

  private PhylogeneticTreeNode treeNode;

  /**
   * Initialize a phylogenetic tree node.
   */
  @Before
  public void setup() {
    final Reader reader;
    reader = new InputStreamReader(PhylogeneticTreeNode.class.getClassLoader().getResourceAsStream(
        "10tree_custom.rooted.TKK.nwk"));
    BufferedReader br = new BufferedReader(reader);
    TreeParser tp = new TreeParser(br);
    Tree tree = tp.tokenize("Tree");

    try {
      reader.close();
    } catch (IOException ex) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, "couldn't load test tree");
    }
    treeNode = new PhylogeneticTreeNode(new PhylogeneticTreeNode(tree.getRoot(), null));
  }

  /**
   * Test of toString method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testToString() {
    assertEquals(68, treeNode.toString().length());
  }

  /**
   * Test of hasParent method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testHasParent() {
    assertFalse(treeNode.hasParent());
  }

  /**
   * Test of getParent method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetParent() {
    assertEquals(null, treeNode.getParent());
  }

  /**
   * Test of getDirectChildCount method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetDirectChildCount() {
    assertEquals(2, treeNode.getDirectChildCount());
  }

  /**
   * Test of getChildCount method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetChildCount() {
    assertEquals(2, treeNode.getDirectChildCount());
  }

  /**
   * Test of getChild method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetChild() {
    assertEquals(treeNode.getChild(0), treeNode.getChild(0));
  }

  /**
   * Test of getChildIndex method, of class PhylogeneticTreeNode.
   */
  @Test(expected = IndexOutOfBoundsException.class)
  public void testGetChildIndex() {
    treeNode.getChildIndex(null);
  }

  /**
   * Test of getGenomes method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetGenomes() {
    assertEquals(10, treeNode.getGenomes().size());
  }

  /**
   * Test of getGenomeIds method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetGenomeIds() {
    assertEquals(10, treeNode.getGenomeIds().size());
  }

  /**
   * Test of getEdgeLength method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetEdgeLength() {
    assertEquals(0.0, treeNode.getEdgeLength(), 0.001);
  }

  /**
   * Test of isLeaf method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testIsLeaf() {
    assertFalse(treeNode.isLeaf());
  }

  /**
   * Test of getDrawnInTopProperty method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetDrawnInTopProperty() {
    assertFalse(treeNode.getDrawnInTopProperty().get());
  }

  /**
   * Test of getDrawnInBottomProperty method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetDrawnInBottomProperty() {
    assertFalse(treeNode.getDrawnInBottomProperty().get());
  }

  /**
   * Test of getInHighlightedPathProperty method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetInHighlightedPathProperty() {
    assertFalse(treeNode.getInHighlightedPathProperty().get());
  }

  /**
   * Test of getLineageColor method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetLineageColor() {
    assertEquals(LineageColor.NONE.getColor(), treeNode.getLineageColor());
  }

  /**
   * Test of iterator method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testIterator() {
    Iterator<PhylogeneticTreeNode> it = treeNode.iterator();
    assertTrue(it.hasNext());
    it.next();
    assertTrue(it.hasNext());
  }

  /**
   * Test of getMetaData method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetMetaData() {
    assertEquals("", treeNode.getMetaData());
  }

  /**
   * Test of setDrawnInTop method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testSetDrawnInTop() {
    treeNode.setDrawnInTop(true);
    assertFalse(treeNode.getDrawnInTopProperty().get());
  }

  /**
   * Test of setDrawnInBottom method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testSetDrawnInBottom() {
    treeNode.setDrawnInBottom(true);
    assertFalse(treeNode.getDrawnInBottomProperty().get());
  }

  /**
   * Test of getLabel method, of class PhylogeneticTreeNode.
   */
  @Test(expected = AssertionError.class)
  public void testGetLabel() {
    treeNode.getLabel();
  }

  /**
   * Test of setMetaData method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testSetMetaData() {
    MetaData meta = new MetaData();
    meta.lineage = "";
    treeNode.setMetaData(meta);
    assertEquals(meta.buildMetaDataString(), treeNode.getMetaData());
  }

  /**
   * Test of unhighlightPath method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testUnhighlightPath() {
    treeNode.unhighlightPath();
    assertFalse(treeNode.getInHighlightedPathProperty().get());
  }

  /**
   * Test of highlightPath method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testHighlightPath() {
    treeNode.highlightPath();
    assertTrue(treeNode.getInHighlightedPathProperty().get());
  }

  /**
   * Test of getGenomeId method, of class PhylogeneticTreeNode.
   */
  @Test
  public void testGetGenomeId() {
    assertEquals(-1, treeNode.getGenomeId());
  }
}
