package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;

/**
 * This class is used by tree leafs to offer a tree leaf description view to the selection manager.
 *
 * @author Faris
 */
public class TreeLeafDescription implements ISelectionInfo {

  private final IPhylogeneticTreeNode treeLeaf;

  /**
   * Construct a tree leaf description.
   *
   * @param treeLeaf the leaf node to describe.
   */
  public TreeLeafDescription(IPhylogeneticTreeNode treeLeaf) {
    this.treeLeaf = treeLeaf;
  }

  @Override
  public Node getNode() {
    TextArea text = new TextArea();
    text.setWrapText(true);
    text.setEditable(false);
    text.setText(treeLeaf.getMetaData());
    return text;
  }

}
