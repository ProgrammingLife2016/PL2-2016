package nl.tudelft.pl2016gr2.parser.controller;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.OriginalGraph;

import org.junit.Test;

/**
 * This class tests the {@link FullGfaReader} class.
 *
 * @author Cas
 */
public class FullGfaReaderTest {

  private static final String filename = "SMALL.gfa";
  private static final int GRAPH_SIZE = 5;

  @Test
  public void test() {
    FullGfaReader fgr = new FullGfaReader(filename, GRAPH_SIZE);
    OriginalGraph og = fgr.getGraph();
    assertEquals(og.getGenoms().size(), 11);
    assertEquals(og.getNode(3).getGenomes().size(), 1);
  }

}
