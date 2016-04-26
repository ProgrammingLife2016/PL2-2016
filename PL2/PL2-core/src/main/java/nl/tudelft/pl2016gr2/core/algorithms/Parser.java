package nl.tudelft.pl2016gr2.core.algorithms;

import nl.tudelft.pl2016gr2.model.Graph;
import nl.tudelft.pl2016gr2.parser.controller.GFAReader;

/**
 * This class acts as the launcher for my simple nl.tudelft.pl2016gr2.parser.
 * @author Cas
 *
 */
public class Parser {
	
	public static final String FILENAME = Parser.class.getClassLoader().getResource("TB10.gfa").getFile();
	public static final int GRAPH_SIZE = 8728;
	//public static final int GRAPH_SIZE = 10;

	/**
	 * Main method to call. It prints the number of lines in the file (in the reader class), the number of nodes (+1 for node 0), 
	 * the time it took to run the program.
	 * @param args does not need to be specified and is not used.
	 */
	public static void main(String[] args) {
		long s = System.currentTimeMillis();
		GFAReader r = new GFAReader(FILENAME, GRAPH_SIZE);
		Graph g = r.getGraph();
		System.out.println("Size of the graph: " + g.getSize());
		long e = System.currentTimeMillis();
		System.out.println("The loading took " + (e - s) + " milliseconds to run");
		//MutationAlgorithm m = new MutationAlgorithm();
		//m.calc(g);
		FindBubbles findBubbles = new FindBubbles(g);
		findBubbles.calculateFlows();
		FilterInDels filter = new FilterInDels(g);
		Graph filteredGraph = filter.filterGraph();
		filteredGraph.print();
		g.print();
		//System.out.println(g.getNodes().get(8).getIn().get(0).getParent().getFlow());
		long f = System.currentTimeMillis();
		System.out.println("The algorithm took " + (f - e) + " milliseconds to run");
	}
}
