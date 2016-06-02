package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.core.algorithms.subgraph.CompareSubgraphs;
import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.PhyloBubble;
import nl.tudelft.pl2016gr2.model.PhyloFilter;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.visitor.BubbleChildrenVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Class used to filter bubbles based on the phylogenetic tree.
 *
 * @author Casper
 *
 */
public class FilterBubbles implements PhyloFilter {

  private final SequenceGraph originalGraph;
  private final ZoomOut zoomOut;
  private int mutationId;

  /**
   * Creates a FilterBubbles object, with the graph to be filtered and the node of the phylogenetic
   * tree based on which this graph will be filtered.
   *
   * @param originalGraph : the graph to be filtered.
   */
  public FilterBubbles(SequenceGraph originalGraph) {
    this.originalGraph = originalGraph;
    zoomOut = new ZoomOut(originalGraph);
    mutationId = -1;
  }

  /**
   * Zooms out on this node, if it's a bubble, in the given graph.
   *
   * @param node  bubble to zoom out on
   * @param graph the graph
   * @return a zoomed out graph
   */
  @Override
  public SequenceGraph zoomOut(Bubble bubble, SequenceGraph graph) {
    return zoomOut.zoomOut(bubble, graph);
  }

  /**
   * Zooms in on this node, if it's a bubble, by going down a level in the phylogenetic tree.
   *
   * @param node  the node to zoom in on
   * @param graph the graph
   * @return a zoomed in graph
   */
  @Override
  public Collection<GraphNode> zoomIn(Bubble bubble) {
    mutationId--;
    List<GraphNode> orderedNodes = new ZoomIn(originalGraph, zoomOut, this).zoom(bubble);
    orderedNodes.sort((GraphNode node1, GraphNode node2) -> node1.getLevel() - node2.getLevel());
    
    // temporary fix for exception in alignVertically
    for (GraphNode sortedNestedNode : orderedNodes) {
      sortedNestedNode.setRelativeYPos(0.5);
      sortedNestedNode.setMaxHeight(0.1);
    }
//    CompareSubgraphs.alignVertically(sortedNestedNodes);

    return orderedNodes;
  }

  /**
   * Filters the input graph of this object, using the phylogenetic tree. It finds all leaves of the
   * current node of the tree, which correspond to a certain genome. The filtered graph then only
   * shows nodes that have all these genomes going through them, and shows mutation nodes in between
   * these.
   *
   * @param treeRoot the root of the tree.
   * @parem genomes the genomes that are selected
   * @return a filtered graph.
   */
  public ArrayList<GraphNode> filter(IPhylogeneticTreeRoot treeRoot, Collection<String> genomes) {
    IPhylogeneticTreeRoot newRoot = new BuildTree(treeRoot, genomes).getTree();
    System.out.println("Genomes of new tree: " + newRoot.getGenomes());

    ArrayList<GraphNode> graphNodes = new ArrayList<>();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(graphNodes, newRoot, newBubbles);
    pruneNodes(graphNodes, newBubbles);

    Collections.sort(graphNodes, (GraphNode node1, GraphNode node2) -> {
      return node1.getLevel() - node2.getLevel();
    });

    ////////////////////////////////////////////
    // TEMPORARY HACK TO GET THE RIGHT EDGES:
    // currently nodes got in and out edges to the incorrect (main) graph.
    HashMap<GraphNode, GraphNode> graphMap = new HashMap<>();
    for (GraphNode graphNode : graphNodes) {
      graphMap.put(graphNode, graphNode);
    }
    for (GraphNode graphNode : graphNodes) {
      ArrayList<GraphNode> inEdges = new ArrayList<>();
      for (GraphNode inEdge : graphNode.getInEdges()) {
        inEdges.add(graphMap.get(inEdge));
      }
      graphNode.setInEdges(inEdges);
      ArrayList<GraphNode> outEdges = new ArrayList<>();
      for (GraphNode outEdge : graphNode.getOutEdges()) {
        outEdges.add(graphMap.get(outEdge));
      }
      graphNode.setOutEdges(outEdges);
    }
    ////////////////////////////////////////////
    return graphNodes;
  }

  // TODO : REMOVE
  private void printGraphNodes(List<GraphNode> graphNodes) {
    for (GraphNode node : graphNodes) {
      System.out.println(node.getId() + ", in: " + printIds(node.getInEdges())
          + ", out: " + printIds(node.getOutEdges()));
    }
  }

  private String printIds(Collection<GraphNode> nodes) {
    StringBuilder builder = new StringBuilder();
    builder.append("[");
    for (GraphNode node : nodes) {
      builder.append(node.getId() + ", ");
    }
    builder.append("]");
    return builder.toString();
  }

  private List<Bubble> debubble(List<GraphNode> graphNodes, IPhylogeneticTreeNode treeNode,
      ArrayList<Bubble> newBubbles) {
    mutationId--;
    ArrayList<String> leaves = treeNode.getGenomes();
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();

    Iterator<GraphNode> rootIterator = originalGraph.getRootNodes().iterator();
    while (rootIterator.hasNext()) {
      GraphNode rootNode = rootIterator.next();
      if (FilterHelpers.isShared(rootNode, leaves)) {
        toVisit.add(rootNode);
      } else {
        List<GraphNode> bubbleStart = new ArrayList<>();
        bubbleStart.add(rootNode);
        createBubble(treeNode, null, bubbleStart, leaves, toVisit, visited,
            newBubbles, graphNodes);
      }
    }

    filterBubbles(toVisit, visited, graphNodes, leaves, null, treeNode, newBubbles);
    return newBubbles;
  }

  protected void filterBubbles(Queue<GraphNode> toVisit, Set<GraphNode> visited,
      List<GraphNode> poppedNodes, ArrayList<String> leaves, Bubble bubble,
      IPhylogeneticTreeNode treeNode, List<Bubble> newBubbles) {
    Set<GraphNode> endNodes = new HashSet<>();
    if (bubble != null) {
      endNodes.addAll(bubble.getOutEdges());
    }
    while (!toVisit.isEmpty()) {
      GraphNode next = toVisit.poll();
      visited.add(next);
      poppedNodes.add(next.copy());
      if (bubble != null && endNodes.contains(next)) {
        continue;
      }

      // DO SOMETHING HERE TO ALSO INCLUDE NODES WITH ONLY REF
      List<GraphNode> outlinks = calcNodeOutlinks(next, leaves, bubble);
      List<GraphNode> bubbleLinks = new ArrayList<>();

      for (GraphNode outlink : outlinks) {
        GraphNode node = outlink;
        if (FilterHelpers.isShared(node, leaves)) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        } else {
          bubbleLinks.add(outlink);
        }
      }

      createBubble(treeNode, next, bubbleLinks, leaves, toVisit, visited,
          newBubbles, poppedNodes);
    }
  }

  private void addNestedNodes(List<GraphNode> nestedNodes, Bubble bubble) {
    for (GraphNode node : nestedNodes) {
      BubbleChildrenVisitor visitor = new BubbleChildrenVisitor(node);
      bubble.accept(visitor);
    }
  }

  private void createBubble(IPhylogeneticTreeNode treeNode, GraphNode inlink,
      List<GraphNode> bubbleLinks, List<String> leaves, Queue<GraphNode> toVisit,
      Set<GraphNode> visited, List<Bubble> newBubbles, List<GraphNode> poppedNodes) {
    if (!bubbleLinks.isEmpty()) {
      Bubble newBubble = new PhyloBubble(mutationId, treeNode, this);
      if (inlink != null) {
        newBubble.addInEdge(inlink);
      }
      addNestedNodes(bubbleLinks, newBubble);
      Queue<GraphNode> toVisitNodes = makeBubble(newBubble, bubbleLinks, leaves);
      while (!toVisitNodes.isEmpty()) {
        FilterHelpers.addToVisit(toVisitNodes.poll(), toVisit, visited);
      }

      newBubbles.add(newBubble);
      poppedNodes.add(newBubble);
      mutationId--;
    }
  }

  private Queue<GraphNode> makeBubble(Bubble bubble, List<GraphNode> start, List<String> leaves) {
    Queue<GraphNode> endPoints = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();
    Queue<GraphNode> toVisit = new LinkedList<>();

    toVisit.addAll(start);

    while (!toVisit.isEmpty()) {
      GraphNode next = toVisit.poll();
      visited.add(next);

      if (FilterHelpers.isShared(next, leaves)) {
        endPoints.add(next);
        bubble.addOutEdge(next);
      } else {
        BubbleChildrenVisitor visitor = new BubbleChildrenVisitor(next);
        bubble.accept(visitor);

        for (GraphNode outlink : next.getOutEdges()) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        }
      }
    }

    return endPoints;
  }

  private List<GraphNode> calcNodeOutlinks(GraphNode node, ArrayList<String> leaves,
      Bubble bubble) {
    List<GraphNode> curNodeOutlinks = new ArrayList<>();

    for (GraphNode outlink : node.getOutEdges()) {
      if (bubble != null && !bubble.hasChild(outlink)) {
        continue;
      }
      ArrayList<String> genomes = new ArrayList<>(outlink.getGenomes());

      for (String leaf : leaves) {
        if (genomes.contains(leaf)) {
          curNodeOutlinks.add(outlink);
          break;
        }
      }
    }

    return curNodeOutlinks;
  }

  protected void pruneNodes(List<GraphNode> graphNodes, ArrayList<Bubble> newBubbles) {
    pruneBubbles(newBubbles);

    Iterator<GraphNode> iterator = graphNodes.iterator();
    while (iterator.hasNext()) {
      GraphNode node = iterator.next();
      if (node.hasChildren()) {
        continue;
      }
      // Get the original inedges and see which ones are still in the graph
      Collection<GraphNode> inlinks = pruneLinks(
          originalGraph.getNode(node.getId()).getInEdges(), graphNodes);
      inlinks.forEach(node::addInEdge);
      Collection<GraphNode> outlinks = pruneLinks(
          originalGraph.getNode(node.getId()).getOutEdges(), graphNodes);
      outlinks.forEach(node::addOutEdge);
    }
  }

  private void pruneBubbles(ArrayList<Bubble> newBubbles) {
    newBubbles.forEach(bubble -> {
      Iterator<GraphNode> inlinks = bubble.getInEdges().iterator();
      while (inlinks.hasNext()) {
        inlinks.next().addOutEdge(bubble);
      }

      Iterator<GraphNode> outlinks = bubble.getOutEdges().iterator();
      while (outlinks.hasNext()) {
        outlinks.next().addInEdge(bubble);
      }
    });
  }

  private Collection<GraphNode> pruneLinks(Collection<GraphNode> links, List<GraphNode> graphNodes) {
    Collection<GraphNode> prunedLinks = new ArrayList<>();
    links.forEach(link -> {
      if (graphNodes.contains(link)) {
        prunedLinks.add(link);
      }
    });
    return prunedLinks;
  }
}
