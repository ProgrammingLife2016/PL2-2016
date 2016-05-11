package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.BubbledGraph;
import nl.tudelft.pl2016gr2.model.GraphInterface;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.test.utility.TestId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Class used to filter bubbles based on the phylogenetic tree. 
 * @author Casper
 *
 */
public class FilterBubbles {
  
  //private GraphInterface graph;
  private OriginalGraph originalGraph;
  private IPhylogeneticTreeNode treeRoot;
  private HashSet<Integer> allSharedNodes = new HashSet<>();
  private int mutationId;
  
  /**
   * Creates a FilterBubbles object, with the graph to be filtered and the node of the phylogenetic
   * tree based on which this graph will be filtered.
   * @param originalGraph : the graph to be filtered.
   * @param root : the node of the phylogenetic tree to use for filtering. This will be the root of 
   *     the tree when a full overview is wanted.
   */
  public FilterBubbles(OriginalGraph originalGraph, IPhylogeneticTreeNode root) {
    //this.graph = graph;
    this.originalGraph = originalGraph;
    this.treeRoot = root;
    mutationId = 10000;
  }
  
  /**
   * Filters the input graph of this object, using the phylogenetic tree. It finds all leaves of the
   * current node of the tree, which correspond to a certain genome. The filtered graph then only 
   * shows nodes that have all these genomes going through them, and shows mutation nodes in between
   * these.
   * @return : a filtered graph.
   */
  public GraphInterface filter() {
    GraphInterface filteredGraph = new BubbledGraph();
    
    PriorityQueue<Integer> sharedNodes = getSharedNodes();
    buildGraph(filteredGraph, sharedNodes);
    filteredGraph.print();
    System.out.println(filteredGraph.getSize());
    
    return filteredGraph;
  }
  
  /**
   * Builds the filtered graph. It loops through the shared nodes (which are ordered on id),
   * and adds each shared node to the filtered graph. Between two subsequent shared nodes,
   * a bubble is placed. 
   * @param filteredGraph : the resulting filtered graph.
   * @param sharedNodes : a list of shared nodes, sorted on id.
   */
  @TestId(id = "method_buildGraph")
  private void buildGraph(GraphInterface filteredGraph, PriorityQueue<Integer> sharedNodes) {
    Iterator<Integer> it = sharedNodes.iterator();
    int previous = it.next();
    
    while (it.hasNext()) {
      Node node = originalGraph.getNode(previous);
      node.getOutlinks().clear();
      node.getInlinks().clear();
      node.addOutlink(mutationId);
      
      int next = it.next();
      Node end = originalGraph.getNode(next);
      end.getInlinks().clear();
      end.getOutlinks().clear();
      end.addInlink(mutationId);
      
      // Fix sequencelength
      Bubble bubble = new Bubble(mutationId, 1);
      bubble.addInlink(node.getId());
      bubble.addOutlink(end.getId());
      
      filteredGraph.addNode(node);
      filteredGraph.addNode(bubble);
      filteredGraph.addNode(end);
      
      previous = next;
      mutationId++;
    }
  }
  
  /**
   * Finds all the nodes which have all the genomes going through them.
   * @return : a queue of shared nodes, sorted on id.
   */
  @TestId(id = "method_getSharedNodes")
  private PriorityQueue<Integer> getSharedNodes() {
    ArrayList<String> leaves = getLeaves();
    PriorityQueue<Integer> sharedNodes = new PriorityQueue<>();
    
    HashSet<Integer> visited = new HashSet<>();
    Queue<Integer> toVisit = new LinkedList<>();
    
    toVisit.add(originalGraph.getRoot().getId());
    
    while (!toVisit.isEmpty()) {
      int current = toVisit.poll();
      visited.add(current);
      
      Node node = originalGraph.getNode(current);
      if (containsAllLeaves(node, leaves)) {
        sharedNodes.add(node.getId());
        allSharedNodes.add(node.getId());
      }
      
      for (Integer outlink : node.getOutlinks()) {
        if (!(visited.contains(outlink) || toVisit.contains(outlink))) {
          toVisit.offer(outlink);
        }
      }
    }
    
    return sharedNodes;
  }
  
  /**
   * Checks if a node contains all leaves.
   * @param node : the node to check.
   * @param leaves : the list of leaves.
   * @return : true if the node contains all leaves.
   */
  @TestId(id = "method_containsAllLeaves")
  private boolean containsAllLeaves(Node node, ArrayList<String> leaves) {
    for (String leaf : leaves) {
      if (!node.getGenomes().contains(leaf)) {
        return false;
      }
    }
    
    return true;
  }
  
}
