package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Right now this class mainly just shows the selection(s).
 *
 * <p>
 * It has some code ready for doing a comparison, but consider this more of a
 * proof of concept. There has to be an easy way for the user to compare the two
 * selections he has made.
 * </p>
 *
 */
public class SelectionPaneController implements Initializable {

  @FXML
  private AnchorPane mainAnchor;
  @FXML
  private AnchorPane primarySelectionPane;
  @FXML
  private Separator separaror;
  @FXML
  private AnchorPane secondarySelectionPane;
  @FXML
  private ImageView copyButton;

  private SimpleObjectProperty<ISelectable> primarySelection;
  private SimpleObjectProperty<ISelectable> secondarySelection;

  /**
   * Method wrapper for an exception.
   *
   * <p>
   * This method calls {@link ISelectionInfo#getNode()} on the given {@link ISelectionInfo}
   * and catches its {@link IOException}. In the latter case it will return a simple
   * {@link Text} with an error.
   * </p>
   *
   * @param info the given {@link ISelectionInfo}
   * @return a {@link Node} that can be displayed
   */
  private Node safeGetDescription(ISelectionInfo info) {
    try {
      return info.getNode();
    } catch (IOException e) {
      Logger.getLogger(SelectionPaneController.class.getName()).log(Level.SEVERE, null, e);
      return new Text("Something went wrong when loading a file.");
    }
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.primarySelection = new SimpleObjectProperty<>();
    this.secondarySelection = new SimpleObjectProperty<>();
    initializeSeparatorAnchor();

    primarySelectionPane.prefWidthProperty().bind(mainAnchor.widthProperty(
    ).add(separaror.widthProperty().negate()).divide(2.0));
    secondarySelectionPane.prefWidthProperty().bind(mainAnchor.widthProperty()
        .add(separaror.widthProperty().negate()).divide(2.0));
    copyButton.translateXProperty().bind(
        mainAnchor.widthProperty().divide(2.0).add(copyButton.fitWidthProperty().negate()));
    
    primarySelection.addListener((observable, oldValue, newValue) -> {
      expandInAnchorPane(safeGetDescription(newValue.getSelectionInfo()), primarySelectionPane);
    });
    secondarySelection.addListener((observable, oldValue, newValue) -> {
      expandInAnchorPane(safeGetDescription(newValue.getSelectionInfo()), secondarySelectionPane);
    });

    primarySelection.set(SelectionManager.NO_SELECTION);
    secondarySelection.set(SelectionManager.NO_SELECTION);
  }
  
  /**
   * Initialize the anchor of the separator so it will always be in the middle of the screen.
   */
  private void initializeSeparatorAnchor() {
    mainAnchor.widthProperty().addListener((obs, old, newValue) -> {
      AnchorPane.setLeftAnchor(separaror, (newValue.doubleValue() - separaror.getWidth()) / 2.0);
    });
  }

  /**
   * Sets up this controller.
   *
   * @param selectionManager a selection manager is needed.
   */
  public void setup(SelectionManager selectionManager) {
    selectionManager.addListener((observable, oldValue, newValue) -> {
      primarySelection.set(newValue);
    });

    copyButton.setOnMouseClicked(event -> {
      secondarySelection.set(primarySelection.getValue());
    });
  }

  /**
   * Sets all anchors to 0 and adds.
   */
  private void expandInAnchorPane(Node content, AnchorPane anchorPane) {
    anchorPane.getChildren().clear();
    if (content != null) {
      AnchorPane.setTopAnchor(content, 0.0d);
      AnchorPane.setBottomAnchor(content, 0.0d);
      AnchorPane.setLeftAnchor(content, 0.0d);
      AnchorPane.setRightAnchor(content, 0.0d);
      anchorPane.getChildren().add(content);
    }
  }
}
