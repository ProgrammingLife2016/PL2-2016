//package nl.tudelft.pl2016gr2.core.algorithms;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//
//import nl.tudelft.pl2016gr2.model.Node;
//import nl.tudelft.pl2016gr2.model.OriginalGraph;
//import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//
//public class FilterSnipsTest {
//
//  private OriginalGraph graph = new OriginalGraph();
//
//  /**
//   * Make graph.
//    //
//    //    - 2 -       - 5 -       - 8
//    // 1 -      - 4 -       - 7 -
//    //    - 3 -       - 6 -       - 9
//    //
//   */
//  @Before
//  public void setup() {
//    for (int i = 1; i <= 9; i++) {
//      Node node = new Node(i, 1, new ArrayList<>(), 0);
//      if (i == 1 || i == 4 || i == 7) {
//        node.addOutlink(i + 1);
//        node.addOutlink(i + 2);
//
//        if (i != 1) {
//          node.addInlink(i - 1);
//          node.addInlink(i - 2);
//        }
//      } else if (i == 2 || i == 5) {
//        node.addOutlink(i + 2);
//        node.addInlink(i - 1);
//      } else if (i == 3 || i == 6) {
//        node.addOutlink(i + 1);
//        node.addInlink(i - 2);
//      } else if (i == 8) {
//        node.addInlink(i - 1);
//      } else {
//        node.addInlink(i - 2);
//      }
//      graph.addNode(node);
//    }
//  }
//
//  /**
//   * Test filter method. The resulting graph should have 3 nodes: - 8 1 - - 9
//   */
//  @Test
//  public void testFilter() {
//    FilterSnips filter = new FilterSnips(graph);
//    OriginalGraph filteredGraph = filter.filter();
//
//    assertEquals(3, filteredGraph.getSize());
//    Node root = filteredGraph.getNode(filteredGraph.getRootNodes().get(0));
//    assertEquals(1, root.getId());
//    assertTrue(root.getOutlinks().contains(8));
//    assertTrue(root.getOutlinks().contains(9));
//
//    assertEquals(graph.getNode(8), filteredGraph.getNode(8));
//    assertEquals(graph.getNode(9), filteredGraph.getNode(9));
//  }
//
//  /**
//   * Test method make snip for node 1. It should merge all nodes between 1 and 7 into one node.
//   */
//  @Test
//  public void testMakeSnip() {
//    FilterSnips filter = new FilterSnips(graph);
//    Node snip = AccessPrivate.callMethod("method_makeSnip", FilterSnips.class, filter,
//        graph.getNode(1));
//
//    assertEquals(2, snip.getOutlinks().size());
//    assertTrue(snip.getOutlinks().contains(8));
//    assertTrue(snip.getOutlinks().contains(9));
//
//    assertEquals(0, snip.getInlinks().size());
//
//    assertEquals(1, snip.getId());
//    assertEquals(2, snip.getSnips());
//  }
//
//  /**
//   * Test method make snip for node 4. It should merge all nodes between 4 and 7 into one node.
//   */
//  @Test
//  public void testMakeSnipNodeFour() {
//    FilterSnips filter = new FilterSnips(graph);
//    Node snip = AccessPrivate.callMethod("method_makeSnip", FilterSnips.class, filter,
//        graph.getNode(4));
//
//    assertEquals(2, snip.getOutlinks().size());
//    assertTrue(snip.getOutlinks().contains(8));
//    assertTrue(snip.getOutlinks().contains(9));
//
//    assertEquals(2, snip.getInlinks().size());
//    assertTrue(snip.getInlinks().contains(2));
//    assertTrue(snip.getInlinks().contains(3));
//
//    assertEquals(4, snip.getId());
//    assertEquals(1, snip.getSnips());
//  }
//
//  /**
//   * Test method is snip for node 1 (which is a snip). So the method should return true.
//   */
//  @Test
//  public void testIsSnipTrue() {
//    FilterSnips filter = new FilterSnips(graph);
//    boolean ret = AccessPrivate.callMethod("method_isSnip", FilterSnips.class, filter,
//        graph.getNode(1));
//    assertTrue(ret);
//  }
//
//  /**
//   * Test method is snip for node 2 (which is not a snip). So the method should return false.
//   */
//  @Test
//  public void testIsSnipFalse() {
//    FilterSnips filter = new FilterSnips(graph);
//    boolean ret = AccessPrivate.callMethod("method_isSnip", FilterSnips.class, filter,
//        graph.getNode(2));
//    assertFalse(ret);
//  }
//
//  /**
//   * Test method update links. The outlinks of node 1 are set to 5 and 6, to pretend it is a snip
//   * from node 1 to 4. So the inlink of node 5 and 6, which is 4 now, should be changed to 1.
//   */
//  @Test
//  public void testUpdateLinks() {
//    ArrayList<Integer> outlinks = new ArrayList<>();
//    outlinks.add(5);
//    outlinks.add(6);
//    FilterSnips filter = new FilterSnips(graph);
//    graph.getNode(1).setOutlinks(outlinks);
//    AccessPrivate.callMethod("method_updateLinks", FilterSnips.class, filter, graph.getNode(1), 4);
//    assertEquals(1, graph.getNode(5).getInlinks().get(0).intValue());
//    assertEquals(1, graph.getNode(6).getInlinks().get(0).intValue());
//  }
//
//}
