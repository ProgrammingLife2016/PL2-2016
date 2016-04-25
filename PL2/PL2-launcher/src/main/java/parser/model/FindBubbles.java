package parser.model;

import java.util.ArrayList;

public class FindBubbles {
	
	private Graph graph;
	
	public FindBubbles(Graph graph) {
		this.graph = graph;
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
