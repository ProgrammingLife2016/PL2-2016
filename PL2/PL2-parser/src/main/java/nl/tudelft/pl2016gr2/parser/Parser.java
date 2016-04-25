package nl.tudelft.pl2016gr2.parser;

import java.util.ArrayList;

import nl.tudelft.pl2016gr2.parser.controller.GFAReader;
import nl.tudelft.pl2016gr2.parser.controller.MutationAlgorithm;
import nl.tudelft.pl2016gr2.parser.model.Edge;
import nl.tudelft.pl2016gr2.parser.model.Graph;

/**
 * This class acts as the launcher for my simple nl.tudelft.pl2016gr2.parser.
 * @author Cas
 *
 */
public class Parser {
	
	public static final String FILENAME = "PL2-launcher///src/main/resources/TB10.gfa";

	/**
	 * Main method to call. It prints the number of lines in the file (in the reader class), the number of nodes (+1 for node 0), 
	 * a random node its outgoing edges (2), the time it took to run the program.
	 * @param args does not need to be specified and is not used.
	 */
	public static void main(String[] args) {
		long s = System.currentTimeMillis();
		GFAReader r = new GFAReader(FILENAME);
		Graph g = r.getGraph();
		System.out.println("Size of the graph: " + g.getSize());
		ArrayList<Edge> out = g.getNodes().get(3141).getOut();
		for (Edge e : out) {
			//For manual verification purposes.
			System.out.println("Outgoing node: " + e.getChild().getId());
		}
		long e = System.currentTimeMillis();
		System.out.println("The loading took " + (e - s) + " milliseconds to run");
		MutationAlgorithm.calc(g);
		long f = System.currentTimeMillis();
		System.out.println("The algorithm took " + (f - e) + " milliseconds to run");
	}

}
