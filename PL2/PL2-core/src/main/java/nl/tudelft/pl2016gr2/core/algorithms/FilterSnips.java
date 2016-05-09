package nl.tudelft.pl2016gr2.core.algorithms;

import java.util.ArrayList;
import java.util.HashSet;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.test.utility.TestId;

/**
 * Class to filter snips. A snip is a point mutation. 
 * @author Casper
 *
 */
public class FilterSnips {

  /**
   * The original, unfiltered graph.
   */
  private OriginalGraph graph;
  /**
   * The nodes that are merged together into bigger nodes (snips).
   */
  private HashSet<Integer> collapsedNodes = new HashSet<>();
  
  /**
   * Constructs a FilterSnips object with a graph. This object can then be used to filter the
   * snips out of the graph.
   * @param graph : the graph to filter.
   */
  public FilterSnips(OriginalGraph graph) {
    this.graph = graph;
  }

  /**
   * Filter the graph by finding and merging snips together.
   * @return the filtered graph.
   */
  public OriginalGraph filter() {
    OriginalGraph filteredGraph = new OriginalGraph();

    for (int i = 1; i <= graph.getSize(); i++) {
      if (!collapsedNodes.contains(i)) {
        Node current = graph.getNode(i);

        if (isSnip(current)) {
          Node snip = makeSnip(current);
          filteredGraph.addNode(snip);
        } else {
          filteredGraph.addNode(current);
        }
      }

    }

    return filteredGraph;
  }

  /**
   * Method to make a snip. The node which is put in is already assumed to be a snip.
   * The method then runs until it encounters a node which is not a snip.
   * @param current : the first node of this snip, which is already assumed to be a snip.
   * @return : a node in which all concurrent snips from the input node are merged together.
   */
  @TestId(id = "method_makeSnip")
  private Node makeSnip(Node current) {
    Node snip = null;
    boolean isSnip = true;

    while (isSnip) {
      for (Integer outlink : current.getOutlinks()) {
        collapsedNodes.add(outlink);
      }

      Node intermediate = graph.getNode(current.getOutlinks().get(0));
      Node end = graph.getNode(intermediate.getOutlinks().get(0));
      collapsedNodes.add(end.getId());

      snip = new Node(current.getId(), current.getSequenceLength() + 1 + end.getSequenceLength(),
          current.getGenomes(), current.getSnips() + 1);
      snip.setInlinks(current.getInlinks());
      snip.setOutlinks(end.getOutlinks());
      current = snip;

      updateLinks(snip, end.getId());
      isSnip = isSnip(snip);
    }

    return snip;
  }

  /**
   * Method to check if a node is a snip
   * @param snip : the node to check.
   * @return : true when this node is a snip, false otherwise.
   */
  @TestId(id = "method_isSnip")
  private boolean isSnip(Node snip) {
    ArrayList<Node> targets = new ArrayList<>();
    for (Integer outlink : snip.getOutlinks()) {
      targets.add(graph.getNode(outlink));
    }

    return targets.size() == 2 && targets.get(0).getSequenceLength() == 1
        && targets.get(1).getSequenceLength() == 1 && targets.get(0).getOutlinks().size() == 1
        && targets.get(1).getOutlinks().size() == 1 && targets.get(0).getInlinks().size() == 1
        && targets.get(1).getInlinks().size() == 1
        && graph.getNode(targets.get(0).getOutlinks().get(0)).getInlinks().size() == 2;
  }

  /**
   * Private method to replace the inlinks of the outlinks of a snip. The outlinks are
   * the outlinks of the last node in the snip. The inlinks of these outlinks have to
   * get the id of the snip, instead of the id of the last node within this snip.
   * @param snip : the snip for which the inlinks of the outlinks have to be updated.
   * @param originalId : the id of the last node in this snip.
   */
  @TestId(id = "method_updateLinks")
  private void updateLinks(Node snip, int originalId) {
    for (Integer outlink : snip.getOutlinks()) {
      Node out = graph.getNode(outlink);
      out.replaceInlink(originalId, snip.getId());
    }
  }
}
