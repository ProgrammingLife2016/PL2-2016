package nl.tudelft.pl2016gr2.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import nl.tudelft.pl2016gr2.model.Annotation;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

@SuppressWarnings({"MemberName", "AbbreviationAsWordInName"})
public class AnnotationDAOTest {

  AnnotationDAO dao;
  Annotation a1 = new Annotation();
  Annotation a2 = new Annotation();
  Annotation a3 = new Annotation();
  Annotation a4 = new Annotation();

  /**
   * Sets up the {@link #dao} with the above test annotations.
   */
  @Before
  public void setup() {
    a1.specimenId = "a1";
    a2.specimenId = "a2";
    a3.specimenId = "a3";
    a4.specimenId = "a4";

    Collection<Annotation> testAnnotations = new ArrayList<>();

    testAnnotations.add(a1);
    testAnnotations.add(a2);
    testAnnotations.add(a3);
    testAnnotations.add(a4);

    dao = new AnnotationDAO(testAnnotations);
  }

  @Test
  public void testGetAnnotation() throws Exception {
    assertSame(dao.getAnnotation("a1"), a1);
    assertSame(dao.getAnnotation("a2"), a2);
    assertSame(dao.getAnnotation("a3"), a3);
    assertSame(dao.getAnnotation("a4"), a4);
  }

  @Test
  public void testgetAllAnnotations() throws Exception {
    Collection<Annotation> annotations = dao.getAllAnnotations();

    assertEquals(4, annotations.size());
    Stream.of(a1, a2, a3, a4).forEach(annotation -> {
      assertTrue(annotations.contains(annotation));
    });
  }

}
