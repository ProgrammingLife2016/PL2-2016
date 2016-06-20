package nl.tudelft.pl2016gr2.model.graph.nodes;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.data.GraphNodeGuiData;
import nl.tudelft.pl2016gr2.model.metadata.LineageColor;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Consumer;

/**
 * Aides in implementing the <code>GraphNode</code> interface by implementing methods that should
 * have the same behaviour for all nodes.
 *
 * @author Wouter Smit
 */
public abstract class AbstractGraphNode implements GraphNode {

  @TestId(id = "id_field")
  private int identifier;
  private final GraphNodeGuiData guiData = new GraphNodeGuiData();
  private PriorityQueue<Annotation> annotations;
  private LineageColor lineage;

  private HashSet<GraphNode> inEdges;
  private HashSet<GraphNode> outEdges;

  /**
   * Construct a bare abstract node with an ID.
   *
   * @param identifier the id of the node.
   */
  public AbstractGraphNode(int identifier) {
    this.identifier = identifier;
    this.inEdges = new HashSet<>();
    this.outEdges = new HashSet<>();
    this.annotations = new PriorityQueue<>();
  }

  /**
   * Construct an abstract node with an ID and the in/out edges.
   *
   * @param identifier the id of the node.
   * @param inEdges    the in edges of the node.
   * @param outEdges   the out edges of the node.
   */
  public AbstractGraphNode(int identifier, Collection<GraphNode> inEdges,
      Collection<GraphNode> outEdges) {
    this.identifier = identifier;
    this.inEdges = new HashSet<>(inEdges);
    this.outEdges = new HashSet<>(outEdges);
    this.annotations = new PriorityQueue<>();
  }

  /**
   * Constructor to copy an AbstractGraphNode.
   *
   * @param abstractGraphNode the abstractGraphNode to copy.
   */
  protected AbstractGraphNode(AbstractGraphNode abstractGraphNode) {
    this.identifier = abstractGraphNode.identifier;
    this.inEdges = abstractGraphNode.inEdges;
    this.outEdges = abstractGraphNode.outEdges;
    this.annotations = abstractGraphNode.annotations;
  }

  @Override
  public int getId() {
    return identifier;
  }

  @Override
  public Collection<GraphNode> getChildren() {
    return null;
  }

  @Override
  public Collection<Integer> getGenomesOverEdge(GraphNode node) {
    Collection<Integer> genomes = new ArrayList<>();
    Collection<Integer> otherGenomes = new HashSet<>();
    // Mark genomes that are seen in other out edges which appear before the node.
    this.getOutEdges().forEach(outEdge -> {
      if (outEdge.getLevel() < node.getLevel() && !outEdge.equals(node)) {
        otherGenomes.addAll(outEdge.getGenomes());
      }
    });
    List<Integer> thisGenomes = getGenomes();
    node.forEachContainedGenome(thisGenomes, genome -> {
      if (!otherGenomes.contains(genome)) {
        genomes.add(genome);
      }
    });
    return genomes;
  }

  @Override
  public void forEachContainedGenome(List<Integer> genomes, Consumer<Integer> genomeConsumer) {
    List<Integer> thisGenomes = getGenomes();
    int thisIndex = 0;
    int otherIndex = 0;
    while (thisIndex < thisGenomes.size() && otherIndex < genomes.size()) {
      int thisValue = thisGenomes.get(thisIndex);
      int otherValue = genomes.get(otherIndex);
      if (thisValue < otherValue) {
        thisIndex++;
      } else if (thisValue > otherValue) {
        otherIndex++;
      } else {
        thisIndex++;
        otherIndex++;
        genomeConsumer.accept(thisValue);
      }
    }
  }

  @Override
  public int approximateGenomesOverEdge(GraphNode node) {
    return Math.min(getGenomeSize(), node.getGenomeSize());
  }

  @Override
  public Collection<GraphNode> getInEdges() {
    return inEdges;
  }

  @Override
  public void setInEdges(Collection<GraphNode> edges) {
    inEdges = new HashSet<>(edges);
    //inEdges.trimToSize();
  }

  @Override
  public void addAllInEdges(Collection<GraphNode> nodes) {
    inEdges.addAll(nodes);
  }

  @Override
  public void addInEdge(GraphNode node) {
    inEdges.add(node);
  }

  @Override
  public void removeInEdge(GraphNode node) {
    inEdges.remove(node);
  }

  @Override
  public Collection<GraphNode> getOutEdges() {
    return outEdges;
  }

  @Override
  public void setOutEdges(Collection<GraphNode> edges) {
    outEdges = new HashSet<>(edges);
    //outEdges.trimToSize();
  }

  @Override
  public void addAllOutEdges(Collection<GraphNode> nodes) {
    outEdges.addAll(nodes);
  }

  @Override
  public void addOutEdge(GraphNode node) {
    outEdges.add(node);
  }

  @Override
  public void removeOutEdge(GraphNode node) {
    outEdges.remove(node);
  }

  @Override
  public void trimToSize() {
    //inEdges.trimToSize();
    //outEdges.trimToSize();
  }

  @Override
  public String toString() {
    return "id: " + getId();
  }

  @Override
  public int hashCode() {
    return identifier * 37;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !getClass().equals(obj.getClass())) {
      return false;
    }
    return identifier == ((AbstractGraphNode) obj).identifier;
  }

  @Override
  public GraphNodeGuiData getGuiData() {
    return guiData;
  }
  
  protected void setAnnotations(AbstractGraphNode node) {
    this.annotations = node.annotations;
  }

  @Override
  public void addAnnotation(Annotation annotation) {
    annotations.add(annotation);
  }

  @Override
  public boolean hasAnnotations() {
    return !annotations.isEmpty();
  }

  @Override
  public PriorityQueue<Annotation> getAnnotations() {
    return annotations;
  }

  @Override
  public LineageColor getMostFrequentLineage() {
    if (lineage == null) {
      HashMap<LineageColor, Integer> lineageFrequency = new HashMap<>();
      GenomeMap genomeMap = GenomeMap.getInstance();
      for (Integer genome : getGenomes()) {
        LineageColor color = LineageColor.toLineage(genomeMap.getMetadata(genome));
        lineageFrequency.put(color, lineageFrequency.getOrDefault(color, 0) + 1);
      }
      final IntegerProperty maxFreq = new SimpleIntegerProperty(0);
      lineage = LineageColor.NONE;
      lineageFrequency.forEach((LineageColor color, Integer freq) -> {
        if (freq > maxFreq.get()) {
          maxFreq.set(freq);
          lineage = color;
        }
      });
    }
    return lineage;
  }
  
  @Override
  public boolean containsAllGenomes(List<Integer> genomes) {
    List<Integer> thisGenomes = getGenomes();
    int index = 0;
    for (int i = 0; i < thisGenomes.size() && index < genomes.size(); i++) {
      if (thisGenomes.get(i).intValue() == genomes.get(index).intValue()) {
        index++;
      } else if (thisGenomes.get(i) > genomes.get(index)) {
        return false;
      }
    }
    return index == genomes.size();
  }
  
  @Override
  public boolean containsAnyGenome(List<Integer> genomes) {
    List<Integer> thisGenomes = getGenomes();
    int thisIndex = 0;
    int otherIndex = 0;
    while (thisIndex < thisGenomes.size() && otherIndex < genomes.size()) {
      int thisValue = thisGenomes.get(thisIndex);
      int otherValue = genomes.get(otherIndex);
      if (thisValue < otherValue) {
        thisIndex++;
      } else if (thisValue > otherValue) {
        otherIndex++;
      } else {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public boolean hasSameGenomes(List<Integer> genomes) {
    List<Integer> thisGenomes = getGenomes();
    if (thisGenomes.size() != genomes.size()) {
      return false;
    }
    for (int i = 0; i < genomes.size(); i++) {
      if (genomes.get(i).intValue() != thisGenomes.get(i).intValue()) {
        return false;
      }
    }
    return true;
  }
}
