package nl.tudelft.pl2016gr2.model;

public interface GraphInterface {

  AbstractNode getNode(int id);

  int getSize();

  void addNode(AbstractNode node);

  AbstractNode getRoot();

  // public HashMap<Integer, AbstractNode> getNodes();
}
