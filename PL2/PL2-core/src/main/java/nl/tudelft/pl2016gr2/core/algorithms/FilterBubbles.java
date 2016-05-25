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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * Class used to filter bubbles based on the phylogenetic tree. 
 * @author Casper
 *
 */
public class FilterBubbles {
  
  private OriginalGraph originalGraph;
  private int mutationId;
  private Map<Integer, Map<Integer, Stack<List<AbstractNode>>>> oldGraphs = new HashMap<>();
  
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
    saveOldGraph(inlink, outlink, graph);
    
    if (bubble.getNestedNodes().size() == 1) {
      graph.replace(bubble, originalGraph.getNode(bubble.getNestedNodes().get(0)));
      return graph;
    }
    
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
 
    GraphInterface zoomedGraph = new BubbledGraph();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(zoomedGraph, childOne, bubble, newBubbles);
    debubble(zoomedGraph, childTwo, bubble, newBubbles);
    
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
    graph.replace(bubble, zoomedGraph);
    return graph;
  }
  
  private void saveOldGraph(int start, int end, GraphInterface graph) {
    List<AbstractNode> oldView = getNodesOnPath(start, end, graph, true);
    printNodeList(oldView, "Old nodes: ");
    
    if (oldGraphs.containsKey(start)) {
      if (oldGraphs.get(start).containsKey(end)) {
        oldGraphs.get(start).get(end).push(oldView);
      } else {
        Stack<List<AbstractNode>> stack = new Stack<>();
        stack.push(oldView);
        oldGraphs.get(start).put(end, stack);
      }
    } else {
      Map<Integer, Stack<List<AbstractNode>>> endMap = new HashMap<>();
      Stack<List<AbstractNode>> stack = new Stack<>();
      stack.push(oldView);
      endMap.put(end, stack);
      oldGraphs.put(start, endMap);
    }
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
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(filteredGraph, treeRoot, newBubbles);
    pruneNodes(filteredGraph, originalGraph, newBubbles);
    
    return filteredGraph;
  }
  
  public GraphInterface zoomOut(IPhylogeneticTreeNode treeNode, Bubble bubble, 
      GraphInterface graph) {
    // find start
    if (!treeNode.hasParent()) {
      return graph;
    }
    
    IPhylogeneticTreeNode parent = treeNode.getParent();
    
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    
    toVisit.add(bubble.getId());
    ArrayList<String> leaves = parent.getLeaves();
    int start = -1;
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      if (originalGraph.getNode(next) == null) {
        for (Integer inlink : graph.getNode(next).getInlinks()) {
          addToVisit(inlink, toVisit, visited);
        }
      } else {
        Node curNode = originalGraph.getNode(next);
        if (isShared(curNode, leaves)) {
          start = curNode.getId();
          System.out.println("Start: " + start);
          break;
        } else {
          for (Integer inlink : graph.getNode(next).getInlinks()) {
            addToVisit(inlink, toVisit, visited);
          }
        }
      }
    }
    
    int end = -1;
    toVisit.clear();
    visited.clear();
    toVisit.addAll(bubble.getOutlinks());
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      if (originalGraph.getNode(next) == null) {
        for (Integer outlink : graph.getNode(next).getOutlinks()) {
          addToVisit(outlink, toVisit, visited);
        }
      } else {
        Node curNode = originalGraph.getNode(next);
        if (isShared(curNode, leaves)) {
          end = curNode.getId();
          System.out.println("End: " + end);
          break;
        } else {
          for (Integer outlink : graph.getNode(next).getOutlinks()) {
            addToVisit(outlink, toVisit, visited);
          }
        }
      }
    }
    
    graph = getPreviousView(start, end, graph);
    return graph;
  }
  
  private GraphInterface getPreviousView(int start, int end, GraphInterface graph) {
    if (!oldGraphs.containsKey(start) || !oldGraphs.get(start).containsKey(end) 
        || oldGraphs.get(start).get(end).isEmpty()) {
      return graph;
    }
    
    List<AbstractNode> nodesToRemove = getNodesOnPath(start, end, graph, false);
    printNodeList(nodesToRemove, "Nodes to remove: ");
    nodesToRemove.forEach(node -> {
      if (node.getId() != start && node.getId() != end)
        graph.remove(node);
      else 
        graph.getAbstractNodes().remove(node.getId());
    });
    
    List<AbstractNode> previousView = oldGraphs.get(start).get(end).pop();
    printNodeList(previousView, "Nodes to add: ");
    previousView.forEach(node -> {
      graph.addNode(node);
    });
    
    return graph;
  }
  
  private void printNodeList(List<AbstractNode> list, String header) {
    System.out.print(header + "[");
    list.forEach(node -> {
      System.out.print(node.getId() + ", ");
    });
    System.out.println("]");
  }
  
  private List<AbstractNode> getNodesOnPath(int start, int end, GraphInterface graph, boolean copy) {
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    
    List<AbstractNode> nodesOnPath = new ArrayList<>();
    Set<Integer> seenPaths = new HashSet<>();
    toVisit.add(start);
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      AbstractNode node = graph.getNode(next);
      if (copy) 
        nodesOnPath.add(node.copyAll());
      else
        nodesOnPath.add(node);
      
      for (Integer outlink : node.getOutlinks()) {
        if (seenPaths.contains(outlink) || hasPathTo(outlink, end, graph, seenPaths)) {
          addToVisit(outlink, toVisit, visited);
        }
      }
    }
    
    return nodesOnPath;
  }
  
  private boolean hasPathTo(int from, int to, GraphInterface graph, Set<Integer> seenPaths) {
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    
    toVisit.add(from);
    Map<Integer, List<Integer>> toFromMap = new HashMap<>();
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      if (next == to) {
        backtrackPath(toFromMap, seenPaths, to);
        return true;
      }
      
      AbstractNode abstractNode = graph.getNode(next);
      
      if (next < to || !(abstractNode instanceof Node)) {
        for (Integer outlink : abstractNode.getOutlinks()) {
          addToVisit(outlink, toVisit, visited);
          if (toFromMap.containsKey(outlink)) {
            toFromMap.get(outlink).add(next);
          } else {
            List<Integer> fromList = new ArrayList<>();
            fromList.add(next);
            toFromMap.put(outlink, fromList);
          }
        }
      }
    }
    
    return false;
  }
  
  private void backtrackPath(Map<Integer, List<Integer>> toFromMap, 
      Set<Integer> seenPaths, int end) {
    Set<Integer> visited = new HashSet<>();
    Queue<Integer> toVisit = new LinkedList<>();
    
    toVisit.add(end);
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      seenPaths.add(next);
      if (toFromMap.containsKey(next)) {
        for (Integer from : toFromMap.get(next)) {
          if (toFromMap.containsKey(from)) {
            addToVisit(from, toVisit, visited);
          }
        }
      }
    }
  }
  
  private List<Bubble> debubble(GraphInterface filteredGraph, IPhylogeneticTreeNode treeNode, 
      ArrayList<Bubble> newBubbles) {
    mutationId++;
    
    ArrayList<String> leaves = treeNode.getLeaves();
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();

    Node rootNode = originalGraph.getRoot();
    int rootId = rootNode.getId();
    if (isShared(rootNode, leaves)) {
      toVisit.add(rootId);
    } else {
      List<Integer> bubbleStart = new ArrayList<>();
      bubbleStart.add(rootId);
      createBubble(treeNode, -1, bubbleStart, leaves, toVisit, visited, 
          newBubbles, filteredGraph);
    }
    
    filterBubbles(toVisit, visited, filteredGraph, leaves, null, treeNode, newBubbles, null);
    
    return newBubbles;
  }
  
  private List<Bubble> debubble(GraphInterface filteredGraph, IPhylogeneticTreeNode treeNode, 
      Bubble bubble, ArrayList<Bubble> newBubbles) {
    mutationId++;
    Node start = originalGraph.getNode(bubble.getInlinks().get(0));
    
    ArrayList<String> leaves = treeNode.getLeaves();
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    toVisit.add(start.getId());
    
    filterBubbles(toVisit, visited, filteredGraph, leaves, bubble, treeNode, newBubbles, null);
    
    Node end = originalGraph.getNode(bubble.getOutlinks().get(0));
    filteredGraph.addNode(end.copy());
    
    return newBubbles;
  }
  
  private void filterBubbles(Queue<Integer> toVisit, Set<Integer> visited, 
      GraphInterface filteredGraph, ArrayList<String> leaves, Bubble bubble, 
      IPhylogeneticTreeNode treeNode, List<Bubble> newBubbles, Set<Integer> endIds) {
    if (endIds == null) {
      endIds = new HashSet<>();
      if (bubble != null) {
        endIds.addAll(bubble.getOutlinks());
      }
    }
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      visited.add(next);
      Node current = originalGraph.getNode(next);
      filteredGraph.addNode(current.copy());
      if ((bubble != null && endIds.contains(next)) || (!endIds.isEmpty() && endIds.contains(next))) {
        continue;
      }
      
      // DO SOMETHING HERE TO ALSO INCLUDE NODES WITH ONLY REF
      List<Integer> outlinks = calcNodeOutlinks(current, leaves, bubble);
      List<Integer> bubbleLinks = new ArrayList<>();

      for (Integer outlink : outlinks) {
        Node node = originalGraph.getNode(outlink);
        if (isShared(node, leaves)) {
          addToVisit(outlink, toVisit, visited);
        } else {
          bubbleLinks.add(outlink);
        }
      }
      
      createBubble(treeNode, next, bubbleLinks, leaves, toVisit, visited, 
          newBubbles, filteredGraph);
    }
  }
  
  private void addNestedNodes(List<Integer> nestedNodes, Bubble bubble) {
    for (Integer node : nestedNodes) {
      bubble.addNestedNode(node);
    }
  }
  
  private void createBubble(IPhylogeneticTreeNode treeNode, int inlink, List<Integer> bubbleLinks, 
      List<String> leaves, Queue<Integer> toVisit, Set<Integer> visited, List<Bubble> newBubbles, 
      GraphInterface filteredGraph) {
    if (!bubbleLinks.isEmpty()) {
      Bubble newBubble = new Bubble(mutationId, 1);
      newBubble.setTreeNode(treeNode);
      if (inlink != -1) {
        newBubble.addInlink(inlink);
      }
      addNestedNodes(bubbleLinks, newBubble);
      Queue<Integer> toVisitNodes = makeBubble(newBubble, bubbleLinks, leaves);
      while (!toVisitNodes.isEmpty()) {
        addToVisit(toVisitNodes.poll(), toVisit, visited);
      }

      newBubbles.add(newBubble);
      filteredGraph.addNode(newBubble);
      mutationId++;
    }
  }
  
  private void addToVisit(int visitor, Queue<Integer> toVisit, Set<Integer> visited) {
    if (!toVisit.contains(visitor) && !visited.contains(visitor)) {
      toVisit.add(visitor);
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
      if (bubble != null && !bubble.getNestedNodes().contains(outlink)) {
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
  
  private void pruneNodes(GraphInterface zoomedGraph, GraphInterface oldGraph, 
      ArrayList<Bubble> newBubbles) {
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
}
