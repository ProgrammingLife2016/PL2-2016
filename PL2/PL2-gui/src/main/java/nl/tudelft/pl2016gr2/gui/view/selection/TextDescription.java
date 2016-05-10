package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.scene.Node;
import javafx.scene.control.TextArea;

/**
 * Simple text description.
 *
 * @author Faris
 */
public class TextDescription implements ISelectionInfo {

  private final TextArea text;

  /**
   * Create a simple text description container.
   *
   * @param description the description of the selected node.
   */
  public TextDescription(String description) {
    text = new TextArea(description);
    text.setLayoutX(50);
    text.setLayoutY(50);
    text.setPrefHeight(200);
    text.setPrefWidth(300);
    text.setWrapText(true);
  }

  /**
   * Get the node of the text description.
   *
   * @return the node of the text description.
   */
  @Override
  public Node getNode() {
    return text;
  }

}
