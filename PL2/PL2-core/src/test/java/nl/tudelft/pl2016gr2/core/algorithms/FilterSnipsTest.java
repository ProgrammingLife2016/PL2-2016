package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

import org.junit.Test;

public class FilterSnipsTest {

  @Test
  public void testMakeSnip() {
    OriginalGraph graph = new OriginalGraph();

    for (int i = 1; i <= 9; i++) {
      Node node = new Node(i, 1, null, 0);

      if (i == 1 || i == 4 || i == 7) {
        node.addOutlink(i + 1);
        node.addOutlink(i + 2);

        if (i != 1) {
          node.addInlink(i - 1);
          node.addInlink(i - 2);
        }
      } else if (i == 2 || i == 5) {
        node.addOutlink(i + 2);
        node.addInlink(i - 1);
      } else if (i == 3 || i == 6) {
        node.addOutlink(i + 1);
        node.addInlink(i - 2);
      } else if (i == 8) {
        node.addInlink(i - 1);
      } else {
        node.addInlink(i - 2);
      }

      graph.addNode(node);
    }

    // Node zeroNode = new Node(0, 1, null, 0);
    // zeroNode.addOutlink(1);
    // graph.addNode(zeroNode);

    graph.print();
    System.out.println("-------------------");
    FilterSnips filter = new FilterSnips(graph);
    OriginalGraph filteredGraph = filter.filter();
    // filteredGraph.print();
  }
  
  @Test
  public void testUpdateLinks() {
    
  }

}
