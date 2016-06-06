package nl.tudelft.pl2016gr2.core.algorithms.mutations;

import nl.tudelft.pl2016gr2.model.BaseSequence;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.SequenceNode;

import org.junit.Test;

import java.util.ArrayList;

public class IndelTest {
  
  @Test
  public void indelTest() {
    BaseSequence b1 = new BaseSequence("ACT");
    BaseSequence b2 = new BaseSequence("A");
    BaseSequence b3 = new BaseSequence("C");
    
    GraphNode g1 = new SequenceNode(1, b1);
    GraphNode g2 = new SequenceNode(2, b2);
    GraphNode g3 = new SequenceNode(3, b3);
    
    g1.setOverlapping(true);
    g2.setOverlapping(true);
    g3.setOverlapping(true);
    
    g1.setLevel(1);
    g2.setLevel(2);
    g3.setLevel(3);
    
    
    g1.addOutEdge(g2);
    g1.addOutEdge(g3);
    g2.addOutEdge(g3);
    
    g2.addInEdge(g1);
    g3.addInEdge(g1);
    g3.addInEdge(g2);
    
    ArrayList<GraphNode> orderedNodes = new ArrayList<>();
    orderedNodes.add(g1);
    orderedNodes.add(g2);
    orderedNodes.add(g3);  

    ArrayList<GraphNode> result = MutationBubbleAlgorithms.makeBubbels(orderedNodes);
    System.out.println(result);
  }

}
