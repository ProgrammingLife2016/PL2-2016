package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;

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
      selectionManager.setTopGraphNode(topNode);
      selectionManager.setBottomGraphNode(bottomNode);
      selectionManager.drawGraph(topNode.getGenomes(), bottomNode.getGenomes());
    }
  };

  public TreeNodeDescription(SelectionManager selectionManager, IPhylogeneticTreeNode treeNode) {
    this.selectionManager = selectionManager;
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
