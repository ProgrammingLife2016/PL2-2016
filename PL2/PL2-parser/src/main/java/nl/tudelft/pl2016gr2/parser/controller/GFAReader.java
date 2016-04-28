package nl.tudelft.pl2016gr2.parser.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import nl.tudelft.pl2016gr2.model.Graph;
import nl.tudelft.pl2016gr2.model.Node;

/**
 * This class reads a gfa file
 * @author Cas
 *
 */
public class GFAReader {

	private Scanner sc;
	private Graph g;
	//This is ugly hardcoded, but this way we know how much nodes we have to initialize.
	//@Wouter and Justin, you can probably find some better way to know this beforehand. (read the file from the bottom for example)
	public final int NUM_NODES;
	private ArrayList<Node> nodes;
	
	/**
	 * Creates a reader object and reads the gfa data from the filename.
	 * @param filename the name of the file to be read.
	 */
	public GFAReader(String filename, int graphsize) {
		this.NUM_NODES = graphsize;
		File f = new File(filename);
		try {
			this.sc = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
		prepNodes();
		read();
	}

	/**
	 * Because we know (hardcoded) how many nodes there will be, we can prepare them.
	 */
	private void prepNodes() {
		this.nodes = new ArrayList<Node>(NUM_NODES + 1);
		for (int i = 0; i < NUM_NODES + 1; i++) {
			nodes.add(new Node(i));
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
				Node p = nodes.get(parent);
				Node c = nodes.get(child);
				p.addOutLink(c);
				c.addInLink(p);
				sc.nextLine();
				break;
			default:
				System.out.println("Not S, H or L at: " + count);
			}
		}
		System.out.println("Number of lines read: " + count);
		this.g = new Graph(nodes);
	}
	
	/**
	 * Method which returns the read graph.
	 * @return The graph.
	 */
	public Graph getGraph() {
		return this.g;
	}

}
