
package nl.tudelft.pl2016gr2.model.phylogenetictree;

import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class tests the {@link PhylogeneticTreeRoot} class.
 *
 * @author Faris
 */
public class PhylogeneticTreeRootTest {


  private PhylogeneticTreeRoot treeNode;

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
    treeNode = new PhylogeneticTreeRoot(new PhylogeneticTreeNode(tree.getRoot(), null), 
        new ArrayList<>());
  }
  
  /**
   * Test of setDrawnInTop method, of class PhylogeneticTreeRoot.
   */
  @Test
  public void testSetDrawnInTop() {
  }

  /**
   * Test of setDrawnInBottom method, of class PhylogeneticTreeRoot.
   */
  @Test
  public void testSetDrawnInBottom() {
  }

  /**
   * Test of getMetaDatas method, of class PhylogeneticTreeRoot.
   */
  @Test
  public void testGetMetaDatas() {
  }

  /**
   * Test of highlightPaths method, of class PhylogeneticTreeRoot.
   */
  @Test
  public void testHighlightPaths() {
    ArrayList<Integer> highlightedPath = new ArrayList<>();
    highlightedPath.add(-1);
    treeNode.highlightPaths(new ArrayList<>(), highlightedPath);
    Assert.assertFalse(treeNode.getInHighlightedPathProperty().get());
  }

}
