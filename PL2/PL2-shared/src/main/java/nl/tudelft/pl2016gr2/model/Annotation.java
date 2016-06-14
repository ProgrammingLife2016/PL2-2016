package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.util.Pair;

import java.util.HashMap;

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
  public final double score;
  public final String strand;
  public final String phase;
  private final HashMap<String, String> attributes = new HashMap<>();

  /**
   * Create an Annotation, used by the AnnotationReader.
   * @param sequenceId the id of the sequence
   * @param source the source
   * @param type the type
   * @param start the start
   * @param end the end
   * @param score the score 
   * @param strand the strand
   * @param phase the phase
   */
  public Annotation(String sequenceId, String source, String type, int start, int end, 
      double score, String strand, String phase) {
    this.sequenceId = sequenceId;
    this.source = source;
    this.type = type;
    this.start = start;
    this.end = end;
    this.score = score;
    this.strand = strand;
    this.phase = phase;
  }

  /**
   * Add an attribute to the attributes.
   *
   * @param attribute a Pair containing the attribute/value pair. 
   */
  public void addAttribute(Pair<String, String> attribute) {
    attributes.put(attribute.left.toLowerCase(), attribute.right);
  }

  /**
   * Get an attribute.
   *
   * @param attribute the attribute to get.
   * @return the value of the attribute.
   */
  public String getAttribute(String attribute) {
    return attributes.get(attribute.toLowerCase());
  }

  /**
   * Check if there is a value for the given attribute.
   *
   * @param attribute the attribute to check.
   * @return if the attribute has a value.
   */
  public boolean containsAttribute(String attribute) {
    return attributes.containsKey(attribute.toLowerCase());
  }
  
  /**
   * Returns all attributes.
   * @return a hashmap of attributes.
   */
  public HashMap<String, String> getAttributes() {
    return attributes;
  }
}
