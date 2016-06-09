package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.ResourceBundle;

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

  private static final Color BACKGROUND_COLOR = new Color(0.4, 0.4, 0.45, 1);

  @FXML
  private Pane rootPane;
  @FXML
  private AnchorPane primarySelectionPane;
  @FXML
  private AnchorPane secondarySelectionPane;
  @FXML
  private AnchorPane compareSelectionPane;

  private Button setSecondaryButton = new Button("Copy over from left to right");
  private Button compareButton = new Button("Compare left to right");
  // Add compareButton when wanting to do the actual comparison!
  private Pane buttonContainer = new VBox(setSecondaryButton);

  {
    AnchorPane.setLeftAnchor(buttonContainer, 5.0d);
    AnchorPane.setRightAnchor(buttonContainer, 5.0d);
    setSecondaryButton.setWrapText(true);
    compareButton.setWrapText(true);
  }

  private SimpleObjectProperty<ISelectable> primarySelection;
  private SimpleObjectProperty<ISelectable> secondarySelection;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    this.primarySelection = new SimpleObjectProperty<>();
    this.secondarySelection = new SimpleObjectProperty<>();

    primarySelection.addListener((observable, oldValue, newValue) -> {
      expandInAnchorPane(newValue.getSelectionInfo().getNode(), primarySelectionPane);
    });
    secondarySelection.addListener((observable, oldValue, newValue) -> {
      expandInAnchorPane(newValue.getSelectionInfo().getNode(), secondarySelectionPane);
    });

    primarySelection.set(SelectionManager.NO_SELECTION);
    secondarySelection.set(SelectionManager.NO_SELECTION);

    resetCompare();

    rootPane.setBackground(new Background(
        new BackgroundFill(BACKGROUND_COLOR, null, Insets.EMPTY)));
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

    setSecondaryButton.setOnAction(actionEvent -> {
      System.out.println("clicked (onAction)");
      secondarySelection.set(primarySelection.getValue());
    });
    compareButton.setOnAction(actionEvent -> {
      Pane pane = startCompare();
      pane.setMaxWidth(150);
      expandInAnchorPane(pane, compareSelectionPane);
    });
  }

  /**
   * Removes the visual comparison and puts button back.
   */
  private void resetCompare() {
    compareSelectionPane.getChildren().clear();
    compareSelectionPane.getChildren().add(buttonContainer);
  }

  /**
   * This method is not used yet. It is called when
   * the compare button (now hidden) is pressed.
   * @return A pane that should contain a visual comparison of primary and secondary.
   */
  private Pane startCompare() {
    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(actionEvent1 -> {
      resetCompare();
    });

    TextArea textArea  = new TextArea(String.format("Compare %s to %s",
        primarySelection.get().getSelectionInfo(),
        secondarySelection.get().getSelectionInfo()));
    textArea.setWrapText(true);

    return new VBox(textArea, cancelButton);
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
