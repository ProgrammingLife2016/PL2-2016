package nl.tudelft.pl2016gr2.model;

import java.util.HashMap;

public class OriginalGraph implements GraphInterface {
	
	private HashMap<Integer, Node> nodes;
	private int lowestId;
	
	public OriginalGraph() {
		nodes = new HashMap<>();
		lowestId = Integer.MAX_VALUE;
	}
	
	@Override
	public Node getNode(int id) {
		return nodes.get(id);
	}

	@Override
	public int getSize() {
		return nodes.size();
	}

	@Override
	public void addNode(AbstractNode node) {
		assert(node instanceof Node);
		
		int id = node.getId();
		if (id < lowestId) {
			lowestId = id;
		}
		
		nodes.put(id, (Node)node);
	}

	@Override
	public Node getRoot() {
		return nodes.get(lowestId);
	}

}
