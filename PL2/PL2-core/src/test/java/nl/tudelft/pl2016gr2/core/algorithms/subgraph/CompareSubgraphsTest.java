package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.util.Pair;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Test of class {@link CompareSubgraphs}.
 *
 * @author Faris
 */
public class CompareSubgraphsTest {

  private static final String TEST_GRAPH_RESOURCE = "SMALL.gfa";
  private GfaReader gfaReader;
  private File file;

  /**
   * Initialize some testing variables.
   *
   * @throws IOException When file creation fails.
   */
  @Before
  public void initialize() throws IOException {

    file = File.createTempFile("GfaReaderTest", TEST_GRAPH_RESOURCE);
    FileUtils.copyInputStreamToFile(
        GfaReader.class.getClassLoader().getResourceAsStream(TEST_GRAPH_RESOURCE), file);

    gfaReader = new GfaReader(new FileInputStream(file));
  }

  @After
  public void tearDown() {
    file.delete();
  }

  /**
   * Test of compareGraphs method, of class CompareSubgraphs.
   */
  @Test
  public void testCompareGraphs() {

    SequenceGraph graph = gfaReader.read();
    GraphOrdererThread thread = new GraphOrdererThread(graph);
    thread.start();
    HashMap<Integer, NodePosition> mainGraphOrder = thread.getOrderedGraph();

    ArrayList<String> topGenomes = new ArrayList<>();
    topGenomes.add("TKK_02_0005.fasta");
    ArrayList<String> bottomGenomes = new ArrayList<>();
    bottomGenomes.add("TKK_02_0008.fasta");
    SequenceGraph topGraph = new SplitGraphs(graph).getSubgraph(topGenomes);
    SequenceGraph bottomGraph = new SplitGraphs(graph).getSubgraph(bottomGenomes);

    Pair<ArrayList<NodePosition>, ArrayList<NodePosition>> res
        = CompareSubgraphs.compareGraphs(mainGraphOrder, topGraph, bottomGraph);
    assertEquals(4, res.left.size());
    assertEquals(4, res.right.size());
  }
}
