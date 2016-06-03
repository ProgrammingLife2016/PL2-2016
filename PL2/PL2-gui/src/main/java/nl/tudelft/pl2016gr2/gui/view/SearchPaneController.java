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
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;

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
  private TableView<Annotation> annotationTable;

  @FXML private TableColumn<Annotation, String> specimenIdColumn;
  @FXML private TableColumn<Annotation, String> specimentTypeColumn;
  @FXML private TableColumn<Annotation, String> genotypicDSTPatternColumn;
  @FXML private TableColumn<Annotation, String> phenotypicDSTPatternColumn;
  @FXML private TableColumn<Annotation, String> lineageColumn;

  private ObservableSet<String> setGenotypicDSTpattern
      = FXCollections.observableSet(new HashSet<>());
  private ObservableSet<String> setPhenotypicDSTPattern
      = FXCollections.observableSet(new HashSet<>());

  @FXML
  private ComboBox<String> genotypicDSTPatternComboBox;
  @FXML
  private Button genotypicDSTPatternButton;
  @FXML
  private ComboBox<String> phenotypicDSTPatternComboBox;
  @FXML
  private Button phenotypicDSTPatternButton;

  private final ObservableList<Annotation> masterData = FXCollections.observableArrayList();

  private final FilteredList<Annotation> filteredData = new FilteredList<>(masterData, p -> true);

  private SelectionManager selectionManager;

  public SearchPaneController() {
  }

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

    root.visibleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        filterField.requestFocus();
      }
    });
    initializeTable();
    initializeExtendedSearchPane();
  }

  @SuppressWarnings("checkstyle:MethodLength")
  private void initializeTable() {
    // from http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
    annotationTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    annotationTable.getSelectionModel().getSelectedItems().addListener(
        (ListChangeListener<Annotation>) c -> {
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
    SortedList<Annotation> sortedData = new SortedList<>(filteredData);

    // Bind the SortedList comparator to the TableView comparator.
    sortedData.comparatorProperty().bind(annotationTable.comparatorProperty());

    // Add sorted (and filtered) data to the table.
    annotationTable.setItems(sortedData);

    annotationTable.setRowFactory(tv -> {
      TableRow<Annotation> row = new TableRow<>();

      row.setOnDragDetected((MouseEvent event) -> {
        ClipboardContent clipboard = new ClipboardContent();
        clipboard.putString(row.getItem().specimenId);
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
   * call this whenever data changes.
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

  @SuppressWarnings("checkstyle:MethodLength")
  private void initializeExtendedSearchPane() {
    masterData.addListener((ListChangeListener<Annotation>) c -> {
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

  public void setData(Collection<Annotation> annotations) {
    masterData.clear();
    masterData.addAll(annotations);
  }

  public void setSelectionManager(SelectionManager selectionManager) {
    this.selectionManager = selectionManager;
  }
}
