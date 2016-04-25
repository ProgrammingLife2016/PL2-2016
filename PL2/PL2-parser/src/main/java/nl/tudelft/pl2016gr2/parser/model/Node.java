package nl.tudelft.pl2016gr2.parser.model;

import java.util.ArrayList;

/**
 * Mainly a dataholder class which represents a Node.
 * @author Cas
 *
 */
public class Node {
	
	private ArrayList<Edge> in;
	private ArrayList<Edge> out;
	private int id;
	
	/**
	 * Construct a new node.
	 * @param id the id (index) of the node.
	 */
	public Node(int id) {
		this.in = new ArrayList<Edge>();
		this.out = new ArrayList<Edge>();
		this.id = id;
	}
	
	/**
	 * Add an incoming edge to the node.
	 * @param e the incoming edge.
	 */
	public void addIn(Edge e) {
		in.add(e);
	}
	
	/**
	 * Add an outgoing edge to the node.
	 * @param e the outgoing edge.
	 */
	public void addOut(Edge e) {
		out.add(e);
	}

	public ArrayList<Edge> getIn() {
		return in;
	}

	public void setIn(ArrayList<Edge> in) {
		this.in = in;
	}

	public ArrayList<Edge> getOut() {
		return out;
	}

	public void setOut(ArrayList<Edge> out) {
		this.out = out;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
