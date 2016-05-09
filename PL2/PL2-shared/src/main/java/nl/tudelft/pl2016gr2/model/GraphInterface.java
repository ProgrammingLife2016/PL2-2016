package nl.tudelft.pl2016gr2.model;

import java.util.HashMap;

public interface GraphInterface {
  
  void print();

  AbstractNode getNode(int id);

  int getSize();

  void addNode(AbstractNode node);

  AbstractNode getRoot();

  public HashMap<Integer, AbstractNode> getAbstractNodes();
}
