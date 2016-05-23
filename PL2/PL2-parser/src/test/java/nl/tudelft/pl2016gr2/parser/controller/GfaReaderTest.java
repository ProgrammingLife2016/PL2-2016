//package nl.tudelft.pl2016gr2.parser.controller;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//import nl.tudelft.pl2016gr2.model.Node;
//import nl.tudelft.pl2016gr2.model.OriginalGraph;
//import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import java.util.ArrayList;
//
///**
// * This class tests the {@link GfaReader} class.
// *
// * @author Cas
// */
//public class GfaReaderTest {
//
//  private static final String TEST_GRAPH = "SMALL.gfa";
//  private GfaReader gfaReader;
//
//  /**
//   * Initialize some testing variables.
//   */
//  @Before
//  public void initialize() {
//    gfaReader = new GfaReader(TEST_GRAPH);
//  }
//
//  /**
//   * Large integration test.
//   */
//  @Test
//  public void integrationTest() {
//    OriginalGraph og = gfaReader.read();
//    assertEquals(og.getGenomes().size(), 11);
//    assertEquals(og.getNode(3).getGenomes().size(), 1);
//  }
//
//  /**
//   * Test of read method, of class GfaReader.
//   */
//  @Test
//  public void testRead() {
//    OriginalGraph actual = gfaReader.read();
//    OriginalGraph expected = AccessPrivate.getFieldValue("originalGraph", GfaReader.class,
//        gfaReader);
//    assertEquals(expected, actual);
//  }
//
//  /**
//   * Test of parseEdge method, of class GfaReader.
//   */
//  @Test
//  public void testParseEdge() {
//    char[] chars = "L\t2\t+\t3\t+\t0M".toCharArray();
//    AccessPrivate.callMethod("parseEdge", GfaReader.class, gfaReader, chars);
//    Node node2 = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 2);
//    Node node3 = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 3);
//    assertEquals(1, node2.getOutlinks().size());
//    assertEquals(3, (long) node2.getOutlinks().get(0));
//    assertEquals(1, node3.getInlinks().size());
//    assertEquals(2, (long) node3.getInlinks().get(0));
//  }
//
//  /**
//   * Test of parseNode method, of class GfaReader.
//   */
//  @Test
//  public void testParseNode() {
//    char[] chars = ("S\t3\tC\t*\tORI:Z:TKK_02_0008.fasta\tCRD:Z:TKK_02_0008.fasta\t"
//        + "CRDCTG:Z:NZ_KK327777.1\tCTG:Z:NZ_KK327777.1\tSTART:Z:1451").toCharArray();
//    AccessPrivate.callMethod("parseNode", GfaReader.class, gfaReader, chars);
//    Node node = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 3);
//    assertEquals("C", node.getBases());
//    assertEquals(1451, node.getAlignment());
//    assertEquals(1, node.getGenomes().size());
//    assertEquals("TKK_02_0008.fasta", node.getGenomes().iterator().next());
//  }
//
//  /**
//   * Test of parseNodeBases method, of class GfaReader.
//   */
//  @Test
//  public void testParseNodeBases() {
//    char[] chars = "S\t2\tAGACACCACAACCGACAACGACGAGATTGATGAC\t".toCharArray();
//    Node nodeSpy = Mockito.spy(new Node(0, 0, new ArrayList<>(), 0));
//    AccessPrivate.callMethod("parseNodeBases", GfaReader.class, null, nodeSpy, chars, 4);
//    verify(nodeSpy, times(1)).setBases("AGACACCACAACCGACAACGACGAGATTGATGAC");
//  }
//
//  /**
//   * Test of parseNodeGenomes method, of class GfaReader.
//   */
//  @Test
//  public void testParseNodeGenomes() {
//    char[] chars = "\t*\tORI:Z:MT_H37RV_BRD_V5.ref.fasta;TKK_02_0005.fasta;\t".toCharArray();
//    Node node = new Node(0, 0, new ArrayList<>(), 0);
//    AccessPrivate.callMethod("parseNodegenomes", GfaReader.class, null, node, chars, 1);
//    assertTrue(node.getGenomes().contains("MT_H37RV_BRD_V5.ref.fasta"));
//    assertTrue(node.getGenomes().contains("TKK_02_0005.fasta"));
//  }
//
//  /**
//   * Test of parseNodeOrientation method, of class GfaReader.
//   */
//  @Test
//  public void testParseNodeOrientation() {
//    char[] chars = ("CRD:Z:MT_H37RV_BRD_V5.ref.fasta\tCRDCTG:Z:MT_H37RV_BRD_V5\tCTG:Z:NZ_KK327777.1"
//        + ";NZ_KK350906.1;NZ_KK327775.1;MT_H37RV_BRD_V5;NZ_KK350895.1\tSTART:Z:371").toCharArray();
//    Node nodeSpy = Mockito.spy(new Node(0, 0, new ArrayList<>(), 0));
//    AccessPrivate.callMethod("parseNodeOrientation", GfaReader.class, null, nodeSpy, chars, 0);
//    verify(nodeSpy, times(1)).setAlignment(371);
//  }
//
//  /**
//   * Test of parseHeader method, of class GfaReader.
//   */
//  @Test
//  public void testParseHeader() {
//    char[] chars = "ORI:Z:MT_H37RV_BRD_V5.ref.fasta;TKK_02_0001.fasta;".toCharArray();
//    AccessPrivate.callMethod("parseHeader", GfaReader.class, gfaReader, chars);
//    ArrayList<String> actual = AccessPrivate.getFieldValue("genomes", GfaReader.class, gfaReader);
//    assertTrue(actual.contains("MT_H37RV_BRD_V5.ref.fasta"));
//    assertTrue(actual.contains("TKK_02_0001.fasta"));
//  }
//
//  /**
//   * Test of skipTillCharacter method, of class GfaReader.
//   */
//  @Test
//  public void testSkipTillCharacter() {
//    char[] chars = "four:four::".toCharArray();
//    int toSkip = AccessPrivate.callMethod("skipTillCharacter", GfaReader.class, null, chars, 2, ':',
//        2);
//    assertEquals(9, toSkip);
//  }
//
//  /**
//   * Test of skipTillCharacter method, of class GfaReader.
//   */
//  @Test
//  public void testgetNode() {
//    Node node = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 10);
//    Node node2 = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 10);
//    assertEquals(node, node2);
//  }
//}
