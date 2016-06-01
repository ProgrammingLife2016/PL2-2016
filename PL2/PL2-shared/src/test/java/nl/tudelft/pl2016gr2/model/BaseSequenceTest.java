package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.junit.Test;

/**
 * This class tests the {@link BaseSequence} class.
 *
 * @author Faris
 */
public class BaseSequenceTest {

  private final String longString = "ATGCATTAGCAATGCATTAGCAATGCATTAGCAATGCATTAGCAATGCATTAGCATACTNNN"
      + "ATGCATTAGCAATGCATTAGCAATGCATTAGCAATGCANTTAGCAATGCATTAGCAATGCATTAGCAATGCANTTAGCAATGCATTAGCA"
      + "ATGCATTAGCAATGCATTAGCAATGCATTAGCAATGCATTAGCAATGCATTNNAGCAATGCATTAGCAATGCATTAGCAATGCATTAGCA"
      + "ATGCATTAGCAATGCATTAGCNNNAATGCATTAGCAATGCATTAGCAATGCATTAGCAATGCATGCATGCAATTAGCAATGCATTAGCA";

  /**
   * Test of getBaseSequence method, of class BaseSequence.
   */
  @Test
  public void testGetBaseSequenceA() {
    BaseSequence seq = new BaseSequence("A");
    assertEquals("A", seq.getBaseSequence());
  }

  /**
   * Test of getBaseSequence method, of class BaseSequence.
   */
  @Test
  public void testGetBaseSequenceT() {
    BaseSequence seq = new BaseSequence("T");
    assertEquals("T", seq.getBaseSequence());
  }

  /**
   * Test of getBaseSequence method, of class BaseSequence.
   */
  @Test
  public void testGetBaseSequenceG() {
    BaseSequence seq = new BaseSequence("G");
    assertEquals("G", seq.getBaseSequence());
  }

  /**
   * Test of getBaseSequence method, of class BaseSequence.
   */
  @Test
  public void testGetBaseSequenceC() {
    BaseSequence seq = new BaseSequence("C");
    assertEquals("C", seq.getBaseSequence());
  }

  /**
   * Test of getBaseSequence method, of class BaseSequence.
   */
  @Test
  public void testGetBaseSequenceN() {
    BaseSequence seq = new BaseSequence("N");
    assertEquals("N", seq.getBaseSequence());
  }

  /**
   * Test of getBaseSequence method, of class BaseSequence.
   */
  @Test(expected = AssertionError.class)
  public void testGetBaseSequenceInvalid() {
    BaseSequence seq = new BaseSequence("I");
  }

  /**
   * Test of getBaseSequence method, of class BaseSequence.
   */
  @Test
  public void testGetBaseSequenceLongString() {
    BaseSequence seq = new BaseSequence(longString);
    assertEquals(longString, seq.getBaseSequence());
  }

  /**
   * Test of getBaseSequence method, of class BaseSequence.
   */
  @Test
  public void testGetBaseSequenceMemoryUsage() {
    BaseSequence seq = new BaseSequence(longString);
    int[] bases = AccessPrivate.getFieldValue("bases", BaseSequence.class, seq);
    assertEquals(32, bases.length); //32 ints are needed to store the 1011 bases
  }
}
