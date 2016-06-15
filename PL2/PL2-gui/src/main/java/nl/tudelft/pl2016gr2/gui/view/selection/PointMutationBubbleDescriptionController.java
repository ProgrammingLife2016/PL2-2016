package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This controller controls the selection View for a {@link PointMutationBubble}.
 *
 * <p>
 * It shows two lists containing the genome names for each of the base
 * mutation they belong to.
 * </p>
 */
public class PointMutationBubbleDescriptionController implements Initializable {

  @FXML
  private Label labelGenomesOne;
  @FXML
  private Label labelGenomesTwo;

  @FXML
  private ListView<String> listViewGenomesOne;

  @FXML
  private ListView<String> listViewGenomesTwo;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
  }

  /**
   * Call this to set-up this view.
   *
   * <p>
   * Fills in the data of this view. Please refer to
   * {@link PointMutationBubbleDescriptionController}
   * for more information about this view.
   * </p>
   *
   * @param bubble the {@link PointMutationBubble} that holds the data.
   */
  public void setup(PointMutationBubble bubble) {
    if (bubble.getChildren().size() == 2) {
      Iterator<GraphNode>  it = bubble.getChildren().iterator();
      GraphNode childOne = it.next();
      GraphNode childTwo = it.next();

      if (childOne.getClass() == SequenceNode.class && childTwo.getClass() == SequenceNode.class) {
        SequenceNode seqOne = ((SequenceNode) childOne);
        SequenceNode seqTwo = ((SequenceNode) childTwo);

        setupLabels(seqOne, seqTwo);
        setupListViews(seqOne, seqTwo);
      }
    }
  }

  /**
   * Sets up the labels.
   */
  private void setupLabels(SequenceNode seqOne, SequenceNode seqTwo) {
    String labelText = "Genomes with base %s:";
    labelGenomesOne.setText(String.format(labelText, seqOne.getSequence()));
    labelGenomesTwo.setText(String.format(labelText, seqTwo.getSequence()));
  }

  /**
   * Sets up the list views.
   */
  private void setupListViews(SequenceNode seqOne, SequenceNode seqTwo) {
    List<String> genomesOne = seqOne.getGenomes().stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().collect(Collectors.toCollection(ArrayList::new));
    List<String> genomesTwo = seqTwo.getGenomes().stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().collect(Collectors.toCollection(ArrayList::new));

    listViewGenomesOne.getItems().addAll(genomesOne);
    listViewGenomesTwo.getItems().addAll(genomesTwo);
  }
}
