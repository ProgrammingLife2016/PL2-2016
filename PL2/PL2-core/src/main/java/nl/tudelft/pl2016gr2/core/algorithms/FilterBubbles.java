package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.GraphInterface;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.ArrayList;

public class FilterBubbles {
  
  private GraphInterface graph;
  private OriginalGraph originalGraph;
  private IPhylogeneticTreeNode treeRoot;
  
  public FilterBubbles(GraphInterface graph, OriginalGraph originalGraph, IPhylogeneticTreeNode root) {
    this.graph = graph;
    this.originalGraph = originalGraph;
    this.treeRoot = root;
  }
  
  public ArrayList<IPhylogeneticTreeNode> getLeaves() {
    ArrayList<IPhylogeneticTreeNode> leaves = new ArrayList<>();
    addLeaf(leaves, treeRoot);    
    return leaves;
  }
  
  private void addLeaf(ArrayList<IPhylogeneticTreeNode> leaves, IPhylogeneticTreeNode node) {
    if (node.isLeaf()) {
      leaves.add(node);
      return;
    }
    
    for (int i = 0; i < node.getDirectChildCount(); i++) {
      addLeaf(leaves, node.getChild(i));
    }
  }
  
}
