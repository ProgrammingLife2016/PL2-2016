package nl.tudelft.pl2016gr2.model;

/**
 * This is a data-container class for the metadata of a Node.
 * @author Cas
 *
 */
public class MetaData {
  
  private String bases;
  private int passes;
  private Node node;
  
  /**
   * Constructs a MetaData object from a Node, 
   * it's bases and the number of times the node gets visited (as an example).
   * @param no The node
   * @param bas The bases
   * @param pass The number of passes (could be something else, this is just an example.
   */
  public MetaData(Node no, String bas, int pass) {
    this.node = no;
    this.bases = bas;
    this.passes = pass;    
  }

  public String getBases() {
    return bases;
  }

  public void setBases(String bases) {
    this.bases = bases;
  }

  public int getPasses() {
    return passes;
  }

  public void setPasses(int passes) {
    this.passes = passes;
  }

  public Node getNode() {
    return node;
  }

  public void setNode(Node node) {
    this.node = node;
  }

}
