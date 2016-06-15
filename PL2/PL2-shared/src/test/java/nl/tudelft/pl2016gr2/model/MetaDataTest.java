
package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * This class tests the {@link MetaData} class.
 *
 * @author Faris
 */
public class MetaDataTest {

  @Test
  public void testEnums() {
    assertEquals(MetaData.Sex.Female, MetaData.Sex.Female);
    assertEquals(MetaData.Sex.Male, MetaData.Sex.Male);
    assertEquals(MetaData.Sex.unknown, MetaData.Sex.unknown);
    assertEquals(MetaData.Isolation.NonSingle, MetaData.Isolation.NonSingle);
    assertEquals(MetaData.Isolation.Single, MetaData.Isolation.Single);
    assertEquals(MetaData.Status.Negative, MetaData.Status.Negative);
    assertEquals(MetaData.Status.Positive, MetaData.Status.Positive);
    assertEquals(MetaData.Status.unknown, MetaData.Status.unknown);
  }

  /**
   * Test of buildMetaDataString method, of class MetaData.
   */
  @Test
  public void testBuildMetaDataString() {
    MetaData metadata = new MetaData();
    assertEquals(25, metadata.buildMetaDataString().split("\n").length);
  }

}
