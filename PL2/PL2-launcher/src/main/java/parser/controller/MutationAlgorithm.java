package parser.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import parser.model.Edge;
import parser.model.Graph;
import parser.model.Node;

public class MutationAlgorithm {
	
//	Queue<Node> q = new LinkedList<Node>();
//	
//	public void calc(Graph g) {
//		Node r = g.getRoot();
//		ArrayList<Edge> out = r.getOut();
//		sendFlow(out, 1);	
//		while(!q.isEmpty()) {
//			Node n = q.poll();
//			iterate(n);
//			System.out.println(n);
//		}
//	}
//	
//	private void iterate(Node n) {
//		ArrayList<Edge> inedge = n.getIn();
//		ArrayList<Edge> outedge = n.getOut();
//		int inSize = inedge.size();
//		int outSize = outedge.size();
//		int flow;
//		if (inSize == outSize) {
//			flow = n.getFlow();
//		} else if (inSize > outSize) {
//			flow = findMinimumFlow(inedge) - 1;
//			n.setFlow(flow);
//		} else {
//			flow = n.getFlow() + 1;
//		}
//		sendFlow(outedge, flow);
//	}
//	
//	private void sendFlow(ArrayList<Edge> out, int flow) {
//		for (Edge e : out) {
//			Node child = e.getChild();
//			child.setFlow(flow);
//			//if (!q.contains(child)) {
//				q.add(child);
//			//}
//		}
//	}
//	
//	private int findMinimumFlow(ArrayList<Edge> inedge) {
//		int res = Integer.MAX_VALUE;
//		for(Edge e : inedge) {
//			Node p = e.getParent();
//			int f = p.getFlow();
//			if (f < res) res = f;
//		}
//		return res;
//	}

}
