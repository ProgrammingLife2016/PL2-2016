package nl.tudelft.pl2016gr2.core.algorithms.bubbles.tree;

import nl.tudelft.pl2016gr2.core.algorithms.bubbles.AbstractBubbleFilter;
import nl.tudelft.pl2016gr2.core.algorithms.bubbles.FilterHelpers;
import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.visitor.BubbleChildrenVisitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Class used to make PhyloBubbles. These are bubbles based on the phylogenetic tree.
 * 
 * @author Casper
 *
 */
public class PhyloBubbleFilter extends AbstractBubbleFilter {
  
  private int mutationId = -1;
  
  /**
   * Creates an instance of this class.
   * 
   * @param orderedNodes : the nodes of the original graph.
   */
  public PhyloBubbleFilter(Collection<GraphNode> orderedNodes) {
    super(orderedNodes);
  }
  
  /**
   * Zooms in on this node, if it's a bubble, by going down a level in the phylogenetic tree.
   *
   * @param bubble the bubble to zoom in on.
   * @return a zoomed in graph.
   */
  @Override
  public List<GraphNode> zoomIn(Bubble bubble) {
    mutationId--;
    return new PhyloBubbleZoom(this).zoom(bubble);
  }

  /**
   * Filters the input graph of this object, using the phylogenetic tree. It finds all leaves of the
   * current node of the tree, which correspond to a certain genome. The filtered graph then only
   * shows nodes that have all these genomes going through them, and shows mutation nodes in between
   * these.
   *
   * @param treeRoot the root of the tree.
   * @param genomes the genomes that are selected
   * @return a filtered graph.
   */
  public ArrayList<GraphNode> filter(IPhylogeneticTreeRoot<?> treeRoot, 
      Collection<Integer> genomes) {
    IPhylogeneticTreeRoot<?> newRoot = new TreeBuilder(treeRoot, genomes).getTree();
    Set<GraphNode> graphNodes = new HashSet<>();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(graphNodes, newRoot, newBubbles);
    ArrayList<GraphNode> poppedNodes = new ArrayList<>(graphNodes);
    pruneNodes(poppedNodes, newBubbles);

    Collections.sort(poppedNodes, (GraphNode first, GraphNode second) -> {
      return first.getLevel() - second.getLevel();
    });
    return poppedNodes;
  }

  private List<Bubble> debubble(Set<GraphNode> graphNodes, IPhylogeneticTreeNode<?> treeNode,
      ArrayList<Bubble> newBubbles) {
    mutationId--;
    ArrayList<Integer> leaves = treeNode.getGenomes();
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();

    Iterator<GraphNode> rootIterator = getRootNodes().iterator();
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

  /**
   * Filters a set of nodes to make bubbles out of them based on the phylogenetic tree.
   *
   * @param toVisit     : nodes which need to be visited.
   * @param visited     : nodes which have already been visited.
   * @param poppedNodes : nodes that are in the new graph.
   * @param leaves      : the leaves of the phylogenetic tree on which the bubbling is based.
   * @param bubble      : the bubble which is zoomed in on.
   * @param treeNode    : the treenode on which the bubbling is based.
   * @param newBubbles  : list of newly made bubbles.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  protected void filterBubbles(Queue<GraphNode> toVisit, Set<GraphNode> visited,
      Set<GraphNode> poppedNodes, ArrayList<Integer> leaves, Bubble bubble,
      IPhylogeneticTreeNode<?> treeNode, List<Bubble> newBubbles) {
    Set<GraphNode> endNodes = new HashSet<>();
    if (bubble != null) {
      endNodes.addAll(bubble.getOutEdges());
    }
    while (!toVisit.isEmpty()) {
      GraphNode next = toVisit.poll();
      if (bubble != null && endNodes.contains(next)) {
        continue;
      }
      next.setInEdges(new HashSet<>());
      next.setOutEdges(new HashSet<>());
      visited.add(next);
      poppedNodes.add(next);
      List<GraphNode> bubbleLinks = new ArrayList<>();
      for (GraphNode outlink : calcNodeOutlinks(getOriginalOutEdges().get(next.getId()),
          leaves, bubble)) {
        GraphNode node = outlink;
        if (FilterHelpers.isShared(node, leaves)) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        } else {
          bubbleLinks.add(outlink);
        }
      }
      createBubble(treeNode, next, bubbleLinks, leaves, toVisit, visited, newBubbles, poppedNodes);
    }
  }

  private void addNestedNodes(List<GraphNode> nestedNodes, Bubble bubble) {
    for (GraphNode node : nestedNodes) {
      BubbleChildrenVisitor visitor = new BubbleChildrenVisitor(node);
      bubble.accept(visitor);
    }
  }

  private void createBubble(IPhylogeneticTreeNode<?> treeNode, GraphNode inlink,
      List<GraphNode> bubbleLinks, List<Integer> leaves, Queue<GraphNode> toVisit,
      Set<GraphNode> visited, List<Bubble> newBubbles, Set<GraphNode> poppedNodes) {
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

  private Queue<GraphNode> makeBubble(Bubble bubble, List<GraphNode> start, List<Integer> leaves) {
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
        bubble.accept(new BubbleChildrenVisitor(next));

        for (GraphNode outlink : next.getOutEdges()) {
          FilterHelpers.addToVisit(outlink, toVisit, visited);
        }
      }
    }

    return endPoints;
  }

  private List<GraphNode> calcNodeOutlinks(Collection<GraphNode> outEdges,
      ArrayList<Integer> leaves, Bubble bubble) {
    List<GraphNode> curNodeOutlinks = new ArrayList<>();

    for (GraphNode outlink : outEdges) {
      if (bubble != null && !bubble.hasChild(outlink)) {
        continue;
      }
      ArrayList<Integer> genomes = new ArrayList<>(outlink.getGenomes());

      for (Integer leaf : leaves) {
        if (genomes.contains(leaf)) {
          curNodeOutlinks.add(outlink);
          break;
        }
      }
    }

    return curNodeOutlinks;
  }
  
  
}
