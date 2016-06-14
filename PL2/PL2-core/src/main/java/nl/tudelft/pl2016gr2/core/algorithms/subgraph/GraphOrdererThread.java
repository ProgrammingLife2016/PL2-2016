package nl.tudelft.pl2016gr2.core.algorithms.subgraph;

import nl.tudelft.pl2016gr2.core.algorithms.GraphBaseMapper;
import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Thread which can be used to order a graph.
 *
 * @author Faris
 */
public class GraphOrdererThread extends Thread {

  private final SequenceGraph graph;
  private GraphBaseMapper mapper;
  private List<Annotation> annotations;

  /**
   * Construct a graph orderer thread.
   *
   * @param graph the graph to order.
   */
  public GraphOrdererThread(SequenceGraph graph) {
    this.graph = graph;
  }

  /**
   * Construct a graph orderer thread.
   *
   * @param graph       the graph to order.
   * @param annotations the annotations to map onto the ordered graph.
   */
  public GraphOrdererThread(SequenceGraph graph, List<Annotation> annotations) {
    this.graph = graph;
    this.annotations = annotations;
  }

  /**
   * Calculate the order of the nodes of the graph, so there are no backward edges. The given array
   * list contains the nodes in order from left to right. Nodes which are at the same horizontal
   * position have sequential positions in the array list and have the same value for their level
   * field.
   *
   * @param graph the graph.
   * @return the node order.
   */
  private void calculateGraphOrder() {
    HashMap<GraphNode, Integer> reachedCount = new HashMap<>();
    Set<GraphNode> currentLevel = new HashSet<>();
    currentLevel.addAll(graph.getRootNodes());
    while (!currentLevel.isEmpty()) {
      Set<GraphNode> nextLevel = new HashSet<>();
      ArrayList<ArrayList<GraphNode>> addedOutLinks = new ArrayList<>();
      for (GraphNode node : currentLevel) {
        int count = reachedCount.getOrDefault(node, 0);
        if (node.getInEdges().size() == count) {
          int maxInLevel = 0;
          for (GraphNode inEdge : node.getInEdges()) {
            if (inEdge.getLevel() > maxInLevel) {
              maxInLevel = inEdge.getLevel();
            }
          }
          node.setLevel(maxInLevel + node.size());
          nextLevel.addAll(node.getOutEdges());
          addedOutLinks.add(new ArrayList<>(node.getOutEdges()));
        }
      }
      updateReachedCount(reachedCount, addedOutLinks);
      currentLevel = nextLevel;
    }
  }

  /**
   * Update the reached count according to the out edges which have been iterated over.
   *
   * @param reachedCount  the reached count map.
   * @param addedOutLinks the outlinks which have been iterated over.
   */
  private static void updateReachedCount(HashMap<GraphNode, Integer> reachedCount,
      ArrayList<ArrayList<GraphNode>> addedOutLinks) {
    for (ArrayList<GraphNode> outEdges : addedOutLinks) {
      for (GraphNode outEdge : outEdges) {
        reachedCount.put(outEdge, reachedCount.getOrDefault(outEdge, 0) + 1);
      }
    }
  }

  /**
   * Wait till the thread completes its execution and get the ordered map of nodes.
   *
   * @return a hashmap containing an id, node order mapping.
   */
  public SequenceGraph getGraph() {
    try {
      this.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
    }
    return graph;
  }

  /**
   * Wait till the thread completes its execution and get the graph base mapper.
   *
   * @return get the graph base mapper.
   */
  public GraphBaseMapper getGraphMapper() {
    try {
      this.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(CompareSubgraphs.class.getName()).log(Level.SEVERE, null, ex);
    }
    return mapper;
  }

  /**
   * Calculate the graph order.
   */
  @Override
  public void run() {
    calculateGraphOrder();
    if (annotations != null) {
      mapper = new GraphBaseMapper(graph);
      mapper.mapAnnotations(new LinkedList<>(annotations));
      annotations = null;
    }
  }
}
