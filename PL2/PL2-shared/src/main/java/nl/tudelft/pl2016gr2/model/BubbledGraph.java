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
  public void print() {
    System.out.println("GRAPH ------------------------------");
    for (AbstractNode node : nodes.values()) {
      System.out.println(node);
    }
    System.out.println("-------------------------------------");
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
  public HashMap<Integer, AbstractNode> getAbstractNodes() {
    return nodes;
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
  
  @Override
  public void replace(Bubble bubble, GraphInterface graph) {
    nodes.remove(bubble.getId());
    
    for (AbstractNode node : graph.getAbstractNodes().values()) {
      nodes.put(node.getId(), node);
    }
  }
  
  public void replace(Bubble bubble, Node node) {
    nodes.remove(bubble.getId());
    
    for (Integer inlink : bubble.getInlinks()) {
      nodes.get(inlink).getOutlinks().remove((Integer)bubble.getId());
    }
    
    for (Integer outlink : bubble.getOutlinks()) {
      nodes.get(outlink).getInlinks().remove((Integer)bubble.getId());
    }
    
    nodes.put(node.getId(), node);
    
    for (Integer inlink : node.getInlinks()) {
      nodes.get(inlink).addOutlink(node.getId());
    }
    
    for (Integer outlink : node.getOutlinks()) {
      nodes.get(outlink).addInlink(node.getId());
    }
  }
  
  @Override
  public boolean hasNode(int nodeId) {
    return nodes.containsKey(nodeId);
  }
  
  public void remove(AbstractNode node) {
    nodes.remove(node.getId());
    
    for (Integer inlink : node.getInlinks()) {
      if (nodes.containsKey(inlink)) {
        nodes.get(inlink).getOutlinks().remove((Integer)node.getId());
      }
    }
    
    for (Integer outlink : node.getOutlinks()) {
      if (nodes.containsKey(outlink)) {
        nodes.get(outlink).getInlinks().remove((Integer)node.getId());
      }
    }
  }

}
