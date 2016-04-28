package nl.tudelft.pl2016gr2.core.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.Graph;
import nl.tudelft.pl2016gr2.model.Node;

public class FindBubbles {
	
	private Graph graph;
	private HashMap<Integer, PriorityQueue<Bubble>> bubbles = new HashMap<>();;
	private Set<Bubble> collapsedBubbles = new HashSet<>();
	private PriorityQueue<Node> flows;
	
	public FindBubbles(Graph graph) {
		this.graph = graph;
		
		flows = new PriorityQueue<>((Node node1, Node node2) -> {
			int comparison = Double.compare(node1.getFlow(), node2.getFlow());
			
			if (comparison == 0) {
				return Integer.compare(node1.getId(), node2.getId());
			}
			
			return comparison;
		});
	}
	
	public Graph calculateBubbles() {
		Graph overview = new Graph(new ArrayList<Node>());
		
		init();
		sendFlow();
		
		while(!flows.isEmpty()) {
			Node curNode = flows.poll();
			//System.out.println(curNode);
			
			Node nextNode = flows.peek();
			
			if (curNode.getFlow() == nextNode.getFlow()) {
				// is bubble
				
			}
		}
		
		return overview;
	}
	
	private void init() {
		// Puts the bubble with the highest level first
		Comparator<Bubble> compareLevels = (Bubble bubble1, Bubble bubble2) -> {
			return Integer.compare(bubble2.getLevel(), bubble1.getLevel());
		};
		
		for (Node node : graph.getNodes()) {
			PriorityQueue<Bubble> levels = new PriorityQueue<>(compareLevels);
			levels.add(node);
			bubbles.put(node.getId(), levels);
		}
	}

	private void sendFlow() {
		double flowStart = (double)graph.getSize();
		graph.getRoot().setFlow(flowStart);
		
		for (Node node : graph.getNodes()) {
			flows.offer(node);
			
//			System.out.println(node);
			ArrayList<Bubble> outLinks = node.getOutLinks();
			double remainingFlow = node.getFlow();
			
			for (int i = 0; i < outLinks.size(); i++) {
				Node target = (Node) outLinks.get(i);
				
				if (i == outLinks.size() - 1) {
					target.addFlow(remainingFlow);
				} else {
					double random = Math.random();
					if (random == 0) {
						random += 0.1;
					} else if (random == 1) {
						random -= 0.1;
					}
					double flowToAdd = random * remainingFlow;
					remainingFlow -= flowToAdd;
					target.addFlow(flowToAdd);
				}
			}
		}
	}
}
