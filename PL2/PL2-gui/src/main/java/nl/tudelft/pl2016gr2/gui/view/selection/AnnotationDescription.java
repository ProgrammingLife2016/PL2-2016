package nl.tudelft.pl2016gr2.gui.view.selection;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import nl.tudelft.pl2016gr2.model.Annotation;

/**
 * This class can be used to describe an annotation.
 *
 * @author Faris
 */
public class AnnotationDescription implements ISelectionInfo {

  private final Annotation annotation;

  public AnnotationDescription(Annotation annotation) {
    this.annotation = annotation;
  }

  @Override
  public Node getNode() {
    VBox vbox = new VBox();
    vbox.setPrefWidth(248);
    annotation.forEachProperty((String property, String value) -> {
      Label label = new Label(property + ": " + value);
      vbox.getChildren().add(label);
    });
    return vbox;
  }
}
