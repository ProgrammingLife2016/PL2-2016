package nl.tudelft.pl2016gr2.gui.view.selection;

import static nl.tudelft.pl2016gr2.gui.view.RootLayoutController.MONO_SPACED_FONT;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.Node;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

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
  private TextArea genomeListOne;
  @FXML
  private TextArea genomeListTwo;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    genomeListOne.setFont(MONO_SPACED_FONT);
    genomeListTwo.setFont(MONO_SPACED_FONT);
  }

  /**
   * Call this to set-up this view.
   *
   * <p>
   * Fills in the data of this view. Please refer to
   * {@link PointMutationBubbleDescriptionController} for more information about this view.
   * </p>
   *
   * @param bubble the {@link PointMutationBubble} that holds the data.
   */
  public void setup(PointMutationBubble bubble) {
    if (bubble.getChildren().size() == 2) {
      Iterator<GraphNode>  it = bubble.getChildren().iterator();
      GraphNode childOne = it.next();
      GraphNode childTwo = it.next();

      if (!childOne.hasChildren() && !childTwo.hasChildren()) {
        /* we can now assume the children implement Node */
        Node seqOne = ((Node) childOne);
        Node seqTwo = ((Node) childTwo);

        setupLabels(seqOne, seqTwo);
        setupListViews(seqOne, seqTwo);
      }
    }
  }

  /**
   * Sets up the labels.
   */
  private void setupLabels(Node seqOne, Node seqTwo) {
    String labelText = "Genomes with base %s (%d):";
    labelGenomesOne.setText(String.format(labelText, seqOne.getSequence(), seqOne.getGenomeSize()));
    labelGenomesTwo.setText(String.format(labelText, seqTwo.getSequence(), seqTwo.getGenomeSize()));
  }

  /**
   * Sets up the list views.
   */
  private void setupListViews(Node seqOne, Node seqTwo) {
    setText(seqOne, genomeListOne);
    setText(seqTwo, genomeListTwo);
  }

  private void setText(Node sequence, TextArea textArea) {
    StringBuilder sb = new StringBuilder();
    sequence.getGenomes().stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().forEach(genome -> sb.append(genome).append('\n'));
    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    textArea.setText(sb.toString());
  }
}
