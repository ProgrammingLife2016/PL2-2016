package nl.tudelft.pl2016gr2.model;

/**
 * This is an interface which contains all of the methods which must be implemented by a container
 * class of a phylogenetic tree.
 *
 * @author Faris
 */
public interface IPhylogeneticTreeNode {

  /**
   * Prints information about the current node.
   */
  void print();
  
  /**
   * Check if this node is a leaf.
   * 
   * @return true if this node is a leaf.
   */
  boolean isLeaf();
  
  /**
   * Check if this node has a parent.
   *
   * @return if this node has a parent.
   */
  boolean hasParent();

  /**
   * Get the parent of this node.
   *
   * @return the parent of this node.
   */
  IPhylogeneticTreeNode getParent();

  /**
   * Get the amount of direct child nodes (i.e. child nodes with an edge to this node).
   *
   * @return theamount of direct child nodes.
   */
  int getDirectChildCount();

  /**
   * Get the total amount of child nodes. This includes the direct child nodes and the indirect
   * child nodes.
   *
   * @return the total amount of child nodes.
   */
  int getChildCount();

  /**
   * Get the child at the given index.
   *
   * @param index the index of the child node.
   * @return the child at the given index.
   */
  IPhylogeneticTreeNode getChild(int index);

  /**
   * Get the index of a direct child node.
   *
   * @param child a direct child node.
   * @return the index of the child ndoe.
   */
  int getChildIndex(IPhylogeneticTreeNode child);
}
