//package nl.tudelft.pl2016gr2.core.algorithms;
//
//import nl.tudelft.pl2016gr2.model.GraphNode;
//import nl.tudelft.pl2016gr2.model.HashGraph;
//import nl.tudelft.pl2016gr2.model.Node;
//import nl.tudelft.pl2016gr2.model.SequenceGraph;
//import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//
///**
// * Class to filter snips. A snip is a point mutation.
// * @author Casper
// *
// */
//public class FilterSnips {
//
//  /**
//   * The original, unfiltered graph.
//   */
//  private SequenceGraph graph;
//  /**
//   * The nodes that are merged together into bigger nodes (snips).
//   */
//  private HashSet<Integer> collapsedNodes = new HashSet<>();
//
//  /**
//   * Constructs a FilterSnips object with a graph. This object can then be used to filter the
//   * snips out of the graph.
//   * @param graph : the graph to filter.
//   */
//  public FilterSnips(SequenceGraph graph) {
//    this.graph = graph;
//  }
//
//  /**
//   * Filter the graph by finding and merging snips together.
//   * @return the filtered graph.
//   */
//  public SequenceGraph filter() {
//    SequenceGraph filteredGraph = new HashGraph();
//
//    for (int i = 1; i <= graph.size(); i++) {
//      if (!collapsedNodes.contains(i)) {
//        GraphNode current = graph.getNode(i);
//
//        if (isSnip(current)) {
//          Node snip = makeSnip(current);
//          filteredGraph.add(snip);
//        } else {
//          filteredGraph.add(current);
//        }
//      }
//
//    }
//
//    return filteredGraph;
//  }
//
//  /**
//   * Method to make a snip. The node which is put in is already assumed to be a snip.
//   * The method then runs until it encounters a node which is not a snip.
//   * @param current : the first node of this snip, which is already assumed to be a snip.
//   * @return : a node in which all concurrent snips from the input node are merged together.
//   */
//  @TestId(id = "method_makeSnip")
//  private Node makeSnip(Node current) {
//    Node snip = null;
//    boolean isSnip = true;
//
//    while (isSnip) {
//      for (Integer outlink : current.getOutEdges()) {
//        collapsedNodes.add(outlink);
//      }
//
//      Node intermediate = graph.getNode(current.getOutEdges().get(0));
//      Node end = graph.getNode(intermediate.getOutEdges().get(0));
//      collapsedNodes.add(end.getId());
//
//      snip = new Node(current.getId(), current.size() + 1 + end.size(),
//          current.getGenomes(), current.getSnips() + 1);
//      snip.setInlinks(current.getInEdges());
//      snip.setOutlinks(end.getOutEdges());
//      current = snip;
//
//      updateLinks(snip, end.getId());
//      isSnip = isSnip(snip);
//    }
//
//    return snip;
//  }
//
//  /**
//   * Method to check if a node is a snip
//   * @param snip : the node to check.
//   * @return : true when this node is a snip, false otherwise.
//   */
//  @TestId(id = "method_isSnip")
//  private boolean isSnip(Node snip) {
//    ArrayList<Node> targets = new ArrayList<>();
//    for (Integer outlink : snip.getOutEdges()) {
//      targets.add(graph.getNode(outlink));
//    }
//
//    return targets.size() == 2 && targets.get(0).getSequenceLength() == 1
//        && targets.get(1).getSequenceLength() == 1 && targets.get(0).getOutEdges().size() == 1
//        && targets.get(1).getOutEdges().size() == 1 && targets.get(0).getInEdges().size() == 1
//        && targets.get(1).getInEdges().size() == 1
//        && graph.getNode(targets.get(0).getOutEdges().get(0)).getInEdges().size() == 2;
//  }
//
//  /**
//   * Private method to replace the inlinks of the outlinks of a snip. The outlinks are
//   * the outlinks of the last node in the snip. The inlinks of these outlinks have to
//   * get the id of the snip, instead of the id of the last node within this snip.
//   * @param snip : the snip for which the inlinks of the outlinks have to be updated.
//   * @param originalId : the id of the last node in this snip.
//   */
//  @TestId(id = "method_updateLinks")
//  private void updateLinks(Node snip, int originalId) {
//    for (Integer outlink : snip.getOutEdges()) {
//      Node out = graph.getNode(outlink);
//      out.replaceInlink(originalId, snip.getId());
//    }
//  }
//}