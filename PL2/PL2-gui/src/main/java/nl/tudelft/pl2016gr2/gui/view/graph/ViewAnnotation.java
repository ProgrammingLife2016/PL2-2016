package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.selection.AnnotationDescription;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.Annotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * This is an annotation indicator object which can be added to a IViewGraphNode to indicate that it
 * has an annotation.
 *
 * @author Faris
 */
public class ViewAnnotation extends Rectangle implements ISelectable {

  private static final double MINIMUM_LABEL_WIDTH = 10.0;
  private static final double MINIMUM_LABEL_HEIGHT = 12.0;
  private static final double MINIMUM_ARROW_WIDTH = 25.0;
  private static final Image RIGHT_ARROW = new Image(
      ViewAnnotation.class.getClassLoader().getResourceAsStream("images/arrow-right.png"));
  private static final Image LEFT_ARROW = new Image(
      ViewAnnotation.class.getClassLoader().getResourceAsStream("images/arrow-left.png"));
  private final Annotation annotation;
  private boolean oddOffset = false;

  /**
   * Construct a view annotation which can be drawn in the UI.
   *
   * @param annotation       the annotation of the view annotation.
   * @param selectionManager the selection manager.
   */
  public ViewAnnotation(Annotation annotation, SelectionManager selectionManager) {
    this.annotation = annotation;
    getStyleClass().add("annotationBox");
    setOnMouseClicked(event -> {
      selectionManager.select(this);
      event.consume();
    });

    selectionManager.checkSelected(this);
  }

  /**
   * Add a label with the name of the annotation to the given pane at the same position as this
   * rectangle. Make sure that the position and dimensions of this rectangle are set before calling
   * this!
   *
   * @param nodes the list of nodes to which to add the label.
   */
  public void addLabel(ArrayList<Node> nodes) {
    if (getWidth() < MINIMUM_LABEL_WIDTH || getHeight() < MINIMUM_LABEL_HEIGHT) {
      return;
    }
    Label label = new Label(annotation.getName());
    label.getStyleClass().add("annotationBoxLabel");
    label.setMouseTransparent(true);
    nodes.add(label);
    label.setMaxWidth(getWidth());
    label.setLayoutY(getLayoutY());
    label.setLayoutX(getLayoutX());
  }

  /**
   * Add an arrow to indicate the annotation direction.
   *
   * @param nodes the list of nodes to which to add the arrow.
   */
  public void addArrow(ArrayList<Node> nodes) {
    if (getWidth() < MINIMUM_ARROW_WIDTH || getHeight() < MINIMUM_LABEL_HEIGHT) {
      return;
    }
    ImageView arrow;
    if (Objects.equals(annotation.strand, "-")) {
      arrow = new ImageView(LEFT_ARROW);
    } else {
      arrow = new ImageView(RIGHT_ARROW);
    }
    nodes.add(arrow);
    arrow.setMouseTransparent(true);
    if (oddOffset || getLayoutY() - LEFT_ARROW.getHeight() < 0) {
      arrow.setLayoutY(getLayoutY() + getHeight());
    } else {
      arrow.setLayoutY(getLayoutY() - LEFT_ARROW.getHeight());
    }
    arrow.setLayoutX(getLayoutX() + (getWidth() - LEFT_ARROW.getWidth()) / 2.0);
  }

  /**
   * Sets a vertical offset to this label so if doesn't overlap with a different label.
   */
  public void setOddOffset() {
    oddOffset = true;
    setLayoutY(getLayoutY() + getHeight());
  }

  @Override
  public void select() {
    Collections.replaceAll(getStyleClass(), "annotationBox", "annotationBoxSelected");
  }

  @Override
  public void deselect() {
    Collections.replaceAll(getStyleClass(), "annotationBoxSelected", "annotationBox");
  }

  @Override
  public ISelectionInfo getSelectionInfo() {
    return new AnnotationDescription(annotation);
  }

  @Override
  public boolean isEqualSelection(ISelectable other) {
    if (other instanceof ViewAnnotation) {
      ViewAnnotation that = (ViewAnnotation) other;
      return this.annotation == that.annotation; // == to compare objects is intended
    }
    return false;
  }
}
