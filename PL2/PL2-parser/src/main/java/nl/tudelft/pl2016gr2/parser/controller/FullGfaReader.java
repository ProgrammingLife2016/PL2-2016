package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class reads a gfa file.
 * 
 * @author Cas
 *
 */
public class FullGfaReader {
  private Scanner sc;
  
  private ArrayList<String> genoms = new ArrayList<String>();

  private OriginalGraph originalGraph;
  // This is ugly hardcoded, but this way we know how much nodes we have to
  // initialize.
  // @Wouter and Justin, you can probably find some better way to know this
  // beforehand. (read the file from the bottom for example)
  public final int numNodes;

  /**
   * Creates a reader object and reads the gfa data from the filename.
   * 
   * @param filename
   *          the name of the file to be read.
   */
  public FullGfaReader(String filename, int graphsize) {
    this.numNodes = graphsize;
    this.sc = new Scanner(FullGfaReader.class.getClassLoader().getResourceAsStream(filename));
    originalGraph = new OriginalGraph();
    prepNodes();
    read();

    sc.close();
    sc = null;
  }

  /**
   * Because we know (hardcoded) how many nodes there will be, we can prepare
   * them.
   */
  private void prepNodes() {
    for (int i = 1; i < numNodes + 1; i++) {
      originalGraph.addNode(new Node(i, 1, new ArrayList<>(), 0));
    }
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
          handleH(sc);
          break;
        case "S":
          handleS(sc);
          break;
        case "L":
          handleL(sc);
          break;
        default:
          System.out.println("Not S, H or L at: " + count);
          sc.nextLine();
      }
    }
    System.out.println("Number of lines read: " + count);
  }

  private void handleH(Scanner sc) {
    String line = sc.nextLine();
    this.genoms = extractGenoms(line);
    
  }
  
  private ArrayList<String> extractGenoms(String line) {
    String[] words = line.split(":");
    if (words[0].endsWith("ORI")) {
      String[] gens = words[2].split(";");
      ArrayList<String> gensAr = new ArrayList<String>();
      for (String s : gens) {
        gensAr.add(s);
      }
      return gensAr;   
    }
    return null;
  }

  private void handleS(Scanner sc) {
    int nodeId = sc.nextInt();
    String bases = sc.next();
    String line = sc.nextLine();
    String[] words = line.split("\\s+");
    ArrayList<String> nodeGens = extractGenoms(words[2]);
    Node no = originalGraph.getNode(nodeId);
    no.setGenomes(nodeGens);
    no.setBases(bases);
  }
  
  private void handleL(Scanner sc) {
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
    originalGraph.setGenoms(genoms);
    return originalGraph;    
  }

}
