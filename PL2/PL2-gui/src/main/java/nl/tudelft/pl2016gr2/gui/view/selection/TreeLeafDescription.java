package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeNode;

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
    Pane pane = new Pane();
    addText(pane);
    return pane;
  }

  /**
   * Add a text description to the pane.
   *
   * @param pane the pane.
   */
  private void addText(Pane pane) {
    TextArea text = new TextArea();
    text.setLayoutX(50);
    text.setLayoutY(50);
    text.setPrefHeight(500);
    text.setPrefWidth(300);
    text.setWrapText(true);
    text.setText(treeLeaf.getMetaData());

    pane.getChildren().add(text);
  }
}
