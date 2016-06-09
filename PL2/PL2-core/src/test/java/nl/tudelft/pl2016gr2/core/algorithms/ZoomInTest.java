package nl.tudelft.pl2016gr2.core.algorithms;

import static org.junit.Assert.assertTrue;

import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.core.InputStreamGraphFactory;
import nl.tudelft.pl2016gr2.core.InputStreamTreeFactory;
import nl.tudelft.pl2016gr2.core.TreeFactory;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.GraphOrdererThread;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.OrderedGraph;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.SubgraphAlgorithmManager;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.MetaData;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.phylogenetictree.PhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.parser.controller.MetaDataReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * This class tests the {@link ZoomIn} class.
 *
 * @author Faris
 */
public class ZoomInTest {

  private SequenceGraph graph;
  private IPhylogeneticTreeRoot treeRoot;

  /**
   * Initialize classes.
   *
   * @throws Exception won't be thrown.
   */
  @Before
  public void setup() throws Exception {
    GenomeMap.getInstance().clear();
    InputStream in = GfaReader.class.getClassLoader().getResourceAsStream("TB10.gfa");
    InputStreamGraphFactory graphFactory = new InputStreamGraphFactory(in);
    graph = graphFactory.getGraph();

    TreeFactory treeFactory = new InputStreamTreeFactory(GfaReader.class.getClassLoader()
        .getResourceAsStream("10tree_custom.rooted.TKK.nwk"));
    Tree tree = treeFactory.getTree();
    List<MetaData> metaDatas = new MetaDataReader(GfaReader.class.getClassLoader()
        .getResourceAsStream("metadata.xlsx")).read();
    treeRoot = new PhylogeneticTreeRoot(tree.getRoot(), metaDatas);
  }

  @After
  public void cleanup() {
    GenomeMap.getInstance().clear();
  }

  /**
   * Test of zoom method, of class ZoomIn.
   */
  @Test
  public void testZoom() {
    GraphOrdererThread mainGraphOrder = new GraphOrdererThread(graph);
    mainGraphOrder.start();
    Collection<Integer> allGenomes = GenomeMap.getInstance().copyAllGenomes();
    OrderedGraph agraph = SubgraphAlgorithmManager.alignOneGraph(allGenomes, graph, mainGraphOrder,
        treeRoot);
    PhyloBubble firstBubble = null;
    for (GraphNode graphNode : agraph.getGraphOrder()) {
      if (graphNode instanceof PhyloBubble) {
        firstBubble = (PhyloBubble) graphNode;
      }
    }
    Collection<GraphNode> poppedNodes = firstBubble.pop();
    assertTrue(poppedNodes.size() > 0);
  }
}
