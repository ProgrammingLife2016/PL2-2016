package nl.tudelft.pl2016gr2.dao;

import nl.tudelft.pl2016gr2.model.MetaData;

import java.util.Collection;
import java.util.HashMap;

/**
 * A DAO for the {@link MetaData} class.
 *
 * <p>
 *  Typical usage:
 *  <pre>
 *  {@code
 *
 *    File file = new File("some/path/to/file");
 *    AnnotationReader reader = new AnnotationReader(file);
 *    MetaDataDAO dao = new MetaDataDAO(reader.read());
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
public class MetaDataDAO {

  private HashMap<String, MetaData> metaDatas = new HashMap<>();

  /**
   * Simple constructor for the DAO.
   *
   * @param metaDatas A collection of metaDatas you want to be avaiable via this DAO.
   */
  public MetaDataDAO(Collection<MetaData> metaDatas) {
    metaDatas.forEach(annotation ->
        this.metaDatas.put(annotation.specimenId, annotation)
    );
  }

  public MetaData getAnnotation(String speciesId) {
    return metaDatas.get(speciesId);
  }

  public Collection<MetaData> getAllAnnotations() {
    return metaDatas.values();
  }

}
