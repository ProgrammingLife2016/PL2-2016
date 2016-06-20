package nl.tudelft.pl2016gr2.gui.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LegendController implements Initializable {

  @FXML
  private Node toggle;

  @FXML
  private Label title;

  @FXML
  private Pane legendItemContainer;

  @FXML
  private Node legendItemContainerWrapper;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    toggle.setOnMouseClicked(event -> {
      legendItemContainerWrapper.setVisible(!legendItemContainerWrapper.isVisible());
    });
  }

  /**
   * Setup the legend with actual data.
   *
   * @param title title above the legend.
   * @param toggleX x offset of toggle, positive for top, negative for bottom.
   * @param toggleY xyoffset of toggle, positive for left, negative for right.
   * @param items Items to show.
   */
  public void initializeData(String title, double toggleX, double toggleY, LegendItem ... items) {
    this.title.setText(title);

    setTogglePosition(toggleX, toggleY);

    ObservableList<Node> children = legendItemContainer.getChildren();
    for (LegendItem item : items) {
      children.add(buildNodeForItem(item));
    }
  }

  private void setTogglePosition(double toggleX, double toggleY) {
    if (toggleX > 0) {
      AnchorPane.setLeftAnchor(toggle, toggleX);
    } else {
      AnchorPane.setRightAnchor(toggle, -toggleX);
    }
    if (toggleY > 0) {
      AnchorPane.setTopAnchor(toggle, toggleY);
    } else {
      AnchorPane.setBottomAnchor(toggle, -toggleY);
    }
  }

  private static Node buildNodeForItem(LegendItem item) {
    StackPane symbolPane = new StackPane(item.node);
    symbolPane.setPrefWidth(50.0);
    symbolPane.setPrefHeight(25.0);
    symbolPane.setAlignment(Pos.CENTER);

    StackPane textPane = new StackPane(new Label(item.shortDescription));
    textPane.setPrefHeight(25.0);
    textPane.setAlignment(Pos.CENTER_LEFT);

    HBox hBox = new HBox(symbolPane, textPane);
    hBox.setAlignment(Pos.CENTER_LEFT);
    Tooltip.install(hBox, new Tooltip(item.description));
    return hBox;
  }

  protected static class LegendItem {

    private final String description;
    private final String shortDescription;
    private final Node node;

    protected LegendItem(String description, String shortDescription, Node node) {
      this.description = description;
      this.shortDescription = shortDescription;
      this.node = node;
    }
  }
}
