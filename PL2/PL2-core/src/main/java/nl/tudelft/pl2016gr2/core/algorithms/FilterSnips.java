package nl.tudelft.pl2016gr2.core.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

public class FilterSnips {
	
	private OriginalGraph graph;
	private OriginalGraph filteredGraph;
	private HashSet<Integer> collapsedNodes = new HashSet<>();
	
	public FilterSnips(OriginalGraph graph) {
		this.graph = graph;
		filteredGraph = new OriginalGraph();
	}
	
	public OriginalGraph filter() {
		filteredGraph = new OriginalGraph();
		
		Queue<Integer> toVisit = new LinkedList<>();
		Set<Integer> visited = new HashSet<>();
		toVisit.add(graph.getRoot().getId());
		//int totalSnips = 0;
		
		while (!toVisit.isEmpty()) {
//			System.out.println("to visit: " + toVisit);
//			System.out.println("visited: " + visited);
			Node current = graph.getNode(toVisit.poll());
			visited.add(current.getId());
//			System.out.println(current);
			
			if (isSnip(current)) {
				Node snip = makeSnip(current);
				//totalSnips += snip.getSnips();
				
				for (Integer outlink : snip.getOutlinks()) {
//					if (current.getId() == 6557) {
//						System.out.println(snip);
//						System.out.println(visited.contains(outlink));
//						System.out.println(toVisit.contains(outlink));
//						System.out.println(visited.size());
//						System.out.println(totalSnips);
//					}
					if (!visited.contains(outlink) && !toVisit.contains(outlink))
						toVisit.offer(outlink);
				}
				
				filteredGraph.addNode(snip);
			} else {
				for (Integer outlink : current.getOutlinks()) {
					if (!visited.contains(outlink) && !toVisit.contains(outlink))
						toVisit.offer(outlink);
				}
				
				filteredGraph.addNode(current);
			}
		}
		filteredGraph.print();
		return filteredGraph;
	}
	
	private Node makeSnip(Node current) {
		Node snip = null;
		boolean isSnip = true;
		
		while (isSnip) {
			//System.out.println("Current: " + current);
			Node intermediate = graph.getNode(current.getOutlinks().get(0));
			Node end = graph.getNode(intermediate.getOutlinks().get(0));
			
			snip = new Node(current.getId(), current.getSequenceLength() + 1 + end.getSequenceLength(), 
					current.getGenomes(), current.getSnips() + 1);
			snip.setInlinks(current.getInlinks());
			snip.setOutlinks(end.getOutlinks());
			current = snip;
			//System.out.println("Snip: " + snip);
			
			updateLinks(snip, end.getId());
			
			isSnip = isSnip(snip);
			//System.out.println("Is Snip: " + isSnip);
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
				graph.getNode(targets.get(0).getOutlinks().get(0)).getInlinks().size() == 2;
	}
	
	private void updateLinks(Node snip, int originalId) {
		for (Integer outlink : snip.getOutlinks()) {
			Node out = graph.getNode(outlink);
			out.replaceInlink(originalId, snip.getId());
			
			Node outFiltered = filteredGraph.getNode(outlink);
			if (outFiltered != null) {
				outFiltered.replaceInlink(originalId, snip.getId());
			}
		}
	}
}
