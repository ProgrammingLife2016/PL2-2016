package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.HashGraph;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.PhyloBubble;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.visitor.BubbleChildrenVisitor;

import java.util.ArrayList;
import java.util.Collection;
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
public class FilterBubbles {

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
    // This should be based on highest id just to be safe (instead of size)
    mutationId = -1;
  }

  /**
   * Zooms out on this node, if it's a bubble, in the given graph.
   *
   * @param node  bubble to zoom out on
   * @param graph the graph
   * @return a zoomed out graph
   */
  public SequenceGraph zoomOut(GraphNode node, SequenceGraph graph) {
    if (!node.hasChildren()) {
      return graph;
    } else {
      Bubble bubble = (Bubble) node;
      return zoomOut.zoomOut(bubble, graph);
    }
  }

  /**
   * Zooms in on this node, if it's a bubble, by going down a level in the phylogenetic tree.
   *
   * @param node  the node to zoom in on
   * @param graph the graph
   * @return a zoomed in graph
   */
  public SequenceGraph zoomIn(GraphNode node, SequenceGraph graph) {
    if (!node.hasChildren()) {
      return graph;
    } else {
      Bubble bubble = (Bubble) node;
      mutationId--;
      return new ZoomIn(originalGraph, zoomOut, this).zoom(bubble, graph);
    }
  }

  /**
   * Filters the input graph of this object, using the phylogenetic tree. It finds all leaves of the
   * current node of the tree, which correspond to a certain genome. The filtered graph then only
   * shows nodes that have all these genomes going through them, and shows mutation nodes in between
   * these.
   *
   * @param treeRoot the root of the tree.
   * @return a filtered graph.
   */
  public SequenceGraph filter(IPhylogeneticTreeNode treeRoot) {
    SequenceGraph filteredGraph = new HashGraph();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(filteredGraph, treeRoot, newBubbles);
    pruneNodes(filteredGraph, originalGraph, newBubbles);

    return filteredGraph;
  }

  private List<Bubble> debubble(SequenceGraph filteredGraph, IPhylogeneticTreeNode treeNode,
      ArrayList<Bubble> newBubbles) {
    mutationId--;

    ArrayList<String> leaves = treeNode.getGenomes();
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();

    Collection<GraphNode> rootNodes = originalGraph.getRootNodes();
    //Node rootNode = originalGraph.getRootNodes();
    GraphNode rootNode = rootNodes.iterator().next();
    if (FilterHelpers.isShared(rootNode, leaves)) {
      toVisit.add(rootNode);
    } else {
      List<GraphNode> bubbleStart = new ArrayList<>();
      bubbleStart.add(rootNode);
      createBubble(treeNode, null, bubbleStart, leaves, toVisit, visited,
          newBubbles, filteredGraph);
    }

    filterBubbles(toVisit, visited, filteredGraph, leaves, null, treeNode, newBubbles);
    return newBubbles;
  }

  protected void filterBubbles(Queue<GraphNode> toVisit, Set<GraphNode> visited,
      SequenceGraph filteredGraph, ArrayList<String> leaves, Bubble bubble,
      IPhylogeneticTreeNode treeNode, List<Bubble> newBubbles) {
    Set<GraphNode> endNodes = new HashSet<>();
    if (bubble != null) {
      endNodes.addAll(bubble.getOutEdges());
    }

    while (!toVisit.isEmpty()) {
      GraphNode next = toVisit.poll();
      visited.add(next);
      GraphNode current = next;
      filteredGraph.add(current.copy());
      if (bubble != null && endNodes.contains(next)) {
        continue;
      }

      // DO SOMETHING HERE TO ALSO INCLUDE NODES WITH ONLY REF
      List<GraphNode> outlinks = calcNodeOutlinks(current, leaves, bubble);
      List<GraphNode> bubbleLinks = new ArrayList<>();

      for (GraphNode outlink : outlinks) {
        GraphNode node = originalGraph.getNode(outlink.getId());
        if (FilterHelpers.isShared(node, leaves)) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        } else {
          bubbleLinks.add(outlink);
        }
      }

      createBubble(treeNode, next, bubbleLinks, leaves, toVisit, visited,
          newBubbles, filteredGraph);
    }
  }

  private void addNestedNodes(List<GraphNode> nestedNodes, Bubble bubble) {
    for (GraphNode node : nestedNodes) {
      BubbleChildrenVisitor visitor = new BubbleChildrenVisitor(originalGraph.getNode(node.getId()));
      bubble.accept(visitor);
    }
  }

  private void createBubble(IPhylogeneticTreeNode treeNode, GraphNode inlink,
      List<GraphNode> bubbleLinks,
      List<String> leaves, Queue<GraphNode> toVisit, Set<GraphNode> visited, List<Bubble> newBubbles,
      SequenceGraph filteredGraph) {
    if (!bubbleLinks.isEmpty()) {
      Bubble newBubble = new PhyloBubble(mutationId, treeNode);
      if (inlink != null) {
        newBubble.addInEdge(inlink);
      }
      addNestedNodes(bubbleLinks, newBubble);
      Queue<GraphNode> toVisitNodes = makeBubble(newBubble, bubbleLinks, leaves);
      while (!toVisitNodes.isEmpty()) {
        FilterHelpers.addToVisit(toVisitNodes.poll(), toVisit, visited);
      }

      newBubbles.add(newBubble);
      filteredGraph.add(newBubble);
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
      GraphNode current = originalGraph.getNode(next.getId());

      if (FilterHelpers.isShared(current, leaves)) {
        endPoints.add(next);
        bubble.addOutEdge(next);
      } else {
        BubbleChildrenVisitor visitor = new BubbleChildrenVisitor(originalGraph.
            getNode(next.getId()));
        bubble.accept(visitor);

        for (GraphNode outlink : current.getOutEdges()) {
          if (!(visited.contains(outlink) || toVisit.contains(outlink))) {
            toVisit.add(outlink);
          }
        }
      }
    }

    return endPoints;
  }

  private List<GraphNode> calcNodeOutlinks(GraphNode node, ArrayList<String> leaves, Bubble bubble) {
    List<GraphNode> curNodeOutlinks = new ArrayList<>();

    for (GraphNode outlink : node.getOutEdges()) {
      if (bubble != null && !bubble.hasChild(outlink)) {
        continue;
      }
      ArrayList<String> genomes = new ArrayList<>(originalGraph.getNode(outlink.getId()).
          getGenomes());

      for (String leaf : leaves) {
        if (genomes.contains(leaf)) {
          curNodeOutlinks.add(outlink);
          break;
        }
      }
    }

    return curNodeOutlinks;
  }

  protected void pruneNodes(SequenceGraph zoomedGraph, SequenceGraph oldGraph,
      ArrayList<Bubble> newBubbles) {
    newBubbles.forEach(bubble -> {
      Collection<GraphNode> inlinks = bubble.getInEdges();
      if (!inlinks.isEmpty()) {
        zoomedGraph.getNode(inlinks.iterator().next().getId()).addOutEdge(bubble);
      }

      Collection<GraphNode> outlinks = bubble.getOutEdges();
      if (!outlinks.isEmpty()) {
        outlinks.forEach(outlink -> {
          zoomedGraph.getNode(outlink.getId()).addInEdge(bubble);
        });
      }
    });

    Iterator<GraphNode> iterator = zoomedGraph.iterator();
    while (iterator.hasNext()) {
      GraphNode node = iterator.next();
      if (node.hasChildren()) {
        continue;
      }
      Collection<GraphNode> inlinks = pruneInlinks(originalGraph.getNode(node.getId()), zoomedGraph);
      inlinks.forEach(node::addInEdge);
      Collection<GraphNode> outlinks = pruneOutlinks(originalGraph.getNode(node.getId()),
          zoomedGraph);
      outlinks.forEach(node::addOutEdge);
    }
  }

  private Collection<GraphNode> pruneInlinks(GraphNode original, SequenceGraph zoomedGraph) {
    Collection<GraphNode> prunedInlinks = new ArrayList<>();
    original.getInEdges().forEach(link -> {
      if (zoomedGraph.contains(link)) {
        prunedInlinks.add(link);
      }
    });
    return prunedInlinks;
  }

  private Collection<GraphNode> pruneOutlinks(GraphNode original, SequenceGraph zoomedGraph) {
    Collection<GraphNode> prunedOutlinks = new ArrayList<>();
    original.getOutEdges().forEach(link -> {
      if (zoomedGraph.contains(link)) {
        prunedOutlinks.add(link);
      }
    });
    return prunedOutlinks;
  }
}
