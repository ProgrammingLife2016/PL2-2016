package nl.tudelft.pl2016gr2.core.algorithms.bubbles.graph;

import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class can be used to map annotations on the genomes in the graph and also to find bases in
 * the graph.
 *
 * @author Faris
 */
public class GraphBaseMapper {

  private static final Logger LOGGER = Logger.getLogger(GraphBaseMapper.class.getName());
  private final ArrayList<GraphNode> orderedGraph;

  public GraphBaseMapper(SequenceGraph graph) {
    orderedGraph = graph.getOrderedGraph();
  }

  /**
   * Map the given annotations on the given graph.
   *
   * @param annotations the given annotations.
   */
  public void mapAnnotations(LinkedList<Annotation> annotations) {
    annotations.sort((Annotation first, Annotation second) -> {
      return first.start - second.start;
    });
    HashMap<Integer, GraphNode> nodeMapping = new HashMap<>();
    GenomeMap genomeMap = GenomeMap.getInstance();
    for (Annotation annotation : annotations) {
      Integer genomeId = genomeMap.getId(annotation.sequenceId);
      if (!nodeMapping.containsKey(genomeId)) {
        nodeMapping.put(genomeId, findBase(genomeId, 0));
      }
    }
    mapAnnotations(annotations, nodeMapping, genomeMap);
  }

  /**
   * Map the given annotations on the given graph.
   *
   * @param annotations the given annotations.
   * @param nodeMapping the node map, containing the start node of each genome which is present in
   *                    the list of annotations.
   * @param genomeMap   the genome map.
   */
  private void mapAnnotations(LinkedList<Annotation> annotations,
      HashMap<Integer, GraphNode> nodeMapping, GenomeMap genomeMap) {
    while (!annotations.isEmpty()) {
      String genomeName = annotations.getFirst().sequenceId;
      Integer genomeId = genomeMap.getId(annotations.getFirst().sequenceId);
      GraphNode curNode = nodeMapping.get(genomeId);
      int curBasePosition = 0;
      for (int i = 0; i < annotations.size();) {
        Annotation annotation = annotations.get(i);
        if (!genomeName.equals(annotation.sequenceId) || annotation.start < curBasePosition) {
          i++;
          continue;
        }
        annotations.removeFirst();
        Pair<Integer, GraphNode> nextBase = findBase(genomeId, annotation.start, curBasePosition,
            curNode);
        curNode = nextBase.right;
        curBasePosition = nextBase.left;
        if (nextBase.left == -1) {
          LOGGER.log(Level.WARNING, "Couldn't find base position of annotation: {0}", annotation);
          continue;
        }
        mapAnnotation(curNode, annotation, curBasePosition, genomeId);
      }
    }
  }

  private void mapAnnotation(GraphNode firstNode, Annotation annotation, int nodeStart,
      Integer genomeId) {
    annotation.setStartInGraph(firstNode.getLevel() - firstNode.size()
        + annotation.start - nodeStart);

    firstNode.addAnnotation(annotation);
    int basePosition = nodeStart + firstNode.size();
    GraphNode curNode = firstNode;
    while (basePosition < annotation.end) {
      curNode = getNextNode(curNode, genomeId);
      curNode.addAnnotation(annotation);
      basePosition += curNode.size();
    }
    annotation.setEndInGraph(curNode.getLevel() + annotation.end - basePosition);
  }

  /**
   * Find a base in a genome.
   *
   * @param genome     the genome to search for.
   * @param baseOffset the base offset to find for the given genome.
   * @return the found base.
   */
  public GraphNode findBase(Integer genome, int baseOffset) {
    Iterator<GraphNode> it = orderedGraph.iterator();
    GraphNode firstNode = it.next();
    while (it.hasNext() && !firstNode.getGenomes().contains(genome)) {
      firstNode = it.next();
    }
    return findBase(genome, baseOffset, 0, firstNode).right;
  }

  private Pair<Integer, GraphNode> findBase(Integer genome, int baseOffset, int startOffset,
      GraphNode startNode) {
    int curBaseIndex = startOffset + startNode.size();
    GraphNode curGraphNode = startNode;
    while (curBaseIndex < baseOffset) {
      GraphNode nextNode = getNextNode(curGraphNode, genome);
      if (nextNode == null || nextNode.equals(curGraphNode)) {
        return new Pair<>(-1, curGraphNode);
      }
      curBaseIndex += nextNode.size();
      curGraphNode = nextNode;
    }
    return new Pair<>(curBaseIndex - curGraphNode.size(), curGraphNode);
  }

  private GraphNode getNextNode(GraphNode curGraphNode, Integer genome) {
    GraphNode nextNode = null;
    int nextLevel = Integer.MAX_VALUE;
    for (GraphNode outEdge : curGraphNode.getOutEdges()) {
      if (outEdge.getLevel() < nextLevel && outEdge.getGenomes().contains(genome)) {
        nextLevel = outEdge.getLevel();
        nextNode = outEdge;
      }
    }
    return nextNode;
  }
}
