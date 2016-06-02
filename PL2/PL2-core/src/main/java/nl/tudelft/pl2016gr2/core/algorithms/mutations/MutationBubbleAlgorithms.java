package nl.tudelft.pl2016gr2.core.algorithms.mutations;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IndelBubble;
import nl.tudelft.pl2016gr2.model.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.SimpleBubble;
import nl.tudelft.pl2016gr2.model.StraightSequenceBubble;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Cas
 */
public class MutationBubbleAlgorithms {

  /**
   * Make bubbles in the given array list of nodes.
   *
   * @param orderedNodes the ordered list of nodes (by x coordinate) of the graph.
   * @return the new array list which contains the newly created bubbles and other nodes of the
   *         graph.
   */
  public static ArrayList<GraphNode> makeBubbels(ArrayList<GraphNode> orderedNodes) {
    // message to Cas:
    // This method is called when the straight sequences, indels and point mutations must be wrapped
    // in bubbles. Replace the nodes in the orderedNodes array list with bubbles where needed,
    // adjust the in/out links of the other nodes (so the nodes are correctly linked to the new
    // bubbles).
    // If you want, you may create a new array list and copy the nodes into it (just make sure a
    // correct array list of graphnodes which contains all of the nodes of the graph is returned).
    
    // First you must fix the bubble classes to store the content of the bubbles (check the 
    // PhyloBubble class to get an idea of how it should work, you can copy most of that code).
    // Please don't add any more methods to the interfaces of the bubbles/nodes. They don't need
    // extra functionality for your code (except that each kind of bubble should get a different
    // kind of representation, but you can just use the white square for now.
    
    // I have deactivated the phylogenetic bubbles for you to make it easier to test your code.

    // if you want to sort the list of nodes again (this shouldn't really be needed), 
    // uncomment the following line:
    //orderedNodes.sort((node1, node2) -> node1.getLevel() - node2.getLevel());
    return initStraightInDelPoint(orderedNodes);
  }

  private static ArrayList<GraphNode> initStraightInDelPoint(ArrayList<GraphNode> orderedGraph) {
//    HashMap<Integer, Integer> location = new HashMap<Integer, Integer>();
//    for (int i = 0; i < orderedGraph.size(); i++) {
//      NodePosition nodeOrder = (NodePosition) orderedGraph.get(i);
//      GraphNode node = nodeOrder.getNode();
//      location.put(node.getId(), i);
//    }
    return findStraightInDelPoint(orderedGraph);
  }

  @SuppressWarnings("checkstyle:methodlength")
  private static ArrayList<GraphNode> findStraightInDelPoint(ArrayList<GraphNode> orderedGraph) {
    ArrayList<GraphNode> newOrder = new ArrayList<>();
    Set<GraphNode> visited = new HashSet<GraphNode>();
    for (int i = 0; i < orderedGraph.size(); i++) {
      //NodePosition order = (NodePosition) orderedGraph.get(i);
      GraphNode node = orderedGraph.get(i);
      if (!visited.contains(node)) {
        int oldLevel = node.getLevel();
        int newLevel = node.getLevel();
        boolean oldOverlap = node.isOverlapping();
        boolean newOverlap = node.isOverlapping();
        Bubble bubble = null;
        while (oldOverlap == newOverlap && node.getOutEdges().size() == 1
            && node.getInEdges().size() <= 1 && newLevel <= oldLevel + 1
            && !visited.contains(node)) {
          // De visited hierboven kan wrs weg omdat het niet voor kan komen
          if (bubble == null) {
            //je wilt aan de semantic bubble waarschijnlijk een andere id meegeven
            bubble = new SimpleBubble(node.getId(), node.getInEdges(), node);
//            if (nodeInEdges.size() > 0) {
//              GraphNode graphNode = graph.getNode(nodeInEdges.get(0));
//              graphNode.setInBubble(true);
//              bubblePosition.getBubble().addNode(graphNode);
//            }
            //Hier nog eerste node aan toevoegen en in/out edges goed zetten
            //bubble.setOverlapping(oldOverlap); 
          } else {
            bubble.addChild(node);
          }
          visited.add(node);
          //node.setInBubble(true);
          node = ((ArrayList<GraphNode>) node.getOutEdges()).get(0);
          //order = (NodePosition) orderedGraph.get(location.get(node.getId()));
          oldLevel = newLevel;
          newLevel = node.getLevel();
          oldOverlap = newOverlap;
          newOverlap = node.isOverlapping();
        }
        if (bubble != null) {
          if (bubble.getChildren().size() == 1) {
            // Een bubble van size 1 is een in/del of point mutation,
            // die kunnen we hier dus gelijk filteren.
            //bubble = checkInDel(bubble);
            //bubble = checkPoint(bubble);
            //bubblePosition.getBubble().getNodes().get(0).setInBubble(false);
          } else /*if (oldOverlap == newOverlap && node.getInEdges().size() <= 1 && newLevel <= oldLevel + 1
              && !visited.contains(node))*/ {
            bubble = new StraightSequenceBubble(bubble);
//            ArrayList<GraphNode> nodeOutEdges = (ArrayList<GraphNode>) node.getOutEdges();
//            bubble.setOutEdges(nodeOutEdges);
//            ArrayList<Integer> nodeOutEdges = (ArrayList<Integer>) node.getOutEdges();
//            GraphNode graphNode = graph.getNode(nodeOutEdges.get(0));
//            bubblePosition.getBubble().addNode(graphNode);
//            bubblePosition.getBubble().setOutEdges(graphNode.getOutEdges());
//            bubblePosition.getBubble().setTag("Straight");
          }
          ArrayList<GraphNode> nodeOutEdges = (ArrayList<GraphNode>) node.getOutEdges();
          bubble.setOutEdges(nodeOutEdges);
        } else {
          //node.setInBubble(false);
          bubble = new SimpleBubble(node.getId(), node.getInEdges(), node.getOutEdges(), node);
          //bubble.setOverlapping(oldOverlap);
        }
        newOrder.add(bubble);
      }
    }
    return newOrder;
  }

  private static Bubble checkInDel(Bubble bubble) {
    HashSet<GraphNode> nestedNodes = (HashSet<GraphNode>) bubble.getChildren();
    GraphNode nodeInBubble = nestedNodes.iterator().next();
    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0) {
      //Hier eerst nog een geschikte bubble van maken.
      return new SimpleBubble(bubble);
    }
    GraphNode parent = ((ArrayList<GraphNode>) nodeInBubble.getInEdges()).get(0);
    GraphNode child = ((ArrayList<GraphNode>) nodeInBubble.getOutEdges()).get(0);
    ArrayList<GraphNode> parentOutlinks = (ArrayList<GraphNode>) parent.getOutEdges();
    boolean isInDel = false;
    for (int i = 0; i < parentOutlinks.size(); i++) {
      GraphNode node = parentOutlinks.get(i);
      if (node == child) {
        isInDel = true;
        break;
      }
    }
    if (isInDel) {
      return new IndelBubble(bubble);
    } else {
      return bubble;
    }
  }

  @SuppressWarnings("checkstyle:methodlength")
  private static Bubble checkPoint(Bubble bubble) {
    HashSet<GraphNode> nestedNodes = (HashSet<GraphNode>) bubble.getChildren();
    GraphNode nodeInBubble = nestedNodes.iterator().next();
    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0 || nodeInBubble.
        size() > 1) {
      //Hier nog een geschikte bubble van maken.
      return new SimpleBubble(bubble);
    }
    GraphNode parent = ((ArrayList<GraphNode>) nodeInBubble.getInEdges()).get(0);
    GraphNode child = ((ArrayList<GraphNode>) nodeInBubble.getOutEdges()).get(0);
    ArrayList<GraphNode> parentOutlinks = (ArrayList<GraphNode>) parent.getOutEdges();
    boolean isPoint = true;
    for (int i = 0; i < parentOutlinks.size(); i++) {
      GraphNode node = parentOutlinks.get(i);
      if (node != nodeInBubble) {
        if (node.getOutEdges().size() == 1) {
          ArrayList<GraphNode> nodeOutEdges = (ArrayList<GraphNode>) node.getOutEdges();
          if (nodeOutEdges.get(0) == child && node.size() == 1) {
            //Only for completeness, this assignment does actually do nothing.
            isPoint = true;
          } else {
            isPoint = false;
            break;
          }
        } else {
          isPoint = false;
          break;
        }
      }
    }
    //Waarom dit nodig is weet ik even niet.
    if (parentOutlinks.size() == 1) {
      isPoint = false;
    }
    if (isPoint) {
      bubble.addChild(child);
      return new PointMutationBubble(bubble);
    } else {
      return bubble;
    }
  }
}
