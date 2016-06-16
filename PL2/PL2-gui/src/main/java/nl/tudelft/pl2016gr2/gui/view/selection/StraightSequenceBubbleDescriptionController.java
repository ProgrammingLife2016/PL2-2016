package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.StraightSequenceBubble;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This controller controls the selection View for a {@link StraightSequenceBubble}.
 *
 * <p>
 * It will list the count of Indels and Point mutations in the children. Also lists all
 * genomes that are contained in this bubble.
 * </p>
 *
 */
public class StraightSequenceBubbleDescriptionController implements Initializable {

  @FXML
  private Label labelIndelCount;

  @FXML
  private Label labelPointMutationCount;

  @FXML
  private ListView<String> listViewGenomes;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

  }

  /**
   * Call this to set-up this view.
   *
   * <p>
   * Fills in the data of this view. Please refer to
   * {@link StraightSequenceBubbleDescriptionController} for more information about this view.
   * </p>
   *
   * @param bubble the {@link StraightSequenceBubble} that holds the data.
   */
  public void setup(StraightSequenceBubble bubble) {
    List<String> genomes = bubble.getGenomes().stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().collect(Collectors.toCollection(ArrayList::new));
    listViewGenomes.getItems().addAll(genomes);

    setupCountLabels(bubble);
  }

  /**
   * This method setups the count labels.
   *
   * <p>
   * Occurrences of {@link IndelBubble} and {@link PointMutationBubble} are
   * counted and displayed on the labels.
   * </p>
   */
  private void setupCountLabels(StraightSequenceBubble bubble) {
    long indelCnt =  bubble.getChildren().stream().filter(
        child -> child.getClass() == IndelBubble.class
    ).count();
    long pointMutationCnt = bubble.getChildren().stream().filter(
        child -> child.getClass() == PointMutationBubble.class
    ).count();

    labelIndelCount.setText(String.format("Indels: %d", indelCnt));
    labelPointMutationCount.setText(String.format("Point mutations: %d", pointMutationCnt));
  }

}
