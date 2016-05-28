package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.GraphOrdererThread;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.OrderedGraph;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.SubgraphAlgorithmManager;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.model.SequenceNode;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;
import nl.tudelft.pl2016gr2.util.Pair;

import java.io.IOException;
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
  private static final double MIN_VISIBILITY_RADIUS = 2.0;
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
  private final IntegerProperty amountOfLevels = new SimpleIntegerProperty(0);

  private GraphOrdererThread mainGraphOrder;
  private SequenceGraph mainGraph;

  private ContextMenu contextMenu;
  private IPhylogeneticTreeRoot treeRoot;

  private static final double SCROLL_ZOOM_FACTOR = 0.0025;
  private static final double MAX_ZOOM_FACTOR = 2.0;

  private final DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);

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

    initializeScroll();
    mainPane.setOnMouseClicked(event -> {
      if (contextMenu != null) {
        contextMenu.hide();
        contextMenu = null;
      }
    });
  }

  /**
   * Initialize the scrollbar and scroll event.
   */
  private void initializeScroll() {
    scrollbar.valueProperty().addListener(invalidate -> updateGraph());
    scrollbar.unitIncrementProperty().bind(new SimpleDoubleProperty(UNIT_INCREMENT_RATE).divide(
        amountOfLevels).divide(zoomFactor));
    scrollbar.blockIncrementProperty().bind(new SimpleDoubleProperty(BLOCK_INCREMENT_RATE).divide(
        amountOfLevels).divide(zoomFactor));
    scrollbar.visibleAmountProperty().bind(mainPane.widthProperty().divide(NODE_X_OFFSET)
        .divide(zoomFactor).divide(amountOfLevels));

    zoomFactor.addListener(invalid -> verifyZoomFactor());
    mainPane.widthProperty().addListener(invalid -> verifyZoomFactor());
    amountOfLevels.addListener(invalid -> verifyZoomFactor());

    mainPane.setOnScroll((ScrollEvent event) -> {
      double relativePos = event.getX() / mainPane.getWidth();
      if (event.getDeltaY() > 0) {
        updateScrollbarValue(
            scrollbar.getValue() + (relativePos - 0.5) * scrollbar.getVisibleAmount() / 9.0);
        zoomIn(event.getDeltaY());
      } else {
        zoomOut(-event.getDeltaY());
        updateScrollbarValue(
            scrollbar.getValue() + (0.5 - relativePos) * scrollbar.getVisibleAmount() / 9.0);
      }
    });
  }

  private void updateScrollbarValue(double newValue) {
    if (newValue < 0.0) {
      scrollbar.setValue(0.0);
    } else if (newValue > 1.0) {
      scrollbar.setValue(1.0);
    } else {
      scrollbar.setValue(newValue);
    }
  }

  /**
   * Zoom in.
   *
   * @param deltaY the delta Y of the scroll wheel event.
   */
  private void zoomIn(double deltaY) {
    zoomFactor.set(zoomFactor.get() * (1.0 + deltaY * SCROLL_ZOOM_FACTOR));
  }

  /**
   * Zoom out.
   *
   * @param deltaY the delta Y of the scroll wheel event.
   */
  private void zoomOut(double deltaY) {
    zoomFactor.set(zoomFactor.get() / (1.0 + deltaY * SCROLL_ZOOM_FACTOR));
  }

  /**
   * Verify that the zoom factor is not too small or too large.
   */
  private void verifyZoomFactor() {
    if (amountOfLevels.get() <= 0) {
      return;
    }
    if (zoomFactor.get() > MAX_ZOOM_FACTOR) {
      zoomFactor.set(MAX_ZOOM_FACTOR);
    } else if (zoomFactor.get() < mainPane.getWidth() / NODE_X_OFFSET / amountOfLevels.get()) {
      zoomFactor.set(mainPane.getWidth() / NODE_X_OFFSET / amountOfLevels.get());
    }
    updateGraph();
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
        try {
          handleGenomesDropped(genomes, event, topGraphGenomes, bottomGraphGenomes);
        } catch (Exception ex) {
          Logger.getLogger(DrawComparedGraphs.class.getName()).log(Level.SEVERE, null, ex);
        }
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

//  //TODO : REMOVE
//  private Tree tree;
//  private IPhylogeneticTreeRoot treeRoot;
//  private FilterBubbles filter;
//  private SequenceGraph curGraph;
//
//  private void loadTree() {
//    Reader reader = new InputStreamReader(
//        GfaReader.class.getClassLoader().getResourceAsStream("10tree_custom.rooted.TKK.nwk"));
//    BufferedReader br = new BufferedReader(reader);
//    TreeParser tp = new TreeParser(br);
//
//    tree = tp.tokenize("10tree_custom.rooted.TKK.nwk");
//    treeRoot = new PhylogeneticTreeRoot(tree.getRoot());
//  }
  /**
   * Draw the given collection of genomes in the top graph.
   *
   * @param genomes the collection of genomes.
   */
  private void drawOneGraph(Collection<String> genomes) {
    topGraph = SubgraphAlgorithmManager.alignOneGraph(genomes, mainGraph, mainGraphOrder, treeRoot);
    ArrayList<GraphNode> topGraphOrder = topGraph.getGraphOrder();

//    // TODO : remove
//    if (treeRoot == null) {
//      loadTree();
//    }
//    if (filter == null) {
//      filter = new FilterBubbles(mainGraph);
//    }
//
//    if (curGraph == null) {
//      curGraph = mainGraph;
//      curGraph = filter.filter(treeRoot);
//    }
//
//    GraphOrdererThread o = new GraphOrdererThread(curGraph);
//    o.start();
//    ArrayList<NodePosition> topGraphOrder = new ArrayList(o.getOrderedGraph().values());
//    Collections.sort(topGraphOrder);
//    topGraph = new OrderedGraph(curGraph, topGraphOrder);
//    // END OF CHANGED CODE
    amountOfLevels.set(topGraphOrder.get(topGraphOrder.size() - 1).getLevel());
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
    ArrayList<GraphNode> topGraphOrder = topGraph.getGraphOrder();
    ArrayList<GraphNode> bottomGraphOrder = bottomGraph.getGraphOrder();
    int highestTopLevel = topGraphOrder.get(topGraphOrder.size() - 1).getLevel();
    int highestBottomLevel = bottomGraphOrder.get(bottomGraphOrder.size() - 1).getLevel();
    if (highestTopLevel > highestBottomLevel) {
      amountOfLevels.set(highestTopLevel);
    } else {
      amountOfLevels.set(highestBottomLevel);
    }
    updateGraphSize();
    updateGraph();
  }

  /**
   * Load a new main graph.
   *
   * @param graph the graph.
   * @param root  the root of the phylogenetic tree.
   */
  public void loadMainGraph(SequenceGraph graph, IPhylogeneticTreeRoot root) {
    clear();
    treeRoot = root;
    mainGraph = graph;
    mainGraphOrder = new GraphOrdererThread(mainGraph);
    mainGraphOrder.start();
  }

  /**
   * Clear the content of the class.
   */
  private void clear() {
    topGraphGenomes.clear();
    bottomGraphGenomes.clear();
    topPane.getChildren().clear();
    bottomPane.getChildren().clear();
    bottomGraph = null;
    topGraph = null;
    updateGraphSize();
    updateGraph();
  }

  /**
   * Update the graph by redrawing it.
   */
  private void updateGraph() {
    double viewPosition = scrollbar.getValue();
    double graphWidth = mainPane.getWidth();
    int levelsToDraw = (int) (graphWidth / (NODE_X_OFFSET * zoomFactor.get()));
    int startLevel = (int) ((amountOfLevels.get() - levelsToDraw + 2) * viewPosition);
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
  private void drawGraph(Pane pane, ArrayList<GraphNode> graphOrder, SequenceGraph graph,
      int startLevel, int endLevel) {
    pane.getChildren().clear();
    int startIndex = calculateStartIndex(graphOrder, startLevel);
    HashMap<GraphNode, IGraphNode> circleMap = new HashMap<>();
    int curLevel = startLevel;
    int endIndex;
    ArrayList<GraphNode> levelNodes = new ArrayList<>();
    for (endIndex = startIndex; endIndex < graphOrder.size() && graphOrder.get(
        endIndex).getLevel() <= endLevel + OFFSCREEN_DRAWN_LEVELS; endIndex++) {
      GraphNode node = graphOrder.get(endIndex);
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
  private static int calculateStartIndex(ArrayList<GraphNode> graphOrder, int startLevel) {
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
  private static int findStartIndexOfLevel(ArrayList<GraphNode> graphOrder, int level) {
    GraphNode comparer = new SequenceNode(0);
    comparer.setLevel(level);
    int index = Collections.binarySearch(graphOrder, comparer, (GraphNode node1, GraphNode node2)
        -> node1.getLevel() - node2.getLevel()
    );
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
  private void drawNode(Pane pane, HashMap<GraphNode, IGraphNode> graphNodeMap,
      ArrayList<GraphNode> nodes, int level, int startLevel) {
    for (int i = 0; i < nodes.size(); i++) {
      GraphNode node = nodes.get(i);
      double relativeHeight = (i + 0.5) / nodes.size();
      if (node.hasChildren()) {
        constructBubble(pane, graphNodeMap, node, relativeHeight,
            0.5 / nodes.size(), level, startLevel);
      } else {
        constructNode(pane, graphNodeMap, node, relativeHeight, 0.5 / nodes.size(),
            level, startLevel);
      }
    }
  }

  private void constructNode(Pane pane, HashMap<GraphNode, IGraphNode> graphNodeMap,
      GraphNode node, double relativeHeight, double maxYOffset,
      int level, int startLevel) {
    GraphNodeCircle circle = new GraphNodeCircle(calculateNodeRadius(node),
        relativeHeight, maxYOffset);
    pane.getChildren().add(circle);
    graphNodeMap.put(node, circle);
    if (node.isOverlapping()) {
      circle.setFill(OVERLAP_COLOR);
    } else {
      circle.setFill(NO_OVERLAP_COLOR);
    }
    circle.setCenterX(NODE_X_OFFSET * zoomFactor.get() * (level + 1 - startLevel));
    circle.centerYProperty().bind(pane.heightProperty().multiply(
        circle.getRelativeHeightProperty()));
    if (circle.getRadius() < MIN_VISIBILITY_RADIUS) {
      circle.setVisible(false);
    } else {
      addLabel(pane, circle, node.getId());
    }
  }

  private void constructBubble(Pane pane, HashMap<GraphNode, IGraphNode> graphNodeMap,
      GraphNode node, double relativeHeight, double maxYOffset,
      int level, int startLevel) {
    GraphNodeSquare square = new GraphNodeSquare(calculateNodeRadius(node),
        relativeHeight, maxYOffset);
    pane.getChildren().add(square);
    graphNodeMap.put(node, square);
    square.centerXProperty().set(NODE_X_OFFSET * zoomFactor.get() * (level + 1 - startLevel));
    square.centerYProperty().bind(pane.heightProperty().multiply(
        square.getRelativeHeightProperty()));
    if (square.getRadius() < MIN_VISIBILITY_RADIUS) {
      square.setVisible(false);
    } else {
      addLabel(pane, square, node.getId());
    }
  }

//  /**
//   * Calculate the radius of the bubble.
//   *
//   * @param node the node position of the bubble.
//   * @return the radius.
//   */
//  private static double calculateBubbleRadius(NodePosition node) {
//    double radius = Math.sqrt(node.getNode().size()) * 5.0;
//    if (radius > MAX_NODE_RADIUS) {
//      return MAX_NODE_RADIUS;
//    }
//    if (radius < MIN_NODE_RADIUS) {
//      return MIN_NODE_RADIUS;
//    } else {
//      return radius;
//    }
//  }
  /**
   * Calculate the radius of the node. The radius depends on the amount of bases inside the node.
   * The mapping function from amount of bases to node radius is completely random (hence the magic
   * numbers). It was created by drawing graphs of different functions, till a somewhat nice mapping
   * function was found.
   *
   * @param node the node.
   * @return the radius.
   */
  private double calculateNodeRadius(GraphNode node) {
    int amountOfBases = node.size();
    double radius;
    if (amountOfBases > 1000) {
      radius = (Math.log(amountOfBases) * 4.0 - 17.0) * zoomFactor.get(); // see javadoc
      if (radius > MAX_NODE_RADIUS * zoomFactor.get()) {
        return MAX_NODE_RADIUS * zoomFactor.get();
      }
    } else {
      radius = (Math.log(amountOfBases) * 0.7 + 5.1) * zoomFactor.get(); // see javadoc
      if (radius < MIN_NODE_RADIUS * zoomFactor.get()) {
        return MIN_NODE_RADIUS * zoomFactor.get();
      }
    }
    return radius;
  }

  /**
   * Draw the edges between all of the nodes.
   *
   * @param pane         the pane to draw the edges in.
   * @param graphOrder   the graph node order containing the nodes to draw edges between.
   * @param graph        the graph.
   * @param startIndex   the index where to start in the graph.
   * @param endIndex     the index where to end in the graph.
   * @param graphNodeMap a map which maps each node id to the graph node object which represents the
   *                     node in the user interface.
   */
  private static void drawEdges(Pane pane, ArrayList<GraphNode> graphOrder,
      SequenceGraph graph, int startIndex, int endIndex,
      HashMap<GraphNode, IGraphNode> graphNodeMap) {
    for (int i = startIndex; i < endIndex; i++) {
      GraphNode node = graphOrder.get(i);
      IGraphNode fromNode = graphNodeMap.get(node);
      for (GraphNode outlink : node.getOutEdges()) {
        IGraphNode toNode = graphNodeMap.get(outlink);
        if (toNode == null) {
          continue;
        }
        Line edge = new Line();
        edge.setSmooth(true);
        edge.setStrokeWidth(calculateEdgeWidth(graph.getGenomes().size(), node,
            outlink));
        pane.getChildren().add(edge);
        edge.startXProperty().bind(fromNode.centerXProperty());
        edge.startYProperty().bind(fromNode.centerYProperty());
        edge.endXProperty().bind(toNode.centerXProperty());
        edge.endYProperty().bind(toNode.centerYProperty());
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
   * @param graphOrder   the graph node order.
   * @param startIndex   the index where to start in the graph.
   * @param endIndex     the index where to end in the graph.
   * @param graphNodeMap a map which maps each node id to a graph node object.
   */
  private static void repositionOverlappingEdges(ArrayList<GraphNode> graphOrder,
      int startIndex, int endIndex, HashMap<GraphNode, IGraphNode> graphNodeMap) {
    for (int i = startIndex; i < endIndex; i++) {
      GraphNode node = graphOrder.get(i);
      IGraphNode graphNode = graphNodeMap.get(node);
      double subtract = graphNode.getMaxYOffset();
      while (calculateSameHeightNodes(node, graphNode, graphNodeMap) >= 2) {
        subtract /= 2.0;
        graphNode.getRelativeHeightProperty().set(
            graphNode.getRelativeHeightProperty().doubleValue() - subtract);
      }
    }
  }

  /**
   * Calculate the amount of nodes which are drawn at the same height as the given node.
   *
   * @param node         the given node.
   * @param graphNode    the graph node object which represents the node.
   * @param graphNodeMap the map containing all other graph node objects.
   * @return the amount of found nodes (circles) which are at the same height.
   */
  private static int calculateSameHeightNodes(GraphNode node, IGraphNode graphNode,
      HashMap<GraphNode, IGraphNode> graphNodeMap) {
    int sameHeight = 0;
    for (GraphNode inLink : node.getInEdges()) {
      IGraphNode parent = graphNodeMap.get(inLink);
      if (parent != null && Double.compare(parent.getRelativeHeightProperty().doubleValue(),
          graphNode.getRelativeHeightProperty().doubleValue()) == 0) {
        ++sameHeight;
      }
    }
    return sameHeight;
  }

  /**
   * Add a label with the ID of the node to the circle.
   *
   * @param pane      the pane to add the label to.
   * @param graphNode the graph node object to which to add the label.
   * @param id        the id to write in the label.
   */
  private static void addLabel(Pane pane, IGraphNode graphNode, int id) {
    Label label = new Label(Integer.toString(id));
    label.setMouseTransparent(true);
    label.layoutXProperty().bind(graphNode.centerXProperty().add(-graphNode.getRadius() + 3.0));
    label.layoutYProperty().bind(graphNode.centerYProperty().add(-graphNode.getRadius() / 2.0));
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
