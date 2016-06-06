package nl.tudelft.pl2016gr2.visitor;

import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;

/**
 * This class is used to visit a graphnode and if it is a phylo bubble, its phylogenetic tree node
 * will be retrieved.
 *
 * @author Casper
 */
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
