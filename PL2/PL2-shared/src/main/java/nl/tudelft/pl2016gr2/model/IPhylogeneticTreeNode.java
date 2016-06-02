package nl.tudelft.pl2016gr2.model;

import javafx.beans.property.BooleanProperty;
import javafx.scene.paint.Color;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

import java.util.ArrayList;

/**
 * This is an interface which contains all of the methods which must be implemented by a container
 * class of a phylogenetic tree.
 *
 * @author Faris
 */
public interface IPhylogeneticTreeNode {
  
  /**
   * Returns true if this object contains the original juxtaposer treenode.
   * 
   * @return true if the object contains the original juxtaposer treenode.
   */
  boolean hasTreeNode();
  
  /**
   * Get the original treenode. Returns null if this is not there.
   * 
   * @return the original treenode
   */
  TreeNode getTreeNode();
  
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
   * Adds a child node to this node.
   * 
   * @param child the child node to add.
   */
  void addChild(PhylogeneticTreeNode child);

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

  /**
   * Get all of the genomes which are present in this branch of the tree.
   *
   * @return all of the genomes whic are present in this branch of the tree.
   */
  ArrayList<String> getGenomes();

  /**
   * Get the length of the edge to this node.
   *
   * @return the length of the edge to this node.
   */
  double getEdgeLength();

  /**
   * Check if this node is a leaf node.
   *
   * @return if this node is a leaf node.
   */
  boolean isLeaf();
  
  /**
   * Get the label of this leaf node. Note: this must be a leaf node!
   *
   * @return the label of this leaf node.
   */
  String getLabel();

  /**
   * Get the drawn in top property. This property is true iff all of the genomes in this tree node
   * are drawn in the top graph.
   *
   * @return the drawn in top property
   */
  BooleanProperty getDrawnInTopProperty();

  /**
   * Get the drawn in bottom property. This property is true iff all of the genomes in this tree
   * node are drawn in the bottom graph.
   *
   * @return the drawn in bottom property
   */
  BooleanProperty getDrawnInBottomProperty();

  /**
   * Get the lineage color of this node.
   *
   * @return the lineage color of this node.
   */
  Color getLineageColor();

  /**
   * Get a string containing metadata about the genome in the leaf node. Returns an empty string if
   * it is called on a non-leaf node.
   *
   * @return a string containing metadata about the genome in the leaf node.
   */
  String getMetaData();
}
