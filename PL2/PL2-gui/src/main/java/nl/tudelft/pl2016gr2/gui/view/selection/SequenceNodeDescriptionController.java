package nl.tudelft.pl2016gr2.gui.view.selection;

import static nl.tudelft.pl2016gr2.gui.view.RootLayoutController.MONO_SPACED_FONT;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

/**
 * This controller controls the selection View for a {@link SequenceNode}.
 *
 * <p>
 * It shows the actual base sequence and the genomes this sequence belongs to.
 * </p>
 */
public class SequenceNodeDescriptionController implements Initializable {

  @FXML
  private TextField labelSequence;
  @FXML
  private Label genomesInSequence;
  @FXML
  private TextArea genomeList;

  @Override
  public void initialize(URL url, ResourceBundle resourceBundle) {
    labelSequence.textProperty().addListener((obs, old, newText) -> {
      Platform.runLater(() -> {
        Text text = new Text(newText);
        text.setFont(labelSequence.getFont());
        double width = text.getLayoutBounds().getWidth() + labelSequence.getPadding().getLeft()
            + labelSequence.getPadding().getRight() + 10d;
        if (width < labelSequence.getMinWidth()) {
          width = labelSequence.getMinWidth();
        }
        if (width > labelSequence.getPrefWidth()) {
          labelSequence.setPrefWidth(width);
          labelSequence.positionCaret(0);
        }
      });
    });
    genomeList.setFont(MONO_SPACED_FONT);
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
    StringBuilder sb = new StringBuilder();
    Collection<Integer> genomes = sequenceNode.getGenomes();
    genomes.stream().map(
        genomeId -> GenomeMap.getInstance().getGenome(genomeId)
    ).sorted().forEach(genome -> sb.append(genome).append('\n'));
    if (sb.length() > 0) {
      sb.deleteCharAt(sb.length() - 1);
    }
    genomeList.setText(sb.toString());
    labelSequence.setText(sequenceNode.getSequence());
    genomesInSequence.setText("Genomes in this sequence (" + genomes.size() + "):");
  }
}
