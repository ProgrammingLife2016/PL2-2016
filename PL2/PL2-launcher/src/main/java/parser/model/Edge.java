package parser.model;

/**
 * Data holder class which represents a directed edge.
 * @author Cas
 *
 */
public class Edge {
	
	private Node parent;
	private Node child;
	//We can add for example a weight to this.
	
	/**
	 * Creates a directed edge from p to c.
	 * @param p the parent.
	 * @param c the child.
	 */
	public Edge(Node p, Node c) {
		this.parent = p;
		this.child = c;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getChild() {
		return child;
	}

	public void setChild(Node child) {
		this.child = child;
	}

}
