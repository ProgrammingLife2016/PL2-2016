package nl.tudelft.pl2016gr2.gui.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
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
import java.util.ResourceBundle;

public class SearchPaneController implements Initializable {

  @FXML
  private Node root;

  @FXML
  private TextField filterField;
  @FXML
  private TableView<Annotation> annotationTable;

  @FXML
  private TableColumn<Annotation, String> specimenIdColumn;
  @FXML
  private TableColumn<Annotation, String> specimentTypeColumn;
  @FXML
  private TableColumn<Annotation, String> lineageColumn;

  private final ObservableList<Annotation> masterData = FXCollections.observableArrayList();

  private SelectionManager selectionManager;

  public SearchPaneController() {
  }

  @Override
  @SuppressWarnings("checkstyle:MethodLength")
  public void initialize(URL url, ResourceBundle resourceBundle) {

    root.visibleProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue) {
        filterField.requestFocus();
      }
    });

    // from http://code.makery.ch/blog/javafx-8-tableview-sorting-filtering/
    // 0. Initialize the columns.
    // JK: Annotation just has String members, no Property members. JavaFX wants Properties...
    specimenIdColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().specimenId));
    specimentTypeColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().specimenType));
    lineageColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().lineage));

    // 1. Wrap the ObservableList in a FilteredList
    FilteredList<Annotation> filteredData = new FilteredList<>(masterData, p -> false);

    // 2. Set the filter Predicate whenever the filter changes.
    filterField.textProperty().addListener((observable, oldValue, newValue) -> {
      filteredData.setPredicate(annotation -> {
        // If filter text is empty
        if (newValue == null || newValue.isEmpty()) {
          return false;
        }

        // Compare first name and last name of every annotation with filter text.
        String lowerCaseFilter = newValue.toLowerCase();

        if (annotation.specimenId.toLowerCase().contains(lowerCaseFilter)) {
          return true; // Filter matches first name.
        } else if (annotation.specimenType.toLowerCase().contains(lowerCaseFilter)) {
          return true; // Filter matches last name.
        }
        return false; // Does not match.
      });
    });

    // 3. Wrap the FilteredList in a SortedList.
    SortedList<Annotation> sortedData = new SortedList<>(filteredData);

    // 4. Bind the SortedList comparator to the TableView comparator.
    sortedData.comparatorProperty().bind(annotationTable.comparatorProperty());

    // 5. Add sorted (and filtered) data to the table.
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

      row.setOnMouseClicked(event -> {
        if (!row.isEmpty()) {
          Annotation annotation = row.getItem();
          Integer id = GenomeMap.getInstance().getId(annotation.specimenId);
          selectionManager.getSearchBoxSelectedGenome().set(id);
        }
      });

      return row;
    });

  }

  public void setData(Collection<Annotation> annotations) {
    masterData.clear();
    masterData.addAll(annotations);
  }

  public void setSelectionManager(SelectionManager selectionManager) {
    this.selectionManager = selectionManager;
  }
}
