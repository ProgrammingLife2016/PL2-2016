package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages all of the subgraph algorithms. It makes sure the correct algorithms are
 * applied to the input and the correct result is returned.
 *
 * @author Faris
 */
public class SubgraphAlgorithmManager {

  /**
   * This is a utility class, so let no one create an instance of it.
   */
  private SubgraphAlgorithmManager() {
  }

  /**
   * Create, compare and align the nodes of two subgraphs.
   *
   * @param topGenomes     the genomes which must be present in the top subgraph.
   * @param bottomGenomes  the genomes which must be present in the bottom subgraph.
   * @param mainGraph      the main graph.
   * @param mainGraphOrder the order of the main graph.
   * @return a pair containing as left value the ordered graph of the top subgraph and as right
   *         value the ordered graph of the bottom subgraph.
   */
  public static Pair<OrderedGraph, OrderedGraph> compareTwoGraphs(Collection<Integer> topGenomes,
      Collection<Integer> bottomGenomes, SequenceGraph mainGraph,
      GraphOrdererThread mainGraphOrder) {
    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        topGenomes);
    SplitGraphsThread bottomSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        bottomGenomes);
    topSubGraphThread.start();
    bottomSubGraphThread.start();

    Pair<ArrayList<NodePosition>, ArrayList<NodePosition>> alignedGraphs
        = CompareSubgraphs.compareGraphs(mainGraphOrder.getOrderedGraph(),
            topSubGraphThread.getSubGraph(), bottomSubGraphThread.getSubGraph());
    ArrayList<NodePosition> topGraphOrder = alignedGraphs.left;
    ArrayList<NodePosition> bottomGraphOrder = alignedGraphs.right;

    OrderedGraph orderedTopGraph = new OrderedGraph(topSubGraphThread.getSubGraph(), topGraphOrder);
    OrderedGraph orderedBottomGraph = new OrderedGraph(bottomSubGraphThread.getSubGraph(),
        bottomGraphOrder);
    return new Pair<>(orderedTopGraph, orderedBottomGraph);
  }

  /**
   * Create and align the nodes of a single subgraph.
   *
   * @param genomes        the genomes which must be present in the subgraph.
   * @param mainGraph      the main graph.
   * @param mainGraphOrder the order of the main graph.
   * @return the ordered graph.
   */
  public static OrderedGraph alignOneGraph(Collection<Integer> genomes, SequenceGraph mainGraph,
      GraphOrdererThread mainGraphOrder) {
    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        genomes);
    topSubGraphThread.start();
    SubGraphOrderer graphOrder = new SubGraphOrderer(mainGraphOrder.getOrderedGraph(),
        topSubGraphThread.getSubGraph());
    graphOrder.start();

    CompareSubgraphs.removeEmptyLevels(graphOrder.getNodeOrder());
    for (NodePosition nodePosition : graphOrder.getNodeOrder()) {
      nodePosition.setOverlapping(true);
    }
    return new OrderedGraph(topSubGraphThread.getSubGraph(), graphOrder.getNodeOrder());
  }

  /**
   * Thread which is used to get a graph of a subset of the genomes from a graph.
   */
  private static class SplitGraphsThread extends Thread {

    private SequenceGraph subGraph;
    private final SplitGraphs splitGraphs;
    private final Collection<Integer> genomes;

    /**
     * Construct a split graph thread. Subtracts a subgraph from the given graph, containing all of
     * the given genomes.
     *
     * @param splitGraphs a {@link SplitGraphs} object.
     * @param genomes     the list of genomes which must be present in the subgraph.
     */
    private SplitGraphsThread(SplitGraphs splitGraphs, Collection<Integer> genomes) {
      this.splitGraphs = splitGraphs;
      this.genomes = genomes;
    }

    /**
     * Wait till the thread completes its execution and get the subgraph.
     *
     * @return the subgraph.
     */
    private SequenceGraph getSubGraph() {
      try {
        this.join();
      } catch (InterruptedException ex) {
        Logger.getLogger(SubgraphAlgorithmManager.class.getName()).log(Level.SEVERE, null, ex);
      }
      return subGraph;
    }

    /**
     * Subtract a subgraph from the given graph, containing all of the given genomes.
     */
    @Override
    public void run() {
      subGraph = splitGraphs.getSubgraph(genomes);
    }
  }
}
