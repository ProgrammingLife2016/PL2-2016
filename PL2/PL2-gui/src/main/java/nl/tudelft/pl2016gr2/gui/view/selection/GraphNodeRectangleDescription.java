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

public class GraphNodeRectangleDescription implements ISelectionInfo {

  final ViewGraphNodeRectangle viewGraphNodeRectangle;

  public GraphNodeRectangleDescription(ViewGraphNodeRectangle viewGraphNodeRectangle) {
    this.viewGraphNodeRectangle = viewGraphNodeRectangle;
  }

  @Override
  public Node getNode() {
    FXMLLoader loader = new FXMLLoader();
    try {
      loader.setLocation(ViewGraphNodeRectangle.class.getClassLoader().getResource(
          "pages/selection_descriptions/graphNodeRectangleDescription.fxml"));
      Node out = loader.load();
      GraphNodeRectangleDescriptionController controller = loader.getController();

      controller.setup(viewGraphNodeRectangle);

      return out;

    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException("This shouldn't happen", e);
    }
  }

  public static class GraphNodeRectangleDescriptionController implements Initializable {

    ViewGraphNodeRectangle viewGraphNodeRectangle;

    @FXML
    public TextArea textArea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    void setup(ViewGraphNodeRectangle viewGraphNodeRectangle) {
      this.viewGraphNodeRectangle = viewGraphNodeRectangle;

      textArea.setText(viewGraphNodeRectangle.);

    }

  }

}
