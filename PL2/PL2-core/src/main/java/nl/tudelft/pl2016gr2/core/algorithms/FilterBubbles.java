package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.HashGraph;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.PhyloBubble;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.visitor.BubbleChildrenVisitor;
import nl.tudelft.pl2016gr2.visitor.BubblePhyloVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
  
  private SequenceGraph originalGraph;
  private int mutationId;
  private Map<Integer, Map<Integer, Stack<List<GraphNode>>>> oldGraphs = new HashMap<>();
  
  /**
   * Creates a FilterBubbles object, with the graph to be filtered and the node of the phylogenetic
   * tree based on which this graph will be filtered.
   * @param originalGraph : the graph to be filtered.
   */
  public FilterBubbles(SequenceGraph originalGraph) {
    this.originalGraph = originalGraph;
    // This should be based on highest id just to be safe
    mutationId = originalGraph.size() + 1;
  }
  
  public SequenceGraph zoom(Bubble bubble, SequenceGraph graph) {
    BubblePhyloVisitor visitor = new BubblePhyloVisitor();
    bubble.accept(visitor);
    IPhylogeneticTreeNode curTreeNode = visitor.getTreeNode();
    
    int inlink = bubble.getInEdges().iterator().next();
    int outlink = bubble.getOutEdges().iterator().next();
    saveOldGraph(inlink, outlink, graph);
    
    if (bubble.getChildren().size() == 1) {
      graph.remove(bubble.getId(), true, true);
      GraphNode child = originalGraph.getNode(bubble.getChildren().iterator().next());
      child.getInEdges().forEach(inEdge -> {
        graph.getNode(inEdge).addOutEdge(child.getId());
      });
      child.getOutEdges().forEach(outEdge -> {
        graph.getNode(outEdge).addInEdge(child.getId());
      });
//      graph.replace(bubble, originalGraph.getNode(bubble.getChildren().iterator().next()));
      return graph;
    }
    
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
 
    SequenceGraph zoomedGraph = new HashGraph();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(zoomedGraph, childOne, bubble, newBubbles);
    debubble(zoomedGraph, childTwo, bubble, newBubbles);
    
 // Add outlink to other bubbles that are not affected
    GraphNode startNode = zoomedGraph.getNode(inlink);
    GraphNode oldStartNode = graph.getNode(inlink);
    startNode.setInEdges(oldStartNode.getInEdges());
    for (Integer curOutlink : oldStartNode.getOutEdges()) {
      if (curOutlink != bubble.getId() && !startNode.getOutEdges().contains(curOutlink)) {
        startNode.addOutEdge(curOutlink);
      }
    }
    
    // Add inlink to other bubbles that are not affected
    GraphNode endNode = zoomedGraph.getNode(outlink);
    GraphNode oldEndNode = graph.getNode(outlink);
    endNode.setOutEdges(oldEndNode.getOutEdges());
    for (Integer curInlink : oldEndNode.getInEdges()) {
      if (curInlink != bubble.getId() && !endNode.getInEdges().contains(curInlink)) {
        endNode.addInEdge(curInlink);
      }
    }
    
    pruneNodes(zoomedGraph, graph, newBubbles);
    replace(bubble, graph, zoomedGraph);
    //graph.replace(bubble, zoomedGraph);
    return graph;
  }
  
  private void replace(Bubble bubble, SequenceGraph completeGraph, SequenceGraph partGraph) {
    completeGraph.remove(bubble.getId(), false, false);
    
    Iterator<GraphNode> iterator = partGraph.iterator();
    while (iterator.hasNext()) {
      GraphNode next = iterator.next();
      completeGraph.add(next);
    }
  }
  
  private void saveOldGraph(int start, int end, SequenceGraph graph) {
    List<GraphNode> oldView = getNodesOnPath(start, end, graph, true);
    printNodeList(oldView, "Old nodes: ");
    
    if (oldGraphs.containsKey(start)) {
      if (oldGraphs.get(start).containsKey(end)) {
        oldGraphs.get(start).get(end).push(oldView);
      } else {
        Stack<List<GraphNode>> stack = new Stack<>();
        stack.push(oldView);
        oldGraphs.get(start).put(end, stack);
      }
    } else {
      Map<Integer, Stack<List<GraphNode>>> endMap = new HashMap<>();
      Stack<List<GraphNode>> stack = new Stack<>();
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
  public SequenceGraph filter(IPhylogeneticTreeNode treeRoot) {
    SequenceGraph filteredGraph = new HashGraph();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    System.out.println("Debubble!");
    debubble(filteredGraph, treeRoot, newBubbles);
    pruneNodes(filteredGraph, originalGraph, newBubbles);
    
    return filteredGraph;
  }
  
  public SequenceGraph zoomOut(IPhylogeneticTreeNode treeNode, Bubble bubble, 
      SequenceGraph graph) {
    // find start
    if (!treeNode.hasParent()) {
      return graph;
    }
    
    IPhylogeneticTreeNode parent = treeNode.getParent();
    
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    
    toVisit.add(bubble.getId());
    ArrayList<String> leaves = parent.getGenomes();
    int start = -1;
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      if (originalGraph.getNode(next) == null) {
        for (Integer inlink : graph.getNode(next).getInEdges()) {
          addToVisit(inlink, toVisit, visited);
        }
      } else {
        GraphNode curNode = originalGraph.getNode(next);
        if (isShared(curNode, leaves)) {
          start = curNode.getId();
          System.out.println("Start: " + start);
          break;
        } else {
          for (Integer inlink : graph.getNode(next).getInEdges()) {
            addToVisit(inlink, toVisit, visited);
          }
        }
      }
    }
    
    int end = -1;
    toVisit.clear();
    visited.clear();
    toVisit.addAll(bubble.getOutEdges());
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      if (originalGraph.getNode(next) == null) {
        for (Integer outlink : graph.getNode(next).getOutEdges()) {
          addToVisit(outlink, toVisit, visited);
        }
      } else {
        GraphNode curNode = originalGraph.getNode(next);
        if (isShared(curNode, leaves)) {
          end = curNode.getId();
          System.out.println("End: " + end);
          break;
        } else {
          for (Integer outlink : graph.getNode(next).getOutEdges()) {
            addToVisit(outlink, toVisit, visited);
          }
        }
      }
    }
    
    graph = getPreviousView(start, end, graph);
    return graph;
  }
  
  private SequenceGraph getPreviousView(int start, int end, SequenceGraph graph) {
    if (!oldGraphs.containsKey(start) || !oldGraphs.get(start).containsKey(end) 
        || oldGraphs.get(start).get(end).isEmpty()) {
      return graph;
    }
    
    List<GraphNode> nodesToRemove = getNodesOnPath(start, end, graph, false);
    printNodeList(nodesToRemove, "Nodes to remove: ");
    nodesToRemove.forEach(node -> {
      if (node.getId() != start && node.getId() != end) {
        graph.remove(node.getId(), true, true);
      } else {
        graph.remove(node.getId(), false, false);
      }
    });
    
    List<GraphNode> previousView = oldGraphs.get(start).get(end).pop();
    printNodeList(previousView, "Nodes to add: ");
    previousView.forEach(node -> {
      graph.add(node);
    });
    
    return graph;
  }
  
  private void printNodeList(List<GraphNode> list, String header) {
    System.out.print(header + "[");
    list.forEach(node -> {
      System.out.print(node.getId() + ", ");
    });
    System.out.println("]");
  }
  
  private List<GraphNode> getNodesOnPath(int start, int end, SequenceGraph graph, boolean copy) {
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    
    List<GraphNode> nodesOnPath = new ArrayList<>();
    Set<Integer> seenPaths = new HashSet<>();
    toVisit.add(start);
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      GraphNode node = graph.getNode(next);
      if (copy) {
        nodesOnPath.add(node.copyAll());
      } else {
        nodesOnPath.add(node);
      }
      
      for (Integer outlink : node.getOutEdges()) {
        if (seenPaths.contains(outlink) || hasPathTo(outlink, end, graph, seenPaths)) {
          addToVisit(outlink, toVisit, visited);
        }
      }
    }
    
    return nodesOnPath;
  }
  
  private boolean hasPathTo(int from, int to, SequenceGraph graph, Set<Integer> seenPaths) {
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
      
      GraphNode GraphNode = graph.getNode(next);
      
      if (next < to || !(GraphNode instanceof Node)) {
        for (Integer outlink : GraphNode.getOutEdges()) {
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
  
  private List<Bubble> debubble(SequenceGraph filteredGraph, IPhylogeneticTreeNode treeNode, 
      ArrayList<Bubble> newBubbles) {
    mutationId++;
    
    ArrayList<String> leaves = treeNode.getGenomes();
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();

    Collection<Integer> rootNodes = originalGraph.getRootNodes();
    //Node rootNode = originalGraph.getRootNodes();
    GraphNode rootNode = originalGraph.getNode(rootNodes.iterator().next());
    int rootId = rootNode.getId();
    if (isShared(rootNode, leaves)) {
      toVisit.add(rootId);
    } else {
      List<Integer> bubbleStart = new ArrayList<>();
      bubbleStart.add(rootId);
      createBubble(treeNode, -1, bubbleStart, leaves, toVisit, visited, 
          newBubbles, filteredGraph);
    }
    
    filterBubbles(toVisit, visited, filteredGraph, leaves, null, treeNode, newBubbles);
    return newBubbles;
  }
  
  private List<Bubble> debubble(SequenceGraph filteredGraph, IPhylogeneticTreeNode treeNode, 
      Bubble bubble, ArrayList<Bubble> newBubbles) {
    mutationId++;
    GraphNode start = originalGraph.getNode(bubble.getInEdges().iterator().next());
    
    ArrayList<String> leaves = treeNode.getGenomes();
    Queue<Integer> toVisit = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    toVisit.add(start.getId());
    
    filterBubbles(toVisit, visited, filteredGraph, leaves, bubble, treeNode, newBubbles);
    
    GraphNode end = originalGraph.getNode(bubble.getOutEdges().iterator().next());
    filteredGraph.add(end.copy());
    
    return newBubbles;
  }
  
  private void filterBubbles(Queue<Integer> toVisit, Set<Integer> visited, 
      SequenceGraph filteredGraph, ArrayList<String> leaves, Bubble bubble, 
      IPhylogeneticTreeNode treeNode, List<Bubble> newBubbles) {
    Set<Integer> endIds = new HashSet<>();
    if (bubble != null) {
      endIds.addAll(bubble.getOutEdges());
    }
    
    while (!toVisit.isEmpty()) {
      int next = toVisit.poll();
      visited.add(next);
      GraphNode current = originalGraph.getNode(next);
      filteredGraph.add(current.copy());
      if (bubble != null && endIds.contains(next)) {
        continue;
      }
      
      // DO SOMETHING HERE TO ALSO INCLUDE NODES WITH ONLY REF
      List<Integer> outlinks = calcNodeOutlinks(current, leaves, bubble);
      List<Integer> bubbleLinks = new ArrayList<>();

      for (Integer outlink : outlinks) {
        GraphNode node = originalGraph.getNode(outlink);
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
      BubbleChildrenVisitor visitor = new BubbleChildrenVisitor(node);
      bubble.accept(visitor);
    }
  }
  
  private void createBubble(IPhylogeneticTreeNode treeNode, int inlink, List<Integer> bubbleLinks, 
      List<String> leaves, Queue<Integer> toVisit, Set<Integer> visited, List<Bubble> newBubbles, 
      SequenceGraph filteredGraph) {
    if (!bubbleLinks.isEmpty()) {
      Bubble newBubble = new PhyloBubble(mutationId, treeNode);
      if (inlink != -1) {
        newBubble.addInEdge(inlink);
      }
      addNestedNodes(bubbleLinks, newBubble);
      Queue<Integer> toVisitNodes = makeBubble(newBubble, bubbleLinks, leaves);
      while (!toVisitNodes.isEmpty()) {
        addToVisit(toVisitNodes.poll(), toVisit, visited);
      }
      
      System.out.println("New bubble: " + newBubble);
      newBubbles.add(newBubble);
      filteredGraph.add(newBubble);
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
      GraphNode current = originalGraph.getNode(next);
      
      if (isShared(current, leaves)) {
        endPoints.add(next);
        bubble.addOutEdge(next);
      } else {
        BubbleChildrenVisitor visitor = new BubbleChildrenVisitor(next);
        bubble.accept(visitor);
        
        for (Integer outlink : current.getOutEdges()) {
          if (!(visited.contains(outlink) || toVisit.contains(outlink))) {
            toVisit.add(outlink);
          }
        }
      }
    }
     
    return endPoints;
  }
  
  private boolean isShared(GraphNode node, List<String> leaves) {
    if (node.getGenomes().containsAll(leaves)) {
      return true;
    }
    
    for (String genome : node.getGenomes()) {
      if (!genome.contains(".ref.") && !leaves.contains(genome)) {
        return true;
      }
    }
    
    return false;
  }
  
  private List<Integer> calcNodeOutlinks(GraphNode node, ArrayList<String> leaves, Bubble bubble) {
    List<Integer> curNodeOutlinks = new ArrayList<>();

    for (Integer outlink : node.getOutEdges()) {
      if (bubble != null && !bubble.getChildren().contains(outlink)) {
        continue;
      }
      ArrayList<String> genomes = new ArrayList<>(originalGraph.getNode(outlink).getGenomes());
      
      for (String leaf : leaves) {
        if (genomes.contains(leaf)) {
          curNodeOutlinks.add(outlink);
          break;
        }
      }
    }
    
    return curNodeOutlinks;
  }
  
  private void pruneNodes(SequenceGraph zoomedGraph, SequenceGraph oldGraph, 
      ArrayList<Bubble> newBubbles) {
    newBubbles.forEach(bubble -> {
      Collection<Integer> inlinks = bubble.getInEdges();
      if (!inlinks.isEmpty()) {
        zoomedGraph.getNode(inlinks.iterator().next()).addOutEdge(bubble.getId());
      }
      
      Collection<Integer> outlinks = bubble.getOutEdges();
      if (!outlinks.isEmpty()) {
        outlinks.forEach(outlink -> {
          zoomedGraph.getNode(outlink).addInEdge(bubble.getId());
        });
      }
    });
    
    Iterator<GraphNode> iterator = zoomedGraph.iterator();
    while (iterator.hasNext()) {
      GraphNode node = iterator.next();
      int id = node.getId();
      if (id >= originalGraph.size()) {
        return;
      }
      Collection<Integer> inlinks = pruneInlinks(originalGraph.getNode(id), zoomedGraph);
      inlinks.forEach(node::addInEdge);
      Collection<Integer> outlinks = pruneOutlinks(originalGraph.getNode(id), zoomedGraph);
      outlinks.forEach(node::addOutEdge);
    }
  }
  
  private Collection<Integer> pruneInlinks(GraphNode original, SequenceGraph zoomedGraph) {
    Collection<Integer> prunedInlinks = new ArrayList<>();
    original.getInEdges().forEach(link -> {
      if (zoomedGraph.contains(link)) {
        prunedInlinks.add(link);
      }
    });
    return prunedInlinks;
  }
  
  private Collection<Integer> pruneOutlinks(GraphNode original, SequenceGraph zoomedGraph) {
    Collection<Integer> prunedOutlinks = new ArrayList<>();
    original.getOutEdges().forEach(link -> {
      if (zoomedGraph.contains(link)) {
        prunedOutlinks.add(link);
      }
    });
    return prunedOutlinks;
  }
}
