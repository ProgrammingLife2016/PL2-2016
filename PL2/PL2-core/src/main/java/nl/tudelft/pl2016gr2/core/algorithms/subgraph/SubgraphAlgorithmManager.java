package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.core.algorithms.FilterBubbles;
import nl.tudelft.pl2016gr2.core.algorithms.mutations.MutationBubbleAlgorithms;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.data.BaseSequence;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
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
   * @param treeRoot       the root of the phylogenetic tree.
   * @return a pair containing as left value the ordered graph of the top subgraph and as right
   *         value the ordered graph of the bottom subgraph.
   */
  public static Pair<OrderedGraph, OrderedGraph> compareTwoGraphs(Collection<Integer> topGenomes,
      Collection<Integer> bottomGenomes, SequenceGraph mainGraph, GraphOrdererThread mainGraphOrder,
      IPhylogeneticTreeRoot treeRoot) {
    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        topGenomes);
    SplitGraphsThread bottomSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        bottomGenomes);
    topSubGraphThread.start();
    bottomSubGraphThread.start();

    FilterBubbleThread topFilter = new FilterBubbleThread(topSubGraphThread.getSubGraph(),
        topGenomes, treeRoot);
    FilterBubbleThread bottomFilter = new FilterBubbleThread(bottomSubGraphThread.getSubGraph(),
        bottomGenomes, treeRoot);
    topFilter.start();
    bottomFilter.start();

    ArrayList<GraphNode> topGraphOrder = topFilter.getOrderedNodes();
    ArrayList<GraphNode> bottomGraphOrder = bottomFilter.getOrderedNodes();

    CompareSubgraphs.compareGraphs(topGraphOrder, bottomGraphOrder);

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
   * @param treeRoot       the root of the phylogenetic tree.
   * @return the ordered graph.
   */
  @SuppressWarnings("checkstyle:methodlength")
  public static OrderedGraph alignOneGraph(Collection<Integer> genomes, SequenceGraph mainGraph,
      GraphOrdererThread mainGraphOrder, IPhylogeneticTreeRoot treeRoot) {
    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        genomes);
    topSubGraphThread.start();
    SequenceGraph subgraph = topSubGraphThread.getSubGraph();

    ArrayList<GraphNode> orderedNodes = subgraph.getOrderedGraph();
    orderedNodes = MutationBubbleAlgorithms.makeBubbels(orderedNodes);

    ArrayList<GraphNode> newNodes = new ArrayList<>();
    for (GraphNode node : orderedNodes) {
      if (node.getInEdges().isEmpty()) {

        SequenceNode newerRoot = new SequenceNode(0, new BaseSequence(""));
        node.addInEdge(newerRoot);
        newerRoot.addOutEdge(node);
        newNodes.add(newerRoot);
      }
    }
    orderedNodes.addAll(newNodes);
    orderedNodes.sort((GraphNode first, GraphNode second) -> {
      return first.getLevel() - second.getLevel();
    });

    FilterBubbles filter = new FilterBubbles(orderedNodes);
    orderedNodes = filter.filter(treeRoot, genomes);
    CompareSubgraphs.alignVertically(orderedNodes);

    return new OrderedGraph(subgraph, orderedNodes);
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

  private static class FilterBubbleThread extends Thread {

    private final SequenceGraph subgraph;
    private final Collection<Integer> genomes;
    private final IPhylogeneticTreeRoot treeRoot;
    private ArrayList<GraphNode> orderedNodes;

    private FilterBubbleThread(SequenceGraph subgraph, Collection<Integer> genomes,
        IPhylogeneticTreeRoot treeRoot) {
      this.subgraph = subgraph;
      this.genomes = genomes;
      this.treeRoot = treeRoot;
    }

    public ArrayList<GraphNode> getOrderedNodes() {
      try {
        this.join();
      } catch (InterruptedException ex) {
        Logger.getLogger(SubgraphAlgorithmManager.class.getName()).log(Level.SEVERE, null, ex);
      }
      return orderedNodes;
    }

    @Override
    public void run() {
      orderedNodes = subgraph.getOrderedGraph();
      orderedNodes = MutationBubbleAlgorithms.makeBubbels(orderedNodes);
      FilterBubbles filter = new FilterBubbles(orderedNodes);
      orderedNodes = filter.filter(treeRoot, genomes);
      ArrayList<GraphNode> newNodes = new ArrayList<>();
      for (GraphNode node : orderedNodes) {
        if (node.getInEdges().isEmpty()) {
          SequenceNode newRoot = new SequenceNode(0, new BaseSequence(""));
          newNodes.add(newRoot);
          newRoot.addOutEdge(node);
          node.addInEdge(newRoot);
        }
      }
      orderedNodes.addAll(newNodes);
      orderedNodes.sort((GraphNode first, GraphNode second) -> {
        return first.getLevel() - second.getLevel();
      });
    }
  }
}
