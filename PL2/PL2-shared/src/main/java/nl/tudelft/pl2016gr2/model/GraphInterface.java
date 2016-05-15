package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

public interface GraphInterface {

  AbstractNode getNode(int id);

  int getSize();

  void addNode(AbstractNode node);

  /**
   * Get the IDs of all root nodes.
   * @return the IDs of all root nodes.
   */
  ArrayList<Integer> getRootNodes();

  // public HashMap<Integer, AbstractNode> getNodes();
}
