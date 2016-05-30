package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.PhyloBubble;
import nl.tudelft.pl2016gr2.model.PhyloFilter;
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
  public Collection<GraphNode> zoomIn(Bubble bubble, SequenceGraph graph) {
    mutationId--;
    return new ZoomIn(originalGraph, zoomOut, this).zoom(bubble, graph);
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
  public Collection<GraphNode> filter(IPhylogeneticTreeNode treeRoot) {
    List<GraphNode> graphNodes = new ArrayList<>();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(graphNodes, treeRoot, newBubbles);
    pruneNodes(graphNodes, newBubbles);

    return graphNodes;
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
//      GraphNode current = next;
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

  private List<GraphNode> calcNodeOutlinks(GraphNode node, ArrayList<String> leaves, Bubble bubble) {
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
    newBubbles.forEach(bubble -> {
      Collection<GraphNode> inlinks = bubble.getInEdges();
      if (!inlinks.isEmpty()) {
        inlinks.iterator().next().addOutEdge(bubble);
      }

      Collection<GraphNode> outlinks = bubble.getOutEdges();
      if (!outlinks.isEmpty()) {
        outlinks.forEach(outlink -> {
          outlink.addInEdge(bubble);
        });
      }
    });

    Iterator<GraphNode> iterator = graphNodes.iterator();
    while (iterator.hasNext()) {
      GraphNode node = iterator.next();
      if (node.hasChildren()) {
        continue;
      }
      Collection<GraphNode> inlinks = pruneInlinks(originalGraph.getNode(node.getId()), graphNodes);
      inlinks.forEach(node::addInEdge);
      Collection<GraphNode> outlinks = pruneOutlinks(originalGraph.getNode(node.getId()),
          graphNodes);
      outlinks.forEach(node::addOutEdge);
    }
  }

  private Collection<GraphNode> pruneInlinks(GraphNode original, List<GraphNode> graphNodes) {
    Collection<GraphNode> prunedInlinks = new ArrayList<>();
    original.getInEdges().forEach(link -> {
      if (graphNodes.contains(link)) {
        prunedInlinks.add(link);
      }
    });
    return prunedInlinks;
  }

  private Collection<GraphNode> pruneOutlinks(GraphNode original, List<GraphNode> graphNodes) {
    Collection<GraphNode> prunedOutlinks = new ArrayList<>();
    original.getOutEdges().forEach(link -> {
      if (graphNodes.contains(link)) {
        prunedOutlinks.add(link);
      }
    });
    return prunedOutlinks;
  }
}
