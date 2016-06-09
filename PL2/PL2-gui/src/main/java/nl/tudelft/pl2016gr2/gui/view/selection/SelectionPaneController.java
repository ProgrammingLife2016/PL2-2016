package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

public class SelectionPaneController implements Initializable {

  private static final Color BACKGROUND_COLOR = new Color(0.4, 0.4, 0.45, 1);

  @FXML
  public AnchorPane rootPane;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    rootPane.setBackground(new Background(
        new BackgroundFill(BACKGROUND_COLOR, null, Insets.EMPTY)));
  }

  /**
   * Sets the content of the selectionPane.
   *
   * <p>
   * This removes previous shown content,
   * call with null to clear.
   * </p>
   *
   * @param content new content, may be null
   */
  public void setContent(Node content) {
    clearContent();
    if (content != null) {
      AnchorPane.setTopAnchor(content, 0.0d);
      AnchorPane.setBottomAnchor(content, 0.0d);
      AnchorPane.setLeftAnchor(content, 0.0d);
      AnchorPane.setRightAnchor(content, 0.0d);
      rootPane.getChildren().add(content);
    }
  }

  /**
   * Clears the pane of information.
   */
  public void clearContent() {
    rootPane.getChildren().clear();
  }

}
