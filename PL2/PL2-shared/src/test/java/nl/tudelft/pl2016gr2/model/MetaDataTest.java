//package nl.tudelft.pl2016gr2.model;
//
//import static org.junit.Assert.assertEquals;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//
//public class MetaDataTest {
//
//  private MetaData md;
//
//  /**
//   * Setup method, creates a new metadata object.
//   */
//  @Before
//  public void setup() {
//    MetaData meta = new MetaData(new Node(5, 4, new ArrayList<>(), 2),
//        "aactgttagcatagctcctagatcgtctcgatagcaagccatactgca", 1);
//    this.md = meta;
//  }
//
//  @Test
//  public void basesTest() {
//    String bas = "cgatatcgcgctacatcgatcatag";
//    md.setBases(bas);
//    assertEquals(bas, md.getBases());
//  }
//
//  @Test
//  public void passesTest() {
//    md.setPasses(4);
//    assertEquals(4, md.getPasses());
//  }
//
//  @Test
//  public void nodeTest() {
//    Node no = new Node(3, 2, new ArrayList<>(), 1);
//    md.setNode(no);
//    assertEquals(no, md.getNode());
//  }
//
//}
