package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.graph.nodes.Bubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;
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

/**
 * Class to zoom in on phylo bubbles.
 * 
 * @author Casper
 *
 */
public class PhyloBubbleZoom extends AbstractZoom {
  
  private final PhyloBubbleFilter filter;
  
  /**
   * Creates an object to zoom in on a phylo bubble with.
   * 
   * @param filter an instance of FilterPhyloBubbles.
   */
  protected PhyloBubbleZoom(PhyloBubbleFilter filter) {
    super();
    this.filter = filter;
  }
  
  @Override
  public List<GraphNode> zoom(Bubble bubble) {
    Map<Integer, Collection<GraphNode>> originalInEdges = new HashMap<>();
    Map<Integer, Collection<GraphNode>> originalOutEdges = new HashMap<>();
    setOriginalEdges(originalInEdges, originalOutEdges, bubble);
    IPhylogeneticTreeNode curTreeNode = getTreeNode(bubble);
    IPhylogeneticTreeNode childOne = curTreeNode.getChild(0);
    IPhylogeneticTreeNode childTwo = curTreeNode.getChild(1);
    Set<GraphNode> poppedNodes = new HashSet<>();
    ArrayList<Bubble> newBubbles = new ArrayList<>();
    debubble(poppedNodes, childOne, bubble, newBubbles);
    debubble(poppedNodes, childTwo, bubble, newBubbles);
    pruneStart(bubble.getInEdges(), originalInEdges, originalOutEdges, bubble.getId());
    pruneEnd(bubble.getOutEdges(), originalOutEdges, originalInEdges, bubble.getId());
    ArrayList<GraphNode> graphNodes = new ArrayList<>(poppedNodes);
    filter.pruneNodes(graphNodes, newBubbles);
    return alignNodes(graphNodes, bubble);
  }
  
  private IPhylogeneticTreeNode getTreeNode(Bubble bubble) {
    BubblePhyloVisitor visitor = new BubblePhyloVisitor();
    bubble.accept(visitor);
    return visitor.getTreeNode();
  }
  
  private List<Bubble> debubble(Set<GraphNode> poppedNodes, IPhylogeneticTreeNode treeNode, 
      Bubble bubble, ArrayList<Bubble> newBubbles) {
    Collection<GraphNode> startNodes = bubble.getInEdges();
    ArrayList<Integer> leaves = treeNode.getGenomes();
    Queue<GraphNode> toVisit = new LinkedList<>();
    Set<GraphNode> visited = new HashSet<>();
    toVisit.addAll(startNodes);
    filter.filterBubbles(toVisit, visited, poppedNodes, leaves, bubble, treeNode, newBubbles);
    Iterator<GraphNode> iterator = bubble.getOutEdges().iterator();
    while (iterator.hasNext()) {
      GraphNode next = iterator.next();
      next.setInEdges(new HashSet<>());
      next.setOutEdges(new HashSet<>());
      poppedNodes.add(next);
    }
    return newBubbles;
  }
}
