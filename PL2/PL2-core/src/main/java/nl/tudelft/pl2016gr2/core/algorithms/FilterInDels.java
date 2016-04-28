//package nl.tudelft.pl2016gr2.core.algorithms;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.Set;
//
//import nl.tudelft.pl2016gr2.model.Edge;
//import nl.tudelft.pl2016gr2.model.Graph;
//import nl.tudelft.pl2016gr2.model.Node;
//
//public class FilterInDels {
//	
//	private Graph graph;
//	/**
//	 * Contains all nodes that are part of an indel
//	 */
//	private Set<Integer> usedNodes = new HashSet<>();
//	
//	public FilterInDels(Graph graph) {
//		this.graph = graph;
//	}
//	
//	public Graph filterGraph() {
//		ArrayList<Node> root = new ArrayList<>();
//		ArrayList<Node> nodes = graph.getNodes();
//		root.add(nodes.get(0));
//		root.add(nodes.get(1));
//		Graph filteredGraph = new Graph(root);
//		
//		for (int i = 0; i < nodes.size(); i++) {
//			Node source = nodes.get(i);
//			
//			if (!usedNodes.contains(source.getId())) {
//				Node[] res = getInsertion(source);
//				if (res != null) {
//					Node insertion = res[0];
//					Node end = res[1];
//					
//					usedNodes.add(insertion.getId());
//					Node inDel = new Node(source.getId());
//					inDel.setInDel(true);
//					inDel.setBubbleStart(source.getId());
//					inDel.setBubbleEnd(end.getId());
//					
//					for (Edge edge : source.getIn()) {
//						inDel.addIn(edge);
//					}
//					
//					for (Edge edge : end.getOut()) {
//						inDel.addOut(edge);
//					}
//					
//					filteredGraph.addNode(inDel);
//				} else {
//					filteredGraph.addNode(source);
//				}
//			}
//		}
//		
//		return filteredGraph;
//	}
//	
//	private Node[] getInsertion(Node source) {
//		ArrayList<Edge> outEdges = source.getOut();
//		
//		if (outEdges.size() != 2) {
//			return null;
//		}
//		
//		Node target1 = outEdges.get(0).getTarget();
//		Node target2 = outEdges.get(1).getTarget();
//		
//		// First one is the insertion, second one is the initial endpoint
//		if (target1.getOut().size() != 0 && target1.getOut().get(0).getTarget().equals(target2) && target1.getIn().size() == 1 && target2.getIn().size() == 2) {
//			Node[] res = {target1, target2};
//			return res;
//		} else if(target2.getOut().size() != 0 && target2.getOut().get(0).getTarget().equals(target1) && target2.getIn().size() == 1 && target1.getIn().size() == 2) {
//			Node[] res = {target2, target1};
//			return res;
//		}
//		
//		return null;
//	}
//}
