package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

/**
 * Mainly a dataholder class which represents a graph.
 * @author Cas
 *
 */
public class Graph {
	
	private Node root;
	private ArrayList<Bubble> nodes;
	
	/**
	 * Constructs a graph from an arrayList of nodes. (the edges are already in these nodes)
	 * @param nodes
	 */
	public Graph(ArrayList<Bubble> nodes) {
		//this.root = nodes.get(1);
		this.nodes = nodes;
	}
	
	public void print() {
		for(Bubble n : nodes) {
			if (n.getId() != 0)
				System.out.println(n);
		}
	}

	public Bubble getRoot() {
		return nodes.get(1);
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public int getSize() {
		return nodes.size();
	}

	public ArrayList<Bubble> getNodes() {
		return nodes;
	}

	public void addNode(Bubble node) {
		nodes.add(node);
	}
	
	

}
