package nl.tudelft.pl2016gr2.core.algorithms.mutations;

import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.SequenceNode;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class MutationBubbleAlgorithmsTest {
  
  private ArrayList<GraphNode> orderedNodes;

  @Before
  public void setup() {
    GraphNode g1 = new SequenceNode(1);
    GraphNode g2 = new SequenceNode(2);
    GraphNode g3 = new SequenceNode(3);
    GraphNode g4 = new SequenceNode(4);
    GraphNode g5 = new SequenceNode(5);
    GraphNode g6 = new SequenceNode(6);
    GraphNode g7 = new SequenceNode(7);
    GraphNode g8 = new SequenceNode(8);
    GraphNode g9 = new SequenceNode(9);
    GraphNode g10 = new SequenceNode(10);
    
    g1.setOverlapping(true);
    g2.setOverlapping(true);
    g3.setOverlapping(true);
    g4.setOverlapping(true);
    g5.setOverlapping(true);
    g6.setOverlapping(true);
    g7.setOverlapping(false);
    g8.setOverlapping(true);
    g9.setOverlapping(true);
    g10.setOverlapping(true);
    
    g1.setLevel(1);
    g2.setLevel(2);
    g3.setLevel(3);
    g4.setLevel(4);
    g5.setLevel(5);
    g6.setLevel(6);
    g7.setLevel(5);
    g8.setLevel(6);
    g9.setLevel(7);
    g10.setLevel(8);
    
    
    g1.addOutEdge(g2);
    g2.addOutEdge(g3);
    g3.addOutEdge(g4);
    g4.addOutEdge(g5);
    g4.addOutEdge(g7);
    g5.addOutEdge(g6);
    g7.addOutEdge(g8);
    g6.addOutEdge(g9);
    g8.addOutEdge(g9);
    g9.addOutEdge(g10);
    
    g2.addInEdge(g1);
    g3.addInEdge(g2);
    g4.addInEdge(g3);
    g5.addInEdge(g4);
    g6.addInEdge(g5);
    g7.addInEdge(g4);
    g8.addInEdge(g7);
    g9.addInEdge(g6);
    g9.addInEdge(g8);
    g10.addInEdge(g9);
    
    orderedNodes = new ArrayList<>();
    orderedNodes.add(g1);
    orderedNodes.add(g2);
    orderedNodes.add(g3);
    orderedNodes.add(g4);
    orderedNodes.add(g5);
    orderedNodes.add(g6);
    orderedNodes.add(g7);
    orderedNodes.add(g8);  
    orderedNodes.add(g9);  
    orderedNodes.add(g10);      
  }
  
  @Test
  public void straightTest() {
    ArrayList<GraphNode> result = MutationBubbleAlgorithms.makeBubbels(orderedNodes);
    System.out.println(result);
  }

}
