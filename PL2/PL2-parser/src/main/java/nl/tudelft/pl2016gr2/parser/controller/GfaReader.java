package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.Scanner;

/**
 * This class reads a gfa file.
 *
 * @author Cas
 *
 */
public class GfaReader {

  private Scanner sc;

  private OriginalGraph originalGraph;
  public final int numNodes;

  /**
   * Creates a reader object and reads the gfa data from the filename.
   *
   * @param filename  the name of the file to be read.
   * @param graphsize the size of the graph.
   */
  public GfaReader(String filename, int graphsize) {
    this.numNodes = graphsize;
    this.sc = new Scanner(GfaReader.class.getClassLoader().getResourceAsStream(filename));
    originalGraph = new OriginalGraph();
    FullGfaReader.prepNodes(originalGraph, numNodes);
    read();

    sc.close();
    sc = null;
  }

  /**
   * Read the actual data.
   */
  private void read() {
    int count = 0;
    while (sc.hasNext()) {
      count++;
      String next = sc.next();
      switch (next) {
        case "H":
          sc.nextLine();
          break;
        case "S":
          sc.nextLine();
          break;
        case "L":
          parseL();
          break;
        default:
          System.out.println("Not S, H or L at: " + count);
      }
    }
    System.out.println("Number of lines read: " + count);
  }

  /**
   * Parse a read 'L' character.
   */
  private void parseL() {
    int parent = sc.nextInt();
    sc.next();
    int child = sc.nextInt();
    Node par = originalGraph.getNode(parent);
    Node ch = originalGraph.getNode(child);
    par.addOutlink(ch.getId());
    ch.addInlink(par.getId());
    sc.nextLine();
  }

  /**
   * Method which returns the read graph.
   *
   * @return The graph.
   */
  public OriginalGraph getGraph() {
    return originalGraph;
  }

}
