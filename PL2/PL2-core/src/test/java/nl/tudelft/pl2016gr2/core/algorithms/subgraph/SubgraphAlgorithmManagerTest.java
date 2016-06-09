package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.core.InputStreamGraphFactory;
import nl.tudelft.pl2016gr2.core.InputStreamTreeFactory;
import nl.tudelft.pl2016gr2.core.TreeFactory;
import nl.tudelft.pl2016gr2.core.algorithms.mutations.VerticalAligner;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.MetaData;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.phylogenetictree.PhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.parser.controller.MetaDataReader;
import nl.tudelft.pl2016gr2.util.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class tests the {@link SubgraphAlgorithmManager} class.
 *
 * @author Faris
 */
public class SubgraphAlgorithmManagerTest {

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
   * Test of compareTwoGraphs method, of class SubgraphAlgorithmManager.
   */
  @Test
  public void testCompareTwoGraphs() {
    GraphOrdererThread mainGraphOrder = new GraphOrdererThread(graph);
    mainGraphOrder.start();
    VerticalAligner.POINT_MUTATION_ALIGNER.align(new ArrayList<>(), new ArrayList<>());
    Collection<Integer> allGenomes = GenomeMap.getInstance().copyAllGenomes();
    Pair<OrderedGraph, OrderedGraph> comparedGraphs = SubgraphAlgorithmManager.compareTwoGraphs(
        allGenomes, allGenomes, graph, mainGraphOrder, treeRoot);
    VerticalAligner.STRAIGHT_SEQUENCE_ALIGNER.align(new ArrayList<>(), new ArrayList<>());
    Assert.assertTrue(comparedGraphs.left.getGraphOrder().size() >= 0);
  }

  /**
   * Test of alignOneGraph method, of class SubgraphAlgorithmManager.
   */
  @Test
  public void testAlignOneGraph() {
    GraphOrdererThread mainGraphOrder = new GraphOrdererThread(graph);
    mainGraphOrder.start();
    Collection<Integer> allGenomes = GenomeMap.getInstance().copyAllGenomes();
    OrderedGraph agraph = SubgraphAlgorithmManager.alignOneGraph(allGenomes, graph, mainGraphOrder,
        treeRoot);
    VerticalAligner.INDEL_ALIGNER.align(new ArrayList<>(), new ArrayList<>());
    Assert.assertTrue(agraph.getGraphOrder().size() >= 0);
  }

}
