package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;


/**
 * Tests the {@link GenomeMap} class.
 *
 * @author Wouter Smit
 */
public class GenomeMapTest {

  private GenomeMap map;
  private static final String FIRST_GENOME = "firstGenome";
  private static final String SECOND_GENOME = "secondGenome";
  private static final String REFERENCE_GENOME = "referenceGenome";
  private static final String NON_EXISTENT_GENOME = "nonExistentGenome";
  private static final int EXTREMELY_HIGH_CAPACITY = 1000000;
  private int amountOfGenomes;

  /**
   * Creates the singleton object and fills it with default values.
   * This instruments the instantiation part of the getInstance method.
   */
  @Before
  public void setUp() {
    assertNull(AccessPrivate.getFieldValue("singleton_instance", GenomeMap.class, null));
    map = GenomeMap.getInstance();
    assertNotNull(map);
    map.addGenome(FIRST_GENOME);
    map.addGenome(SECOND_GENOME);
    amountOfGenomes = 2;
  }

  /**
   * Destroys the singleton instance.
   */
  @After
  public void tearDown() {
    AccessPrivate.setFieldValue("singleton_instance", GenomeMap.class, null, null);
  }

  @Test
  public void testGetInstanceShouldReturnSingleton() {
    assertEquals(map, GenomeMap.getInstance());
  }

  @Test
  public void testGetIdShouldBePersistentBetweenCalls() {
    Integer id = map.getId(FIRST_GENOME);
    assertEquals(id, map.getId(FIRST_GENOME));
  }

  @Test
  public void testGetIdShouldBeUnique() {
    assertFalse(map.getId(FIRST_GENOME).equals(map.getId(SECOND_GENOME)));
  }

  @Test
  public void testContainsGenomeShouldReturnTrueForExistent() {
    assertTrue(map.containsGenome(FIRST_GENOME));
  }

  @Test
  public void testContainsGenomeShouldReturnFalseForNonExistent() {
    assertFalse(map.containsGenome(NON_EXISTENT_GENOME));
  }

  @Test
  public void testGetGenomeShouldReturnSameGenomeForId() {
    int firstId = map.getId(FIRST_GENOME);

    assertEquals(FIRST_GENOME, map.getGenome(firstId));
  }

  @Test
  public void testGetGenomeShouldReturnNullNonExistentNegativeId() {
    int negativeId = -10;

    assertNull(map.getGenome(negativeId));
  }

  @Test
  public void testGetGenomeShouldReturnNullForNonExistent() {
    assertNull(map.getGenome(amountOfGenomes));
  }

  @Test
  public void testContainsIdShouldReturnTrueForExistent() {
    int validId = map.getId(FIRST_GENOME);
    assertTrue(map.containsId(validId));
  }

  @Test
  public void testContainsIdShouldReturnFalseForNonExistent() {
    int validId = 1110;

    assertFalse(validId == map.getId(FIRST_GENOME));
    assertFalse(validId == map.getId(SECOND_GENOME));
    assertNull(map.getGenome(validId));

    assertFalse(map.containsId(validId));
  }

  @Test
  public void testGetIdShouldReturnNullForNonExistent() {
    assertNull(map.getId("nonExistentGenome"));
  }

  @Test
  public void testAddGenomeShouldBindNewId() {
    String nextGenome = "nextGenome";
    assertNull(map.getId(nextGenome));

    map.addGenome(nextGenome);
    assertNotNull(map.getId(nextGenome));
  }

  @Test
  public void testAddGenomeShouldAddGenomeToMap() {
    String nextGenome = "nextGenome";
    assertNull(map.getId(nextGenome));

    map.addGenome(nextGenome);
    int nextId = map.getId(nextGenome);
    assertEquals(nextGenome, map.getGenome(nextId));
  }

  @Test
  public void testGetReferenceIdShouldReturnNullWhenNotSet() {
    assertNull(AccessPrivate.getFieldValue("reference_id", GenomeMap.class, map));
    assertNull(map.getReferenceId());
  }

  @Test
  public void testGetReferenceIdShouldReturnCorrectId() {
    Integer referenceTestId = amountOfGenomes;
    AccessPrivate.setFieldValue("reference_id", GenomeMap.class, map, referenceTestId);

    assertEquals(referenceTestId, map.getReferenceId());
  }

  @Test
  public void testGetReferenceGenomeShouldReturnNullWhenNotSet() {
    assertNull(map.getReferenceGenome());
  }

  @Test
  public void testGetReferenceGenomeShouldReturnCorrectGenome() {
    map.addReferenceGenome(REFERENCE_GENOME);

    assertEquals(REFERENCE_GENOME, map.getReferenceGenome());
  }

  @Test
  public void testAddReferenceGenomeShouldBindNewId() {
    assertNull(map.getReferenceId());

    map.addReferenceGenome(REFERENCE_GENOME);
    assertNotNull(map.getReferenceId());
  }

  @Test
  public void testAddReferenceGenomeShouldAddGenomeToMap() {
    map.addReferenceGenome(REFERENCE_GENOME);
    Integer referenceId = map.getReferenceId();

    assertEquals(REFERENCE_GENOME, map.getGenome(referenceId));
    assertEquals(referenceId, map.getId(REFERENCE_GENOME));
  }

  @Test
  public void testCopyAllGenomesShouldReturnEveryGenomeInMap() {
    Collection<Integer> copiedCollection = map.copyAllGenomes();
    assertTrue(copiedCollection.contains(map.getId(FIRST_GENOME)));
    assertTrue(copiedCollection.contains(map.getId(SECOND_GENOME)));
  }

  @Test
  public void testCopyAllGenomesShouldReturnOnlyGenomesInMap() {
    Collection<Integer> copiedCollection = map.copyAllGenomes();
    assertTrue(copiedCollection.size() == amountOfGenomes);
  }

  @Test
  public void testMapAllShouldMapEveryGenome() {
    Collection<String> newGenomes = new ArrayList<>(Arrays.asList(FIRST_GENOME, SECOND_GENOME));
    Collection<Integer> mappedGenomes = map.mapAll(newGenomes);

    assertTrue(newGenomes.stream().map(genome -> map.getId(genome)).collect(
        Collectors.toCollection(ArrayList::new)).containsAll(mappedGenomes));
  }

  @Test
  public void testMapAllShouldMapOnlySpecifiedGenomes() {
    Collection<String> newGenomes = new ArrayList<>(Arrays.asList(FIRST_GENOME, SECOND_GENOME));
    Collection<Integer> mappedGenomes = map.mapAll(newGenomes);

    assertEquals(newGenomes.size(), mappedGenomes.size());
  }


  @Test
  public void testTrimToSizeShouldFreeMemory() {
    map.ensureCapacity(EXTREMELY_HIGH_CAPACITY);
    System.gc();

    Runtime runtime = Runtime.getRuntime();
    long usedBefore = runtime.totalMemory() - runtime.freeMemory();

    map.trimToSize();
    System.gc();

    long usedAfter = runtime.totalMemory() - runtime.freeMemory();

    assertTrue(usedAfter < usedBefore);
  }
}