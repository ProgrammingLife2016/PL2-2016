//package nl.tudelft.pl2016gr2.core.algorithms.mutations;
//
//import static org.junit.Assert.assertEquals;
//
//import nl.tudelft.pl2016gr2.model.BaseSequence;
//import nl.tudelft.pl2016gr2.model.GraphNode;
//import nl.tudelft.pl2016gr2.model.SequenceNode;
//
//import org.junit.Test;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;
//
//public class PointTest {
//
//  @Test
//  public void pointTest() {
//    BaseSequence b1 = new BaseSequence("ACT");
//    BaseSequence b2 = new BaseSequence("A");
//    BaseSequence b3 = new BaseSequence("C");
//    BaseSequence b4 = new BaseSequence("T");
//    BaseSequence b5 = new BaseSequence("ACT");
//
//    GraphNode g1 = new SequenceNode(1, b1);
//    GraphNode g2 = new SequenceNode(2, b2);
//    GraphNode g3 = new SequenceNode(3, b3);
//    GraphNode g4 = new SequenceNode(4, b4);
//    GraphNode g5 = new SequenceNode(5, b5);
//
//    g1.setOverlapping(true);
//    g2.setOverlapping(true);
//    g3.setOverlapping(true);
//    g4.setOverlapping(true);
//    g5.setOverlapping(true);
//
//    g1.setLevel(1);
//    g2.setLevel(2);
//    g3.setLevel(2);
//    g4.setLevel(2);
//    g5.setLevel(3);
//
//    g1.addOutEdge(g2);
//    g1.addOutEdge(g3);
//    g1.addOutEdge(g4);
//    g2.addOutEdge(g5);
//    g3.addOutEdge(g5);
//    g4.addOutEdge(g5);
//
//    g2.addInEdge(g1);
//    g3.addInEdge(g1);
//    g4.addInEdge(g1);
//    g5.addInEdge(g2);
//    g5.addInEdge(g3);
//    g5.addInEdge(g4);
//
//    ArrayList<GraphNode> orderedNodes = new ArrayList<>();
//    orderedNodes.add(g1);
//    orderedNodes.add(g2);
//    orderedNodes.add(g3);
//    orderedNodes.add(g4);
//    orderedNodes.add(g5);
//
//    ArrayList<GraphNode> result = MutationBubbleAlgorithms.makeBubbels(orderedNodes);
//    System.out.println(result);
//
//  }
//}
