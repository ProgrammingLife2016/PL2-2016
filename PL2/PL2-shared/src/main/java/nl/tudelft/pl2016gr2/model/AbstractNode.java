package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;

public abstract class AbstractNode {
	
	private int id;
	private ArrayList<Integer> inLinks;
	private ArrayList<Integer> outLinks;
	private int sequenceLength;
	
	public AbstractNode(int id, int sequenceLength) {
		this.id = id;
		this.sequenceLength = sequenceLength;
		this.inLinks = new ArrayList<>();
		this.outLinks = new ArrayList<>();
	}
	
	public void addInlink(int inlink) {
		inLinks.add(inlink);
	}
	
	public ArrayList<Integer> getInlinks() {
		return inLinks;
	}
	
	public void addOutlink(int outlink) {
		outLinks.add(outlink);
	}
	
	public ArrayList<Integer> getOutlinks() {
		return outLinks;
	}
	
	public int getSequenceLength() {
		return sequenceLength;
	}
	
	public void setSequenceLength(int length) {
		sequenceLength = length;
	}
	
	public int getId() {
		return id;
	}

}
