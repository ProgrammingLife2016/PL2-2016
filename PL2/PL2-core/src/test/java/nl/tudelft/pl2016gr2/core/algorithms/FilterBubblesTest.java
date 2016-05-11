package nl.tudelft.pl2016gr2.core.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphInterface;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.model.PhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.parser.controller.FullGfaReader;
import nl.tudelft.pl2016gr2.test.utility.AccessPrivate;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Tests the FilterBubbles class. The private method buildGraph is not tested,
 * because by testing the filter method and the getSharedNodes method this 
 * method is basically already tested. 
 * @author Casper
 *
 */
public class FilterBubblesTest {
  
  private PhylogeneticTreeNode treeRoot = mock(PhylogeneticTreeNode.class);
  private ArrayList<String> leaves;
  private OriginalGraph originalGraph;
  
  /**
   * Makes the getLeaves method of the treeRoot return a list with leaves
   * 'leaf1', 'leaf2', 'leaf3', 'leaf4', and 'leaf5'. Also reads a
   * test graph from a file. See the file to see what the tree looks like.
   */
  @Before
  public void setup() {
    leaves = new ArrayList<>();
    for (int i = 1; i <= 5; i++) {
      leaves.add("leaf" + i);
    }
    
    when(treeRoot.getLeaves()).thenReturn(leaves);
    originalGraph = new FullGfaReader("FilterBubblesTest.gfa", 11).getGraph();
  }
  
  /**
   * Tests the filter method of filterbubbles. It should filter the graph so 
   * it only shows nodes that are shared by all leaves, and adds mutation nodes
   * in between these shared nodes.
   */
  @Test
  public void testFilter() {
    FilterBubbles filter = new FilterBubbles(originalGraph, treeRoot);
    GraphInterface filteredGraph = filter.filter();
    
    assertEquals(3, filteredGraph.getSize());
    assertEquals(originalGraph.getRoot(), filteredGraph.getRoot());
    
    ArrayList<Integer> outlinks = filteredGraph.getRoot().getOutlinks();
    assertEquals(1, outlinks.size());
    assertTrue(filteredGraph.getNode(outlinks.get(0)) instanceof Bubble);
    assertEquals(originalGraph.getNode(11), filteredGraph.getNode(11));
  }
  
  /**
   * Tests the get sharedNodes method. This test should return a list with ints
   * 1 and 11, because these are the id's of the only nodes that are shared by all
   * leaves.
   */
  @Test
  public void testGetSharedNodes() {
    FilterBubbles filter = new FilterBubbles(originalGraph, treeRoot);
    @SuppressWarnings("unchecked")
    PriorityQueue<Integer> res = (PriorityQueue<Integer>)AccessPrivate
        .callMethod("method_getSharedNodes", FilterBubbles.class, filter);
    
    assertEquals(2, res.size());
    assertTrue(res.contains(1));
    assertTrue(res.contains(11));
  }
  
  @Test
  public void testContainsAllLeavesTrue() {
    FilterBubbles filter = new FilterBubbles(originalGraph, treeRoot);
    boolean res = (boolean)AccessPrivate.callMethod("method_containsAllLeaves", 
        FilterBubbles.class, filter, originalGraph.getRoot(), leaves);
    assertTrue(res);
  }
  
  @Test
  public void testContainsAllLeavesFalse() {
    FilterBubbles filter = new FilterBubbles(originalGraph, treeRoot);
    boolean res = (boolean)AccessPrivate.callMethod("method_containsAllLeaves", 
        FilterBubbles.class, filter, originalGraph.getNode(2), leaves);
    assertFalse(res);
  }
}
