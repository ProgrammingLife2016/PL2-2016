package nl.tudelft.pl2016gr2.gui.view.selection;

import static nl.tudelft.pl2016gr2.gui.view.RootLayoutController.MONO_SPACED_FONT;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * This controller controls the selection View for a {@link IndelBubble}.
 *
 * <p>
 * It shows the sequence and two lists. In the lists are the genomes that either
 * contain or do not contain the sequence.
 * </p>
 */
public class IndelBubbleDescriptionController implements Initializable {

  @FXML
  private Label genomeInsertLabel;
  @FXML
  private Label genomeDeleteLabel;
  @FXML
  private TextArea genomesIn;
  @FXML
  private TextArea genomesDel;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    genomesIn.setFont(MONO_SPACED_FONT);
    genomesDel.setFont(MONO_SPACED_FONT);
  }

  /**
   * Call this to set-up this view.
   *
   * <p>
   * Fills in the data of this view. Please refer to {@link IndelBubbleDescriptionController}
   * for more information about this view.
   * </p>
   *
   * @param bubble the {@link IndelBubble} that holds the data.
   */
  public void setup(IndelBubble bubble) {

    Collection<Integer> allGenomes = new ArrayList<>();
    for (GraphNode inNode : bubble.getInEdges()) {
      allGenomes.addAll(inNode.getGenomes());
    }
    for (GraphNode inNode : bubble.getOutEdges()) {
      allGenomes.addAll(inNode.getGenomes());
    }
    Collection<Integer> inGenomeIds = new ArrayList<>();
    bubble.getChildren().stream().map(child -> child.getGenomes()).forEach(inGenomeIds::addAll);
    Collection<Integer> delGenomeIds = allGenomes.stream().filter(
        id -> !inGenomeIds.contains(id)
    ).collect(Collectors.toCollection(ArrayList::new));

    setupListViews(inGenomeIds, delGenomeIds);
  }

  /**
   * Sets up the list views.
   *
   * <p>
   * The views represent all the genomes that are either in or not in (del) for this indel.
   * </p>
   */
  private void setupListViews(Collection<Integer> inGenomeIds, Collection<Integer> delGenomeIds) {
    setText(inGenomeIds, genomesIn);
    genomeInsertLabel.setText("genomes in insertion (" + inGenomeIds.size() + "):");
    setText(delGenomeIds, genomesDel);
    genomeDeleteLabel.setText("genomes not in insertion (" + delGenomeIds.size() + "):");
  }

  private void setText(Collection<Integer> genomes, TextArea textArea) {
    StringBuilder sb = new StringBuilder();
    genomes.stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().forEach(genome -> sb.append(genome).append('\n'));
    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    textArea.setText(sb.toString());
  }
}
