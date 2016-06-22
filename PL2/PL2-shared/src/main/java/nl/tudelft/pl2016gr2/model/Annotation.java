package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.util.Pair;

import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Stores the annotations read from the annotation file.
 *
 * @author Faris
 */
public class Annotation implements Comparable<Annotation> {

  public final String sequenceId;
  public final String source;
  public final String type;
  public final int start;
  public final int end;
  public final double score;
  public final String strand;
  public final String phase;
  private final HashMap<String, String> attributes = new HashMap<>();

  private int startInGraph;
  private int endInGraph;

  /**
   * Create an Annotation, used by the AnnotationReader.
   *
   * @param sequenceId the id of the sequence
   * @param source     the source
   * @param type       the type
   * @param start      the start
   * @param end        the end
   * @param score      the score
   * @param strand     the strand
   * @param phase      the phase
   */
  public Annotation(String sequenceId, String source, String type, int start, int end,
      double score, String strand, String phase) {
    this.sequenceId = sequenceId;
    this.source = source;
    this.type = type;
    this.start = start - 1;
    this.end = end - 1;
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
   *
   * @return a hashmap of attributes.
   */
  public HashMap<String, String> getAttributes() {
    return attributes;
  }

  public int getStartInGraph() {
    return startInGraph;
  }

  public void setStartInGraph(int startInGraph) {
    this.startInGraph = startInGraph;
  }

  public int getEndInGraph() {
    return endInGraph;
  }

  public void setEndInGraph(int endInGraph) {
    this.endInGraph = endInGraph;
  }

  /**
   * Get the name of this annotation, or "unknown" if no name was found.
   *
   * @return the name of this annotation, or "unknown" if no name was found.
   */
  public String getName() {
    String name = getAttribute("name");
    if (name != null) {
      return name;
    }
    for (String property : attributes.keySet()) {
      if (property.toLowerCase().contains("name")) {
        return getAttribute(property);
      }
    }
    return "unknown";
  }

  /**
   * Perform an action for each (property, value) pair.
   *
   * @param propertyConsumer the action to perform.
   */
  public void forEachProperty(BiConsumer<String, String> propertyConsumer) {
    attributes.forEach(propertyConsumer);
    propertyConsumer.accept("sequenceId", sequenceId);
    propertyConsumer.accept("source", source);
    propertyConsumer.accept("phase", phase);
    propertyConsumer.accept("strand", strand);
    propertyConsumer.accept("type", type);
    propertyConsumer.accept("score", String.format("%.2f", score));
    propertyConsumer.accept("start", String.format("%d", start));
    propertyConsumer.accept("end", String.format("%d", end));
  }

  @Override
  public int compareTo(Annotation other) {
    return this.start - other.start;
  }
}
