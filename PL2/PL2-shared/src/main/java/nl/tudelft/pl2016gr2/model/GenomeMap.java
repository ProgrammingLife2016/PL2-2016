package nl.tudelft.pl2016gr2.model;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Represents the mapping of genome names to IDs that can be used throughout the application.
 * <p>
 * This is necessary to minimize the memory consumption of storing the genome data in the graph.
 * </p>
 * <p>
 * Unless explicitly necessary, the programmer should use the IDs rather than the names.
 * This helps reduce memory usage when storing genomes.
 * </p>
 * <p>
 * This class implements the <i>Singleton design pattern</i>.
 * It can only be instantiated once.
 * This is useful because the mappings should be accessible everywhere.
 * </p>
 *
 * @author Wouter Smit
 */
public class GenomeMap {

  /**
   * Defines the load factor for the hash map that contains the genomes.
   */
  private static final float LOAD_FACTOR = 0.50f;
  /**
   * Defines the initial capacity for the hash map that contains the genomes.
   * This value is simply the default initial capacity.
   */
  private static final int INIT_CAPACITY = 16;

  @TestId(id = "singleton_instance")
  private static GenomeMap instance;

  private static final Integer REFERENCE_ID = 0;

  private ArrayList<String> genomes = new ArrayList<>();
  private HashMap<String, Integer> identifierMap = new HashMap<>(INIT_CAPACITY, LOAD_FACTOR);

  /**
   * Returns the Singleton instance of this map.
   *
   * @return The Singleton instance
   */
  public static GenomeMap getInstance() {
    if (instance == null) {
      instance = new GenomeMap();
    }
    return instance;
  }

  /**
   * Constructs a new empty GenomeMap.
   */
  private GenomeMap() {
  }

  /**
   * Checks if the specified ID is contained in the map.
   * <p>
   * Runs in O(1).
   * </p>
   *
   * @param identifier the ID to search for
   * @return <code>true</code> iff the specified ID is mapped to a genome
   */
  public boolean containsId(int identifier) {
    return identifier >= 0 && identifier < genomes.size() && genomes.get(identifier) != null;
  }

  /**
   * Checks if the specified genome name is contained in the map.
   * <p>
   * Runs in O(1).
   * </p>
   *
   * @param genome the name to search for
   * @return <code>true</code> iff the specified name is mapped to an ID
   */
  public boolean containsGenome(String genome) {
    return identifierMap.containsKey(genome);
  }

  /**
   * Finds the ID that is associated with the specified genome name.
   *
   * @param genome The name of the genome whose ID to find
   * @return The ID for the genome
   */
  public Integer getId(String genome) {
    return identifierMap.get(genome);
  }

  /**
   * Finds the genome name that is associated with the specified ID.
   *
   * @param identifier The ID of the genome whose name to find
   * @return The String representing the genome name or <code>null</code> if it was not found.
   */
  public String getGenome(int identifier) {
    if (identifier < genomes.size() && identifier >= 0) {
      return genomes.get(identifier);
    }
    return null;
  }

  /**
   * Adds the genome to the map.
   * <p>
   * This generates an ID for the genome, which is returned.
   * </p>
   *
   * @param genome The name of the genome
   * @return The ID that is now associated with the added genome
   * @throws AssertionError if the genome already exists in the map
   */
  public int addGenome(String genome) {
    assert !identifierMap.containsKey(genome) : "Duplicate genome added. Name: " + genome;

    int identifier = genomes.size();
    genomes.add(genome);
    identifierMap.put(genome, identifier);
    return identifier;
  }

  /**
   * Returns the ID of the reference genome.
   *
   * @return The ID of the reference genome, or <code>null</code> if no reference was set
   */
  public Integer getReferenceId() {
    return REFERENCE_ID;
  }

  /**
   * Returns the name of the reference genome.
   *
   * @return The name of the reference genome, or <code>null</code> if no reference was set
   */
  public String getReferenceGenome() {
    return getGenome(REFERENCE_ID);
  }

  /**
   * Ensures that this map is large enough to contain <code>capacity</code> elements.
   * <p>
   * Because trimming a hash map is not possible, the caller should be careful about increasing the
   * capacity too much.
   * </p>
   *
   * @param capacity The minimal capacity that this map should have
   */
  public void ensureCapacity(int capacity) {
    genomes.ensureCapacity(capacity);
    if (identifierMap.size() < capacity) {
      int hashCapacity = (int) Math.ceil(capacity * LOAD_FACTOR);
      HashMap<String, Integer> newMap = new HashMap<>(hashCapacity, LOAD_FACTOR);
      newMap.putAll(identifierMap);
      identifierMap = newMap;
    }
  }

  /**
   * Clears all elements from the map.
   * <p>
   * Can be used to reset the map. <b>All</b> data stored will be lost.
   * </p>
   */
  public void clear() {
    genomes = new ArrayList<>();
    identifierMap = new HashMap<>(INIT_CAPACITY, LOAD_FACTOR);
  }

  /**
   * Trims unused space in the memory of this map.
   * <p>
   * This method should be called after the map has been constructed.
   * </p>
   */
  public void trimToSize() {
    genomes.trimToSize();
  }

  /**
   * Map a collection of genome strings.
   *
   * @param genomes the genomes.
   * @return the ids of the genomes.
   */
  public Collection<Integer> mapAll(Collection<String> genomes) {
    return genomes.stream().map(genome -> identifierMap.get(genome)).collect(
        Collectors.toCollection(ArrayList::new));
  }

  /**
   * Returns a collection of the genomes currently in the map.
   * <p>
   * The collection is a copy of the genomes in this map.
   * It can be used directly.
   * </p>
   *
   * @return a copied collection of all genome ids.
   */
  public Collection<Integer> copyAllGenomes() {
    ArrayList<Integer> list = new ArrayList<>(genomes.size());
    for (int i = 0; i < genomes.size(); i++) {
      list.add(i);
    }
    return Collections.unmodifiableCollection(list);
  }
}
