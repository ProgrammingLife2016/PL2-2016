package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.scene.Node;

import java.io.IOException;

/**
 * This interface provides a method for the selection manager to get a node containing the
 * description of a selection object.
 *
 * @author Faris
 */
public interface ISelectionInfo {

  /**
   * Get the description node which contains a description of the selection object an possibly some
   * way to perform actions on the selected object.
   *
   * @return the node containing the description of the selected object.
   * @throws IOException possible when {@link Node} is loaded via {@link javafx.fxml.FXMLLoader}
   */
  Node getNode() throws IOException;
}
