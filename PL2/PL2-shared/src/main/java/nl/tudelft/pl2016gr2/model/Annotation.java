package nl.tudelft.pl2016gr2.model;

public class Annotation {
  
  private String referenceGenome;
  private String unknownNull;
  private String tag;
  private int firstInt;
  private int secondInt;
  private double firstDouble;
  private String plusMinus;
  private String point; 
  
  private String name;
  private String displayName;
  private String calhounClass;
  private String id;
  
  public Annotation(String refGenome, String unknownNull, String tag, int firstInt, int secondInt,
      double firstDouble, String plusMinus, String point) {
    this.referenceGenome = refGenome;
    this.unknownNull = unknownNull;
    this.tag = tag;
    this.firstInt = firstInt;
    this.secondInt = secondInt;
    this.plusMinus = plusMinus;
    this.point = point;
  }
  
  public void setProperties(String name, String displayName, String calhounClass, String id) {
    this.name = name;
    this.displayName = displayName;
    this.calhounClass = calhounClass;
    this.id = id;
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(referenceGenome).append('\t');
    sb.append(unknownNull).append('\t');
    sb.append(tag).append('\t');
    sb.append(firstInt).append('\t');
    sb.append(secondInt).append('\t');
    sb.append(firstDouble).append('\t');
    sb.append(plusMinus).append('\t');
    sb.append(point).append('\t');
    sb.append(calhounClass).append('\t');
    sb.append(name).append('\t');
    sb.append(id).append('\t');
    sb.append(displayName).append('\n');
    return sb.toString();
  }

}
