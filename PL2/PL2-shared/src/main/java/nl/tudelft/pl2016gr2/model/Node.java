package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

public class Node extends AbstractNode {
	
	private ArrayList<String> genomes;
	private int snips;

	public Node(int id, int sequenceLength, ArrayList<String> genomes, int snips) {
		super(id, sequenceLength);
		
		this.genomes = genomes;
		this.snips = snips;
	}
	
	public ArrayList<String> getGenomes() {
		return genomes;
	}
	
	public int getSnips() {
		return snips;
	}

}
