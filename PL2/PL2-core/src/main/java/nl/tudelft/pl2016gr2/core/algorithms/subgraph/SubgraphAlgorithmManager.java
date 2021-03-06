package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.core.algorithms.bubbles.graph.GraphBubbleFilter;
import nl.tudelft.pl2016gr2.core.algorithms.bubbles.mutations.MutationBubbleAlgorithms;
import nl.tudelft.pl2016gr2.core.algorithms.bubbles.tree.PhyloBubbleFilter;
import nl.tudelft.pl2016gr2.model.Settings;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.data.BaseSequence;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages all of the subgraph algorithms. It makes sure the correct algorithms are
 * applied to the input and the correct result is returned.
 *
 * @author Faris
 */
public class SubgraphAlgorithmManager {

  private static int dummyRootNodeId = Integer.MAX_VALUE;

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
  @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:VariableDeclarationUsageDistance"})
  public static Pair<OrderedGraph, OrderedGraph> compareTwoGraphs(Collection<Integer> topGenomes,
      Collection<Integer> bottomGenomes, SequenceGraph mainGraph, GraphOrdererThread mainGraphOrder,
      IPhylogeneticTreeRoot<?> treeRoot) {
    dummyRootNodeId = Integer.MAX_VALUE;
    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        topGenomes);
    SplitGraphsThread bottomSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        bottomGenomes);
    topSubGraphThread.start();
    bottomSubGraphThread.start();

    FilterBubbleThread topFilter = new FilterBubbleThread(
        topSubGraphThread.getSubGraph().getOrderedGraph(), topGenomes, treeRoot);
    FilterBubbleThread bottomFilter = new FilterBubbleThread(
        bottomSubGraphThread.getSubGraph().getOrderedGraph(), bottomGenomes, treeRoot);
    int topGenomeCount = topSubGraphThread.getSubGraph().getGenomes().size();
    int bottomGenomeCount = bottomSubGraphThread.getSubGraph().getGenomes().size();
    topSubGraphThread = null; // allow garbage collecting
    bottomSubGraphThread = null; // allow garbage collecting
    topFilter.start();
    bottomFilter.start();

    ArrayList<GraphNode> topGraphOrder = topFilter.getOrderedNodes();
    ArrayList<GraphNode> bottomGraphOrder = bottomFilter.getOrderedNodes();
    CompareSubgraphs.compareGraphs(topGraphOrder, bottomGraphOrder);
    addDummyNodes(topGraphOrder);
    addDummyNodes(bottomGraphOrder);

    OrderedGraph orderedTopGraph = new OrderedGraph(topGenomeCount, topGraphOrder);
    OrderedGraph orderedBottomGraph = new OrderedGraph(bottomGenomeCount, bottomGraphOrder);
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
  @SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
  public static OrderedGraph alignOneGraph(Collection<Integer> genomes, SequenceGraph mainGraph,
      GraphOrdererThread mainGraphOrder, IPhylogeneticTreeRoot<?> treeRoot) {
    dummyRootNodeId = Integer.MAX_VALUE;
    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        genomes);
    topSubGraphThread.start();
    SequenceGraph subgraph = topSubGraphThread.getSubGraph();

    ArrayList<GraphNode> orderedNodes = subgraph.getOrderedGraph();
    int amountOfGenomes = subgraph.getGenomes().size();
    subgraph = null; // allow garbage collection
    orderedNodes = performBubblingAlgorithms(orderedNodes, genomes, treeRoot);

    CompareSubgraphs.alignVertically(orderedNodes);
    addDummyNodes(orderedNodes);
    return new OrderedGraph(amountOfGenomes, orderedNodes);
  }

  /**
   * Bubble the given graph according to the selected bubbling algorithms in the menu.
   *
   * @param orderedNodes the ordered list of nodes.
   * @param genomes      the genomes.
   * @param treeRoot     the root of the tree.
   */
  private static ArrayList<GraphNode> performBubblingAlgorithms(ArrayList<GraphNode> orderedNodes,
      Collection<Integer> genomes, IPhylogeneticTreeRoot<?> treeRoot) {
    Settings settings = Settings.getInstance();
    List<Settings.BubbleAlgorithms> algorithms = settings.getAlgorithms();

    orderedNodes = MutationBubbleAlgorithms.makeBubbels(orderedNodes);
    if (algorithms.contains(Settings.BubbleAlgorithms.PHYLO)) {
      PhyloBubbleFilter filter = new PhyloBubbleFilter(orderedNodes);
      orderedNodes = filter.filter(treeRoot, genomes);
    } else if (algorithms.contains(Settings.BubbleAlgorithms.GRAPH)) {
      GraphBubbleFilter graphFilter = new GraphBubbleFilter(orderedNodes);
      orderedNodes = graphFilter.filter();
    }
    return orderedNodes;
  }

  /**
   * Add dummy nodes before each root node.
   *
   * @param orderedNodes the ordered list of nodes.
   */
  private static void addDummyNodes(ArrayList<GraphNode> orderedNodes) {
    ArrayList<GraphNode> newNodes = new ArrayList<>();
    for (GraphNode node : orderedNodes) {
      if (node.getInEdges().isEmpty()) {
        SequenceNode newerRoot = new SequenceNode(getUniqueDummyNodeId(), new BaseSequence(""));
        node.addInEdge(newerRoot);
        newerRoot.addOutEdge(node);
        newNodes.add(newerRoot);
      }
    }
    orderedNodes.addAll(newNodes);
    orderedNodes.sort((GraphNode first, GraphNode second) -> {
      return first.getLevel() - second.getLevel();
    });
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

    private final Collection<Integer> genomes;
    private final IPhylogeneticTreeRoot<?> treeRoot;
    private ArrayList<GraphNode> orderedNodes;

    private FilterBubbleThread(ArrayList<GraphNode> orderedSubgraph, Collection<Integer> genomes,
        IPhylogeneticTreeRoot<?> treeRoot) {
      this.orderedNodes = orderedSubgraph;
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
      orderedNodes = performBubblingAlgorithms(orderedNodes, genomes, treeRoot);
    }
  }

  /**
   * Get a unique dummy node id.
   *
   * @return a unique dummy node id.
   */
  private static synchronized int getUniqueDummyNodeId() {
    return dummyRootNodeId--;
  }
}
