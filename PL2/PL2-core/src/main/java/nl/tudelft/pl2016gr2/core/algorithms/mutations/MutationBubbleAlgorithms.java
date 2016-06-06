package nl.tudelft.pl2016gr2.core.algorithms.mutations;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IndelBubble;
import nl.tudelft.pl2016gr2.model.PointMutationBubble;
import nl.tudelft.pl2016gr2.model.SimpleBubble;
import nl.tudelft.pl2016gr2.model.StraightSequenceBubble;

import java.util.ArrayList;
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
    
    //Because the have to be sorted again.
    orderedNodes.sort((node1, node2) -> node1.getLevel() - node2.getLevel());
    return orderedNodes;
  }

  @SuppressWarnings("checkstyle:methodlength")
  private static ArrayList<GraphNode> findStraightInDelPoint(ArrayList<GraphNode> orderedGraph) {
    //In this list, we will add the new bubbles and also the nodes which will not become a bubble.
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
            //The last statement is less trivial, it recognizes branches coming together, 
            //with which the straight sequence is allowed to start.
            && newLevel <= oldLevel + 1
            && !visited.contains(node)) {
          //Bubble == null if a new bubble has to be made.
          if (bubble == null) {
            //TODO: question: What ID should a node get?
            bubble = new SimpleBubble(node.getId(), node.getInEdges(), node);
          } else {
            bubble.addChild(node);
          }
          visited.add(node);
          //Update the node, so the while loop can go on.
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
            //We want to check for indel or point mutations.
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
    /*
     * This is only for testing purposes.
     */
    test(newOrder);    
    return newOrder;
  }
  
  /**
   * This method makes sure that the inedges at childs of a bubble are referenced correctly.
   * @param bubble the bubble we want to reference.
   * @param nodeOut this node has the correct outlinks of the bubble.
   */
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
   * This method make sure the references from the parent bubble to the new bubble are right.
   * @param bubble The bubble we want to reference.
   * @param firstNode this node has the references which have to be removed from the parent.
   */
  private static void addParentOutToBubble(Bubble bubble, GraphNode firstNode) {
    if (bubble.getInEdges().size() > 0) {
      for (GraphNode parent : bubble.getInEdges()) {
        ArrayList<GraphNode> parentOutEdges = (ArrayList<GraphNode>) parent.getOutEdges();
        parentOutEdges.remove(firstNode);
        parentOutEdges.add(bubble);
      }
    }
  }

  /**
   * This method is only called on 'bubbles' of size one and basically does everything 
   * from making the indel bubble to referencing it right.
   * @param bubble the bubble of size 1
   * @param visited used for efficiency, we dont want to visit bubbles twice.
   * @return
   */
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
      /*
       * Referencing it right, might be put in another method.
       */
      bubble = new IndelBubble(bubble);
      parentOutlinks.remove(nodeInBubble);
      parentOutlinks.add(bubble);
      ArrayList<GraphNode> childInLinks = (ArrayList<GraphNode>) child.getInEdges();
      childInLinks.remove(nodeInBubble);
      childInLinks.add(bubble);
      bubble.setInEdges(Collections.singleton(parent));
      bubble.setOutEdges(Collections.singleton(child));
      return bubble;
    } else {
      return nodeInBubble;
    }
  }

  /**
   * This method is only called on 'bubbles' of size one and basically does everything 
   * from making the pointmutation bubbles to referencing it right.
   * @param bubble The bubble of size one.
   * @param visited is used for efficiency, we dont want to visit bubbles twice.
   * @return
   */
  @SuppressWarnings("checkstyle:methodlength")
  private static GraphNode checkPoint(Bubble bubble, Set<GraphNode> visited) {
    HashSet<GraphNode> nestedNodes = (HashSet<GraphNode>) bubble.getChildren();
    GraphNode nodeInBubble = nestedNodes.iterator().next();
    if (nodeInBubble.getInEdges().size() == 0 || nodeInBubble.getOutEdges().size() == 0 || nodeInBubble.
        size() > 1) {
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
      //Only if there are two branches or more it can be a pointmutation.
      isPoint = false;
    }
    if (isPoint) {
      /*
       * Referencing the bubble, might be put in another method.
       */
      bubble = new PointMutationBubble(bubble);
      parent.setOutEdges(Collections.singleton(bubble));
      checkInEdges(bubble, child);
      bubble.setInEdges(Collections.singleton(parent));
      bubble.setOutEdges(Collections.singleton(child));
      return bubble;
    } else {
      return nodeInBubble;
    }
  }
  
  /**
   * For loop should probably be adjusted to accommodate indexes and better complexity 
   * for removal (we already know the index if we want to remove)
   * @param bubble
   * @param child
   */
  private static void checkInEdges(Bubble bubble, GraphNode child) {
    Set<GraphNode> nestedNodes = (Set<GraphNode>) bubble.getChildren();
    ArrayList<GraphNode> childInEdges = (ArrayList<GraphNode>) child.getInEdges();
    for (GraphNode node : nestedNodes) {
      if(childInEdges.contains(node)) {
        childInEdges.remove(node);
      }
    }
    childInEdges.add(bubble);    
  }
  
  /**
   * Only some sysouts to provide fast statistics and testing.
   * @param newOrder
   */
  private static void test(ArrayList<GraphNode> newOrder) {
    int count = 0;
    for (GraphNode node : newOrder) {
      for (GraphNode inEdge : node.getInEdges()) {
        if(!inEdge.getOutEdges().contains(node)) {
          System.out.println(inEdge.getId() + " heeft geen: " + node.getId());
          System.out.println("Het moet dus geen sequenceNode maar bubble worden");
          System.out.println(inEdge.getOutEdges());
          count++;
        }
      }
      for (GraphNode outEdge : node.getOutEdges()) {
        if(!outEdge.getInEdges().contains(node)) {
          System.out.println("Er gaat iets fout2");
          count++;
        }
      }
    }
    System.out.println(count);
  }
}
