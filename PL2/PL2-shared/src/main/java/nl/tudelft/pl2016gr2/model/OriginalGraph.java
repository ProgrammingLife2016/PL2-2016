package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;
import java.util.HashMap;

public class OriginalGraph implements GraphInterface {

  private HashMap<Integer, Node> nodes;
  private int lowestId;
  private ArrayList<String> genoms;

  public OriginalGraph() {
    nodes = new HashMap<>();
    lowestId = Integer.MAX_VALUE;
  }

  public void print() {
    for (Node node : nodes.values()) {
      System.out.println(node);
    }
  }

  @Override
  public Node getNode(int id) {
    return nodes.get(id);
  }

  public ArrayList<Node> getTargets(int id) {
    ArrayList<Integer> outLinks = nodes.get(id).getOutlinks();
    ArrayList<Node> targets = new ArrayList<>();

    for (Integer outLink : outLinks) {
      targets.add(nodes.get(outLink));
    }

    return targets;
  }

  @Override
  public int getSize() {
    return nodes.size();
  }

  @Override
  public void addNode(AbstractNode node) {
    assert (node instanceof Node);

    int id = node.getId();
    if (id < lowestId) {
      lowestId = id;
    }

    nodes.put(id, (Node) node);
  }

  @Override
  public Node getRoot() {
    return nodes.get(lowestId);
  }

  public HashMap<Integer, Node> getNodes() {
    return nodes;
  }
  
  public ArrayList<String> getGenoms() {
    return this.genoms;
  }
  
  public void setGenoms(ArrayList<String> gs) {
    this.genoms = gs;
  }

}
