package nl.tudelft.pl2016gr2.gui.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.model.Annotation;

import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.ResourceBundle;

public class AnnotationSearchPaneController implements Initializable {

  @FXML
  private TextField filterField;

  @FXML
  private TableView<Annotation> annotationTableView;
  @FXML
  private TableColumn<Annotation, String> nameColumn;

  private GraphPaneController graphPaneController;

  private final ObservableList<Annotation> masterData = FXCollections.observableArrayList();

  private final FilteredList<Annotation> filteredData = new FilteredList<>(masterData, p -> true);

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    initializeTable();
  }

  private void initializeTable() {
    nameColumn.setCellValueFactory(
        cellData -> new SimpleStringProperty(cellData.getValue().getAttribute("name")));

    // Set the filter Predicate whenever the filter changes.
    filterField.textProperty().addListener((observable, oldValue, newValue) -> {
      updateTable();
    });

    // Wrap the FilteredList in a SortedList.
    SortedList<Annotation> sortedData = new SortedList<>(filteredData);

    // Bind the SortedList comparator to the TableView comparator.
    sortedData.comparatorProperty().bind(annotationTableView.comparatorProperty());
    annotationTableView.setItems(sortedData);
  }


  /**
   * Call this whenever data changes.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  private void updateTable() {
    annotationTableView.getSelectionModel().clearSelection();
    filteredData.setPredicate(annotation -> {

      String lowerCaseFilter = filterField.getText().toLowerCase();
      if (lowerCaseFilter.isEmpty()) {
        return true;
      }

      for (Map.Entry<String, String> entry : annotation.getAttributes().entrySet()) {
        if (entry.getValue().toLowerCase().contains(lowerCaseFilter)) {
          return true;
        }
      }

      return false;
    });
  }

  /**
   * Sets up this Controller.
   * @param graphPaneController the graphPaneController
   */
  public void setup(GraphPaneController graphPaneController) {
    this.graphPaneController = graphPaneController;
  }

  /**
   * Sets the data for this controller. When not called, no annotations are shown.
   * @param annotations the metadata object.
   */
  public void setData(Collection<Annotation> annotations) {
    masterData.clear();
    masterData.addAll(annotations);
  }
}
