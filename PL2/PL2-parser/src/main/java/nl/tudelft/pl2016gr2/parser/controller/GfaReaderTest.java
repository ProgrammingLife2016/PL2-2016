package nl.tudelft.pl2016gr2.parser.controller;

import static org.junit.Assert.assertEquals;

import nl.tudelft.pl2016gr2.model.OriginalGraph;

import org.junit.Test;

public class GfaReaderTest {
  
  private static final String filename = "TBSMALL.gfa";
  private static final int GRAPH_SIZE = 5;

  @Test
  public void test() {
    GfaReader fgr = new GfaReader(filename, GRAPH_SIZE);
    OriginalGraph og = fgr.getGraph();
    assertEquals(og.getNodes().size(), GRAPH_SIZE);
  }

}
