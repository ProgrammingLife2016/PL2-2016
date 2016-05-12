package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.BubbledGraph;
import nl.tudelft.pl2016gr2.model.GraphInterface;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.test.utility.TestId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Class used to filter bubbles based on the phylogenetic tree. 
 * @author Casper
 *
 */
public class FilterBubbles {
  
  private OriginalGraph originalGraph;
  private int mutationId;
  
  /**
   * Creates a FilterBubbles object, with the graph to be filtered and the node of the phylogenetic
   * tree based on which this graph will be filtered.
   * @param originalGraph : the graph to be filtered.
   */
  public FilterBubbles(OriginalGraph originalGraph) {
    this.originalGraph = originalGraph;
    mutationId = originalGraph.getHighestId() + 1;
  }
  
  public GraphInterface zoom(Bubble bubble, GraphInterface graph) {
    IPhylogeneticTreeNode curTreeNode = bubble.getTreeNode();
    int inlink = bubble.getInlinks().get(0);
    int outlink = bubble.getOutlinks().get(0);
    
    if (bubble.getNestedNodes().size() == 1) {
      graph.replace(bubble, originalGraph.getNode(bubble.getNestedNodes().get(0)));
      return graph;
    }
    
    // Do something here to indicate that zooming is not possible
    if (curTreeNode.isLeaf()) {
      System.out.println(curTreeNode);
      for (Integer nestedNode : bubble.getNestedNodes()) {
        graph.replace(bubble, originalGraph.getNode(nestedNode));
      }
      return graph;
    }
    
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    PriorityQueue<Integer> sharedNodesOne = getSharedNodes(childOne, inlink, outlink);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
    PriorityQueue<Integer> sharedNodesTwo = getSharedNodes(childTwo, inlink, outlink);
    
    HashSet<Integer> allSharedNodes = new HashSet<>();
    allSharedNodes.addAll(sharedNodesTwo);
    allSharedNodes.addAll(sharedNodesOne);
    
    GraphInterface zoomedGraph = new BubbledGraph();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    buildGraph(zoomedGraph, sharedNodesOne, childOne, allSharedNodes, newBubbles, false);
    buildGraph(zoomedGraph, sharedNodesTwo, childTwo, allSharedNodes, newBubbles, false);
    
    // Add outlink to other bubbles that are not affected
    AbstractNode startNode = zoomedGraph.getNode(inlink);
    startNode.setInlinks(graph.getNode(inlink).getInlinks());
    for (Integer curOutlink : graph.getNode(inlink).getOutlinks()) {
      if (curOutlink != bubble.getId() && !startNode.getOutlinks().contains(curOutlink)) {
        startNode.addOutlink(curOutlink);
      }
    }
    
    // Add inlink to other bubbles that are not affected
    AbstractNode endNode = zoomedGraph.getNode(outlink);
    endNode.setOutlinks(graph.getNode(outlink).getOutlinks());
    for (Integer curInlink : graph.getNode(outlink).getInlinks()) {
      if (curInlink != bubble.getId() && !endNode.getInlinks().contains(curInlink)) {
        endNode.addInlink(curInlink);
      }
    }
    
    for (Bubble newBubble : newBubbles) {
      addNestedNodes(newBubble, zoomedGraph, allSharedNodes);
    }

    graph.replace(bubble, zoomedGraph);
    
    return graph;
  }
  
  /**
   * Filters the input graph of this object, using the phylogenetic tree. It finds all leaves of the
   * current node of the tree, which correspond to a certain genome. The filtered graph then only 
   * shows nodes that have all these genomes going through them, and shows mutation nodes in between
   * these.
   * @return : a filtered graph.
   */
  public GraphInterface filter(IPhylogeneticTreeNode treeRoot) {
    GraphInterface filteredGraph = new BubbledGraph();
    
    PriorityQueue<Integer> sharedNodes = getSharedNodes(treeRoot, originalGraph.getRoot().getId(), 
        originalGraph.getHighestId());
    HashSet<Integer> allSharedNodes = new HashSet<>();
    allSharedNodes.addAll(sharedNodes);
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    buildGraph(filteredGraph, sharedNodes, treeRoot, allSharedNodes, newBubbles, true);
    for (Bubble newBubble : newBubbles) {
      addNestedNodes(newBubble, filteredGraph, allSharedNodes);
    }
    
    return filteredGraph;
  }
  
  /**
   * Builds the filtered graph. It loops through the shared nodes (which are ordered on id),
   * and adds each shared node to the filtered graph. Between two subsequent shared nodes,
   * a bubble is placed. 
   * @param filteredGraph : the resulting filtered graph.
   * @param sharedNodes : a list of shared nodes, sorted on id.
   */
  private void buildGraph(GraphInterface filteredGraph, PriorityQueue<Integer> sharedNodes, 
      IPhylogeneticTreeNode treeNode, HashSet<Integer> allSharedNodes, ArrayList<Bubble> newBubbles, boolean addEnds) {
    boolean predecessorIsBubble = false;
    
    if (addEnds) {
      predecessorIsBubble = true;
      Bubble firstBubble = new Bubble(mutationId, 1);
      firstBubble.setTreeNode(treeNode);
      filteredGraph.addNode(firstBubble);
      mutationId++;
    }
    
    HashMap<Integer, Integer> inlinksToShared = new HashMap<>();
    int predecessor = -1;
    
    while (!sharedNodes.isEmpty()) {
      int next = sharedNodes.poll();
      
      // add code to check if previous has an outlink to next: if this is the case, add this
      // outlink/inlink to node/end respectively
      Node originalNode = originalGraph.getNode(next);
      
      if (predecessorIsBubble) {
        filteredGraph.getNode(mutationId - 1).addOutlink(next);
      }
      
      if (hasBubbleChild(originalNode, allSharedNodes)) {
        Node node = null;
        if (filteredGraph.hasNode(next)) {
          node = (Node)filteredGraph.getNode(next);
        } else {
          node = originalNode.copy();
        }
        
        if (predecessorIsBubble) {
          node.addInlink(mutationId - 1);
          if (inlinksToShared.containsKey(node.getId())) {
            node.addInlink(inlinksToShared.get(node.getId()));
            inlinksToShared.remove(node.getId());
          }
        } else if (predecessor != -1) {
          node.addInlink(predecessor);
        }
        
        // Fix sequencelength
        if ((!sharedNodes.isEmpty() || addEnds) && !filteredGraph.hasNode(next)) {
          node.addOutlink(mutationId);
          ArrayList<Integer> outlinksToShared = outlinkToShared(originalNode, allSharedNodes);
          for (Integer outlink : outlinksToShared) {
            node.addOutlink(outlink);
            inlinksToShared.put(outlink, node.getId());
          }

          Bubble bubble = new Bubble(mutationId, 1);
          bubble.addInlink(node.getId());
          bubble.setTreeNode(treeNode);
          newBubbles.add(bubble);
          filteredGraph.addNode(bubble);
          mutationId++;
          predecessorIsBubble = true;
        }
        
        filteredGraph.addNode(node);
        
      } else {
        Node node = null;
        if (filteredGraph.hasNode(next)) {
          node = (Node)filteredGraph.getNode(next);
        } else {
          node = originalNode.copy();
        }
        
        if (predecessorIsBubble) {
          node.addInlink(mutationId - 1);
          if (inlinksToShared.containsKey(node.getId())) {
            node.addInlink(inlinksToShared.get(node.getId()));
            inlinksToShared.remove(node.getId());
          }
        } else if (predecessor != -1) {
          node.addInlink(predecessor);
        }        

        node.setOutlinks(outlinkToShared(originalNode, allSharedNodes));
        filteredGraph.addNode(node);
        predecessorIsBubble = false;
        predecessor = node.getId();
      }
    }
  }
  
  private void addNestedNodes(Bubble bubble, GraphInterface filteredGraph, HashSet<Integer> allSharedNodes) {
    int startId;
    if (!bubble.getInlinks().isEmpty()) {
      startId = bubble.getInlinks().get(0);
    } else {
      startId = 1;
    }
    
    int endId;
    if (!bubble.getOutlinks().isEmpty()) {
      endId = bubble.getOutlinks().get(0);
    } else {
      endId = Integer.MAX_VALUE;
    }
    
    AbstractNode start = filteredGraph.getNode(startId);
    Queue<Integer> toVisit = new LinkedList<>();
    HashSet<Integer> visited = new HashSet<>();
    
    for (Integer outlink : originalGraph.getNode(startId).getOutlinks()) {
      if ((start == null || !start.getOutlinks().contains(outlink)) && outlink != endId && !allSharedNodes.contains(outlink)) {
        toVisit.add(outlink);
      }
    }
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      bubble.addNestedNode(next);
      visited.add(next);
      
      Node node = originalGraph.getNode(next);
      for (Integer outlink : node.getOutlinks()) {
        if ((start == null || !start.getOutlinks().contains(outlink)) && !toVisit.contains(outlink) && !visited.contains(outlink) && outlink < endId
            && !allSharedNodes.contains(outlink)) {
          toVisit.add(outlink);
        }
      }
    }
  }
  
  private boolean hasBubbleChild(Node node, HashSet<Integer> allSharedNodes) {
    for (Integer outlink : node.getOutlinks()) {
      if (!allSharedNodes.contains(outlink)) {
        return true;
      }
    }
    
    return false;
  }
  
  private ArrayList<Integer> outlinkToShared(Node node, HashSet<Integer> allSharedNodes) {
    ArrayList<Integer> outlinksToShared = new ArrayList<>();
    for (Integer outlink : node.getOutlinks()) {
      if (allSharedNodes.contains(outlink)) {
        outlinksToShared.add(outlink);
      }
    }
    return outlinksToShared;
  }
  
  /**
   * Finds all the nodes which have all the genomes going through them.
   * @return : a queue of shared nodes, sorted on id.
   */
  @TestId(id = "method_getSharedNodes")
  private PriorityQueue<Integer> getSharedNodes(IPhylogeneticTreeNode treeRoot, int start, int end) {
    ArrayList<String> leaves = treeRoot.getLeaves();
    PriorityQueue<Integer> sharedNodes = new PriorityQueue<>();
    
    HashSet<Integer> visited = new HashSet<>();
    Queue<Integer> toVisit = new LinkedList<>();
    
    toVisit.add(start);
    
    while (!toVisit.isEmpty()) {
      int current = toVisit.poll();
      visited.add(current);
      
      Node node = originalGraph.getNode(current);
      if (containsAllLeaves(node, leaves)) {
        sharedNodes.add(node.getId());
      }
      
      for (Integer outlink : node.getOutlinks()) {
        if (!(visited.contains(outlink) || toVisit.contains(outlink) || outlink > end)) {
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
