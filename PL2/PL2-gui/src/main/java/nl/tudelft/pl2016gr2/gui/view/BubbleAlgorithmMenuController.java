package nl.tudelft.pl2016gr2.gui.view;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Menu;
import nl.tudelft.pl2016gr2.model.Settings;
import nl.tudelft.pl2016gr2.model.Settings.BubbleAlgorithms;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * This class controlls the bubble algorithm selection menu item.
 *
 * @author Faris
 */
public class BubbleAlgorithmMenuController implements Initializable {

  @FXML
  private Menu menu;
  @FXML
  private CheckBox mutationBasedCollapsingCheckbox;
  @FXML
  private CheckBox graphBasedCollapsingCheckbox;
  @FXML
  private CheckBox pointMutationCheckbox;
  @FXML
  private CheckBox indelCheckbox;
  @FXML
  private CheckBox straightSequenceCheckbox;
  @FXML
  private CheckBox phylogeneticCheckbox;
  @FXML
  private CheckBox graphCheckbox;

  /**
   * Bind the properties of the checkboxes so the graph checkboxes are disabled when the mutation
   * checkbox is selected and the mutation checkboxes are disabled when the graph checkbox is
   * selected.
   *
   * @param location  unused.
   * @param resources unused.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    mutationBasedCollapsingCheckbox.selectedProperty().addListener((obs, old, newValue) -> {
      graphBasedCollapsingCheckbox.setSelected(!newValue);
    });
    graphBasedCollapsingCheckbox.selectedProperty().addListener((obs, old, newValue) -> {
      mutationBasedCollapsingCheckbox.setSelected(!newValue);
    });
    graphCheckbox.disableProperty().bind(graphBasedCollapsingCheckbox.selectedProperty().not());
    BooleanBinding inverseMutation = mutationBasedCollapsingCheckbox.selectedProperty().not();
    pointMutationCheckbox.disableProperty().bind(inverseMutation);
    indelCheckbox.disableProperty().bind(inverseMutation);
    straightSequenceCheckbox.disableProperty().bind(inverseMutation);
    phylogeneticCheckbox.disableProperty().bind(inverseMutation);
    initializeMenuListener();
  }

  /**
   * Initialize the menu listeners. When the menu is hidden, the selected algorithms must be
   * updated.
   */
  private void initializeMenuListener() {
    menu.setOnHidden(event -> {
      ArrayList<BubbleAlgorithms> algorithms = new ArrayList<>();
      if (mutationBasedCollapsingCheckbox.isSelected()) {
        addAlgorithm(algorithms, BubbleAlgorithms.POINT, pointMutationCheckbox.isSelected());
        addAlgorithm(algorithms, BubbleAlgorithms.INDEL, indelCheckbox.isSelected());
        addAlgorithm(algorithms, BubbleAlgorithms.STRAIGHT, straightSequenceCheckbox.isSelected());
        addAlgorithm(algorithms, BubbleAlgorithms.PHYLO, phylogeneticCheckbox.isSelected());
      } else {
        addAlgorithm(algorithms, BubbleAlgorithms.GRAPH, graphCheckbox.isSelected());
      }
      Settings.getInstance().setBubblingAlgorithms(algorithms);
    });
  }

  private void addAlgorithm(ArrayList<BubbleAlgorithms> algorithms, BubbleAlgorithms algorithm,
      boolean add) {
    if (add) {
      algorithms.add(algorithm);
    }
  }
}