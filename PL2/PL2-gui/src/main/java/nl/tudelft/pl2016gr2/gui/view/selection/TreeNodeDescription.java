package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;

import java.util.ArrayList;

/**
 * This class is used by tree nodes to offer a tree node description view to the selection manager.
 *
 * @author Faris
 */
public class TreeNodeDescription implements ISelectionInfo {

  private final IPhylogeneticTreeNode treeNode;
  private final EventHandler<ActionEvent> buttonClicked = new EventHandler<ActionEvent>() {

    @Override
    public void handle(ActionEvent event) {
      ArrayList<String> genomes1 = treeNode.getChild(0).getGenomes();
      ArrayList<String> genomes2 = treeNode.getChild(1).getGenomes();
      RootLayoutController.getController().drawGraph(genomes1, genomes2);
    }
  };

  public TreeNodeDescription(IPhylogeneticTreeNode treeNode) {
    this.treeNode = treeNode;
  }

  @Override
  public Node getNode() {
    Button button = new Button("Compare children");
    button.setLayoutX(50);
    button.setLayoutY(50);
    button.setPrefHeight(100);
    button.setPrefWidth(250);
    button.setOnAction(buttonClicked);
    return button;
  }

}
