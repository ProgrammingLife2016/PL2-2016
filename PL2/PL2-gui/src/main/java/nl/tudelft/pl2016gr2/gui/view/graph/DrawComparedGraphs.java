package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import nl.tudelft.pl2016gr2.core.algorithms.CompareSubgraphs;
import nl.tudelft.pl2016gr2.core.algorithms.GraphOrdererThread;
import nl.tudelft.pl2016gr2.core.algorithms.SplitGraphs;
import nl.tudelft.pl2016gr2.gui.view.RootLayoutController;
import nl.tudelft.pl2016gr2.gui.view.events.GraphicsChangedEvent;
import nl.tudelft.pl2016gr2.model.AbstractNode;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;
import nl.tudelft.pl2016gr2.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Draws two compared graphs above each other in a pane.
 *
 * @author Faris
 */
public class DrawComparedGraphs implements Initializable {

  @FXML
  private AnchorPane mainPane;
  @FXML
  private Pane bottomPane;
  @FXML
  private Pane topPane;
  @FXML
  private ScrollBar scrollbar;
  @FXML
  private Rectangle topGraphIndicator;
  @FXML
  private Rectangle bottomGraphIndicator;

  public static final Color TOP_GRAPH_COLOR = Color.rgb(204, 114, 24);
  public static final Color BOTTOM_GRAPH_COLOR = Color.rgb(24, 114, 204);

  private static final int OFFSCREEN_DRAWN_LEVELS = 10;
  private static final double X_OFFSET = 50.0;
  private static final double MAX_NODE_RADIUS = 45.0;
  private static final double MIN_NODE_RADIUS = 5.0;
  private static final double MAX_EDGE_WIDTH = 4.0;
  private static final double MIN_EDGE_WIDTH = 0.3;
  private static final double UNIT_INCREMENT_RATE = 1.0;
  private static final double BLOCK_INCREMENT_RATE = 10.0;
  private static final Color OVERLAP_COLOR = Color.rgb(0, 73, 73);
  private static final Color NO_OVERLAP_COLOR = Color.rgb(146, 0, 0);

  private OriginalGraph topGraph;
  private OriginalGraph bottomGraph;
  @TestId(id = "topGraphOrder")
  private ArrayList<NodePosition> topGraphOrder;
  @TestId(id = "bottomGraphOrder")
  private ArrayList<NodePosition> bottomGraphOrder;
  @TestId(id = "amountOfLevels")
  private int amountOfLevels;

  private GraphOrdererThread mainGraphOrder;
  private OriginalGraph mainGraph;

  /**
   * Load this view.
   *
   * @return the controller of the loaded view.
   */
  public static DrawComparedGraphs loadView() {
    FXMLLoader loader = new FXMLLoader();
    try {
      loader.setLocation(DrawComparedGraphs.class.getClassLoader()
          .getResource("pages/CompareGraphsPane.fxml"));
      loader.load();
      return loader.<DrawComparedGraphs>getController();
    } catch (IOException ex) {
      Logger.getLogger(DrawComparedGraphs.class.getName()).log(Level.SEVERE, null, ex);
    }
    throw new RuntimeException("failed to load the fxml file: " + loader.getLocation());
  }

  /**
   * Initialize the controller class.
   *
   * @param location  unused variable.
   * @param resources unused variable.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    topPane.prefHeightProperty().bind(mainPane.heightProperty().divide(2.0));
    bottomPane.prefHeightProperty().bind(mainPane.heightProperty().divide(2.0));

    topGraphIndicator.heightProperty().bind(mainPane.heightProperty().divide(2.0));
    topGraphIndicator.setFill(TOP_GRAPH_COLOR);
    bottomGraphIndicator.heightProperty().bind(mainPane.heightProperty().divide(2.0));
    bottomGraphIndicator.setFill(BOTTOM_GRAPH_COLOR);

    scrollbar.valueProperty().addListener(invalidate -> updateGraph());
    mainPane.widthProperty().addListener(invalidate -> updateGraph());

    initializeDragEvent();
  }

  /**
   * Initialize a drag event receiver.
   */
  private void initializeDragEvent() {
    mainPane.setOnDragOver((DragEvent event) -> {
      if (event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.ANY);
      }
      event.consume();
    });
    mainPane.setOnDragDropped((DragEvent event) -> {
      Dragboard dragboard = event.getDragboard();
      if (dragboard.hasString()) {
        System.out.println(dragboard.getString());
        System.out.println("");
        event.setDropCompleted(true);
        event.consume();
      }
    });
  }

  /**
   * Get the pane in which the graphs are drawn.
   *
   * @return the pane in which the graphs are drawn.
   */
  public Region getGraphPane() {
    return mainPane;
  }

  /**
   * Draw two subgraphs.
   *
   * @param topGenomes    the genomes of the top graph.
   * @param bottomGenomes the genomes of the bottom graph.
   */
  public void drawGraphs(ArrayList<String> topGenomes, ArrayList<String> bottomGenomes) {

    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        topGenomes);
    SplitGraphsThread bottomSubGraphThread = new SplitGraphsThread(new SplitGraphs(mainGraph),
        bottomGenomes);
    topSubGraphThread.start();
    bottomSubGraphThread.start();
    OriginalGraph topSubgraph = topSubGraphThread.getSubGraph();
    OriginalGraph bottomSubgraph = bottomSubGraphThread.getSubGraph();
    Pair<ArrayList<NodePosition>, ArrayList<NodePosition>> alignedGraphs
        = CompareSubgraphs.compareGraphs(mainGraphOrder.getOrderedGraph(), topSubgraph,
            bottomSubgraph);
    drawGraphs(topSubgraph, bottomSubgraph, alignedGraphs.left, alignedGraphs.right);
  }

  /**
   * Draw two graphs to compare.
   *
   * @param topGraph         the top graph.
   * @param bottomGraph      the bottom graph
   * @param topGraphOrder    the order of the top graph.
   * @param bottomGraphOrder the order of the bottom graph.
   */
  private void drawGraphs(OriginalGraph topGraph, OriginalGraph bottomGraph,
      ArrayList<NodePosition> topGraphOrder, ArrayList<NodePosition> bottomGraphOrder) {
    this.topGraph = topGraph;
    this.bottomGraph = bottomGraph;
    this.topGraphOrder = topGraphOrder;
    this.bottomGraphOrder = bottomGraphOrder;
    int highestTopLevel = topGraphOrder.get(topGraphOrder.size() - 1).getLevel();
    int highestBottomLevel = bottomGraphOrder.get(bottomGraphOrder.size() - 1).getLevel();
    if (highestTopLevel > highestBottomLevel) {
      amountOfLevels = highestTopLevel;
    } else {
      amountOfLevels = highestBottomLevel;
    }
    scrollbar.setUnitIncrement(UNIT_INCREMENT_RATE / amountOfLevels);
    scrollbar.setBlockIncrement(BLOCK_INCREMENT_RATE / amountOfLevels);
    updateGraph();
  }

  /**
   * Load a new main graph.
   *
   * @param filename the filename of the graph.
   */
  public void loadMainGraph(String filename) {
    mainGraph = new GfaReader(filename).read();
    mainGraphOrder = new GraphOrdererThread(mainGraph);
    mainGraphOrder.start();
  }

  /**
   * Update the graph by redrawing it.
   */
  private void updateGraph() {
    if (topGraphOrder == null || bottomGraphOrder == null) {
      return;
    }
    double viewPosition = scrollbar.getValue();
    double graphWidth = mainPane.getWidth();
    int levelsToDraw = (int) (graphWidth / X_OFFSET);
    int startLevel = (int) ((amountOfLevels - levelsToDraw + 2) * viewPosition);
    if (startLevel < 0) {
      startLevel = 0;
    }
    drawGraph(topPane, topGraphOrder, topGraph, startLevel, startLevel + levelsToDraw);
    drawGraph(bottomPane, bottomGraphOrder, bottomGraph, startLevel, startLevel + levelsToDraw);
    mainPane.fireEvent(new GraphicsChangedEvent());
  }

  /**
   * Draw the given graph in the given pane.
   *
   * @param pane       the pane to draw the graph in.
   * @param graphOrder the graph node order.
   * @param graph      the graph.
   * @param startLevel the level where to start drawing.
   * @param endLevel   the level where to stop drawing.
   */
  private static void drawGraph(Pane pane, ArrayList<NodePosition> graphOrder,
      OriginalGraph graph, int startLevel, int endLevel) {
    pane.getChildren().clear();
    int startIndex = calculateStartIndex(graphOrder, startLevel);
    HashMap<Integer, NodeCircle> circleMap = new HashMap<>();
    int curLevel = startLevel;
    int endIndex;
    ArrayList<NodePosition> levelNodes = new ArrayList<>();
    for (endIndex = startIndex; endIndex < graphOrder.size()
        && graphOrder.get(endIndex).getLevel() <= endLevel + OFFSCREEN_DRAWN_LEVELS; endIndex++) {
      NodePosition node = graphOrder.get(endIndex);
      if (node.getLevel() == curLevel) {
        levelNodes.add(node);
      } else {
        drawNode(pane, circleMap, levelNodes, curLevel, startLevel);
        curLevel = node.getLevel();
        levelNodes.clear();
        levelNodes.add(node);
      }
    }
    drawNode(pane, circleMap, levelNodes, curLevel, startLevel);
    repositionOverlappingEdges(graphOrder, startIndex, endIndex, circleMap);
    drawEdges(pane, graphOrder, graph, startIndex, endIndex, circleMap);
  }

  /**
   * Calculates the starting index (where to start in the graph with drawing). Lowers the start
   * index by {@link #OFFSCREEN_DRAWN_LEVELS} to keep some margin at the left of the screen (so the
   * edges are drawn correctly)
   *
   * @param graphOrder the graph node order.
   * @param startLevel the start level.
   * @return the start index.
   */
  private static int calculateStartIndex(ArrayList<NodePosition> graphOrder, int startLevel) {
    int actualStartLevel = startLevel - OFFSCREEN_DRAWN_LEVELS;
    if (actualStartLevel < 0) {
      actualStartLevel = 0;
    }
    return findStartIndexOfLevel(graphOrder, actualStartLevel);
  }

  /**
   * Finds the first node with the given level in log(n) time (graph must be sorted by level).
   *
   * @param graphOrder the graph node order in which to search.
   * @param level      the level to find.
   * @return the index of the first occurence of the level.
   */
  private static int findStartIndexOfLevel(ArrayList<NodePosition> graphOrder, int level) {
    NodePosition comparer = new NodePosition(null, level);
    int index = Collections.binarySearch(graphOrder, comparer);
    while (index > 0 && graphOrder.get(index - 1).getLevel() == level) {
      --index;
    }
    if (index < 0) {
      index = -index - 1;
    }
    return index;
  }

  /**
   * Draw the given list of nodes as circles in the given pane.
   *
   * @param pane       the pane in which to draw the nodes.
   * @param circleMap  the circle map to which to add all of the drawn circles (which represent the
   *                   nodes).
   * @param nodes      the list of nodes to draw.
   * @param level      the level in the tree at which to draw the nodes.
   * @param startLevel the level at which to start drawing nodes.
   */
  private static void drawNode(Pane pane, HashMap<Integer, NodeCircle> circleMap,
      ArrayList<NodePosition> nodes, int level, int startLevel) {
    for (int i = 0; i < nodes.size(); i++) {
      NodePosition graphNodeOrder = nodes.get(i);
      AbstractNode node = graphNodeOrder.getNode();
      double relativeHeight = (i + 0.5) / nodes.size();
      NodeCircle circle = new NodeCircle(calculateNodeRadius(graphNodeOrder), relativeHeight,
          0.5 / nodes.size());
      pane.getChildren().add(circle);
      circleMap.put(node.getId(), circle);
      if (graphNodeOrder.isOverlapping()) {
        circle.setFill(OVERLAP_COLOR);
      } else {
        circle.setFill(NO_OVERLAP_COLOR);
      }
      circle.setCenterX(X_OFFSET * (level + 1 - startLevel));
      circle.centerYProperty().bind(pane.heightProperty().multiply(
          circle.getRelativeHeightProperty()));
      addLabel(pane, circle, node.getId());
    }
  }

  /**
   * Calculate the radius of the node. The radius depends on the amount of bases inside the node.
   * The mapping function from amount of bases to node radius is completely random (hence the magic
   * numbers). It was created by drawing graphs of different functions, till a somewhat nice mapping
   * function was found.
   *
   * @param node the node.
   * @return the radius.
   */
  private static double calculateNodeRadius(NodePosition node) {
    int amountOfBases = node.getNode().getSequenceLength();
    double radius;
    if (amountOfBases > 1000) {
      radius = Math.log(amountOfBases) * 4.0 - 17.0; // see javadoc
      if (radius > MAX_NODE_RADIUS) {
        return MAX_NODE_RADIUS;
      }
    } else {
      radius = Math.log(amountOfBases) * 0.7 + 5.1; // see javadoc
      if (radius < MIN_NODE_RADIUS) {
        return MIN_NODE_RADIUS;
      }
    }
    return radius;
  }

  /**
   * Draw the edges between all of the nodes.
   *
   * @param pane       the pane to draw the edges in.
   * @param graphOrder the graph node order containing the nodes to draw edges between.
   * @param graph      the graph.
   * @param startIndex the index where to start in the graph.
   * @param endIndex   the index where to end in the graph.
   * @param circleMap  a map which maps each node id to the circle which represents the node in the
   *                   user interface.
   */
  private static void drawEdges(Pane pane, ArrayList<NodePosition> graphOrder,
      OriginalGraph graph, int startIndex, int endIndex, HashMap<Integer, NodeCircle> circleMap) {
    for (int i = startIndex; i < endIndex; i++) {
      AbstractNode node = graphOrder.get(i).getNode();
      Circle fromCircle = circleMap.get(node.getId());
      for (Integer outlink : node.getOutlinks()) {
        Circle toCircle = circleMap.get(outlink);
        if (toCircle == null) {
          continue;
        }
        Line edge = new Line();
        edge.setStrokeWidth(calculateEdgeWidth(graph.getGenomes().size(), node,
            graph.getNode(outlink)));
        pane.getChildren().add(edge);
        edge.startXProperty().bind(fromCircle.centerXProperty());
        edge.startYProperty().bind(fromCircle.centerYProperty());
        edge.endXProperty().bind(toCircle.centerXProperty());
        edge.endYProperty().bind(toCircle.centerYProperty());
        edge.toBack();
      }
    }
  }

  /**
   * Calculate the edge width. The amount of genomes over an edge to edge width mapping function is
   * completely random (hence the magic numbers). It was created by drawing graphs of different
   * functions, till a somewhat nice mapping function was found.
   *
   * @param maxGenomes the total amount of genomes in the graph.
   * @param from       the node from which the edge comes.
   * @param to         the node to which the edge goes.
   * @return the edge width.
   */
  public static double calculateEdgeWidth(int maxGenomes, AbstractNode from, AbstractNode to) {
    int genomesOverEdge = from.getGenomesOverEdge(to);
    double edgeWith = Math.log(100.0 * genomesOverEdge / maxGenomes) * 0.8; // see javadoc
    if (edgeWith > MAX_EDGE_WIDTH) {
      return MAX_EDGE_WIDTH;
    } else if (edgeWith < MIN_EDGE_WIDTH) {
      return MIN_EDGE_WIDTH;
    }
    return edgeWith;
  }

  /**
   * Reposition the nodes, so there are no overlapping (horizontal) edges. It is still possible for
   * non-horizontal edges to overlap, but this rarely happens.
   *
   * @param graphOrder the graph node order.
   * @param startIndex the index where to start in the graph.
   * @param endIndex   the index where to end in the graph.
   * @param circleMap  a map which maps each node id to a circle.
   */
  private static void repositionOverlappingEdges(ArrayList<NodePosition> graphOrder,
      int startIndex, int endIndex, HashMap<Integer, NodeCircle> circleMap) {
    for (int i = startIndex; i < endIndex; i++) {
      NodePosition graphNode = graphOrder.get(i);
      AbstractNode node = graphNode.getNode();
      NodeCircle circle = circleMap.get(node.getId());
      double subtract = circle.getMaxYOffset();
      while (true) {
        subtract /= 2.0;
        int sameHeight = 0;
        for (Integer inLink : node.getInlinks()) {
          NodeCircle parent = circleMap.get(inLink);
          if (parent != null && Double.compare(parent.getRelativeHeightProperty().doubleValue(),
              circle.getRelativeHeightProperty().doubleValue()) == 0) {
            ++sameHeight;
          }
        }
        if (sameHeight < 2) {
          break;
        }
        circle.getRelativeHeightProperty().set(
            circle.getRelativeHeightProperty().doubleValue() - subtract);
      }
    }
  }

  /**
   * Add a label with the ID of the node to the circle.
   *
   * @param pane   the pane to add the label to.
   * @param circle the circle to which to add the label.
   * @param id     the id to write in the label.
   */
  private static void addLabel(Pane pane, Circle circle, int id) {
    Label label = new Label(Integer.toString(id));
    label.setMouseTransparent(true);
    label.layoutXProperty().bind(circle.centerXProperty().add(-circle.getRadius() + 3.0));
    label.layoutYProperty().bind(circle.centerYProperty().add(-circle.getRadius() / 2.0));
    label.setTextFill(Color.ALICEBLUE);
    pane.getChildren().add(label);
  }

  /**
   * Thread which is used to get a graph of a subset of the genomes from a graph.
   */
  private class SplitGraphsThread extends Thread {

    private OriginalGraph subGraph;
    private final SplitGraphs splitGraphs;
    private final ArrayList<String> genomes;

    /**
     * Construct a split graph thread. Subtracts a subgraph from the given graph, containing all of
     * the given genomes.
     *
     * @param splitGraphs a {@link SplitGraphs} object.
     * @param genomes     the list of genomes which must be present in the subgraph.
     */
    private SplitGraphsThread(SplitGraphs splitGraphs, ArrayList<String> genomes) {
      this.splitGraphs = splitGraphs;
      this.genomes = genomes;
    }

    /**
     * Wait till the thread completes its execution and get the subgraph.
     *
     * @return the subgraph.
     */
    public OriginalGraph getSubGraph() {
      try {
        this.join();
      } catch (InterruptedException ex) {
        Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
      }
      return subGraph;
    }

    /**
     * Subtract a subgraph from the given graph, containing all of the given genomes.
     */
    @Override
    public void run() {
      subGraph = splitGraphs.getSubgraph(genomes);
    }
  }
}
