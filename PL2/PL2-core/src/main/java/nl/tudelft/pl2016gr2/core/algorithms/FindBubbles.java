package nl.tudelft.pl2016gr2.core.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
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
		Graph overview = new Graph(new ArrayList<Bubble>());
		
		init();
		sendFlow();
		
		while(!flows.isEmpty()) {
			Node curNode = flows.poll();
			//System.out.println(curNode);
			
			Node nextNode = flows.peek();
			
			if (curNode.getFlow() == nextNode.getFlow()) {
				// is bubble
				Bubble bubble = new Bubble(curNode.getId());
				bubble.setInLinks(curNode.getInLinks());
				bubble.setOutLinks(nextNode.getOutLinks());
				bubble.setStartBubble(curNode);
				bubble.setEndBubble(nextNode);
				findNestedBubbles(curNode, nextNode, bubble);
				bubbles.get(bubble.getId()).offer(bubble);
			}
		}
		
		for (PriorityQueue<Bubble> bubbleLevels : bubbles.values()) {
			overview.addNode(bubbleLevels.peek());
		}
		
		return overview;
	}
	
	private void findNestedBubbles(Bubble startBubble, Bubble endBubble, Bubble newBubble) {
		if (startBubble.getOutLinks().size() == 1 && startBubble.getOutLinks().get(0).equals(endBubble)) {
			return;
		}
		
		Set<Integer> visited = new HashSet<>();
		visited.add(endBubble.getId());
		
		Queue<Integer> toVisit = new LinkedList<>();
		toVisit.addAll(startBubble.getOutLinks());
		
		int highestLevel = 0;
		
		while (!toVisit.isEmpty()) {
			Bubble bubble = bubbles.get(toVisit.poll()).peek();
			
			if (!bubble.equals(endBubble)) {
				newBubble.addNestedBubble(bubble.getId());
				visited.add(bubble.getId());
				
				highestLevel = bubble.getLevel() > highestLevel ? bubble.getLevel() : highestLevel;
				
				for (Integer target : bubble.getOutLinks()) {
					if (!visited.contains(target)) {
						toVisit.add(target);
					}
				}
			}
		}
		
		newBubble.setLevel(highestLevel);
	}
	
	private void init() {
		// Puts the bubble with the highest level first
		Comparator<Bubble> compareLevels = (Bubble bubble1, Bubble bubble2) -> {
			return Integer.compare(bubble2.getLevel(), bubble1.getLevel());
		};
		
		for (Bubble node : graph.getNodes()) {
			PriorityQueue<Bubble> levels = new PriorityQueue<>(compareLevels);
			levels.add(node);
			bubbles.put(node.getId(), levels);
		}
	}

	private void sendFlow() {
		double flowStart = (double)graph.getSize();
		//graph.getRoot().setFlow(flowStart);
		Node root = (Node) graph.getRoot();
		root.setFlow(flowStart);
		
		for (Bubble bubble : graph.getNodes()) {
			Node node = (Node) bubble;
			flows.offer(node);
			
//			System.out.println(node);
			ArrayList<Integer> outLinks = node.getOutLinks();
			double remainingFlow = node.getFlow();
			
			for (int i = 0; i < outLinks.size(); i++) {
				Node target = (Node) bubbles.get(outLinks.get(i)).peek();
				
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
