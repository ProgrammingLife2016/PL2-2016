package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.util.Pair;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test of class {@link CompareSubgraphs}.
 *
 * @author Faris
 */
public class CompareSubgraphsTest {

  /**
   * Test of compareGraphs method, of class CompareSubgraphs.
   */
  @Test
  public void testCompareGraphs() {
    SequenceGraph graph = new GfaReader("SMALL.gfa").read();
    GraphOrdererThread thread = new GraphOrdererThread(graph);
    thread.start();
    HashMap<Integer, NodePosition> mainGraphOrder = thread.getOrderedGraph();

    ArrayList<String> topGenomes = new ArrayList<>();
    topGenomes.add("TKK_02_0005");
    ArrayList<String> bottomGenomes = new ArrayList<>();
    bottomGenomes.add("TKK_02_0008");
    SequenceGraph topGraph = new SplitGraphs(graph).getSubgraph(topGenomes);
    SequenceGraph bottomGraph = new SplitGraphs(graph).getSubgraph(bottomGenomes);

    Pair<ArrayList<NodePosition>, ArrayList<NodePosition>> res
        = CompareSubgraphs.compareGraphs(mainGraphOrder, topGraph, bottomGraph);
    assertEquals(4, res.left.size());
    assertEquals(4, res.right.size());
  }
}
