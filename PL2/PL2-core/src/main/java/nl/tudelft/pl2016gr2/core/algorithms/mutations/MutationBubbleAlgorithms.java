//package nl.tudelft.pl2016gr2.core.algorithms.mutations;
//
//import nl.tudelft.pl2016gr2.model.GraphNode;
//import nl.tudelft.pl2016gr2.model.SemanticBubble;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// *
// * @author Faris
// */
//public class MutationBubbleAlgorithms {
//
//  private static ArrayList<Position> initStraightInDelPoint(ArrayList<Position> orderedGraph,
//      SequenceGraph graph) {
//    HashMap<Integer, Integer> location = new HashMap<Integer, Integer>();
//    for (int i = 0; i < orderedGraph.size(); i++) {
//      NodePosition nodeOrder = (NodePosition) orderedGraph.get(i);
//      GraphNode node = nodeOrder.getNode();
//      location.put(node.getId(), i);
//    }
//    return findStraightInDelPoint(orderedGraph, graph, location);
//  }
//
//  @SuppressWarnings("checkstyle:methodlength")
//  private static ArrayList<Position> findStraightInDelPoint(ArrayList<Position> orderedGraph,
//      SequenceGraph graph,
//      HashMap<Integer, Integer> location) {
//    ArrayList<Position> newOrder = new ArrayList<Position>();
//    Set<GraphNode> visited = new HashSet<GraphNode>();
//    for (int i = 0; i < orderedGraph.size(); i++) {
//      NodePosition order = (NodePosition) orderedGraph.get(i);
//      GraphNode node = order.getNode();
//      if (!visited.contains(node)) {
//        int oldLevel = order.getLevel();
//        int newLevel = order.getLevel();
//        boolean oldOverlap = order.isOverlapping();
//        boolean newOverlap = order.isOverlapping();
//        BubblePosition bubblePosition = null;
//        while (oldOverlap == newOverlap && node.getOutEdges().size() == 1
//            && node.getInEdges().size() <= 1 && newLevel <= oldLevel + 1
//            && !visited.contains(node)) {
//          // De visited hierboven kan wrs weg omdat het niet voor kan komen
//          if (bubblePosition == null) {
//            //je wilt aan de semantic bubble waarschijnlijk een andere id meegeven
//            bubblePosition = new BubblePosition(new SemanticBubble(node.getId(), node), oldLevel);
//            ArrayList<Integer> nodeInEdges = (ArrayList<Integer>) node.getInEdges();
//            if (nodeInEdges.size() > 0) {
//              GraphNode graphNode = graph.getNode(nodeInEdges.get(0));
//              graphNode.setInBubble(true);
//              bubblePosition.getBubble().addNode(graphNode);
//            }
//            //Hier nog eerste node aan toevoegen en in/out edges goed zetten
//            bubblePosition.setOverlapping(oldOverlap);
//
//          } else {
//            bubblePosition.getBubble().addNode(node);
//          }
//          visited.add(node);
//          //node.setInBubble(true);
//          node = graph.getNode(((ArrayList<Integer>) node.getOutEdges()).get(0));
//          order = (NodePosition) orderedGraph.get(location.get(node.getId()));
//          oldLevel = newLevel;
//          newLevel = order.getLevel();
//          oldOverlap = newOverlap;
//          newOverlap = order.isOverlapping();
//        }
//        if (bubblePosition != null) {
//          if (bubblePosition.size() == 1) {
//            // Een bubble van size 1 is een in/del of point mutation,
//            // die kunnen we hier dus gelijk filteren.
//            checkInDel(bubblePosition, graph);
//            checkPoint(bubblePosition, graph);
//            //bubblePosition.getBubble().getNodes().get(0).setInBubble(false);
//          } else if (oldOverlap == newOverlap && node.getInEdges().size() <= 1 && newLevel <= oldLevel + 1
//              && !visited.contains(node)) {
//            ArrayList<Integer> nodeOutEdges = (ArrayList<Integer>) node.getOutEdges();
//            GraphNode graphNode = graph.getNode(nodeOutEdges.get(0));
//            bubblePosition.getBubble().addNode(graphNode);
//            bubblePosition.getBubble().setOutEdges(graphNode.getOutEdges());
//            bubblePosition.getBubble().setTag("Straight");
//          }
//          newOrder.add(bubblePosition);
//        } else {
//          //node.setInBubble(false);
//          bubblePosition = new BubblePosition(new SemanticBubble(node.getId(), node), oldLevel);
//          bubblePosition.setOverlapping(oldOverlap);
//          newOrder.add(bubblePosition);
//        }
//      }
//    }
//    return newOrder;
//  }
//
//  private static void checkInDel(BubblePosition bubble, SequenceGraph graph) {
//    GraphNode nodeInBubble = bubble.getBubble().getNodes().get(0);
//    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0) {
//      return;
//    }
//    GraphNode parent = graph.getNode(((ArrayList<Integer>) nodeInBubble.getInEdges()).get(0));
//    GraphNode child = graph.getNode(((ArrayList<Integer>) nodeInBubble.getOutEdges()).get(0));
//    ArrayList<Integer> parentOutlinks = (ArrayList<Integer>) parent.getOutEdges();
//    boolean isInDel = false;
//    for (int i = 0; i < parentOutlinks.size(); i++) {
//      GraphNode node = graph.getNode(parentOutlinks.get(i));
//      if (node == child) {
//        isInDel = true;
//        break;
//      }
//    }
//    if (isInDel) {
//      bubble.getBubble().setTag("InDel");
//    }
//  }
//
//  @SuppressWarnings("checkstyle:methodlength")
//  private static void checkPoint(BubblePosition bubble, SequenceGraph graph) {
//    GraphNode nodeInBubble = bubble.getBubble().getNodes().get(0);
//    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0 || nodeInBubble.
//        size() > 1) {
//      return;
//    }
//    GraphNode parent = graph.getNode(((ArrayList<Integer>) nodeInBubble.getInEdges()).get(0));
//    GraphNode child = graph.getNode(((ArrayList<Integer>) nodeInBubble.getOutEdges()).get(0));
//    ArrayList<Integer> parentOutlinks = (ArrayList<Integer>) parent.getOutEdges();
//    boolean isPoint = true;
//    for (int i = 0; i < parentOutlinks.size(); i++) {
//      GraphNode node = graph.getNode(parentOutlinks.get(i));
//      if (node != nodeInBubble) {
//        if (node.getOutEdges().size() == 1) {
//          ArrayList<Integer> nodeOutEdges = (ArrayList<Integer>) node.getOutEdges();
//          if (graph.getNode(nodeOutEdges.get(0)) == child && node.size() == 1) {
//            //Only for completeness, this assignment does actually do nothing.
//            isPoint = true;
//          } else {
//            isPoint = false;
//            break;
//          }
//        } else {
//          isPoint = false;
//          break;
//        }
//      }
//    }
//    if (parentOutlinks.size() == 1) {
//      isPoint = false;
//    }
//    if (isPoint) {
//      bubble.getBubble().setTag("PointMutation");
//    }
//  }
//}
