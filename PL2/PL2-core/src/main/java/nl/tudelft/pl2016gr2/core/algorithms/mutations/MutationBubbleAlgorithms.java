package nl.tudelft.pl2016gr2.core.algorithms.mutations;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IndelBubble;
import nl.tudelft.pl2016gr2.model.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.SimpleBubble;
import nl.tudelft.pl2016gr2.model.StraightSequenceBubble;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    orderedNodes = findStraightInDelPoint(orderedNodes);
    orderedNodes.sort((node1, node2) -> node1.getLevel() - node2.getLevel());
    return orderedNodes;
  }

  @SuppressWarnings("checkstyle:methodlength")
  private static ArrayList<GraphNode> findStraightInDelPoint(ArrayList<GraphNode> orderedGraph) {
    ArrayList<GraphNode> newOrder = new ArrayList<>();
    Set<GraphNode> visited = new HashSet<GraphNode>();
    for (int i = 0; i < orderedGraph.size(); i++) {
      GraphNode node = orderedGraph.get(i);
      GraphNode lastNode = node;
      GraphNode firstNode = node;
      if (!visited.contains(node)) {
        int oldLevel = node.getLevel();
        int newLevel = node.getLevel();
        boolean oldOverlap = node.isOverlapping();
        boolean newOverlap = node.isOverlapping();
        Bubble bubble = null;
        while (oldOverlap == newOverlap && node.getOutEdges().size() <= 1
            && (node.getInEdges().size() <= 1 || (node.getInEdges().size() > 1 && bubble == null)) 
            //Die laatste statement herkent het begin van een bubble na samenkomen van een afsplitsing
            && newLevel <= oldLevel + 1
            && !visited.contains(node)) {
          if (bubble == null) {
            //je wilt aan de semantic bubble waarschijnlijk een andere id meegeven
            bubble = new SimpleBubble(node.getId(), node.getInEdges(), node);
          } else {
            bubble.addChild(node);
          }
          visited.add(node);
          if (node.getOutEdges() != null && node.getOutEdges().size() > 0) {
            lastNode = node;
            node = ((ArrayList<GraphNode>) node.getOutEdges()).get(0);
            oldLevel = newLevel;
            newLevel = node.getLevel();
            oldOverlap = newOverlap;
            newOverlap = node.isOverlapping(); 
          } else {
            lastNode = node;
            break;
          }         
        }
        boolean stop = false;
        GraphNode newBubbleOrNode = null;
        if (bubble != null) {
          if (bubble.getChildren().size() == 1) {
            newBubbleOrNode = checkPoint(bubble, visited);
            if (!(newBubbleOrNode instanceof PointMutationBubble)) {
              newBubbleOrNode = checkInDel(bubble, visited);
            }
          } else if (oldOverlap == newOverlap && node.getInEdges().size() <= 1 && newLevel <= oldLevel + 1
              && !visited.contains(node)) {
            bubble.addChild(node);
            visited.add(node);
            bubble = new StraightSequenceBubble(bubble);
            stop = true;
            setChildsInedgesToBubble(bubble, node);
            addParentOutToBubble(bubble, firstNode);
          } else {
            bubble = new StraightSequenceBubble(bubble);
            setChildsInedgesToBubble(bubble, lastNode);
            addParentOutToBubble(bubble, firstNode);
          }
          GraphNode nodeOut;
          if (stop) {
            nodeOut = node;
          } else {
            nodeOut = lastNode;
          }
          bubble.setOutEdges(nodeOut.getOutEdges());
          if (newBubbleOrNode == null) {
            newOrder.add(bubble);
          } else {
            newOrder.add(newBubbleOrNode);
          }
        } else {
          newOrder.add(node);
        }
      }
    }
    return newOrder;
  }
  
  private static void setChildsInedgesToBubble(Bubble bubble, GraphNode nodeOut) {
    for (GraphNode node : nodeOut.getOutEdges()) {
      if (node.getInEdges().size() <= 1) {
        node.setInEdges(Collections.singleton(bubble));
      } else {
        node.getInEdges().remove(nodeOut);
        node.getInEdges().add(bubble);
      }
    }
  }
  
  /**
   * maybe the iterator in this function is slow and in that case I should find another way 
   * to get the first element of the arraylist.
   * @param bubble
   * @param firstNode
   */
  private static void addParentOutToBubble(Bubble bubble, GraphNode firstNode) {
    if (bubble.getInEdges().size() > 0) {
      ArrayList<GraphNode> parentOutEdges = (ArrayList<GraphNode>) bubble.getInEdges().iterator().next().getOutEdges();
      parentOutEdges.remove(firstNode);
      parentOutEdges.add(bubble);
    }
  }

  private static GraphNode checkInDel(Bubble bubble, Set<GraphNode> visited) {
    HashSet<GraphNode> nestedNodes = (HashSet<GraphNode>) bubble.getChildren();
    GraphNode nodeInBubble = nestedNodes.iterator().next();
    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0) {
      //Hier eerst nog een geschikte bubble van maken.
      return nodeInBubble;
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
      parentOutlinks.remove(nodeInBubble);
      parentOutlinks.add(bubble);
      ArrayList<GraphNode> childInLinks = (ArrayList<GraphNode>) child.getInEdges();
      childInLinks.remove(nodeInBubble);
      childInLinks.add(bubble);
      bubble.setInEdges(Collections.singleton(parent));
      bubble.setOutEdges(Collections.singleton(child));
      return new IndelBubble(bubble);
    } else {
      return nodeInBubble;
    }
  }

  @SuppressWarnings("checkstyle:methodlength")
  private static GraphNode checkPoint(Bubble bubble, Set<GraphNode> visited) {
    HashSet<GraphNode> nestedNodes = (HashSet<GraphNode>) bubble.getChildren();
    GraphNode nodeInBubble = nestedNodes.iterator().next();
    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0 || nodeInBubble.
        size() > 1) {
      //Hier nog een geschikte bubble van maken.
      //return new SimpleBubble(bubble);
      return nodeInBubble;
    }
    GraphNode parent = ((ArrayList<GraphNode>) nodeInBubble.getInEdges()).get(0);
    GraphNode child = ((ArrayList<GraphNode>) nodeInBubble.getOutEdges()).get(0);
    ArrayList<GraphNode> parentOutlinks = (ArrayList<GraphNode>) parent.getOutEdges();
    boolean isPoint = true;
    for (int i = 0; i < parentOutlinks.size(); i++) {
      GraphNode node = parentOutlinks.get(i);
      if (node != nodeInBubble) {
        if (node.getOutEdges().size() == 1 || node == child) {
          ArrayList<GraphNode> nodeOutEdges = (ArrayList<GraphNode>) node.getOutEdges();
          if (nodeOutEdges.size() == 0) {
            //It is an indel, at the end of the graph.
            return nodeInBubble;
          }
          if (nodeOutEdges.get(0) == child && node.size() == 1) {
            //Only for completeness, this assignment does actually do nothing.
            isPoint = true;
            bubble.addChild(node);
            visited.add(node);
          } else {
            isPoint = false;
            break;
          }
        } else {
          isPoint = false;
          break;
        }
      } else {
        bubble.addChild(nodeInBubble);
      }
    }
    if (parentOutlinks.size() == 1) {
      isPoint = false;
    }
    if (isPoint) {
      parent.setOutEdges(Collections.singleton(bubble));
      child.setInEdges(Collections.singleton(bubble));
      bubble.setInEdges(Collections.singleton(parent));
      bubble.setOutEdges(Collections.singleton(child));
      return new PointMutationBubble(bubble);
    } else {
      return nodeInBubble;
    }
  }
}
