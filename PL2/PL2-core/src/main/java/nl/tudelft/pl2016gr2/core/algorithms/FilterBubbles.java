package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.BubbledGraph;
import nl.tudelft.pl2016gr2.model.GraphInterface;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class FilterBubbles {
  
  //private GraphInterface graph;
  private OriginalGraph originalGraph;
  private IPhylogeneticTreeNode treeRoot;
  private HashSet<Integer> allSharedNodes = new HashSet<>();
  private int mutationId;
  
  public FilterBubbles(OriginalGraph originalGraph, IPhylogeneticTreeNode root) {
    //this.graph = graph;
    this.originalGraph = originalGraph;
    this.treeRoot = root;
    mutationId = 10000;
  }
  
  public GraphInterface filter() {
    GraphInterface filteredGraph = new BubbledGraph();
    
    PriorityQueue<Integer> sharedNodes = getSharedNodes();
    buildGraph(filteredGraph, sharedNodes);
    filteredGraph.print();
    System.out.println(filteredGraph.getSize());
    
    return filteredGraph;
  }
  
  private void buildGraph(GraphInterface filteredGraph, PriorityQueue<Integer> sharedNodes) {
    Iterator<Integer> it = sharedNodes.iterator();
    int previous = it.next();
    
    while (it.hasNext()) {
      int next = it.next();
      
      Node node = originalGraph.getNode(previous);
      Node end = originalGraph.getNode(next);
      
      node.getOutlinks().clear();
      node.getInlinks().clear();
      node.addOutlink(mutationId);
      
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
   
  private boolean containsAllLeaves(Node node, ArrayList<String> leaves) {
    for (String leaf : leaves) {
      if (!node.getGenomes().contains(leaf)) {
        return false;
      }
    }
    
    return true;
  }
  
  public ArrayList<String> getLeaves() {
    ArrayList<String> leaves = new ArrayList<>();
    addLeaf(leaves, treeRoot);    
    return leaves;
  }
  
  private void addLeaf(ArrayList<String> leaves, IPhylogeneticTreeNode node) {
    if (node.isLeaf()) {
      leaves.add(node.getLabel());
      return;
    }
    
    for (int i = 0; i < node.getDirectChildCount(); i++) {
      addLeaf(leaves, node.getChild(i));
    }
  }
  
}
