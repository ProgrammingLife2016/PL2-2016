package nl.tudelft.pl2016gr2.model.graph.nodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public abstract class AbstractGraphBubble extends Bubble {
  
  private final BubbleFilter filter;
  private List<GraphNode> poppedNodes;
  private boolean isPopped;

  private final HashMap<Integer, Collection<GraphNode>> originalOutEdges = new HashMap<>(4);
  private final HashMap<Integer, Collection<GraphNode>> originalInEdges = new HashMap<>(4);
  private final HashMap<Integer, Collection<GraphNode>> unpoppedOutEdges = new HashMap<>(4);
  private final HashMap<Integer, Collection<GraphNode>> unpoppedInEdges = new HashMap<>(4);
  
  protected AbstractGraphBubble(int id, BubbleFilter filter) {
    super(id);
    this.filter = filter;
  }
  
  protected AbstractGraphBubble(int id, BubbleFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges) {
    super(id, inEdges, outEdges);
    this.filter = filter;
  }
  
  protected AbstractGraphBubble(int id, BubbleFilter filter,
      Collection<GraphNode> inEdges, Collection<GraphNode> outEdges, List<GraphNode> nestedNodes) {
    super(id, inEdges, outEdges, nestedNodes);
    this.filter = filter;
  }
  
  protected AbstractGraphBubble(Bubble bubble, BubbleFilter filter) {
    super(bubble);
    this.filter = filter;
  }
  
  @Override
  public int getGenomeSize() {
    int count = 0;
    for (GraphNode inEdge : getInEdges()) {
      count += inEdge.getGenomeSize();
    }
    return count;
  }
  
  /**
   * Returns the bubblefilter of this bubble.
   * 
   * @return an instance of BubbleFilter.
   */
  protected BubbleFilter getFilter() {
    return filter;
  }
  
  @Override
  public boolean needsVerticalAligning() {
    return poppedNodes == null;
  }
  
  @Override
  public Collection<GraphNode> pop() {
    if (!isPopped) {
      isPopped = true;
      if (poppedNodes == null) {
        for (GraphNode node : getInEdges()) {
          originalOutEdges.put(node.getId(), new HashSet<>(node.getOutEdges()));
        }
        for (GraphNode node : getOutEdges()) {
          originalInEdges.put(node.getId(), new HashSet<>(node.getInEdges()));
        }
        poppedNodes = filter.zoomIn(this);
      } else {
        for (GraphNode node : getInEdges()) {
          node.setOutEdges(unpoppedOutEdges.get(node.getId()));
        }
        for (GraphNode node : getOutEdges()) {
          node.setInEdges(unpoppedInEdges.get(node.getId()));
        }
      }
    }
    return poppedNodes;
  }

  @Override
  public void unpop() {
    for (GraphNode child : getChildren()) {
      child.unpop();
    }
    if (isPopped) {
      isPopped = false;
      for (GraphNode node : getInEdges()) {
        unpoppedOutEdges.put(node.getId(), new HashSet<>(node.getOutEdges()));
      }
      for (GraphNode node : getOutEdges()) {
        unpoppedInEdges.put(node.getId(), new HashSet<>(node.getInEdges()));
      }
      for (GraphNode node : getInEdges()) {
        node.setOutEdges(originalOutEdges.get(node.getId()));
      }
      for (GraphNode node : getOutEdges()) {
        node.setInEdges(originalInEdges.get(node.getId()));
      }
    }
  }

  @Override
  public boolean isPopped() {
    return isPopped;
  }

}
