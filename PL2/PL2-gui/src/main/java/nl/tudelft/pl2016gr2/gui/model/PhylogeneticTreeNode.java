package nl.tudelft.pl2016gr2.gui.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode;
import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;

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

  private final int genomeId;
  private final float weight;
  private final PhylogeneticTreeNode[] children;
  private final PhylogeneticTreeNode parent;
  private Annotation annotation;

  /**
   * If all of the child nodes of this node are drawn in the top graph.
   */
  private final BooleanProperty drawnInTop = new SimpleBooleanProperty(false);

  /**
   * If all of the child nodes of this node are drawn in the bottom graph.
   */
  private final BooleanProperty drawnInBottom = new SimpleBooleanProperty(false);

  /**
   * If this treenode is part of a highlighted path.
   */
  private BooleanProperty inHighlightedPath = new SimpleBooleanProperty(false);

  /**
   * Construct a phylogenetic tree node.
   *
   * @param node   the TreeNode of this node.
   * @param parent the parent of this phylogenetic tree node.
   */
  protected PhylogeneticTreeNode(TreeNode node, PhylogeneticTreeNode parent) {
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
      genomeId = GenomeMap.getInstance().getId(node.label.split("\\.", 2)[0]);
    } else {
      genomeId = -1;
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
  public ArrayList<Integer> getGenomes() {
    ArrayList<Integer> res = new ArrayList<>();
    if (isLeaf()) {
      res.add(GenomeMap.getInstance().getId(getLabel()));
    } else {
      for (PhylogeneticTreeNode child : children) {
        res.addAll(child.getGenomes());
      }
    }
    return res;
  }

  @Override
  public ArrayList<Integer> getGenomeIds() {
    ArrayList<Integer> res = new ArrayList<>();
    if (isLeaf()) {
      res.add(genomeId);
    } else {
      for (PhylogeneticTreeNode child : children) {
        res.addAll(child.getGenomeIds());
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
  public BooleanProperty getInHighlightedPathProperty() {
    return inHighlightedPath;
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

  @Override
  public String getMetaData() {
    System.out.println("annotation = " + annotation);
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
  protected void setDrawnInTop(boolean isDrawn) {
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
  protected void setDrawnInBottom(boolean isDrawn) {
    if (drawnInBottom.get() != isDrawn && (!isDrawn || childrenAreDrawnInBottom())) {
      drawnInBottom.set(isDrawn);
      if (hasParent()) {
        parent.setDrawnInBottom(isDrawn);
      }
    }
  }

  /**
   * Get the label of this leaf node. Note: this must be a leaf node!
   *
   * @return the label of this leaf node.
   */
  private String getLabel() {
    assert isLeaf();
    return GenomeMap.getInstance().getGenome(genomeId);
  }

  /**
   * Get the genome id of this leaf node. Note: this must be a leaf node!
   *
   * @return the genome id of this leaf node.
   */
  protected int getGenomeId() {
    assert isLeaf();
    return genomeId;
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

  protected void unhighlightPath() {
    inHighlightedPath.set(false);
    parent.unhighlightPath();
  }

  protected void highlightPath() {
    inHighlightedPath.set(true);
    parent.highlightPath();
  }
}
