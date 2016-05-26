package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.ImageView;
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
import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.core.algorithms.FilterBubbles;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.GraphOrdererThread;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.OrderedGraph;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.SubgraphAlgorithmManager;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.Bubble;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.PhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;
import nl.tudelft.pl2016gr2.util.Pair;
import nl.tudelft.pl2016gr2.visitor.BubblePhyloVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Set;
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
  private Pane topPane;
  @FXML
  private Pane bottomPane;
  @FXML
  private ScrollBar scrollbar;
  @FXML
  private Rectangle topGraphIndicator;
  @FXML
  private Rectangle bottomGraphIndicator;
  @FXML
  private ImageView deleteBottomGraphImage;
  @FXML
  private Pane topGraphIndicationArea;
  @FXML
  private Pane bottomGraphIndicationArea;

  public static final Color TOP_GRAPH_COLOR = Color.rgb(204, 114, 24);
  public static final Color BOTTOM_GRAPH_COLOR = Color.rgb(24, 114, 204);

  private static final int OFFSCREEN_DRAWN_LEVELS = 10;
  private static final double NODE_X_OFFSET = 50.0;
  private static final double MAX_NODE_RADIUS = 45.0;
  private static final double MIN_NODE_RADIUS = 5.0;
  private static final double MAX_EDGE_WIDTH = 4.0;
  private static final double MIN_EDGE_WIDTH = 0.3;
  private static final double SCROLL_BAR_HEIGHT = 20.0;
  private static final double UNIT_INCREMENT_RATE = 1.0;
  private static final double BLOCK_INCREMENT_RATE = 10.0;
  private static final Color OVERLAP_COLOR = Color.rgb(0, 73, 73);
  private static final Color NO_OVERLAP_COLOR = Color.rgb(146, 0, 0);

  @TestId(id = "topGraph")
  private OrderedGraph topGraph;
  @TestId(id = "bottomGraph")
  private OrderedGraph bottomGraph;
  private ObservableSet<String> topGraphGenomes;
  private ObservableSet<String> bottomGraphGenomes;
  @TestId(id = "amountOfLevels")
  private int amountOfLevels;

  private GraphOrdererThread mainGraphOrder;
  private SequenceGraph mainGraph;

  private ContextMenu contextMenu;

  /**
   * Load this view.
   *
   * @param selectionManager the selection manager.
   * @return the controller of the loaded view.
   */
  public static DrawComparedGraphs loadView(SelectionManager selectionManager) {
    FXMLLoader loader = new FXMLLoader();
    try {
      loader.setLocation(
          DrawComparedGraphs.class.getClassLoader().getResource("pages/CompareGraphsPane.fxml"));
      loader.load();
      DrawComparedGraphs controller = loader.<DrawComparedGraphs>getController();
      controller.setSelectionManager(selectionManager);
      return controller;
    } catch (IOException ex) {
      Logger.getLogger(DrawComparedGraphs.class.getName()).log(Level.SEVERE, null, ex);
    }
    throw new RuntimeException("failed to load the fxml file: " + loader.getLocation());
  }

  /**
   * Get the top genome and bottom genome observable sets from the selection manager.
   *
   * @param selectionManager the selection manager.
   */
  private void setSelectionManager(SelectionManager selectionManager) {
    this.topGraphGenomes = selectionManager.getTopGraphGenomes();
    this.bottomGraphGenomes = selectionManager.getBottomGraphGenomes();
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
   * Initialize the controller class.
   *
   * @param location  unused variable.
   * @param resources unused variable.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    topPane.prefHeightProperty().bind(mainPane.prefHeightProperty().add(-SCROLL_BAR_HEIGHT));
    bottomPane.setVisible(false);
    deleteBottomGraphImage.setVisible(false);
    bottomPane.prefHeightProperty().bind(
        mainPane.prefHeightProperty().add(-SCROLL_BAR_HEIGHT).divide(2.0));

    initializeIndicatorLayout();
    initializeTopDragEvent();
    initializeBottomDragEvent();

    scrollbar.valueProperty().addListener(invalidate -> updateGraph());
    mainPane.widthProperty().addListener(invalidate -> updateGraph());
    mainPane.setOnMouseClicked(event -> {
      if (contextMenu != null) {
        contextMenu.hide();
        contextMenu = null;
      }
    });
  }

  /**
   * Initialize the layout (position, size, visibility, etc) of all of the indicators (view objects
   * which only give static information and don't have any functionality).
   */
  private void initializeIndicatorLayout() {
    topGraphIndicationArea.prefHeightProperty().bind(topPane.prefHeightProperty());
    bottomGraphIndicationArea.prefHeightProperty().bind(bottomPane.prefHeightProperty());
    bottomGraphIndicationArea.visibleProperty().bind(bottomPane.visibleProperty());

    topGraphIndicator.heightProperty().bind(topPane.prefHeightProperty());
    topGraphIndicator.setFill(TOP_GRAPH_COLOR);
    bottomGraphIndicator.heightProperty().bind(bottomPane.prefHeightProperty());
    bottomGraphIndicator.visibleProperty().bind(bottomPane.visibleProperty());
    bottomGraphIndicator.setFill(BOTTOM_GRAPH_COLOR);
  }

  /**
   * Initialize a drag event receiver for the top pane.
   */
  private void initializeTopDragEvent() {
    topPane.setOnDragOver((DragEvent event) -> {
      if (event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.ANY);
      }
      event.consume();
    });
    topPane.setOnDragDropped((DragEvent event) -> {
      Dragboard dragboard = event.getDragboard();
      if (dragboard.hasString()) {
        Collection<String> genomes = new ArrayList<>();
        for (String genome : dragboard.getString().split("\n")) {
          String gen = genome.replace("\r", "");
          if (mainGraph.getGenomes().contains(gen)) {
            genomes.add(gen);
          }
        }
        handleGenomesDropped(genomes, event, topGraphGenomes, bottomGraphGenomes);
        event.setDropCompleted(true);
        event.consume();
      }
    });
  }

  /**
   * Initialize a drag event receiver for the bottom pane.
   */
  private void initializeBottomDragEvent() {
    bottomPane.setOnDragOver((DragEvent event) -> {
      if (event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.ANY);
      }
      event.consume();
    });
    bottomPane.setOnDragDropped((DragEvent event) -> {
      Dragboard dragboard = event.getDragboard();
      if (dragboard.hasString()) {
        Collection<String> genomes = Arrays.asList(dragboard.getString().split("\n"));
        handleGenomesDropped(genomes, event, bottomGraphGenomes, topGraphGenomes);
        event.setDropCompleted(true);
        event.consume();
      }
    });
  }

  /**
   * Handle the event which occurs when a set of genomes is dropped on one of the panes.
   *
   * @param genomes        the genomes which are dropped.
   * @param dragEvent      the drag event.
   * @param genomeMap      the map containing the genomes of the pane which the drop event occured
   *                       on.
   * @param otherGenomeMap the map containing the genomes of other pane (on which the drop event
   *                       didn't occur).
   */
  @SuppressWarnings("fallthrough")
  private void handleGenomesDropped(Collection<String> genomes, DragEvent dragEvent,
      Set<String> genomeMap, Set<String> otherGenomeMap) {
    contextMenu = new ContextMenu();

    switch (getDrawnGraphs()) {
      case 0:
        topGraphGenomes.addAll(genomes);
        redrawGraphs();
        break;
      case 1:
        contextMenu.getItems().add(createCreateNewGraphMenuItem(genomes, otherGenomeMap));
      /* intended fall through */
      case 2:
        contextMenu.getItems().add(createClearAndAddToGraphMenuItem(genomes, genomeMap));
        contextMenu.getItems().add(createAddToExistingGraphMenuItem(genomes, genomeMap));
        if (!Collections.disjoint(genomes, genomeMap)) {
          contextMenu.getItems().add(createRemoveFromGraphMenuItem(genomes, genomeMap));
        }
        contextMenu.show(mainPane, dragEvent.getScreenX(), dragEvent.getScreenY());
        break;
      default:
        throw new AssertionError();
    }
  }

  /**
   * Get the amount of drawn graphs.
   *
   * @return the amount of drawn graphs.
   */
  private int getDrawnGraphs() {
    int drawnGraphs = 0;
    if (!topGraphGenomes.isEmpty()) {
      drawnGraphs++;
      if (!bottomGraphGenomes.isEmpty()) {
        drawnGraphs++;
      }
    }
    return drawnGraphs;
  }

  /**
   * Create a menu item with the text "add to new graph" which performs the action when it is
   * clicked.
   *
   * @param genomes        the list of genomes.
   * @param otherGenomeMap the genome map of the other graph.
   * @return the menu item.
   */
  private MenuItem createCreateNewGraphMenuItem(Collection<String> genomes,
      Set<String> otherGenomeMap) {
    MenuItem newGraphItem = new MenuItem("add to new graph");
    newGraphItem.setOnAction((ActionEvent event) -> {
      otherGenomeMap.addAll(genomes);
      redrawGraphs();
    });
    return newGraphItem;
  }

  /**
   * Create a menu item with the text "clear graph and add", which performs the action when it is
   * clicked.
   *
   * @param genomes   the list of genomes.
   * @param genomeMap the genome map of the graph.
   * @return the menu item.
   */
  private MenuItem createClearAndAddToGraphMenuItem(Collection<String> genomes,
      Set<String> genomeMap) {
    MenuItem clearAndAddItem = new MenuItem("clear graph and add");
    clearAndAddItem.setOnAction((ActionEvent event) -> {
      genomeMap.clear();
      genomeMap.addAll(genomes);
      redrawGraphs();
    });
    return clearAndAddItem;
  }

  /**
   * Create a menu item with the text "add to existing graph", which performs the action when it is
   * clicked.
   *
   * @param genomes   the list of genomes.
   * @param genomeMap the genome map of the graph.
   * @return the menu item.
   */
  private MenuItem createAddToExistingGraphMenuItem(Collection<String> genomes,
      Set<String> genomeMap) {
    MenuItem addToExistingItem = new MenuItem("add to existing graph");
    addToExistingItem.setOnAction((ActionEvent event) -> {
      genomeMap.addAll(genomes);
      redrawGraphs();
    });
    return addToExistingItem;
  }

  /**
   * Create a menu item with the text "remove from graph", which performs the action when it is
   * clicked.
   *
   * @param genomes   the list of genomes.
   * @param genomeMap the genome map of the graph.
   * @return the menu item.
   */
  private MenuItem createRemoveFromGraphMenuItem(Collection<String> genomes,
      Set<String> genomeMap) {
    MenuItem newGraphItem = new MenuItem("remove from graph");
    newGraphItem.setOnAction((ActionEvent event) -> {
      genomeMap.removeAll(genomes);
      redrawGraphs();
    });
    return newGraphItem;
  }

  /**
   * Redraw the graphs.
   */
  private void redrawGraphs() {
    switch (getDrawnGraphs()) {
      case 2:
        compareTwoGraphs();
        break;
      case 1:
        drawOneGraph(topGraphGenomes);
        break;
      default:
        topPane.getChildren().clear();
        bottomPane.getChildren().clear();
        updateGraphSize();
    }
  }
  
  //TODO : REMOVE
  private Tree tree;
  private IPhylogeneticTreeRoot treeRoot;
  private FilterBubbles filter;
  private SequenceGraph curGraph;
  
  private void loadTree() {
     Reader reader = new InputStreamReader(
         GfaReader.class.getClassLoader().getResourceAsStream("10tree_custom.rooted.TKK.nwk"));
     BufferedReader br = new BufferedReader(reader);
     TreeParser tp = new TreeParser(br);
     
     tree = tp.tokenize("10tree_custom.rooted.TKK.nwk");
     treeRoot = new PhylogeneticTreeRoot(tree.getRoot());
   }

  /**
   * Draw the given collection of genomes in the top graph.
   *
   * @param genomes the collection of genomes.
   */
  private void drawOneGraph(Collection<String> genomes) {
//    topGraph = SubgraphAlgorithmManager.alignOneGraph(genomes, mainGraph, mainGraphOrder);
//    ArrayList<NodePosition> topGraphOrder = topGraph.getGraphOrder();
    
    // TODO : remove
    if (treeRoot == null) {
      loadTree();
    }
    if (filter == null) {
      filter = new FilterBubbles(mainGraph);
    }
    
    if (curGraph == null) {
      curGraph = mainGraph;
      curGraph = filter.filter(treeRoot);
    }
    
    GraphOrdererThread o = new GraphOrdererThread(curGraph);
    o.start();
    ArrayList<NodePosition> topGraphOrder = new ArrayList(o.getOrderedGraph().values());
    Collections.sort(topGraphOrder); 
    topGraph = new OrderedGraph(curGraph, topGraphOrder);
    // END OF CHANGED CODE

    amountOfLevels = topGraphOrder.get(topGraphOrder.size() - 1).getLevel();
    scrollbar.setUnitIncrement(UNIT_INCREMENT_RATE / amountOfLevels);
    scrollbar.setBlockIncrement(BLOCK_INCREMENT_RATE / amountOfLevels);
    updateGraphSize();
    updateGraph();
  }

  /**
   * Draw and compare two subgraphs of genomes.
   *
   * @param topGenomes    the genomes of the top graph.
   * @param bottomGenomes the genomes of the bottom graph.
   */
  public void compareTwoGraphs(Collection<String> topGenomes, Collection<String> bottomGenomes) {
    drawOneGraph(mainGraph.getGenomes());
    //    topGraphGenomes.clear();
//    topGraphGenomes.addAll(topGenomes);
//    bottomGraphGenomes.clear();
//    bottomGraphGenomes.addAll(bottomGenomes);
//
//    compareTwoGraphs();
  }

  /**
   * Draw and compare two subgraphs of genomes.
   */
  private void compareTwoGraphs() {
    Pair<OrderedGraph, OrderedGraph> compareRes
        = SubgraphAlgorithmManager.compareTwoGraphs(topGraphGenomes, bottomGraphGenomes, mainGraph,
            mainGraphOrder);
    this.topGraph = compareRes.left;
    this.bottomGraph = compareRes.right;
    drawTwoGraphs();
  }

  /**
   * Draw two graphs to compare.
   */
  private void drawTwoGraphs() {
    ArrayList<NodePosition> topGraphOrder = topGraph.getGraphOrder();
    ArrayList<NodePosition> bottomGraphOrder = bottomGraph.getGraphOrder();
    int highestTopLevel = topGraphOrder.get(topGraphOrder.size() - 1).getLevel();
    int highestBottomLevel = bottomGraphOrder.get(bottomGraphOrder.size() - 1).getLevel();
    if (highestTopLevel > highestBottomLevel) {
      amountOfLevels = highestTopLevel;
    } else {
      amountOfLevels = highestBottomLevel;
    }
    scrollbar.setUnitIncrement(UNIT_INCREMENT_RATE / amountOfLevels);
    scrollbar.setBlockIncrement(BLOCK_INCREMENT_RATE / amountOfLevels);
    updateGraphSize();
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
    double viewPosition = scrollbar.getValue();
    double graphWidth = mainPane.getWidth();
    int levelsToDraw = (int) (graphWidth / NODE_X_OFFSET);
    int startLevel = (int) ((amountOfLevels - levelsToDraw + 2) * viewPosition);
    if (startLevel < 0) {
      startLevel = 0;
    }

    if (topGraph != null) {
      drawGraph(topPane, topGraph.getGraphOrder(), topGraph.getSubgraph(), startLevel,
          startLevel + levelsToDraw);
    }
    if (bottomGraph != null) {
      drawGraph(bottomPane, bottomGraph.getGraphOrder(), bottomGraph.getSubgraph(), startLevel,
          startLevel + levelsToDraw);
    }
  }

  /**
   * Update the size of the graphs. Show both graphs if 2 graphs are drawn, otherwise only show the
   * top graph and resize it to fit the whole screen.
   */
  private void updateGraphSize() {
    if (getDrawnGraphs() < 2) {
      topPane.prefHeightProperty().bind(mainPane.prefHeightProperty().add(-SCROLL_BAR_HEIGHT));
      bottomPane.setVisible(false);
      deleteBottomGraphImage.setVisible(false);
    } else {
      topPane.prefHeightProperty().bind(
          mainPane.prefHeightProperty().add(-SCROLL_BAR_HEIGHT).divide(2.0));
      bottomPane.setVisible(true);
      deleteBottomGraphImage.setVisible(true);
    }
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
  private void drawGraph(Pane pane, ArrayList<NodePosition> graphOrder, SequenceGraph
      graph, int startLevel, int endLevel) {
    pane.getChildren().clear();
    int startIndex = calculateStartIndex(graphOrder, startLevel);
    HashMap<Integer, GraphNodeCircle> circleMap = new HashMap<>();
    int curLevel = startLevel;
    int endIndex;
    ArrayList<NodePosition> levelNodes = new ArrayList<>();
    for (endIndex = startIndex; endIndex < graphOrder.size() && graphOrder.get(
        endIndex).getLevel() <= endLevel + OFFSCREEN_DRAWN_LEVELS; endIndex++) {
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
  private void drawNode(Pane pane, HashMap<Integer, GraphNodeCircle> circleMap,
      ArrayList<NodePosition> nodes, int level, int startLevel) {
    for (int i = 0; i < nodes.size(); i++) {
      NodePosition graphNodeOrder = nodes.get(i);
      GraphNode node = graphNodeOrder.getNode();
      double relativeHeight = (i + 0.5) / nodes.size();
      GraphNodeCircle circle = new GraphNodeCircle(calculateNodeRadius(graphNodeOrder),
          relativeHeight, 0.5 / nodes.size());
      // TODO : REMOVE THIS CODE
      circle.setOnMouseClicked(event -> {
        GraphNode thisNode = graphNodeOrder.getNode();
        curGraph = filter.zoomIn(thisNode, curGraph);
        drawOneGraph(new ArrayList<>());
      });
      circle.setOnScroll(event -> {
        GraphNode thisNode = graphNodeOrder.getNode();
        curGraph = filter.zoomOut(thisNode, curGraph);
        drawOneGraph(new ArrayList<>());
      });
      ///////////////////
      pane.getChildren().add(circle);
      circleMap.put(node.getId(), circle);
      if (graphNodeOrder.isOverlapping()) {
        circle.setFill(OVERLAP_COLOR);
      } else {
        circle.setFill(NO_OVERLAP_COLOR);
      }
      circle.setCenterX(NODE_X_OFFSET * (level + 1 - startLevel));
      circle.centerYProperty().bind(pane.heightProperty().multiply(
          circle.getRelativeHeightProperty()));
      addLabel(pane, circle, node.getId());
    }
  }

  /**
   * Calculate the radius of the node. The radius depends on the amount of bases inside the node.
   * The mapping function from amount of bases to node radius is completely random (hence the magic
   * numbers). It was created by drawing graphs of different functions, till a somewhat nice
   * mapping
   * function was found.
   *
   * @param node the node.
   * @return the radius.
   */
  private static double calculateNodeRadius(NodePosition node) {
    int amountOfBases = node.getNode().size();
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
      SequenceGraph graph, int startIndex, int endIndex,
      HashMap<Integer, GraphNodeCircle> circleMap) {
    for (int i = startIndex; i < endIndex; i++) {
      GraphNode node = graphOrder.get(i).getNode();
      Circle fromCircle = circleMap.get(node.getId());
      for (Integer outlink : node.getOutEdges()) {
        Circle toCircle = circleMap.get(outlink);
        if (toCircle == null) {
          continue;
        }
        Line edge = new Line();
        edge.setSmooth(true);
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
  private static double calculateEdgeWidth(int maxGenomes, GraphNode from, GraphNode to) {
    int genomesOverEdge = from.getGenomesOverEdge(to).size();
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
      int startIndex, int endIndex, HashMap<Integer, GraphNodeCircle> circleMap) {
    for (int i = startIndex; i < endIndex; i++) {
      NodePosition graphNode = graphOrder.get(i);
      GraphNode node = graphNode.getNode();
      GraphNodeCircle circle = circleMap.get(node.getId());
      double subtract = circle.getMaxYOffset();
      while (calculateSameHeightNodes(node, circle, circleMap) >= 2) {
        subtract /= 2.0;
        circle.getRelativeHeightProperty().set(
            circle.getRelativeHeightProperty().doubleValue() - subtract);
      }
    }
  }

  /**
   * Calculate the amount of nodes which are drawn at the same height as the given node.
   *
   * @param node      the given node.
   * @param circle    the circle which represents the node.
   * @param circleMap the map containing all other circles.
   * @return the amount of found nodes (circles) which are at the same height.
   */
  private static int calculateSameHeightNodes(GraphNode node, GraphNodeCircle circle,
      HashMap<Integer, GraphNodeCircle> circleMap) {
    int sameHeight = 0;
    for (Integer inLink : node.getInEdges()) {
      GraphNodeCircle parent = circleMap.get(inLink);
      if (parent != null && Double.compare(parent.getRelativeHeightProperty().doubleValue(),
          circle.getRelativeHeightProperty().doubleValue()) == 0) {
        ++sameHeight;
      }
    }
    return sameHeight;
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
   * This method clears the bottom graph when the cross icon is clicked. It is linked by JavaFX via
   * the fxml file (using reflection), so it appears to be unused to code quality tools. For this
   * reason the suppress warning "unused" annotation is used.
   */
  @FXML
  @SuppressWarnings("unused")
  private void deleteBottomGraph() {
    bottomGraphGenomes.clear();
    redrawGraphs();
  }
}
