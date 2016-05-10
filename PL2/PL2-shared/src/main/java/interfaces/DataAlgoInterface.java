package interfaces;

import nl.tudelft.pl2016gr2.model.GraphInterface;
import nl.tudelft.pl2016gr2.model.MetaData;
import nl.tudelft.pl2016gr2.model.Node;

public interface DataAlgoInterface {
  
  /**
   * This method should return the node which has id as identifier.
   * @param id the id of the node we want.
   * @return The node which was requested
   */
  Node getNode(int identifier);
  
  /**
   * This method should return all nodes from some Genome in the phylogenetic tree.
   * @param tag The name of the genome.
   * @return The graph of the genome.
   */
  GraphInterface getPhylo(String tag);
  
  /**
   * This method should return metadata of some Node.
   * @param id the Id of the node we want to get metadata from.
   * @return A MetaData object. 
   */
  MetaData getMeta(Node identifier);

}
