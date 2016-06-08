package nl.tudelft.pl2016gr2.model;

import com.sun.istack.internal.logging.Logger;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * Stores the annotations read from the annotation file.
 *
 * @author Faris
 */
public class Annotation {

  public final String sequenceId;
  public final String source;
  public final String type;
  public final int start;
  public final int end;
  public final float score;
  public final String strand;
  public final String phase;
  private final HashMap<String, String> attributes = new HashMap<>();

  /**
   * Construct an annotation.
   *
   * @param annotationLine a line read from the annotation file containing a set of annotations.
   */
  public Annotation(String[] annotationLine) {
    sequenceId = annotationLine[0];
    source = annotationLine[1];
    type = annotationLine[2];
    start = Integer.parseInt(annotationLine[3]);
    end = Integer.parseInt(annotationLine[4]);
    score = Float.parseFloat(annotationLine[5]);
    strand = annotationLine[6];
    phase = annotationLine[7];
    for (String attribute : annotationLine[8].split(";")) {
      addAttribute(attribute);
    }
  }

  /**
   * Add an attribute to the attributes.
   *
   * @param attribute the string containing the attributes. The attribute string must follow the
   *                  following syntax: "attribute=value". If the syntax is incorrect, nothing will
   *                  be added.
   */
  private void addAttribute(String attribute) {
    if (!attribute.contains("=")) {
      Logger.getLogger(getClass()).log(Level.WARNING, "This isn't a valid attribute: " + attribute);
      return;
    }
    String[] parts = attribute.split("=", 2);
    attributes.put(parts[0], parts[1]);
  }

  /**
   * Get an attribute.
   *
   * @param attribute the attribute to get.
   * @return the value of the attribute.
   */
  public String getAttribute(String attribute) {
    return attributes.get(attribute);
  }

  /**
   * Check if there is a value for the given attribute.
   *
   * @param attribute the attribute to check.
   * @return if the attribute has a value.
   */
  public boolean containsAttribute(String attribute) {
    return attributes.containsKey(attribute);
  }
}
