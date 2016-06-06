package nl.tudelft.pl2016gr2.visitor;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.model.PhyloBubble;
import nl.tudelft.pl2016gr2.model.SequenceNode;

public class BubblePhyloVisitor implements NodeVisitor {
  
  private IPhylogeneticTreeNode treeNode;
  
  public IPhylogeneticTreeNode getTreeNode() {
    return treeNode;
  }
  
  @Override
  public void visit(PhyloBubble node) {
    treeNode = node.getTreeNode();
  }

  @Override
  public void visit(GraphNode node) {
    treeNode = null;
  }

  @Override
  public void visit(SequenceNode node) {
    treeNode = null;
  }

}
