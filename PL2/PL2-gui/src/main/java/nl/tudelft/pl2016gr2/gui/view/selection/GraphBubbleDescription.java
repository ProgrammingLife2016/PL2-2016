package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import nl.tudelft.pl2016gr2.gui.view.graph.ViewGraphNodeRectangle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class can be used for simple text.
 */
public abstract class GraphBubbleDescription implements ISelectionInfo {

  public GraphBubbleDescription() {
  }

  @Override
  public Node getNode() {
    FXMLLoader loader = new FXMLLoader();
    try {
      loader.setLocation(ViewGraphNodeRectangle.class.getClassLoader().getResource(
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

  public abstract String getText();

  public static class GraphNodeRectangleDescriptionController implements Initializable {

    @FXML
    public TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    void setup(GraphBubbleDescription graphBubbleDescription) {

      textArea.setText(graphBubbleDescription.getText());
    }
  }
}
