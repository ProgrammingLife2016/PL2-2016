package nl.tudelft.pl2016gr2.gui.view.selection;

import static nl.tudelft.pl2016gr2.gui.view.RootLayoutController.MONO_SPACED_FONT;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import nl.tudelft.pl2016gr2.model.Annotation;

/**
 * This class can be used to describe an annotation.
 *
 * @author Faris
 */
public class AnnotationDescription implements ISelectionInfo {

  private static final int TAB_WIDTH = 8;
  private final Annotation annotation;

  public AnnotationDescription(Annotation annotation) {
    this.annotation = annotation;
  }

  @Override
  public Node getNode() {
    final int amountOfTabs = calculateTabs();
    StringBuilder sb = new StringBuilder();
    annotation.forEachProperty((String property, String value) -> {
      sb.append(property);
      int tabs = amountOfTabs - (property.length()) / TAB_WIDTH;
      for (int i = 0; i < tabs; i++) {
        sb.append('\t');
      }
      sb.append(value);
      sb.append('\n');
    });
    sb.deleteCharAt(sb.length() - 1);

    TextArea textArea = new TextArea(sb.toString());
    textArea.setEditable(false);
    textArea.setFont(MONO_SPACED_FONT);
    return textArea;
  }

  /**
   * Calculate the amount of tabs which will be needed to align all of the property values nicely.
   */
  private int calculateTabs() {
    IntegerProperty maxPropertyWidth = new SimpleIntegerProperty(0);
    annotation.forEachProperty((String property, String value) -> {
      if (property.length() > maxPropertyWidth.get()) {
        maxPropertyWidth.set(property.length());
      }
    });
    if (maxPropertyWidth.get() % TAB_WIDTH == 0) {
      maxPropertyWidth.set(maxPropertyWidth.get() + TAB_WIDTH);
    } else {
      maxPropertyWidth.set(maxPropertyWidth.get() + maxPropertyWidth.get() % TAB_WIDTH);
    }
    return maxPropertyWidth.get() / TAB_WIDTH;
  }
}
