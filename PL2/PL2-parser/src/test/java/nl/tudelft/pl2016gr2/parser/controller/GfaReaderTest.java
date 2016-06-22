package nl.tudelft.pl2016gr2.parser.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.nodes.Node;
import nl.tudelft.pl2016gr2.model.graph.nodes.SequenceNode;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.AccessPrivate;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class tests the {@link GfaReader} class.
 *
 * @author Cas
 */
public class GfaReaderTest {

  private static final String TEST_GRAPH_RESOURCE = "SMALL.gfa";
  private GfaReader gfaReader;
  private File file;

  /**
   * Initialize some testing variables.
   *
   * @throws IOException When file creation fails.
   */
  @Before
  public void initialize() throws IOException {
    GenomeMap genomeMap = GenomeMap.getInstance();
    genomeMap.addGenome("MT_H37RV_BRD_V5");
    genomeMap.addGenome("TKK_02_0001");
    genomeMap.addGenome("TKK_02_0005");
    genomeMap.addGenome("TKK_02_0008");


    file = File.createTempFile("GfaReaderTest", TEST_GRAPH_RESOURCE);
    FileUtils.copyInputStreamToFile(
        GfaReader.class.getClassLoader().getResourceAsStream(TEST_GRAPH_RESOURCE), file);

    gfaReader = new GfaReader(new FileInputStream(file));
  }

  @After
  public void tearDown() {
    file.delete();
    AccessPrivate.setFieldValue("singleton_instance", GenomeMap.class, null, null);
  }

  /**
   * Large integration test.
   */
  @Test
  public void integrationTest() {
    AccessPrivate.setFieldValue("singleton_instance", GenomeMap.class, null, null);
    SequenceGraph generatedGraph = gfaReader.read();
    assertEquals(11, generatedGraph.getGenomes().size());
  }

  /**
   * Test of read method, of class GfaReader.
   */
  @Test
  public void testRead() {
    AccessPrivate.setFieldValue("singleton_instance", GenomeMap.class, null, null);
    SequenceGraph actual = gfaReader.read();
    SequenceGraph expected = AccessPrivate.getFieldValue("originalGraph", GfaReader.class,
        gfaReader);
    assertEquals(expected, actual);
  }

  /**
   * Test of parseEdge method, of class GfaReader.
   */
  @Test
  public void testParseEdge() {
    char[] chars = "L\t2\t+\t3\t+\t0M".toCharArray();
    AccessPrivate.callMethod("parseEdge", GfaReader.class, gfaReader, chars);
    SequenceNode node2 = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 2);
    SequenceNode node3 = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 3);
    assertEquals(1, node2.getOutEdges().size());
    assertEquals(new SequenceNode(3), node2.getOutEdges().iterator().next());
    assertEquals(1, node3.getInEdges().size());
    assertEquals(new SequenceNode(2), node3.getInEdges().iterator().next());
  }

  /**
   * Test of parseNode method, of class GfaReader.
   */
  @Test
  public void testParseNode() {
    char[] chars = ("S\t3\tC\t*\tORI:Z:TKK_02_0008.fasta\tCRD:Z:TKK_02_0008.fasta\t"
        + "CRDCTG:Z:NZ_KK327777.1\tCTG:Z:NZ_KK327777.1\tSTART:Z:1451").toCharArray();
    AccessPrivate.callMethod("parseNode", GfaReader.class, gfaReader, chars);
    Node node = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 3);
    assertEquals("C", node.getSequence());
    assertEquals(1, node.getGenomes().size());
    assertEquals(GenomeMap.getInstance().getId("TKK_02_0008"), node.getGenomes().iterator().next());
  }

  /**
   * Test of parseNodeBases method, of class GfaReader.
   */
  @Test
  public void testParseNodeBases() {
    char[] chars = "S\t2\tAGACACCACAACCGACAACGACGAGATTGATGAC\t".toCharArray();
    Node nodeSpy = Mockito.spy(new SequenceNode(0));
    AccessPrivate.callMethod("parseNodeBases", GfaReader.class, null, nodeSpy, chars, 4);
    verify(nodeSpy, times(1)).setSequence(Mockito.any());
    assertEquals("AGACACCACAACCGACAACGACGAGATTGATGAC", nodeSpy.getSequence());
  }

  /**
   * Test of parseNodeGenomes method, of class GfaReader.
   */
  @Test
  public void testParseNodeGenomes() {
    char[] chars = "\t*\tORI:Z:MT_H37RV_BRD_V5.ref.fasta;TKK_02_0005.fasta\t".toCharArray();
    Node node = new SequenceNode(0);
    AccessPrivate.callMethod("parseNodeGenomes", GfaReader.class, null, node, chars, 1);
    assertTrue(node.getGenomes().contains(GenomeMap.getInstance().getId("MT_H37RV_BRD_V5")));
    assertTrue(node.getGenomes().contains(GenomeMap.getInstance().getId((("TKK_02_0005")))));
  }

  /**
   * Test of parseHeader method, of class GfaReader.
   */
  @Test
  public void testParseHeader() {
    // Reset GenomeMap because this test will instantiate it.
    AccessPrivate.setFieldValue("singleton_instance", GenomeMap.class, null, null);

    char[] chars = "ORI:Z:MT_H37RV_BRD_V5.ref.fasta;TKK_02_0001.fasta;".toCharArray();
    AccessPrivate.callMethod("parseHeader", GfaReader.class, gfaReader, chars);
    //    ArrayList<String> actual = AccessPrivate.getFieldValue("genomes", GfaReader.class,
    // gfaReader);
    assertTrue(GenomeMap.getInstance().containsGenome(("MT_H37RV_BRD_V5")));
    assertTrue(GenomeMap.getInstance().containsGenome(("TKK_02_0001")));
  }

  /**
   * Test of skipTillCharacter method, of class GfaReader.
   */
  @Test
  public void testSkipTillCharacter() {
    char[] chars = "four:four::".toCharArray();
    int toSkip = AccessPrivate.callMethod("skipTillCharacter", GfaReader.class, null, chars, 2, ':',
        2);
    assertEquals(9, toSkip);
  }

  /**
   * Test of skipTillCharacter method, of class GfaReader.
   */
  @Test
  public void testgetNode() {
    Node node = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 10);
    Node node2 = AccessPrivate.callMethod("getNode", GfaReader.class, gfaReader, 10);
    assertEquals(node, node2);
  }
}
