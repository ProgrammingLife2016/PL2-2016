package nl.tudelft.pl2016gr2.model;

import java.util.Collection;

public interface PhyloFilter {
  
  public SequenceGraph zoomOut(Bubble bubble, SequenceGraph graph);
  
  public Collection<GraphNode> zoomIn(Bubble bubble);
  
  public Collection<GraphNode> filter(IPhylogeneticTreeRoot treeRoot, Collection<String> genomes);
}
