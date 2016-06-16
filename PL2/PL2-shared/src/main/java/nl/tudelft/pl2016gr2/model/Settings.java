package nl.tudelft.pl2016gr2.model;

import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a singleton which remembers the chosen settings made by the user.
 *
 * @author Faris
 */
public class Settings {

  private static Settings instance;
  private final SimpleObjectProperty<ArrayList<BubbleAlgorithms>> algorithms;

  private Settings() {
    ArrayList<BubbleAlgorithms> algorithmList;
    algorithmList = new ArrayList<>();
    algorithmList.add(BubbleAlgorithms.POINT);
    algorithmList.add(BubbleAlgorithms.INDEL);
    algorithmList.add(BubbleAlgorithms.STRAIGHT);
    algorithmList.add(BubbleAlgorithms.PHYLO);
    algorithms = new SimpleObjectProperty<>(algorithmList);
  }

  /**
   * Get an instance of the singleton.
   *
   * @return an instance of the singleton.
   */
  public static synchronized Settings getInstance() {
    if (instance == null) {
      instance = new Settings();
    }
    return instance;
  }

  /**
   * Sets the new algorithms if it differs from the previously selected algorithms.
   *
   * @param algorithms the algorithms to set.
   */
  public void setBubblingAlgorithms(ArrayList<BubbleAlgorithms> algorithms) {
    if (this.algorithms.get().size() != algorithms.size()
        || !this.algorithms.get().containsAll(algorithms)) {
      this.algorithms.set(algorithms);
    }
  }

  /**
   * Add an invalidation listener which is triggered when the list of algorithms is updated.
   *
   * @param listener the listener to trigger when the list of algorithms is updated.
   */
  public void addListener(InvalidationListener listener) {
    algorithms.addListener(listener);
  }

  /**
   * Get the selected algorithms.
   *
   * @return the selected algorithms.
   */
  public List<BubbleAlgorithms> getAlgorithms() {
    return algorithms.get();
  }

  /**
   * All of the possible bubbling algorithms which can be used.
   */
  public enum BubbleAlgorithms {
    POINT, INDEL, STRAIGHT, PHYLO, GRAPH;
  }
}
