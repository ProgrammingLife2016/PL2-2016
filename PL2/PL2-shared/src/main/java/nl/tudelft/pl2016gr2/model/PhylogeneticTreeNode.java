package nl.tudelft.pl2016gr2.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class is a storage class for a phylogenetic tree node. It retrieves and stores the needed
 * values of the newick parser {@link TreeNode}.
 *
 * @author Faris
 */
public class PhylogeneticTreeNode implements IPhylogeneticTreeNode, Iterable<PhylogeneticTreeNode> {

  private final String label;
  private final float weight;
  private final PhylogeneticTreeNode[] children;
  private final PhylogeneticTreeNode parent;
  private Annotation annotation;
  private final TreeNode node;

  /**
   * If all of the child nodes of this node are drawn in the top graph.
   */
  private final BooleanProperty drawnInTop = new SimpleBooleanProperty(false);

  /**
   * If all of the child nodes of this node are drawn in the bottom graph.
   */
  private final BooleanProperty drawnInBottom = new SimpleBooleanProperty(false);

  /**
   * Construct a phylogenetic tree node.
   *
   * @param node   the TreeNode of this node.
   * @param parent the parent of this phylogenetic tree node.
   */
  public PhylogeneticTreeNode(TreeNode node, PhylogeneticTreeNode parent) {
    this.node = node;
    this.weight = node.weight;
    this.parent = parent;

    if (node.numberChildren() == 2) { // node with children
      children = new PhylogeneticTreeNode[2];
      children[0] = new PhylogeneticTreeNode(node.getChild(0), this);
      children[1] = new PhylogeneticTreeNode(node.getChild(1), this);
    } else { // leaf node
      children = null;
    }

    if (node.numberChildren() == 0) {
      this.label = node.label.split("\\.", 2)[0];
    } else {
      label = null;
    }
  }
  
  /**
   * Construct a phylogenetic tree node with an existing phylogenetic tree node,
   * constructing it from top to bottom. This node is added as a child to its parent.
   * The children of the original node are not added to this node.
   * 
   * @param node : the existing phylogenetic tree node.
   * @param parent : the parent of this node.
   */
  public PhylogeneticTreeNode(IPhylogeneticTreeNode node, PhylogeneticTreeNode parent) {
    this.weight = (float) node.getEdgeLength();
    this.parent = parent;
    this.node = null;
    
    if (node.getDirectChildCount() == 2) {
      children = new PhylogeneticTreeNode[2];
    } else {
      children = null;
    }
    
    if (node.getDirectChildCount() == 0) {
      this.label = node.getLabel().split("\\.", 2)[0];
    } else {
      this.label = null;
    }
  }
  
  /**
   * Creates a copy of this node, assuming that it's a root node and that it's
   * children are instancesof PhylogeneticTreeNodes. 
   * 
   * @param node : the node to copy.
   */
  public PhylogeneticTreeNode(IPhylogeneticTreeNode node) {
    this.weight = (float) node.getEdgeLength();
    this.parent = null;
    this.node = null;
    
    if (node.getDirectChildCount() == 2) {
      children = new PhylogeneticTreeNode[2];
      children[0] = (PhylogeneticTreeNode) node.getChild(0);
      children[1] = (PhylogeneticTreeNode) node.getChild(1);
    } else {
      children = null;
    }
    
    if (node.getDirectChildCount() == 0) {
      this.label = node.getLabel().split("\\.", 2)[0];
    } else {
      this.label = null;
    }
  }
  
  @Override
  public String toString() {
    if (isLeaf()) {
      return "Leaf node: " + this.label;
    }
    
    return "Leaves: " + getGenomes().toString();
  }
  
  @Override
  public boolean hasTreeNode() {
    return node != null;
  }
  
  @Override
  public TreeNode getTreeNode() {
    return node;
  }
  
  @Override
  public void addChild(PhylogeneticTreeNode child) {
    assert (!isLeaf());
    if (children[0] == null) {
      children[0] = child;
    } else if (children[1] == null) {
      children[1] = child;
    } else {
      System.out.println("cannot add another child");
    }
  }
  
  @Override
  public boolean hasParent() {
    return parent != null;
  }

  @Override
  public IPhylogeneticTreeNode getParent() {
    return parent;
  }

  @Override
  public int getDirectChildCount() {
    if (isLeaf()) {
      return 0;
    } else {
      return 2;
    }
  }

  @Override
  public int getChildCount() {
    if (isLeaf()) {
      return 0;
    } else {
      return 2 + children[0].getChildCount() + children[1].getChildCount();
    }
  }

  @Override
  public IPhylogeneticTreeNode getChild(int index) {
    return children[index];
  }

  @Override
  public int getChildIndex(IPhylogeneticTreeNode child) {
    for (int i = 0; i < 2; i++) {
      if (children[i] == child) {
        return i;
      }
    }
    throw new IndexOutOfBoundsException();
  }

  @Override
  public ArrayList<String> getGenomes() {
    ArrayList<String> res = new ArrayList<>();
    if (isLeaf()) {
      res.add(label);
    } else {
      for (PhylogeneticTreeNode child : children) {
        res.addAll(child.getGenomes());
      }
    }
    return res;
  }

  @Override
  public double getEdgeLength() {
    return weight;
  }

  @Override
  public boolean isLeaf() {
    return children == null;
  }

  @Override
  public BooleanProperty getDrawnInTopProperty() {
    return drawnInTop;
  }

  @Override
  public BooleanProperty getDrawnInBottomProperty() {
    return drawnInBottom;
  }

  @Override
  public Color getLineageColor() {
    if (annotation != null) {
      return LineageColor.toLineage(annotation.lineage).getColor();
    } else {
      return LineageColor.NONE.getColor();
    }
  }

  /**
   * Creates an iterator which iterates over all of the leaf nodes of the tree.
   *
   * @return an iterator which iterates over all of the leaf nodes of the tree.
   */
  @Override
  public Iterator<PhylogeneticTreeNode> iterator() {
    return new LeafNodeIterator();
  }
  
  /**
   * Get the label of this leaf node. Note: this must be a leaf node!
   *
   * @return the label of this leaf node.
   */
  public String getLabel() {
    assert isLeaf();
    return label;
  }

  @Override
  public String getMetaData() {
    if (annotation == null) {
      return "";
    } else {
      return annotation.buildMetaDataString();
    }
  }

  /**
   * Set the value of drawnInTop. If it is different from the previous value, update the parent node
   * as well.
   *
   * @param isDrawn if this node is drawn in the top graph.
   */
  public void setDrawnInTop(boolean isDrawn) {
    if (drawnInTop.get() != isDrawn && (!isDrawn || childrenAreDrawnInTop())) {
      drawnInTop.set(isDrawn);
      if (hasParent()) {
        parent.setDrawnInTop(isDrawn);
      }
    }
  }

  /**
   * Set the value of drawnInBottom. If it is different from the previous value, update the parent
   * node as well.
   *
   * @param isDrawn if this node is drawn in the bottom graph.
   */
  public void setDrawnInBottom(boolean isDrawn) {
    if (drawnInBottom.get() != isDrawn && (!isDrawn || childrenAreDrawnInBottom())) {
      drawnInBottom.set(isDrawn);
      if (hasParent()) {
        parent.setDrawnInBottom(isDrawn);
      }
    }
  }

  /**
   * Check if all of the children of this node are drawn in the top graph.
   *
   * @return if all of the children of this node are drawn in the top graph.
   */
  private boolean childrenAreDrawnInTop() {
    return isLeaf() || children[0].drawnInTop.get() && children[1].drawnInTop.get();
  }

  /**
   * Check if all of the children of this node are drawn in the bottom graph.
   *
   * @return if all of the children of this node are drawn in the bottom graph.
   */
  private boolean childrenAreDrawnInBottom() {
    return isLeaf() || children[0].drawnInBottom.get() && children[1].drawnInBottom.get();
  }

  protected void setAnnotation(Annotation annotation) {
    this.annotation = annotation;
  }

  /**
   * An iterator which iterates over all of the leave nodes of the tree.
   */
  private class LeafNodeIterator implements Iterator<PhylogeneticTreeNode> {

    private final Iterator<PhylogeneticTreeNode> leftChildIterator;
    private final Iterator<PhylogeneticTreeNode> rightChildIterator;
    private boolean nextGiveLeaf = isLeaf();

    /**
     * Construct a leaf node iterator.
     */
    private LeafNodeIterator() {
      if (isLeaf()) {
        leftChildIterator = null;
        rightChildIterator = null;
      } else {
        leftChildIterator = children[0].iterator();
        rightChildIterator = children[1].iterator();
      }
    }

    @Override
    public boolean hasNext() {
      return nextGiveLeaf || !isLeaf()
          && (leftChildIterator.hasNext() || rightChildIterator.hasNext());
    }

    @Override
    public PhylogeneticTreeNode next() {
      if (nextGiveLeaf) {
        nextGiveLeaf = false;
        return PhylogeneticTreeNode.this;
      }
      if (leftChildIterator.hasNext()) {
        return leftChildIterator.next();
      } else if (rightChildIterator.hasNext()) {
        return rightChildIterator.next();
      }
      throw new NoSuchElementException();
    }
  }
}
