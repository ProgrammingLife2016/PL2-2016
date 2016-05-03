package nl.tudelft.pl2016gr2.parser.controller;

import java.util.ArrayList;
import java.util.Scanner;

import nl.tudelft.pl2016gr2.model.Node;
import nl.tudelft.pl2016gr2.model.OriginalGraph;

/**
 * This class reads a gfa file
 * @author Cas
 *
 */
public class GFAReader {

	private Scanner sc;
	private OriginalGraph originalGraph;
	//This is ugly hardcoded, but this way we know how much nodes we have to initialize.
	//@Wouter and Justin, you can probably find some better way to know this beforehand. (read the file from the bottom for example)
	public final int NUM_NODES;
	//private ArrayList<BubbleOld> nodes;
	
	/**
	 * Creates a reader object and reads the gfa data from the filename.
	 * @param filename the name of the file to be read.
	 */
	public GFAReader(String filename, int graphsize) {
		this.NUM_NODES = graphsize;
		this.sc = new Scanner(GFAReader.class.getClassLoader().getResourceAsStream(filename));
		originalGraph = new OriginalGraph();
		prepNodes();
		read();
		
		sc.close();
		sc = null;
	}

	/**
	 * Because we know (hardcoded) how many nodes there will be, we can prepare them.
	 */
	private void prepNodes() {
		//this.nodes = new ArrayList<BubbleOld>(NUM_NODES + 1);
		for (int i = 1; i < NUM_NODES + 1; i++) {
			originalGraph.addNode(new Node(i, 1, new ArrayList<>(), 0));
		}
	}

	/**
	 * Read the actual data.
	 */
	private void read() {
		int count = 0;
		while (sc.hasNext()) {
			count++;
			String next = sc.next();
			switch (next) {
			case "H":
				sc.nextLine();
				break;
			case "S":
				sc.nextLine();
				break;
			case "L":
				int parent = sc.nextInt();
				sc.next();
				int child = sc.nextInt();
				Node p = originalGraph.getNode(parent);
				Node c = originalGraph.getNode(child);
				p.addOutlink(c.getId());
				c.addInlink(p.getId());
				sc.nextLine();
				break;
			default:
				System.out.println("Not S, H or L at: " + count);
			}
		}
		System.out.println("Number of lines read: " + count);
	}
	
	/**
	 * Method which returns the read graph.
	 * @return The graph.
	 */
	public OriginalGraph getGraph() {
		return originalGraph;
	}

}
