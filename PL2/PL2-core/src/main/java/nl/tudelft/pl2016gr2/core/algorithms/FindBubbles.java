package nl.tudelft.pl2016gr2.core.algorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.Edge;
import nl.tudelft.pl2016gr2.model.Graph;
import nl.tudelft.pl2016gr2.model.Node;

public class FindBubbles {
	
	private Graph graph;
	private HashMap<Integer, PriorityQueue<Bubble>> bubbles;
	private Set<Bubble> collapsedBubbles = new HashSet<>();
	
	public FindBubbles(Graph graph) {
		this.graph = graph;
		
		bubbles = new HashMap<>();
	}
	
	public Graph calculateBubbles() {
		Graph overview = new Graph(new ArrayList<Node>());
		
		init();
		sendFlow();
		
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
	
	public void calculateFlows() {
		sendFlow();
	}
	
	private void sendFlow() {
		double flowStart = (double)graph.getSize();
		graph.getRoot().setFlow(flowStart);
		
		for (Node node : graph.getNodes()) {
			System.out.println(node);
			ArrayList<Edge> outEdges = node.getOut();
			double remainingFlow = node.getFlow();
			
			for (int i = 0; i < outEdges.size(); i++) {
				Node target = outEdges.get(i).getTarget();
				
				if (i == node.getOut().size() - 1) {
					target.addFlow(remainingFlow);
				} else {
					double random = Math.random();
					double flowToAdd = random * remainingFlow;
					remainingFlow -= flowToAdd;
					target.addFlow(flowToAdd);
				}
			}
		}
	}
}
