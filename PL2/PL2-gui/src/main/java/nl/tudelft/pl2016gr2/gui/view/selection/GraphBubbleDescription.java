package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class can be used for simple text.
 */
public class GraphBubbleDescription implements ISelectionInfo {

  private final String text;

  public GraphBubbleDescription(String text) {
    this.text = text;
  }
  
  @Override
  public Node getNode() {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(
          "pages/selection_descriptions/graphNodeRectangleDescription.fxml"));
      Node out = loader.load();
      GraphNodeRectangleDescriptionController controller = loader.getController();

      controller.setup(this);

      return out;

    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("This shouldn't happen", e);
    }
  }

  public static class GraphNodeRectangleDescriptionController implements Initializable {

    @FXML
    public TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    void setup(GraphBubbleDescription graphBubbleDescription) {

      textArea.setText(graphBubbleDescription.text);
    }
  }
}
