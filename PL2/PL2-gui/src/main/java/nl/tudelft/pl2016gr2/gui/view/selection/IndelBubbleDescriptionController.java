package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
  private ListView<String> listViewGenomesIn;

  @FXML
  private ListView<String> listViewGenomesDel;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {

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
    Collection<Integer> inGenomeIds = bubble.getGenomes();
    Collection<Integer> delGenomeIds = allGenomes.stream().filter(
        id -> !inGenomeIds.contains(id)
    ).collect(Collectors.toCollection(ArrayList::new));

    setupListViews(inGenomeIds, delGenomeIds);
  }

  /**
   * Sets up the list views.
   *
   * <p>
   * The views represent all the genomes that are either
   * in or not in (del) for this indel.
   * </p>
   */
  private void setupListViews(Collection<Integer> inGenomeIds, Collection<Integer> delGenomeIds) {
    List<String> inGenomes = inGenomeIds.stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().collect(Collectors.toCollection(ArrayList::new));
    List<String> delGenomes = delGenomeIds.stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().collect(Collectors.toCollection(ArrayList::new));

    listViewGenomesIn.getItems().addAll(inGenomes);
    listViewGenomesDel.getItems().addAll(delGenomes);
  }
}
