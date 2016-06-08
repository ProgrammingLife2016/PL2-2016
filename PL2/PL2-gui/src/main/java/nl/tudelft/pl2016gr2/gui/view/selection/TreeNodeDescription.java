package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;

/**
 * This class is used by tree nodes to offer a tree node description view to the selection manager.
 *
 * @author Faris
 */
public class TreeNodeDescription implements ISelectionInfo {

  private final IPhylogeneticTreeNode treeNode;
  private final SelectionManager selectionManager;
  private final EventHandler<ActionEvent> buttonClicked = new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
      IPhylogeneticTreeNode topNode = treeNode.getChild(0);
      IPhylogeneticTreeNode bottomNode = treeNode.getChild(1);
      selectionManager.drawGraph(topNode.getGenomeIds(), bottomNode.getGenomeIds());
    }
  };

  /**
   * Construct a tree node description.
   *
   * @param selectionManager a reference to the selection manager.
   * @param treeNode         the tree node to describe.
   */
  public TreeNodeDescription(SelectionManager selectionManager, IPhylogeneticTreeNode treeNode) {
    this.selectionManager = selectionManager;
    this.treeNode = treeNode;
  }

  @Override
  public Node getNode() {
    Pane pane = new Pane();
    addButton(pane);
    addText(pane);
    return pane;
  }

  /**
   * Add a compare children button to the pane.
   *
   * @param pane the pane.
   */
  private void addButton(Pane pane) {
    Button button = new Button("Compare children");
    button.getStyleClass().add("BigButton");
    button.setLayoutX(75);
    button.setLayoutY(50);
    button.setPrefHeight(50);
    button.setPrefWidth(250);
    button.setOnAction(buttonClicked);

    pane.getChildren().add(button);
  }

  /**
   * Add a text description to the pane.
   *
   * @param pane the pane.
   */
  private void addText(Pane pane) {
    TextArea text = new TextArea();
    text.setLayoutX(50);
    text.setLayoutY(250);
    text.setPrefHeight(300);
    text.setPrefWidth(300);
    text.setWrapText(true);
    text.setText(getGenomes());

    pane.getChildren().add(text);
  }

  /**
   * Get a string of all genomes in the tree node.
   *
   * @return a string of all genomes in the tree node.
   */
  private String getGenomes() {
    StringBuilder sb = new StringBuilder("Genomes: ");
    sb.append('\n');
    for (Integer genomeId : treeNode.getGenomes()) {
      if (genomeId != null) {
        sb.append('\n').append(GenomeMap.getInstance().getGenome(genomeId));
      }
    }
    return sb.toString();
  }
}
