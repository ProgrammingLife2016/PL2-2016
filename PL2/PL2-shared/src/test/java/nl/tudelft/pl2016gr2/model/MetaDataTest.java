package nl.tudelft.pl2016gr2.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MetaDataTest {
  
  private MetaData md;

  /**
   * Setup method, creates a new metadata object.
   */
  @Before
  public void setup() {
    MetaData meta = new MetaData(new Node(5, 4, null, 2), 
        "aactgttagcatagctcctagatcgtctcgatagcaagccatactgca", 1);
    this.md = meta;
  }
  
  @Test
  public void basesTest() {
    String bas = "cgatatcgcgctacatcgatcatag";
    md.setBases(bas);
    assertEquals(md.getBases(), bas);
  }
  
  @Test
  public void passesTest() {
    md.setPasses(4);
    assertEquals(md.getPasses(), 4);
  }
  
  @Test
  public void nodeTest() {
    Node no = new Node(3, 2, null, 1);
    md.setNode(no);
    assertEquals(md.getNode(), no);
  }

}
