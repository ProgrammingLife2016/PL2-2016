package nl.tudelft.pl2016gr2.model;

public interface GraphInterface {
	
	public AbstractNode getNode(int id);
	
	public int getSize();
	
	public void addNode(AbstractNode node);
	
	public AbstractNode getRoot();
}
