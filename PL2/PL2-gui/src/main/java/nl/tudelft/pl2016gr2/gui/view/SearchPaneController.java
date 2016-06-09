package nl.tudelft.pl2016gr2.gui.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.MetaData;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("AbbreviationAsWordInName")
public class SearchPaneController implements Initializable {

  @FXML
  private Node root;

  @FXML
  private TextField filterField;
  @FXML
  private TableView<MetaData> annotationTable;

  @FXML private TableColumn<MetaData, String> specimenIdColumn;
  @FXML private TableColumn<MetaData, String> specimentTypeColumn;
  @FXML private TableColumn<MetaData, String> genotypicDSTPatternColumn;
  @FXML private TableColumn<MetaData, String> phenotypicDSTPatternColumn;
  @FXML private TableColumn<MetaData, String> lineageColumn;

  private final ObservableSet<String> setGenotypicDSTpattern
      = FXCollections.observableSet(new HashSet<>());
  private final ObservableSet<String> setPhenotypicDSTPattern
      = FXCollections.observableSet(new HashSet<>());

  @FXML
  private ComboBox<String> genotypicDSTPatternComboBox;
  @FXML
  private Button genotypicDSTPatternButton;
  @FXML
  private ComboBox<String> phenotypicDSTPatternComboBox;
  @FXML
  private Button phenotypicDSTPatternButton;

  @FXML
  private TextField goToField;
  @FXML
  private Button goToButton;

  private final ObservableList<MetaData> masterData = FXCollections.observableArrayList();

  private final FilteredList<MetaData> filteredData = new FilteredList<>(masterData, p -> true);

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
    annotationTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    annotationTable.getSelectionModel().getSelectedItems().addListener(
        (ListChangeListener<MetaData>) c -> {
          System.out.println("SELECTED: ");
          selectionManager.getSearchBoxSelectedGenomes().setAll(
              c.getList().stream().map(
                  a -> GenomeMap.getInstance().getId(a.specimenId)
              ).collect(Collectors.toList())
          );
        }
    );

    specimenIdColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().specimenId));
    specimentTypeColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().specimenType));
    genotypicDSTPatternColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().genotypicDSTPattern));
    phenotypicDSTPatternColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().phenotypicDSTPattern));
    lineageColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().lineage));

    // Set the filter Predicate whenever the filter changes.
    filterField.textProperty().addListener((observable, oldValue, newValue) -> {
      updateTable();
    });


    // Wrap the FilteredList in a SortedList.
    SortedList<MetaData> sortedData = new SortedList<>(filteredData);

    // Bind the SortedList comparator to the TableView comparator.
    sortedData.comparatorProperty().bind(annotationTable.comparatorProperty());
    annotationTable.setItems(sortedData);

    annotationTable.setRowFactory(tv -> {
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
  private void updateTable() {
    filteredData.setPredicate(annotation -> {
      // If filter text is empty
      String searchString = filterField.getText();

      if (isNotSelected(genotypicDSTPatternComboBox, annotation.genotypicDSTPattern)
          || isNotSelected(phenotypicDSTPatternComboBox, annotation.phenotypicDSTPattern)) {
        return false;
      }

      if (searchString == null || searchString.isEmpty()) {
        return true;
      }

      String lowerCaseFilter = searchString.toLowerCase();
      return annotation.specimenId.toLowerCase().contains(lowerCaseFilter)
          || annotation.specimenType.toLowerCase().contains(lowerCaseFilter)
          || annotation.genotypicDSTPattern.toLowerCase().contains(lowerCaseFilter)
          || annotation.phenotypicDSTPattern.toLowerCase().contains(lowerCaseFilter);
    });
  }

  /**
   * @return true when ComboBox has NOT selected checkAgainst.
   */
  private boolean isNotSelected(ComboBox<String> comboBox, String checkAgainst) {
    String selectedItem = comboBox.getSelectionModel().getSelectedItem();
    return selectedItem != null && !checkAgainst.equals(selectedItem);
  }

  private void initializeGoTo() {
    goToButton.setOnAction(actionEvent -> {
      String fieldText = goToField.getText();
      if (fieldText.matches("\\d+")) {
        graphPaneController.centreOnLevel(Integer.valueOf(fieldText));
      } else {
        goToField.clear();
      }
    });
  }

  @SuppressWarnings("checkstyle:MethodLength")
  private void initializeExtendedSearchPane() {
    masterData.addListener((ListChangeListener<MetaData>) c -> {
      Stream.of(setGenotypicDSTpattern, setPhenotypicDSTPattern).forEach(Set::clear);
      c.getList().forEach(annotation -> {
        setGenotypicDSTpattern.add(annotation.genotypicDSTPattern);
        setPhenotypicDSTPattern.add(annotation.phenotypicDSTPattern);
      });
    });
    // add set listeners
    setGenotypicDSTpattern.addListener(setChangeListener(genotypicDSTPatternComboBox));
    setPhenotypicDSTPattern.addListener(setChangeListener(phenotypicDSTPatternComboBox));

    // add comboBox listeners
    genotypicDSTPatternComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      updateTable();
    });
    phenotypicDSTPatternComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
      updateTable();
    });

    // set button actions
    genotypicDSTPatternButton.setOnAction(getButtonAction(genotypicDSTPatternComboBox));
    phenotypicDSTPatternButton.setOnAction(getButtonAction(phenotypicDSTPatternComboBox));
  }

  private SetChangeListener<String> setChangeListener(ComboBox<String> comboBox) {
    return change -> comboBox.setItems(FXCollections.observableArrayList(change.getSet()));
  }

  private EventHandler<ActionEvent> getButtonAction(ComboBox<String> genotypicDSTPatternComboBox) {
    return event -> genotypicDSTPatternComboBox.getSelectionModel().clearSelection();
  }

  public void setData(Collection<MetaData> metaDatas) {
    masterData.clear();
    masterData.addAll(metaDatas);
  }

}
