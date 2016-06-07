package nl.tudelft.pl2016gr2.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import nl.tudelft.pl2016gr2.model.MetaData;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

@SuppressWarnings({"MemberName", "AbbreviationAsWordInName"})
public class MetaDataDAOTest {

  MetaDataDAO dao;
  MetaData a1 = new MetaData();
  MetaData a2 = new MetaData();
  MetaData a3 = new MetaData();
  MetaData a4 = new MetaData();

  /**
   * Sets up the {@link #dao} with the above test annotations.
   */
  @Before
  public void setup() {
    a1.specimenId = "a1";
    a2.specimenId = "a2";
    a3.specimenId = "a3";
    a4.specimenId = "a4";

    Collection<MetaData> testAnnotations = new ArrayList<>();

    testAnnotations.add(a1);
    testAnnotations.add(a2);
    testAnnotations.add(a3);
    testAnnotations.add(a4);

    dao = new MetaDataDAO(testAnnotations);
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
    Collection<MetaData> metaDatas = dao.getAllAnnotations();

    assertEquals(4, metaDatas.size());
    Stream.of(a1, a2, a3, a4).forEach(annotation -> {
      assertTrue(metaDatas.contains(annotation));
    });
  }

}
