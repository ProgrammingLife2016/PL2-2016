package nl.tudelft.pl2016gr2.gui.view;

import nl.tudelft.pl2016gr2.model.MetaData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * This class stores all of the read values for the catergorical metadata properties.
 *
 * @author Faris
 */
public class MetadataPropertyMap {

  private final SortedMap<String, ArrayList<String>> properties = new TreeMap<>();

  /**
   * Construct a metadata properties object.
   *
   * @param metaDatas the read metadata objects.
   */
  public MetadataPropertyMap(List<MetaData> metaDatas) {
    for (MetaData metaData : metaDatas) {
      Arrays.stream(MetaData.KNOWN_CATEGORICAL_COLUMNS).forEach(column -> {
        ArrayList<String> property = properties.get(column);
        if (property == null) {
          property = new ArrayList<>();
          properties.put(column, property);
        }
        String value = metaData.getValueForColumnName(column);
        if (!property.contains(value)) {
          property.add(value);
        }
      });
    }
    properties.forEach((String column, ArrayList<String> values) -> {
      values.sort(null);
    });
  }

  /**
   * Performs the given action for each entry in this map until all entries have been processed or
   * the action throws an exception.
   *
   * @param action The action to be performed for each entry
   */
  public void forEach(BiConsumer<String, ArrayList<String>> action) {
    properties.forEach(action);
  }
}
