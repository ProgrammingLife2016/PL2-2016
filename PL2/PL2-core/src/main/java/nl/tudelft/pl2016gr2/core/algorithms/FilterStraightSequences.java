package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.BubbledGraph;
import nl.tudelft.pl2016gr2.model.GraphInterface;

public class FilterStraightSequences {
  
  private GraphInterface graph;
  
  public FilterStraightSequences(GraphInterface graph) {
    this.graph = graph;
  }
  
  public GraphInterface filter() {
    BubbledGraph filteredGraph = new BubbledGraph();
    
    AbstractNode root = graph.getRoot();
    
    return filteredGraph;
  }
  
  private boolean isStraightSequence(AbstractNode node) {
    return node.getOutlinks().size() == 1 
        && graph.getNode(node.getOutlinks().get(0)).getInlinks().size() == 1;
  }
}
