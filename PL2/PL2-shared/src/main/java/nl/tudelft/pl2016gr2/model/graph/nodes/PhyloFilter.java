package nl.tudelft.pl2016gr2.model.graph.nodes;

import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;

import java.util.Collection;

public interface PhyloFilter {

  //public SequenceGraph zoomOut(Bubble bubble, SequenceGraph graph);
  public Collection<GraphNode> zoomIn(Bubble bubble);

  public Collection<GraphNode> filter(IPhylogeneticTreeRoot treeRoot,
      Collection<Integer> genomes);
}
