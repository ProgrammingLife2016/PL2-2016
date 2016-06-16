package nl.tudelft.pl2016gr2.parser.controller;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class tests the {@link AnnotationReader} class.
 *
 * @author Faris
 */
public class AnnotationReaderTest {

  private final String testString = "MT_H37RV_BRD_V5\tsource\texplosive\t3552761\t3553087\t0.0\t+"
      + "\t.\tcalhounClass=Gene;Name=transposase;ID=7000008460096610;displayName=transposase\n"
      + "MT_H37RV_BRD_V6\tnull\ttransposase\t3116828\t3118237\t0.1\t++\t..\t"
      + "calhounClass=Gene;Name=transposase;ID=7000008460096613;displayName=transposase\n"
      + "NON_EXISTING_GENOME\tnull\ttransposase\t3116828\t3118237\t0.0\t+\t.\t"
      + "calhounClass=Gene;Name=transposase;ID=7000008460096613;displayName=transposase";
  private Collection<Annotation> readResult;

  /**
   * Initialize the readResult.
   */
  @Before
  public void setup() {
    GenomeMap.getInstance().clear();
    GenomeMap.getInstance().addGenome("MT_H37RV_BRD_V5");
    GenomeMap.getInstance().addGenome("MT_H37RV_BRD_V6");
    InputStream testStringStream = new ByteArrayInputStream(
        testString.getBytes(StandardCharsets.UTF_8));
    AnnotationReader reader = new AnnotationReader(testStringStream);
    readResult = reader.read();
  }

  /**
   * Clear the used variables.
   */
  @After
  public void tearDown() {
    GenomeMap.getInstance().clear();
  }

  /**
   * Test if reading a line which is too large is handled correctly.
   */
  @Test
  public void lineSizeTest() {
    String test = "NON_EXISTING_GENOME\tnull\ttransposase\t3116828\t3118237\t0.0\t+"
        + "\t.\t \t too long"
        + "calhounClass=Gene;Name=transposase;ID=7000008460096613;displayName=transposase";
    InputStream testStringStream = new ByteArrayInputStream(test.getBytes(StandardCharsets.UTF_8));
    AnnotationReader reader = new AnnotationReader(testStringStream);
    Iterator<Annotation> resultIterator = reader.read().iterator();
    assertEquals(false, resultIterator.hasNext());
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadSize() {
    assertEquals(2, readResult.size());
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadId() {
    Iterator<Annotation> it = readResult.iterator();
    assertEquals("MT_H37RV_BRD_V5", it.next().sequenceId);
    assertEquals("MT_H37RV_BRD_V6", it.next().sequenceId);
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadSource() {
    Iterator<Annotation> it = readResult.iterator();
    assertEquals("source", it.next().source);
    assertEquals("null", it.next().source);
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadType() {
    Iterator<Annotation> it = readResult.iterator();
    assertEquals("explosive", it.next().type);
    assertEquals("transposase", it.next().type);
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadStart() {
    Iterator<Annotation> it = readResult.iterator();
    assertEquals(3552760, it.next().start);
    assertEquals(3116827, it.next().start);
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadEnd() {
    Iterator<Annotation> it = readResult.iterator();
    assertEquals(3553086, it.next().end);
    assertEquals(3118236, it.next().end);
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadScore() {
    Iterator<Annotation> it = readResult.iterator();
    assertEquals(0.0, it.next().score, 0.001);
    assertEquals(0.1, it.next().score, 0.001);
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadStrand() {
    Iterator<Annotation> it = readResult.iterator();
    assertEquals("+", it.next().strand);
    assertEquals("++", it.next().strand);
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadPhase() {
    Iterator<Annotation> it = readResult.iterator();
    assertEquals(".", it.next().phase);
    assertEquals("..", it.next().phase);
  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadFirstAttributes() {
    Iterator<Annotation> it = readResult.iterator();
    Annotation firstAnnotation = it.next();
    assertEquals("7000008460096610", firstAnnotation.getAttribute("ID"));
    assertEquals("transposase", firstAnnotation.getAttribute("displayName"));

  }

  /**
   * Test of read method, of class AnnotationReader.
   */
  @Test
  public void testReadSecondAttributes() {
    Iterator<Annotation> it = readResult.iterator();
    it.next();
    Annotation secondAnnotation = it.next();
    assertEquals("7000008460096613", secondAnnotation.getAttribute("id"));
    assertEquals("transposase", secondAnnotation.getAttribute("DiSpLaYNaMe"));
  }
}
