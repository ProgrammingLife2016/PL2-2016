package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This controller controls the selection View for a {@link SequenceNode}.
 *
 * <p>
 * It shows the actual base sequence and the genomes this sequence belongs to.
 * </p>
 */
public class SequenceNodeDescriptionController implements Initializable {

  @FXML
  private Label labelSequence;

  @FXML
  private ListView<String> listViewGenomes;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }

  /**
   * Call this to set-up this view.
   *
   * <p>
   * Fills in the data of this view. Please refer to {@link SequenceNodeDescriptionController} for
   * more information about this view.
   * </p>
   *
   * @param sequenceNode the {@link SequenceNode} that holds the data.
   */
  public void setup(SequenceNode sequenceNode) {
    List<String> genomes = sequenceNode.getGenomes().stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().collect(Collectors.toCollection(ArrayList::new));
    listViewGenomes.getItems().addAll(genomes);
    labelSequence.setText(sequenceNode.getSequence());
  }

}
