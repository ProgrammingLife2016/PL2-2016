package nl.tudelft.pl2016gr2.model;

import java.util.ArrayList;


public class Bubble {
	
	private int level;
	private int id;
	private ArrayList<Bubble> nestedBubbles;
	private ArrayList<Bubble> inLinks;
	private ArrayList<Bubble> outLinks;
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
	
	public ArrayList<Bubble> getInLinks() {
		return inLinks;
	}
	
	public void addInLink(Bubble bubble) {
		inLinks.add(bubble);
	}
	
	public ArrayList<Bubble> getOutLinks() {
		return outLinks;
	}
	
	public void setInLinks(ArrayList<Bubble> inLinks) {
		this.inLinks = inLinks;
	}
	
	public void setOutLinks(ArrayList<Bubble> outLinks) {
		this.outLinks = outLinks;
	}
	
	public void addOutLink(Bubble bubble) {
		outLinks.add(bubble);
	}
	
	public ArrayList<Bubble> getNestedBubbles() {
		return nestedBubbles;
	}
	
	public void addNestedBubble(Bubble bubble) {
		nestedBubbles.add(bubble);
	}

}
