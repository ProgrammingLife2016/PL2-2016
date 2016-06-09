package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeNode;

/**
 * This class is used by tree nodes to offer a tree node description view to the selection manager.
 *
 * @author Faris
 */
public class TreeNodeDescription implements ISelectionInfo {

  private final IPhylogeneticTreeNode treeNode;
  private final GraphPaneController graphPaneController;
  private final EventHandler<ActionEvent> buttonClicked = new EventHandler<ActionEvent>() {
    @Override
    public void handle(ActionEvent event) {
      IPhylogeneticTreeNode topNode = treeNode.getChild(0);
      IPhylogeneticTreeNode bottomNode = treeNode.getChild(1);
      graphPaneController.compareTwoGraphs(topNode.getGenomeIds(), bottomNode.getGenomeIds());
    }
  };

  /**
   * Construct a tree node description.
   *
   * @param graphPaneController a reference to the graphpane controller.
   * @param treeNode         the tree node to describe.
   */
  public TreeNodeDescription(GraphPaneController graphPaneController,
                             IPhylogeneticTreeNode treeNode) {
    this.graphPaneController = graphPaneController;
    this.treeNode = treeNode;
  }

  @Override
  public Node getNode() {
    VBox vbox = new VBox();

    Button button = makeButton();
    TextArea textArea = makeTextArea();

    VBox.setVgrow(button, Priority.NEVER);
    VBox.setVgrow(textArea, Priority.ALWAYS);

    vbox.getChildren().addAll(button, textArea);
    return vbox;
  }

  /**
   * Build a compare children button.
   */
  private Button makeButton() {
    Button button = new Button("Compare children TODO");
    button.getStyleClass().add("BigButton");
    button.setPrefHeight(50);
    button.setOnAction(buttonClicked);
    return button;
  }

  /**
   * Build a text description.
   */
  private TextArea makeTextArea() {
    TextArea text = new TextArea();
    text.setWrapText(true);
    text.setEditable(false);
    text.setText(getGenomes());

    return text;
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
