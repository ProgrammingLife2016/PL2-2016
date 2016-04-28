package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;


public class Bubble {
	
	private int level;
	private int id;
	private ArrayList<Integer> nestedBubbles;
	private ArrayList<Integer> inLinks;
	private ArrayList<Integer> outLinks;
	private Bubble startBubble;
	private Bubble endBubble;
	
	public Bubble(int id) {
		this.id = id;
		nestedBubbles = new ArrayList<>();
		inLinks = new ArrayList<>();
		outLinks = new ArrayList<>();
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof Bubble) {
			Bubble bubble = (Bubble) object;
			
			return bubble.id == this.id && bubble.level == this.level;
		}
		
		return false;
	}
	
	public int getId() {
		return id;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public void setStartBubble(Bubble bubble) {
		this.startBubble = bubble;
	}
	
	public Bubble getStartBubble() {
		return startBubble;
	}
	
	public void setEndBubble(Bubble bubble) {
		this.endBubble = bubble;
	}
	
	public Bubble getEndBubble() {
		return endBubble;
	}
	
	public ArrayList<Integer> getInLinks() {
		return inLinks;
	}
	
	public void addInLink(int bubble) {
		inLinks.add(bubble);
	}
	
	public ArrayList<Integer> getOutLinks() {
		return outLinks;
	}
	
	public void setInLinks(ArrayList<Integer> inLinks) {
		this.inLinks = inLinks;
	}
	
	public void setOutLinks(ArrayList<Integer> outLinks) {
		this.outLinks = outLinks;
	}
	
	public void addOutLink(int bubble) {
		outLinks.add(bubble);
	}
	
	public ArrayList<Integer> getNestedBubbles() {
		return nestedBubbles;
	}
	
	public void addNestedBubble(int bubble) {
		nestedBubbles.add(bubble);
	}

}
