package nl.tudelft.pl2016gr2.parser.model;

/**
 * Data holder class which represents a directed edge.
 * @author Cas
 *
 */
public class Edge {
	
	private Node source;
	private Node target;
	//We can add for example a weight to this.
	
	/**
	 * Creates a directed edge from p to c.
	 * @param p the parent.
	 * @param c the child.
	 */
	public Edge(Node source, Node target) {
		this.source = source;
		this.target = target;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

}
