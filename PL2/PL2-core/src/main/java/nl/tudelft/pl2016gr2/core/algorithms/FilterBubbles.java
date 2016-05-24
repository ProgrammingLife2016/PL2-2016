package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.BubbledGraph;
import nl.tudelft.pl2016gr2.model.GraphInterface;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Class used to filter bubbles based on the phylogenetic tree. 
 * @author Casper
 *
 */
public class FilterBubbles {
  
  private OriginalGraph originalGraph;
  private int mutationId;
  
  private boolean zooming = false;
  
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
    zooming = true;
    IPhylogeneticTreeNode curTreeNode = bubble.getTreeNode();
    int inlink = bubble.getInlinks().get(0);
    int outlink = bubble.getOutlinks().get(0);
    
    if (bubble.getNestedNodes().size() == 1) {
      graph.replace(bubble, originalGraph.getNode(bubble.getNestedNodes().get(0)));
      return graph;
    }
    
    // Do something here to indicate that zooming is not possible
//    if (curTreeNode.isLeaf()) {
//      GraphInterface zoomedGraph = new BubbledGraph();
//      // This doesn't always work. The in/out link might still have other bubbles connected to it
//      zoomedGraph.addNode(originalGraph.getNode(inlink));
//      zoomedGraph.addNode(originalGraph.getNode(outlink));
//      for (Integer nestedNode : bubble.getNestedNodes()) {
//        zoomedGraph.addNode(originalGraph.getNode(nestedNode));
//      }
//      graph.replace(bubble, zoomedGraph);
//      return graph;
//    }
    
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
//    SharedNodesThread sharedNodesThreadOne = new SharedNodesThread(originalGraph, childOne, 
//        inlink, outlink, childTwo);
//    SharedNodesThread sharedNodesThreadTwo = new SharedNodesThread(originalGraph, childTwo, 
//        inlink, outlink, childOne);
//    sharedNodesThreadOne.start();
//    sharedNodesThreadTwo.start();
//
//    Queue<Integer> sharedNodesOne = sharedNodesThreadOne.getSharedNodes();
//    Queue<Integer> sharedNodesTwo = sharedNodesThreadTwo.getSharedNodes();
//    
    GraphInterface zoomedGraph = new BubbledGraph();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(zoomedGraph, childOne, bubble, newBubbles);
    debubble(zoomedGraph, childTwo, bubble, newBubbles);
//    ArrayList<Bubble> newBubbles = new ArrayList<>();
//    buildGraph(zoomedGraph, sharedNodesOne, childOne, new HashSet<>(sharedNodesOne),  
//        sharedNodesThreadOne.getNodeOutlinks(), newBubbles, false);
//    buildGraph(zoomedGraph, sharedNodesTwo, childTwo, new HashSet<>(sharedNodesTwo),  
//        sharedNodesThreadTwo.getNodeOutlinks(), newBubbles, false);
    
 // Add outlink to other bubbles that are not affected
    AbstractNode startNode = zoomedGraph.getNode(inlink);
    AbstractNode oldStartNode = graph.getNode(inlink);
    startNode.setInlinks(oldStartNode.getInlinks());
    for (Integer curOutlink : oldStartNode.getOutlinks()) {
      if (curOutlink != bubble.getId() && !startNode.getOutlinks().contains(curOutlink)) {
        startNode.addOutlink(curOutlink);
      }
    }
    
    // Add inlink to other bubbles that are not affected
    AbstractNode endNode = zoomedGraph.getNode(outlink);
    AbstractNode oldEndNode = graph.getNode(outlink);
    endNode.setOutlinks(oldEndNode.getOutlinks());
    for (Integer curInlink : oldEndNode.getInlinks()) {
      if (curInlink != bubble.getId() && !endNode.getInlinks().contains(curInlink)) {
        endNode.addInlink(curInlink);
      }
    }
    
    pruneNodes(zoomedGraph, graph, newBubbles);
    //zoomedGraph.print();
    System.out.println(bubble);
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
    
    SharedNodesThread sharedNodesThread = new SharedNodesThread(originalGraph, treeRoot);
    sharedNodesThread.start();
    Queue<Integer> sharedNodes = sharedNodesThread.getSharedNodes();
    
    HashSet<Integer> allSharedNodes = new HashSet<>();
    allSharedNodes.addAll(sharedNodes);
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    System.out.println(treeRoot.getLeaves());
    buildGraph(filteredGraph, sharedNodes, treeRoot, allSharedNodes, sharedNodesThread.getNodeOutlinks(), newBubbles, true);
    pruneNodes(filteredGraph, originalGraph, newBubbles);
    
    return filteredGraph;
  }
  
  private List<Bubble> debubble(GraphInterface filteredGraph, IPhylogeneticTreeNode treeNode, 
      Bubble bubble, ArrayList<Bubble> newBubbles) {
    mutationId++;
    Node start = originalGraph.getNode(bubble.getInlinks().get(0));
    Node end = originalGraph.getNode(bubble.getOutlinks().get(0));
    
    ArrayList<String> leaves = treeNode.getLeaves();
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    toVisit.add(start.getId());
    
    System.out.println("Tree node: " + treeNode.getLeaves());
    
    while (!toVisit.isEmpty()) {
      System.out.println("To visit: " + toVisit);
      int next = toVisit.poll();
      System.out.println("Next: " + next);
      visited.add(next);
      Node current = originalGraph.getNode(next);
      System.out.println("Leaves: " + current.getGenomes());
      filteredGraph.addNode(current.copy());
      if (next == end.getId()) {
        continue;
      }
      
      // DO SOMETHING HERE TO ALSO INCLUDE NODES WITH ONLY REF
      List<Integer> outlinks = calcNodeOutlinks(current, leaves, bubble);
      System.out.println("Outlinks: " + outlinks);
      if (outlinks.isEmpty()) {
        continue;
      }

      List<Integer> bubbleLinks = new ArrayList<>();

      for (Integer outlink : outlinks) {
        System.out.println("Leaves of " + outlink + ": " 
            + originalGraph.getNode(outlink).getGenomes());
        
        Node node = originalGraph.getNode(outlink);
        if (node.getGenomes().containsAll(leaves)) {
          //filteredGraph.addNode(node.copy());
          if (!toVisit.contains(outlink) && !visited.contains(outlink)) {
            toVisit.add(outlink);
          }
//          if (node.getId() < end.getId()) {
//            for (Integer nextOutlink : calcNodeOutlinks(node, leaves, bubble)) {
//              if (!toVisit.contains(nextOutlink) && !visited.contains(nextOutlink)) {
//                toVisit.add(nextOutlink);
//              }
//            }
//          }
        } else if (isShared(node, leaves)) {
          //filteredGraph.addNode(node.copy());
          if (!toVisit.contains(outlink) && !visited.contains(outlink)) {
            toVisit.add(outlink);
          }
        } else {
          bubbleLinks.add(outlink);
        }
      }
      System.out.println("Bubble links: " + bubbleLinks);
      
      if (!bubbleLinks.isEmpty()) {
        Bubble newBubble = new Bubble(mutationId, 1);
        newBubble.setTreeNode(treeNode);
        newBubble.addInlink(next);
        addNestedNodes(bubbleLinks, newBubble);
        Queue<Integer> toVisitNodes = makeBubble(newBubble, bubbleLinks, leaves);
        while (!toVisitNodes.isEmpty()) {
          int visit = toVisitNodes.poll();
          if (!toVisit.contains(visit) && !visited.contains(visit)) {
            toVisit.add(visit);
          }
        }

        newBubbles.add(newBubble);
        filteredGraph.addNode(newBubble);
        mutationId++;
        System.out.println("Added bubble: " + newBubble);
      }
    }
    
    filteredGraph.addNode(end.copy());
    
    return newBubbles;
  }
  
  private void addNestedNodes(List<Integer> nestedNodes, Bubble bubble) {
    for (Integer node : nestedNodes) {
      bubble.addNestedNode(node);
    }
  }
  
  private Queue<Integer> makeBubble(Bubble bubble, List<Integer> start, List<String> leaves) {
    Queue<Integer> endPoints = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> toVisit = new LinkedList<>();
    
    toVisit.addAll(start);
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      visited.add(next);
      Node current = originalGraph.getNode(next);
      
      if (isShared(current, leaves)) {
        endPoints.add(next);
        bubble.addOutlink(next);
      } else {
        bubble.addNestedNode(next);
        
        for (Integer outlink : current.getOutlinks()) {
          if (!(visited.contains(outlink) || toVisit.contains(outlink))) {
            toVisit.add(outlink);
          }
        }
      }
    }
     
    return endPoints;
  }
  
  private boolean isShared(Node node, List<String> leaves) {
    if (node.getGenomes().containsAll(leaves)) {
      return true;
    }
    
    for (String genome : node.getGenomes()) {
      if (!leaves.contains(genome)) {
        return true;
      }
    }
    
    return false;
  }
  
  private List<Integer> calcNodeOutlinks(Node node, ArrayList<String> leaves, Bubble bubble) {
    List<Integer> curNodeOutlinks = new ArrayList<>();

    for (Integer outlink : node.getOutlinks()) {
      if (!bubble.getNestedNodes().contains(outlink)) {
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
    
    return curNodeOutlinks;
  }
  
  /**
   * Builds the filtered graph. It loops through the shared nodes (which are ordered on id),
   * and adds each shared node to the filtered graph. Between two subsequent shared nodes,
   * a bubble is placed. 
   * @param filteredGraph : the resulting filtered graph.
   * @param sharedNodes : a list of shared nodes, sorted on id.
   */
  private void buildGraph(GraphInterface filteredGraph, Queue<Integer> sharedNodes, 
      IPhylogeneticTreeNode treeNode, HashSet<Integer> sharedNodesHashed, Map<Integer, List<Integer>> nodeOutlinks,
      ArrayList<Bubble> newBubbles, boolean addEnds) {
    
    if (addEnds) {
      Bubble firstBubble = new Bubble(mutationId, 1);
      firstBubble.setTreeNode(treeNode);
      filteredGraph.addNode(firstBubble);
      mutationId++;
    }
    
    if (zooming) {
      System.out.println(sharedNodesHashed);
    }

    while (!sharedNodes.isEmpty()) {
      int next = sharedNodes.poll();
      Node originalNode = originalGraph.getNode(next);
      
      if (hasBubbleChild(nodeOutlinks.get(next), sharedNodesHashed)) {
        // Fix sequencelength
        if (!sharedNodes.isEmpty() || addEnds) {

          Bubble bubble = new Bubble(mutationId, 1);
          bubble.addInlink(next);
          bubble.setTreeNode(treeNode);
          
          addNestedNodes(bubble, nodeOutlinks.get(next), sharedNodesHashed);
          if (mutationId == 9055) {
            System.out.println("Phylo node 9055: " + treeNode.getLeaves());
            System.out.println("bubble 9055: " + bubble);
          }
          filteredGraph.addNode(bubble);
          newBubbles.add(bubble);
          mutationId++;
        }
      }
      
      if (!filteredGraph.hasNode(next)) {
        filteredGraph.addNode(originalNode.copy());
      }
    }
  }
  
  private void pruneNodes(GraphInterface zoomedGraph, GraphInterface oldGraph, ArrayList<Bubble> newBubbles) {
    newBubbles.forEach(bubble -> {
      ArrayList<Integer> inlinks = bubble.getInlinks();
      if (!inlinks.isEmpty()) {
        zoomedGraph.getNode(inlinks.get(0)).addOutlink(bubble.getId());
      }
      
      ArrayList<Integer> outlinks = bubble.getOutlinks();
      if (!outlinks.isEmpty()) {
        outlinks.forEach(outlink -> {
          zoomedGraph.getNode(outlink).addInlink(bubble.getId());
        });
      }
    });
    
    zoomedGraph.getAbstractNodes().forEach((id, node) -> {
      if (id >= originalGraph.getHighestId()) {
        return;
      }
      Collection<Integer> inlinks = pruneInlinks(originalGraph.getNode(id), zoomedGraph);
      inlinks.forEach(node::addInlink);
      Collection<Integer> outlinks = pruneOutlinks(originalGraph.getNode(id), zoomedGraph);
      outlinks.forEach(node::addOutlink);
      if (id == 5838) {
       System.out.println(node); 
      }
    });
  }
  
  private Collection<Integer> pruneInlinks(Node original, GraphInterface zoomedGraph) {
    Collection<Integer> prunedInlinks = new ArrayList<>();
    original.getInlinks().forEach(link -> {
      if (zoomedGraph.hasNode(link)) {
        prunedInlinks.add(link);
      }
    });
    return prunedInlinks;
  }
  
  private Collection<Integer> pruneOutlinks(Node original, GraphInterface zoomedGraph) {
    Collection<Integer> prunedOutlinks = new ArrayList<>();
    original.getOutlinks().forEach(link -> {
      if (zoomedGraph.hasNode(link)) {
        prunedOutlinks.add(link);
      }
    });
    return prunedOutlinks;
  }
  
  private void addNestedNodes(Bubble bubble, List<Integer> outlinks, 
      HashSet<Integer> sharedNodesHashed) {
    Queue<Integer> toVisit = new LinkedList<>();
    HashSet<Integer> visited = new HashSet<>();
    
    toVisit.addAll(outlinks);
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      visited.add(next);
      if (sharedNodesHashed.contains(next)) {
        if (bubble.getOutlinks().isEmpty()) {
          bubble.addOutlink(next);
        }
        continue;
      }
      bubble.addNestedNode(next);
      
      Node node = originalGraph.getNode(next);
      for (Integer outlink : node.getOutlinks()) {
        if (!(sharedNodesHashed.contains(next) || toVisit.contains(outlink) 
            || visited.contains(outlink))) {
          toVisit.add(outlink);
        }
      }
    }
  }
  
  private boolean hasBubbleChild(List<Integer> outlinks, HashSet<Integer> allSharedNodes) {
    for (Integer outlink : outlinks) {
      if (!allSharedNodes.contains(outlink)) {
        return true;
      }
    }
    
    return false;
  }
  
}
