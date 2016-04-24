package parser.model;

import java.util.ArrayList;

/**
 * Mainly a dataholder class which represents a graph.
 * @author Cas
 *
 */
public class Graph {
	
	private Node root;
	private int size;
	private ArrayList<Node> nodes;
	
	/**
	 * Constructs a graph from an arrayList of nodes. (the edges are already in these nodes)
	 * @param nodes
	 */
	public Graph(ArrayList<Node> nodes) {
		this.root = nodes.get(1);
		this.size = nodes.size();
		this.nodes = nodes;
	}

	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	

}
