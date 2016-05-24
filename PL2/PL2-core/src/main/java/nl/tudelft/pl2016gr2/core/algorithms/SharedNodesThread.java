package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to calculate all nodes that are shared by the leaves of a 
 * phylogenetic tree node. 
 * 
 * @author Casper
 *
 */
public class SharedNodesThread extends Thread {
  
  private OriginalGraph originalGraph;
  private boolean scanCompleteGraph;
  private IPhylogeneticTreeNode treeRoot;
  private IPhylogeneticTreeNode otherTreeRoot;
  private int start;
  private int end;
  private Map<Integer, List<Integer>> nodeOutlinks;
  private Queue<Integer> sharedNodes;
  
  public SharedNodesThread(OriginalGraph originalGraph, IPhylogeneticTreeNode treeRoot) {
    this.originalGraph = originalGraph;
    scanCompleteGraph = true;
    this.treeRoot = treeRoot;
    nodeOutlinks = new HashMap<>();
  }
  
  public SharedNodesThread(OriginalGraph originalGraph, IPhylogeneticTreeNode treeRoot, 
      int start, int end, IPhylogeneticTreeNode otherTreeRoot) {
    this.originalGraph = originalGraph;
    this.treeRoot = treeRoot;
    this.otherTreeRoot = otherTreeRoot;
    nodeOutlinks = new HashMap<>();
    
    scanCompleteGraph = false;
    this.start = start;
    this.end = end;
  }
  
  public Queue<Integer> getSharedNodes() {
    try {
      this.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(SharedNodesThread.class.getName()).log(Level.SEVERE, null, ex);
    }
    return sharedNodes;
  }
  
  public Map<Integer, List<Integer>> getNodeOutlinks() {
    try {
      this.join();
    } catch (InterruptedException ex) {
      Logger.getLogger(SharedNodesThread.class.getName()).log(Level.SEVERE, null, ex);
    }
    return nodeOutlinks;
  }
  
  private Queue<Integer> calcSharedNodes() {
    sharedNodes = new PriorityQueue<>();
    //specialNodes = new HashSet<>();
    ArrayList<String> leaves = treeRoot.getLeaves();
    ArrayList<String> otherLeaves = otherTreeRoot.getLeaves();
    HashSet<Integer> visited = new HashSet<>();
    Queue<Integer> toVisit = new LinkedList<>();
    toVisit.add(start);
    
    while (!toVisit.isEmpty()) {
      int current = toVisit.poll();
      if (current > end) {
        continue;
      }
      visited.add(current);
      Node node = originalGraph.getNode(current);
      List<Integer> nodeOutlinks = null;
      if (node.getGenomes().containsAll(leaves)) {
        sharedNodes.add(node.getId());
        nodeOutlinks = calcNodeOutlinks(node, leaves, otherLeaves);
      } else if (hasBothLeaves(node, leaves, otherLeaves)) {
        sharedNodes.add(node.getId());
        nodeOutlinks = calcNodeOutlinks(node, leaves, otherLeaves);
      }
      
      for (Integer outlink : node.getOutlinks()) {
        if (!(visited.contains(outlink) || toVisit.contains(outlink) || current >= end)) {
          toVisit.offer(outlink);
        }
      }
    }
    
    return sharedNodes;
  }
  
  private boolean hasBothLeaves(Node node, ArrayList<String> leaves, ArrayList<String> otherLeaves) {
    boolean hasLeafOne = false;
    
    for (String leaf : leaves) {
      if (node.getGenomes().contains(leaf)) {
        hasLeafOne = true;
        break;
      }
    }
    
    boolean hasLeafTwo = false;
    
    for (String leaf : otherLeaves) {
      if (node.getGenomes().contains(leaf)) {
        hasLeafTwo = true;
        break;
      }
    }
    
    return hasLeafOne && hasLeafTwo;
  }
  
  private Queue<Integer> calcAllSharedNodes() {
    sharedNodes = new PriorityQueue<>();
    ArrayList<String> leaves = treeRoot.getLeaves();
    
    originalGraph.getNodes().forEach((id, node) -> {
      if (node.getGenomes().containsAll(leaves)) {
        sharedNodes.add(id);
        calcNodeOutlinks(node, leaves, null);
      }
    });
    
    return sharedNodes;
  }
  
  private List<Integer> calcNodeOutlinks(Node node, ArrayList<String> leaves, ArrayList<String> otherLeaves) {
    List<Integer> curNodeOutlinks = new ArrayList<>();

    for (Integer outlink : node.getOutlinks()) {
      if (otherLeaves != null && hasBothLeaves(originalGraph.getNode(outlink), leaves, otherLeaves)) {
        continue;
      }
      
      ArrayList<String> genomes = originalGraph.getNode(outlink).getGenomes();
      
      for (String leaf : leaves) {
        if (genomes.contains(leaf)) {
          curNodeOutlinks.add(outlink);
          break;
        }
      }
    }
    
    nodeOutlinks.put(node.getId(), curNodeOutlinks);
    return curNodeOutlinks;
  }
  
  @Override
  public void run() {
    if (scanCompleteGraph) {
      sharedNodes = calcAllSharedNodes();
    } else {
      sharedNodes = calcSharedNodes();
    }
  }

}
