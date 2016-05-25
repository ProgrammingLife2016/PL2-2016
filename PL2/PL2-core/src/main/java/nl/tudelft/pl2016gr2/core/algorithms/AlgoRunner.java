package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.FullGfaReader;

/**
 * This class acts as the launcher for my simple nl.tudelft.pl2016gr2.parser.
 *
 * @author Cas
 *
 */
public class AlgoRunner {

  //  public static final String FILENAME = "TEST3.gfa";
  ////  public static final int GRAPH_SIZE = 8728;
  //  public static final int GRAPH_SIZE = 10;
  //public static final String FILENAME = "FilterBubblesTest.gfa";
  public static final String FILENAME = "TB10.gfa";
  public static final int GRAPH_SIZE = 8728;
  //public static final String FILENAME = "FilterTest2.gfa";
  //public static final int GRAPH_SIZE = 10;
  //public static final int GRAPH_SIZE = 94258;
  //public static final int GRAPH_SIZE = 11;

  // public static final int GRAPH_SIZE = 10;
  /**
   * Main method to call. It prints the number of lines in the file (in the reader class), the
   * number of nodes (+1 for node 0), the time it took to run the program.
   *
   * @param args does not need to be specified and is not used.
   */
  public static void main(String[] args) {
    long startTime = System.currentTimeMillis();
    FullGfaReader reader = new FullGfaReader(FILENAME, GRAPH_SIZE);
    OriginalGraph graph = reader.getGraph();
    System.out.println("Size of the graph: " + graph.getSize());
    long endTime = System.currentTimeMillis();
    System.out.println("The loading took " + (endTime - startTime) + " milliseconds to run");

    // g.print();
    // System.out.println(g.getNodes().get(8).getIn().get(0).getParent().getFlow());
    long completeTime = System.currentTimeMillis();
    System.out.println("The algorithm took " + (completeTime - endTime) + " milliseconds to run");
  }
}
