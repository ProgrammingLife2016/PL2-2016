package nl.tudelft.pl2016gr2.gui.view.selection;

import static nl.tudelft.pl2016gr2.gui.view.RootLayoutController.MONO_SPACED_FONT;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.nodes.IndelBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PhyloBubble;
import nl.tudelft.pl2016gr2.model.graph.nodes.PointMutationBubble;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * This controller controls the selection View for a {@link PhyloBubble}.
 *
 * <p>
 * It will list the count of Indels and Point mutations in the children. Also lists all genomes
 * that are contained in this bubble. Furthermore it includes a button to select the
 * corresponding treeNode in the tree.
 * </p>
 *
 */
public class PhyloBubbleDescriptionController implements Initializable {

  @FXML
  private Label labelIndelCount;
  @FXML
  private Label labelPointMutationCount;
  @FXML
  private Label genomesInBubble;
  @FXML
  private TextArea genomeList;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    genomeList.setFont(MONO_SPACED_FONT);
  }

  /**
   * Call this to set-up this view.
   *
   * <p>
   * Fills in the data of this view. Please refer to {@link PhyloBubbleDescriptionController}
   * for more information about this view.
   * </p>
   *
   * @param bubble the {@link PhyloBubble} that holds the data.
   */
  public void setup(PhyloBubble bubble) {
    StringBuilder sb = new StringBuilder();
    Collection<Integer> genomes = bubble.getGenomes();
    genomes.stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().forEach(genome -> sb.append(genome).append('\n'));
    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    genomeList.setText(sb.toString());
    genomesInBubble.setText("Genomes in this bubble (" + genomes.size() + "):");

    setupCountLabels(bubble);
  }

  /**
   * This method setups the count labels.
   *
   * <p>
   * Occurrences of {@link IndelBubble} and {@link PointMutationBubble} are counted and displayed
   * on the labels.
   * </p>
   */
  private void setupCountLabels(PhyloBubble bubble) {
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
