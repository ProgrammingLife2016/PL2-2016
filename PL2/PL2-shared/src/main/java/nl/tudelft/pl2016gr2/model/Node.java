package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

/**
 * Mainly a dataholder class which represents a Node.
 * @author Cas
 *
 */
public class Node {
	
	private ArrayList<Edge> in;
	private ArrayList<Edge> out;
	private double flow;
	private int id, bubbleStart, bubbleEnd;
	private boolean isInDel;
	
	/**
	 * Construct a new node.
	 * @param id the id (index) of the node.
	 */
	public Node(int id) {
		this.in = new ArrayList<Edge>();
		this.out = new ArrayList<Edge>();
		this.id = id;
		this.flow = 0;
		this.bubbleStart = 0;
		this.bubbleEnd = 0;
		this.isInDel = false;
	}
	
	@Override
	public String toString() {
		return id + " inedges: " + in.size() + " outedges: " + out.size() + " flow: " + flow + " bubble (" + bubbleStart + ", " + bubbleEnd + ")" + " isInDel: " + isInDel;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Node) {
			Node node = (Node) object;
			
			return node.id == this.id;
		}
		
		return false;
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
	
	public boolean isInDel() {
		return isInDel;
	}
	
	public void setInDel(boolean isInDel) {
		this.isInDel = isInDel;
	}
	
	public void setBubbleStart(int bubbleStart) {
		this.bubbleStart = bubbleStart;
	}
	
	public void setBubbleEnd(int bubbleEnd) {
		this.bubbleEnd = bubbleEnd;
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
	
	public double getFlow() {
		return flow;
	}
	
	public void setFlow(double f) {
		this.flow = f;
	}
	
	public void addFlow(double flow) {
		this.flow += flow;
	}

}
