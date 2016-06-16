package nl.tudelft.pl2016gr2.model.metadata;

import javafx.scene.paint.Color;
import nl.tudelft.pl2016gr2.model.MetaData;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This enum contains all of the lineages and their colors.
 *
 * @author Faris
 */
public enum LineageColor {

  LIN1(Color.web("0xed00c3")),
  LIN2(Color.web("0x0000ff")),
  LIN3(Color.web("0x500079")),
  LIN4(Color.web("0xff0000")),
  LIN5(Color.web("0x4e2c00")),
  LIN6(Color.web("0x69ca00")),
  LIN7(Color.web("0xff7e00")),
  LINB(Color.web("0x00ff9c")),
  LIN_ANIMAL(Color.web("0x00ff9c")),
  LIN_CANETTI(Color.web("0x00ffff")),
  NONE(Color.BLACK);

  private final Color color;

  private LineageColor(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  /**
   * Transforms the lineage string of the given metadata into a lineage.
   *
   * @param metadata the metadata.
   * @return the lineage.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  public static LineageColor toLineage(MetaData metadata) {
    if (metadata == null) {
      return NONE;
    }
    return toLineage(metadata.lineage);
  }

  /**
   * Transforms the given string to a lineage. There is really no logical way to split this method,
   * so the suppress warnings annotation is used for the method length.
   *
   * @param lineage the lineage string.
   * @return the lineage.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  public static LineageColor toLineage(String lineage) {
    switch (lineage.toUpperCase()) {
      case "LIN 1":
        return LIN1;
      case "LIN 2":
        return LIN2;
      case "LIN 3":
        return LIN3;
      case "LIN 4":
        return LIN4;
      case "LIN 5":
        return LIN5;
      case "LIN 6":
        return LIN6;
      case "LIN 7":
        return LIN7;
      case "LIN B":
        return LINB;
      case "LIN ANIMAL":
        return LIN_ANIMAL;
      case "LIN CANETTI":
        return LIN_CANETTI;
      default:
        Logger.getLogger(LineageColor.class.getName()).log(Level.WARNING, "unknown lineage: {0}",
            lineage);
        return NONE;
    }
  }
}
