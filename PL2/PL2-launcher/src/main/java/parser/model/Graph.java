package parser.model;

import java.util.ArrayList;

/**
 * Mainly a dataholder class which represents a graph.
 * @author Cas
 *
 */
public class Graph {
	
	private Node root;
	private ArrayList<Node> nodes;
	
	/**
	 * Constructs a graph from an arrayList of nodes. (the edges are already in these nodes)
	 * @param nodes
	 */
	public Graph(ArrayList<Node> nodes) {
		this.root = nodes.get(1);
		this.nodes = nodes;
	}
	
	public void print() {
		for(Node n : nodes) {
			if (n.getId() != 0)
				System.out.println(n);
		}
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public int getSize() {
		return nodes.size();
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void addNode(Node node) {
		nodes.add(node);
	}
	
	

}
