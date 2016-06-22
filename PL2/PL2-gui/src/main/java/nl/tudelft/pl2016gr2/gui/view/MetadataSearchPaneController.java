package nl.tudelft.pl2016gr2.gui.view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.MetaData;
import nl.tudelft.pl2016gr2.model.metadata.LineageColor;
import org.controlsfx.control.CheckComboBox;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This is the controller for the searchPane.fxml.
 *
 * <p>
 * It allows searching through {@link MetaData}s.
 * </p>
 */
@SuppressWarnings("AbbreviationAsWordInName")
public class MetadataSearchPaneController implements Initializable {

  @FXML
  private Node root;

  @FXML
  private TextField filterField;
  @FXML
  private TableView<MetaData> metaDataTableView;

  @FXML private TableColumn<MetaData, String> specimenIdColumn;
  @FXML private TableColumn<MetaData, String> specimentTypeColumn;
  @FXML private TableColumn<MetaData, LineageColor> lineageColumn;

  @FXML
  private GridPane categoricalGridPane;

  @FXML
  private TextField goToField;
  @FXML
  private Button goToButton;

  private final ObservableList<MetaData> masterData = FXCollections.observableArrayList();

  private final FilteredList<MetaData> filteredData = new FilteredList<>(masterData, p -> true);

  private Map<String, CategoricalProperty> map = new HashMap<>();

  private SelectionManager selectionManager;
  private GraphPaneController graphPaneController;

  /**
   * When this method is called, focus is requested for the searchBox.
   */
  public void requestSearchFieldFocus() {
    filterField.requestFocus();
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    root.visibleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        filterField.requestFocus();
      }
    });
    initializeTable();
    initializeGoTo();
    initializeExtendedSearchPane();
  }

  @SuppressWarnings("checkstyle:MethodLength")
  private void initializeTable() {
    // from http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
    metaDataTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    metaDataTableView.getSelectionModel().getSelectedItems().addListener(
        (ListChangeListener<MetaData>) c -> {
          selectionManager.getSearchBoxSelectedGenomes().setAll(
              c.getList().stream().filter(metadata -> metadata != null).map(
                  metadata -> GenomeMap.getInstance().getId(metadata.specimenId)
              ).collect(Collectors.toList())
          );
        }
    );

    specimenIdColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().specimenId));
    specimentTypeColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().specimenType));

    lineageColumn.setCellValueFactory(cellData ->
        new SimpleObjectProperty<>(LineageColor.toLineage(cellData.getValue())));
    lineageColumn.setCellFactory(metaDataStringTableColumn ->
        new TableCell<MetaData, LineageColor>() {
          @Override
          protected void updateItem(LineageColor item, boolean empty) {
            super.updateItem(item, empty);
            Color color = Color.TRANSPARENT;
            if (!empty && item != null) {
              color = item.getColor();
            }
            setBackground(new Background(new BackgroundFill(
                color, null, Insets.EMPTY
            )));
          }
        }
    );

    // Set the filter Predicate whenever the filter changes.
    filterField.textProperty().addListener((observable, oldValue, newValue) -> {
      updateTable();
    });

    // Wrap the FilteredList in a SortedList.
    SortedList<MetaData> sortedData = new SortedList<>(filteredData);

    // Bind the SortedList comparator to the TableView comparator.
    sortedData.comparatorProperty().bind(metaDataTableView.comparatorProperty());
    metaDataTableView.setItems(sortedData);

    metaDataTableView.setRowFactory(tv -> {
      TableRow<MetaData> row = new TableRow<>();

      row.setOnDragDetected((MouseEvent event) -> {
        // Format the genome selection for dragging
        StringBuilder genomeStringBuilder = new StringBuilder();
        for (int genome : selectionManager.getSearchBoxSelectedGenomes()) {
          genomeStringBuilder.append(GenomeMap.getInstance().getGenome(genome)).append('\n');
        }
        genomeStringBuilder.deleteCharAt(genomeStringBuilder.length() - 1);

        ClipboardContent clipboard = new ClipboardContent();
        clipboard.putString(genomeStringBuilder.toString());
        Dragboard dragboard = row.startDragAndDrop(TransferMode.ANY);
        dragboard.setContent(clipboard);

        SnapshotParameters snapshotParams = new SnapshotParameters();
        snapshotParams.setFill(Color.TRANSPARENT);
        dragboard.setDragView(row.snapshot(snapshotParams, null));
        event.consume();
      });
      return row;
    });
  }

  /**
   * Sets up this Controller.
   * @param selectionManager the selection manager
   * @param graphPaneController the graphPaneController
   */
  public void setup(SelectionManager selectionManager, GraphPaneController graphPaneController) {
    this.selectionManager = selectionManager;
    this.graphPaneController = graphPaneController;
  }

  /**
   * Call this whenever data changes.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  private void updateTable() {
    metaDataTableView.getSelectionModel().clearSelection();
    filteredData.setPredicate(metadata -> {

      for (CategoricalProperty property : map.values()) {
        String valueOfRow = metadata.getValueForColumnName(property.name);
        ObservableList<String> items = property.checkComboBox.getCheckModel().getCheckedItems();
        if (items.size() > 0 && !items.stream().anyMatch(s -> s.equals(valueOfRow))) {
          return false;
        }
      }

      String lowerCaseFilter = filterField.getText().toLowerCase();
      if (lowerCaseFilter.isEmpty()) {
        return true;
      }

      for (CategoricalProperty property : map.values()) {
        String valueOfRow = metadata.getValueForColumnName(property.name);
        if (valueOfRow.toLowerCase().contains(lowerCaseFilter)) {
          return true;
        }
      }

      return metadata.specimenId.toLowerCase().contains(lowerCaseFilter)
          || metadata.specimenType.toLowerCase().contains(lowerCaseFilter);
    });
  }

  private void initializeGoTo() {
    goToField.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        goToButton.fire();
      }
    });
    goToButton.setOnAction(actionEvent -> {
      String fieldText = goToField.getText();
      if (fieldText.matches("\\d+")) {
        graphPaneController.centerOnLevel(Integer.valueOf(fieldText));
      } else {
        goToField.clear();
      }
    });
  }

  /**
   * initializes the categorical comboboxes.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  private void initializeExtendedSearchPane() {
    masterData.addListener((ListChangeListener<MetaData>) c -> {

      categoricalGridPane.getChildren().clear();

      map.clear();
      masterData.forEach(metaData -> {
        Arrays.stream(MetaData.KNOWN_CATEGORICAL_COLUMNS).forEach(columnName -> {
          final CategoricalProperty property;
          if (map.containsKey(columnName)) {
            property = map.get(columnName);
          } else {
            property = new CategoricalProperty(columnName);
            property.checkComboBox.getCheckModel().getCheckedItems()
                .addListener((ListChangeListener<? super String>) change -> updateTable());
            map.put(property.name, property);
          }
          property.addValue(metaData.getValueForColumnName(columnName));
        });
      });

      int rowIndex = 1;
      for (CategoricalProperty property : map.values()) {

        Button button = new Button("clear");
        button.getStyleClass().addAll("filter-box-button", "clear-button");
        button.setTranslateX(2.0);

        button.setOnAction(actionEvent -> {
          property.checkComboBox.getCheckModel().clearChecks();
        });
        Label label = new Label(property.name);

        GridPane.setColumnIndex(label, 1);
        GridPane.setColumnIndex(property.checkComboBox, 2);
        GridPane.setColumnIndex(button, 3);
        button.setMinSize(Button.USE_PREF_SIZE, Button.USE_PREF_SIZE);

        GridPane.setRowIndex(label, rowIndex);
        GridPane.setRowIndex(property.checkComboBox, rowIndex);
        GridPane.setRowIndex(button, rowIndex);
        rowIndex++;

        categoricalGridPane.getChildren().add(label);
        categoricalGridPane.getChildren().add(property.checkComboBox);
        categoricalGridPane.getChildren().add(button);
      }

    });
  }

  /**
   * Sets the data for this controller. When not called, no genomes are shown.
   * @param metaData the metadata object.
   */
  public void setData(Collection<MetaData> metaData) {
    masterData.clear();
    masterData.addAll(metaData);
  }

  /**
   * This class is a helper class to represent a categorical property.
   *
   * <p>
   * It holds the combobox together with a name.
   * </p>
   */
  private static class CategoricalProperty {

    final String name;
    final CheckComboBox<String> checkComboBox;

    CategoricalProperty(String name) {
      this.name = name;
      checkComboBox = new CheckComboBox<>();
      checkComboBox.getStyleClass().add("filter-box");
      checkComboBox.setMaxWidth(150);
    }

    void addValue(String value) {
      if (!checkComboBox.getItems().contains(value)) {
        checkComboBox.getItems().add(value);
      }
    }
  }
}
