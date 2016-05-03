package nl.tudelft.pl2016gr2.core.algorithms;

import java.util.ArrayList;
import java.util.HashSet;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

public class FilterSnips {
	
	private OriginalGraph graph;
	private HashSet<Integer> collapsedNodes = new HashSet<>();
	
	public FilterSnips(OriginalGraph graph) {
		this.graph = graph;
	}
	
	public OriginalGraph filter() {
		OriginalGraph filteredGraph = new OriginalGraph();

		for (int i = 1; i <= graph.getSize(); i++) {
			if (!collapsedNodes.contains(i)){
				Node current = graph.getNode(i);
				
				if (isSnip(current)) {
					Node snip = makeSnip(current);
					filteredGraph.addNode(snip);
				} else {
					filteredGraph.addNode(current);
				}
			}
			
		}
		
		return filteredGraph;
	}
	
	private Node makeSnip(Node current) {
		Node snip = null;
		boolean isSnip = true;
		
		while (isSnip) {
			for (Integer outlink : current.getOutlinks()) {
				if (outlink == 2102) {
					System.out.println("Current snip: " + current);
				}
				collapsedNodes.add(outlink);
			}

			Node intermediate = graph.getNode(current.getOutlinks().get(0));
			Node end = graph.getNode(intermediate.getOutlinks().get(0));
			collapsedNodes.add(end.getId());
			
			snip = new Node(current.getId(), current.getSequenceLength() + 1 + end.getSequenceLength(), 
					current.getGenomes(), current.getSnips() + 1);
			snip.setInlinks(current.getInlinks());
			snip.setOutlinks(end.getOutlinks());
			current = snip;
			
			updateLinks(snip, end.getId());
			isSnip = isSnip(snip);
		}
		
		return snip;
	}
	
	private boolean isSnip(Node snip) {
		ArrayList<Node> targets = new ArrayList<>();
		for (Integer outlink : snip.getOutlinks()) {
			targets.add(graph.getNode(outlink));
		}
		
		return targets.size() == 2 && targets.get(0).getSequenceLength() == 1 &&
				targets.get(1).getSequenceLength() == 1 &&
				targets.get(0).getOutlinks().size() == 1 &&
				targets.get(1).getOutlinks().size() == 1 &&
				targets.get(0).getInlinks().size() == 1 &&
				targets.get(1).getInlinks().size() == 1 &&
				graph.getNode(targets.get(0).getOutlinks().get(0)).getInlinks().size() == 2;
	}
	
	private void updateLinks(Node snip, int originalId) {
		for (Integer outlink : snip.getOutlinks()) {
			Node out = graph.getNode(outlink);
			out.replaceInlink(originalId, snip.getId());
		}
	}
}
