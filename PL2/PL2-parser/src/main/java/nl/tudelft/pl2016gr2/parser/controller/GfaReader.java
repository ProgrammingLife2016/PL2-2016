package nl.tudelft.pl2016gr2.parser.controller;

import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.HashGraph;
import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.model.StringSequenceNode;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class reads a gfa file.
 *
 * @author Cas
 */
public class GfaReader {

  private static final int SHIFT_BY_BASE_10 = 10;
  @TestId(id = "genomes")
  private final ArrayList<String> genomes = new ArrayList<>();
  private final HashMap<Integer, AbstractNode> nodes = new HashMap<>();
  private final String fileName;
  @TestId(id = "originalGraph")
  private SequenceGraph originalGraph;

  /**
   * Creates a reader object and reads the gfa data from the filename.
   *
   * @param fileName the name of the file to read.
   */
  public GfaReader(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Read the GFA file.
   *
   * @return the read graph.
   */
  public SequenceGraph read() {
    if (originalGraph == null) {
      try {
        parse();
      } catch (IOException ex) {
        Logger.getLogger(GfaReader.class.getName()).log(Level.SEVERE, null, ex);
      }
      originalGraph = new HashGraph(nodes, genomes);
    }
    return originalGraph;
  }

  /**
   * Parse a GFA file.
   */
  private void parse() throws IOException {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(GfaReader.class.getClassLoader().getResourceAsStream(fileName)))) {
      br.readLine();
      String line;
      while ((line = br.readLine()) != null) {
        char[] chars = line.toCharArray();
        switch (chars[0]) {
          case 'L':
            parseEdge(chars);
            break;
          case 'S':
            parseNode(chars);
            break;
          case 'H':
            parseHeader(chars);
            break;
          default:
            Logger.getLogger(GfaReader.class.getName()).log(Level.WARNING, null,
                "unknown line read: " + line);
        }
      }
    }
  }

  /**
   * Parse a line of the GFA file that contains an edge.
   *
   * @param chars characters of the line.
   */
  @TestId(id = "parseEdge")
  private void parseEdge(char[] chars) {
    int from = 0;
    int index = 2;
    while (chars[index] != '\t') {
      from *= SHIFT_BY_BASE_10;
      from += chars[index++] - '0';
    }
    index += 3;
    int to = 0;
    while (chars[index] != '\t') {
      to *= SHIFT_BY_BASE_10;
      to += chars[index++] - '0';
    }
    getNode(to).addInEdge(from);
    getNode(from).addOutEdge(to);
  }

  /**
   * Parse a line of the GFA file that contains a node.
   *
   * @param chars characters of the line.
   */
  @TestId(id = "parseNode")
  private void parseNode(char[] chars) {
    int nodeId = 0;
    int index = 2;
    while (chars[index] != '\t') {
      nodeId *= SHIFT_BY_BASE_10;
      nodeId += chars[index++] - '0';
    }
    index++;

    Node node = getNode(nodeId);

    index = parseNodeBases(node, chars, index);
    parseNodegenomes(node, chars, index);
  }

  /**
   * Parse the bases of a node.
   *
   * @param node     the node to add the bases to.
   * @param chars    the character array of the node line in the GFA file.
   * @param curIndex the current index; where the bases start in the line.
   * @return the new index, after reading the genomes.
   */
  @TestId(id = "parseNodeBases")
  private static int parseNodeBases(Node node, char[] chars, int curIndex) {
    int index = curIndex;
    index = skipTillCharacter(chars, index, '\t', 1);
    node.setSequence(new String(chars, curIndex, index - curIndex));
    return index;
  }

  /**
   * Parse the genomes of a node.
   *
   * @param node     the node to add the genomes to.
   * @param chars    the character array of the node line in the GFA file.
   * @param curIndex the current index; where the bases end in the line.
   * @return the new index, after reading the genomes.
   */
  @TestId(id = "parseNodegenomes")
  private static void parseNodegenomes(GraphNode node, char[] chars, int curIndex) {
    int index = curIndex;
    index = skipTillCharacter(chars, index, ':', 2);
    ++index;
    int startIndex = index;
    ArrayList<String> nodeGens = new ArrayList<>();
    while (chars[index] != '\t') {
      ++index;
      while (chars[index] != ';' && chars[index] != '\t') {
        ++index;
      }
      nodeGens.add(new String(chars, startIndex, index - startIndex));
      startIndex = index + 1;
    }
    nodeGens.forEach(node::addGenome);
  }

  /**
   * Parse the header part of the GFA file, which contains all of the genomes.
   *
   * @param chars the characters of the header.
   */
  @TestId(id = "parseHeader")
  private void parseHeader(char[] chars) {
    int index = 0;
    index = skipTillCharacter(chars, index, ':', 2) + 1;
    int start = index;
    while (index < chars.length) {
      index = skipTillCharacter(chars, index, ';', 1);
      genomes.add(new String(chars, start, index - start));
      start = index + 1;
      index += 2;
    }
  }

  /**
   * Skips all characters till the given character is found.
   *
   * @param chars      the array of characters.
   * @param startIndex the index at which to start in the array.
   * @param ch         the character to skip to.
   * @param amount     the amount of times to skip the character.
   * @return the index of the first occurence of the character after the given start index.
   */
  @TestId(id = "skipTillCharacter")
  private static int skipTillCharacter(char[] chars, int startIndex, char ch, int amount) {
    int index = startIndex;
    for (int i = 0; i < amount; i++) {
      ++index;
      while (chars[index] != ch) {
        ++index;
      }
    }
    return index;
  }

  /**
   * Get a node, or create it if it doesn't exist yet.
   *
   * @param id the id of the node.
   * @return the node.
   */
  @TestId(id = "getNode")
  private Node getNode(int id) {
    AbstractNode node = nodes.get(id);
    if (node == null) {
      node = new StringSequenceNode(id);
      nodes.put(id, node);
    }
    return (Node) nodes.get(id);
  }
}
