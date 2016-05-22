package nl.tudelft.pl2016gr2.dao;

import nl.tudelft.pl2016gr2.model.Annotation;

import java.util.Collection;
import java.util.HashMap;

/**
 * A DAO for the {@link Annotation} class.
 *
 * <p>
 *  Typical usage:
 *  <pre>
 *  {@code
 *
 *    File file = new File("some/path/to/file");
 *    AnnotationReader reader = new AnnotationReader(file);
 *    AnnotationDAO dao = new AnnotationDAO(reader.read());
 *    ...
 *    dao.getAllAnnotations();
 *    ...
 *    dao.getAnnotation("TKK-01-0075");
 *  }
 *  </pre>
 * </p>
 * <p>
 *   Feel free to extend this class for ways to access metadata in an efficient way.
 * </p>
 */
@SuppressWarnings("AbbreviationAsWordInName")
public class AnnotationDAO {

  private HashMap<String, Annotation> annotations = new HashMap<>();

  /**
   * Simple constructor for the DAO.
   *
   * @param annotations A collection of annotations you want to be avaiable via this DAO.
   */
  public AnnotationDAO(Collection<Annotation> annotations) {
    annotations.forEach(annotation ->
        this.annotations.put(annotation.specimenID, annotation)
    );
  }

  public Annotation getAnnotation(String speciesId) {
    return annotations.get(speciesId);
  }

  public Collection<Annotation> getAllAnnotations() {
    return annotations.values();
  }

}
