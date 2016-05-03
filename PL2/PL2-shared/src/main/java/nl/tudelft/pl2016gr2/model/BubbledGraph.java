package nl.tudelft.pl2016gr2.model;

import java.util.HashMap;

public class BubbledGraph implements GraphInterface {

  private HashMap<Integer, AbstractNode> nodes;
  private int lowestId;

  public BubbledGraph() {
    nodes = new HashMap<>();
    lowestId = Integer.MAX_VALUE;
  }

  @Override
  public AbstractNode getNode(int id) {
    return nodes.get(id);
  }

  @Override
  public int getSize() {
    return nodes.size();
  }

  @Override
  public void addNode(AbstractNode node) {
    int id = node.getId();
    if (id < lowestId) {
      lowestId = id;
    }

    nodes.put(id, node);
  }

  @Override
  public AbstractNode getRoot() {
    return nodes.get(lowestId);
  }

}
