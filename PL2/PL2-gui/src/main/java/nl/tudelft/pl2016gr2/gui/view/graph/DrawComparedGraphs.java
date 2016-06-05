package nl.tudelft.pl2016gr2.gui.view.graph;

import javafx.animation.AnimationTimer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.GraphNode;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
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

  public static final double NODE_MARGIN = 0.9;
  private static final double HALF_NODE_MARGIN = 1.0 - (1.0 - NODE_MARGIN) / 2.0;
  private static final double MIN_VISIBILITY_WIDTH = 5.0;
  private static final int MINUMUM_BASE_SIZE = 1;
  private static final double MAX_EDGE_WIDTH = 4.0;
  private static final double MIN_EDGE_WIDTH = 0.04;
  private static final double SCROLL_BAR_HEIGHT = 20.0;
  private static final double UNIT_INCREMENT_RATE = 100.0;
  private static final double BLOCK_INCREMENT_RATE = 1000.0;
  private static final Color OVERLAP_COLOR = Color.rgb(0, 73, 73);
  private static final Color NO_OVERLAP_COLOR = Color.rgb(255, 30, 30);
  private static final double SCROLL_ZOOM_FACTOR = 0.025;
  private static final double MAX_ZOOM_FACTOR = 4.0;
  private static final double BUBBLE_POP_SIZE = 150.0;

  @TestId(id = "topGraph")
  private OrderedGraph topGraph;
  @TestId(id = "bottomGraph")
  private OrderedGraph bottomGraph;
  private ObservableSet<Integer> topGraphGenomes;
  private ObservableSet<Integer> bottomGraphGenomes;
  @TestId(id = "amountOfLevels")
  private final IntegerProperty amountOfLevels = new SimpleIntegerProperty(0);

  private GraphOrdererThread mainGraphOrder;
  private SequenceGraph mainGraph;

  private ContextMenu contextMenu;
  private IPhylogeneticTreeRoot treeRoot;

  private final GraphUpdater graphUpdater = new GraphUpdater(this);

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
    scrollbar.maxProperty().bind(new SimpleDoubleProperty(1.0).add(
        mainPane.widthProperty().negate().divide(
            zoomFactor).divide(amountOfLevels)));
    scrollbar.valueProperty().addListener(invalidate -> graphUpdater.update());
    scrollbar.unitIncrementProperty().bind((new SimpleDoubleProperty(UNIT_INCREMENT_RATE)
        .divide(amountOfLevels)).divide(zoomFactor));
    scrollbar.blockIncrementProperty().bind((new SimpleDoubleProperty(BLOCK_INCREMENT_RATE)
        .divide(amountOfLevels)).divide(zoomFactor));
    scrollbar.visibleAmountProperty().bind(scrollbar.maxProperty().multiply(
        mainPane.widthProperty().divide(zoomFactor)
        .divide(amountOfLevels)));

    mainPane.widthProperty().addListener(invalid -> {
      updateScrollbarValue(scrollbar.getValue());
      verifyZoomFactor();
      graphUpdater.update();
    });
    mainPane.heightProperty().addListener(invalid -> {
      updateScrollbarValue(scrollbar.getValue());
      verifyZoomFactor();
      graphUpdater.update();
    });
    amountOfLevels.addListener(invalid -> verifyZoomFactor());

    mainPane.setOnScroll((ScrollEvent event) -> {
      double relativeXPos = event.getX() / mainPane.getWidth();
      if (event.getDeltaY() > 0) {
        zoomIn(event.getDeltaY(), relativeXPos);
      } else {
        zoomOut(-event.getDeltaY(), relativeXPos);
      }
    });
  }

  private void updateScrollbarValue(double newValue) {
    if (newValue < 0.0) {
      scrollbar.setValue(0.0);
    } else if (newValue > scrollbar.getMax()) {
      scrollbar.setValue(scrollbar.getMax());
    } else {
      scrollbar.setValue(newValue);
    }
  }

  /**
   * Zoom in.
   *
   * @param deltaY the delta Y of the scroll wheel event.
   */
  private void zoomIn(double deltaY, double relativeXPos) {
    if (zoomFactor.get() == MAX_ZOOM_FACTOR) {
      return;
    }
    double zoomMultiplier = (1.0 + SCROLL_ZOOM_FACTOR * deltaY);
    double drawnLevels = mainPane.getWidth() / zoomFactor.get();
    double startLevel = amountOfLevels.get() * scrollbar.getValue();
    double newCenter = relativeXPos - (relativeXPos / zoomMultiplier);
    newCenter *= drawnLevels;
    newCenter += startLevel;
    zoomFactor.set(zoomFactor.get() * zoomMultiplier);
    updateScrollbarValue(newCenter / amountOfLevels.get());
    verifyZoomFactor();
  }

  /**
   * Zoom out.
   *
   * @param deltaY the delta Y of the scroll wheel event.
   */
  private void zoomOut(double deltaY, double relativeXPos) {
    if (zoomFactor.get() == calcMinZoomFactor()) {
      return;
    }
    double zoomMultiplier = (1.0 + SCROLL_ZOOM_FACTOR * deltaY);
    double drawnLevels = mainPane.getWidth() / zoomFactor.get();
    double startLevel = amountOfLevels.get() * scrollbar.getValue();
    double newCenter = relativeXPos - (relativeXPos * zoomMultiplier);
    newCenter *= drawnLevels;
    newCenter += startLevel;

    zoomFactor.set(zoomFactor.get() / zoomMultiplier);
    updateScrollbarValue(newCenter / amountOfLevels.get());
    verifyZoomFactor();
  }

  /**
   * Verify that the zoom factor is not too small or too large.
   */
  private void verifyZoomFactor() {
    if (amountOfLevels.get() <= 0) {
      return;
    }
    double minZoom = calcMinZoomFactor();
    if (zoomFactor.get() > MAX_ZOOM_FACTOR && minZoom < MAX_ZOOM_FACTOR) {
      zoomFactor.set(MAX_ZOOM_FACTOR);
    } else if (zoomFactor.get() < minZoom) {
      zoomFactor.set(minZoom);
    }
    graphUpdater.update();
  }

  private double calcMinZoomFactor() {
    return mainPane.getWidth() / amountOfLevels.get();
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
        Collection<Integer> genomes = new ArrayList<>();
        for (String genome : dragboard.getString().split("\n")) {
          String gen = genome.replace("\r", "");
          Integer genId = GenomeMap.getInstance().getId(gen);
          if (genId != null) {
            genomes.add(genId);
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
        handleGenomesDropped(GenomeMap.getInstance().mapAll(genomes), event, bottomGraphGenomes,
            topGraphGenomes);
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
  private void handleGenomesDropped(Collection<Integer> genomes, DragEvent dragEvent,
      Set<Integer> genomeMap, Set<Integer> otherGenomeMap) {
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
  private MenuItem createCreateNewGraphMenuItem(Collection<Integer> genomes,
      Set<Integer> otherGenomeMap) {
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
  private MenuItem createClearAndAddToGraphMenuItem(Collection<Integer> genomes,
      Set<Integer> genomeMap) {
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
  private MenuItem createAddToExistingGraphMenuItem(Collection<Integer> genomes,
      Set<Integer> genomeMap) {
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
  private MenuItem createRemoveFromGraphMenuItem(Collection<Integer> genomes,
      Set<Integer> genomeMap) {
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

  /**
   * Draw the given collection of genomes in the top graph.
   *
   * @param genomes the collection of genomes.
   */
  private void drawOneGraph(Collection<Integer> genomes) {
    topGraph = SubgraphAlgorithmManager.alignOneGraph(genomes, mainGraph, mainGraphOrder, treeRoot);
    ArrayList<GraphNode> topGraphOrder = topGraph.getGraphOrder();

    amountOfLevels.set(topGraphOrder.get(topGraphOrder.size() - 1).getLevel());
    zoomFactor.set(mainPane.getWidth() / amountOfLevels.get());
    updateGraphSize();
    graphUpdater.update();
  }

  /**
   * Draw and compare two subgraphs of genomes.
   *
   * @param topGenomes    the genomes of the top graph.
   * @param bottomGenomes the genomes of the bottom graph.
   */
  public void compareTwoGraphs(Collection<Integer> topGenomes, Collection<Integer> bottomGenomes) {
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
//    Pair<OrderedGraph, OrderedGraph> compareRes
//        = SubgraphAlgorithmManager.compareTwoGraphs(topGraphGenomes, bottomGraphGenomes, mainGraph,
//            mainGraphOrder);
//    this.topGraph = compareRes.left;
//    this.bottomGraph = compareRes.right;
//    drawTwoGraphs();
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
    zoomFactor.set(mainPane.getWidth() / amountOfLevels.get());
    updateGraphSize();
    graphUpdater.update();
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
    graphUpdater.update();
  }

  /**
   * Update the graph by redrawing it.
   */
  private void updateGraph() {
    double graphWidth = mainPane.getWidth();
    int levelsToDraw = (int) (graphWidth / zoomFactor.get());
    int startLevel = (int) (amountOfLevels.get() * scrollbar.getValue());
    if (startLevel < 0) {
      startLevel = 0;
    }

    if (topGraph != null) {
      long start = System.nanoTime();
      drawGraph(topPane, topGraph, topGraph.getSubgraph().getGenomes().size(),
          startLevel, startLevel + levelsToDraw);
      System.out.println("System.nanoTime() - start = " + (System.nanoTime() - start));
    }
    if (bottomGraph != null) {
      drawGraph(bottomPane, bottomGraph, bottomGraph.getSubgraph().getGenomes().
          size(), startLevel, startLevel + levelsToDraw);
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
  private void drawGraph(Pane pane, OrderedGraph orderedGraph, int genomeCount,
      int startLevel, int endLevel) {
    pane.getChildren().clear();
    HashMap<GraphNode, ViewRange> drawnGraphNodes = new HashMap<>();
    for (GraphNode node : orderedGraph.getGraphOrder()) {
      int nodeStart = node.getLevel() - node.size();
      int nodeEnd = node.getLevel();
      if (nodeStart > endLevel || nodeEnd < startLevel) {
        continue;
      }
      drawNode(pane, node, drawnGraphNodes, startLevel, 0,
          new ViewRange(0, pane.getHeight()));
//          pane.getHeight() * (1.0 - HALF_NODE_MARGIN), pane.getHeight() * NODE_MARGIN);
    }
    drawEdges(pane, drawnGraphNodes, startLevel, genomeCount);
  }

  /**
   * Draw the given list of nodes as circles in the given pane.
   *
   * @param pane       the pane in which to draw the nodes.
   * @param nodes      the list of nodes to draw.
   * @param startLevel the level at which to start drawing nodes.
   */
  private void drawNode(Pane pane, GraphNode node, HashMap<GraphNode, ViewRange> drawnGraphNodes,
      int startLevel, int nestedDepth, ViewRange viewRange) {
    if (node.hasChildren()) {
      constructBubble(pane, node, drawnGraphNodes, node.getLevel(), startLevel, nestedDepth,
          viewRange);
    } else {
      constructNode(pane, node, node.getLevel(), startLevel, nestedDepth, viewRange);
    }
    drawnGraphNodes.put(node, viewRange);
  }

  private void constructNode(Pane pane, GraphNode node, int level, int startLevel, int nestedDepth,
      ViewRange viewRange) {
    double width = calculateNodeWidth(node);
    if (width < MIN_VISIBILITY_WIDTH) {
      return;
    }
    double height = node.getMaxHeightPercentage() * viewRange.rangeHeight;
    if (height > width) {
      height = width;
    }
    ViewGraphNodeCircle circle = new ViewGraphNodeCircle(width, height);
    pane.getChildren().add(circle);
    if (node.isOverlapping()) {
      circle.setFill(OVERLAP_COLOR);
    } else {
      circle.setFill(NO_OVERLAP_COLOR);
    }
    circle.setCenterX(zoomFactor.get() * (level - startLevel - node.size() / 2.0));
    circle.centerYProperty().set(
        viewRange.rangeHeight * node.getRelativeYPos() + viewRange.rangeStartY);
      addLabel(pane, circle, node.getId());
  }

  private void constructBubble(Pane pane, GraphNode bubble,
      HashMap<GraphNode, ViewRange> drawnGraphNodes,
      int level, int startLevel, int nestedDepth, ViewRange viewRange) {
    double width = calculateNodeWidth(bubble);
    if (width < MIN_VISIBILITY_WIDTH) {
      return;
    }
    double height = bubble.getMaxHeightPercentage() * viewRange.rangeHeight;
    if (height > width) {
      height = width;
    }
    ViewGraphNodeRectangle square = new ViewGraphNodeRectangle(width, height);
    pane.getChildren().add(square);
    Color fill = Color.ALICEBLUE;
    for (int i = 0; i < nestedDepth; i++) {
      fill = fill.deriveColor(0.0, 1.0, 0.9, 1.0);
    }
    square.setFill(fill);
    square.centerXProperty().set(zoomFactor.get() * (level - startLevel - bubble.size() / 2.0));
    square.centerYProperty().set(
        viewRange.rangeHeight * bubble.getRelativeYPos() + viewRange.rangeStartY);
      addLabel(pane, square, bubble.getId());
    if (width > BUBBLE_POP_SIZE) {
      ViewRange bubbleViewRange = new ViewRange(
          square.getLayoutY(),
          height);
      drawNestedNodes(pane, bubble, drawnGraphNodes, startLevel, nestedDepth, bubbleViewRange);
    } else {
      bubble.unpop();
    }
  }

  private void drawNestedNodes(Pane pane, GraphNode bubble,
      HashMap<GraphNode, ViewRange> drawnGraphNodes,
      int startLevel, int nestedDepth, ViewRange viewRange) {
    Collection<GraphNode> poppedNodes = bubble.pop();
    for (GraphNode poppedNode : poppedNodes) {
      drawNode(pane, poppedNode, drawnGraphNodes, startLevel, nestedDepth + 1, viewRange);
    }
  }

  /**
   * Calculate the radius of the node. The radius depends on the amount of bases inside the node.
   *
   * @param node the node.
   * @return the radius.
   */
  private double calculateNodeWidth(GraphNode node) {
    int nodeSize = node.size();
    if (nodeSize < MINUMUM_BASE_SIZE) {
      nodeSize = MINUMUM_BASE_SIZE;
    }
    return nodeSize * zoomFactor.get();
  }

  /**
   * Draw the edges between all of the nodes.
   *
   * @param pane            the pane to draw the edges in.
   * @param graphOrder      the graph node order containing the nodes to draw edges between.
   * @param graph           the graph.
   * @param startIndex      the index where to start in the graph.
   * @param endIndex        the index where to end in the graph.
   * @param drawnGraphNodes the drawn graph nodes.
   */
  private void drawEdges(Pane pane, HashMap<GraphNode, ViewRange> drawnGraphNodes, int startLevel,
      int genomeCount) {
    drawnGraphNodes.forEach((GraphNode node, ViewRange range) -> {
      if (!node.isPopped()) {
        for (GraphNode outEdge : node.getOutEdges()) {
          double edgeWidth = calculateEdgeWidth(genomeCount, node, outEdge);
          drawEdge(pane, node, outEdge, edgeWidth, startLevel, range);
        }
        for (GraphNode inEdge : node.getInEdges()) {
          if (!drawnGraphNodes.containsKey(inEdge)) {
            double edgeWidth = calculateEdgeWidth(genomeCount, inEdge, node);
            drawEdge(pane, inEdge, node, edgeWidth, startLevel, range);
          }
        }
      }
    });
  }

  private void drawEdge(Pane pane, GraphNode fromNode, GraphNode toNode, double edgeWidth,
      int startLevel, ViewRange range) {
    Line edge = new Line();
//    edge.setSmooth(true);
    edge.setStrokeWidth(edgeWidth * 2);
    pane.getChildren().add(edge);
    if (fromNode.hasChildren()) {
      edge.setStartX(zoomFactor.get()
          * (fromNode.getLevel() - startLevel /*- fromNode.size() * (1.0 - HALF_NODE_MARGIN)*/));
    } else {
      edge.setStartX(zoomFactor.get()
          * (fromNode.getLevel() - startLevel - fromNode.size() * (1.0 - HALF_NODE_MARGIN)));
    }
    if (toNode.hasChildren()) {
      edge.setEndX(zoomFactor.get()
          * (toNode.getLevel() - startLevel - toNode.size()/* * HALF_NODE_MARGIN*/));
    } else {
      edge.setEndX(zoomFactor.get()
          * (toNode.getLevel() - startLevel - toNode.size() * HALF_NODE_MARGIN));
    }
    edge.setStartY(fromNode.getRelativeYPos() * range.rangeHeight + range.rangeStartY);
    edge.setEndY(toNode.getRelativeYPos() * range.rangeHeight + range.rangeStartY);
//    edge.toBack();
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
  private double calculateEdgeWidth(int maxGenomes, GraphNode from, GraphNode to) {
    int genomesOverEdge = from.getGenomesOverEdge(to).size();
    double edgeWith = Math.log(100.0 * genomesOverEdge / maxGenomes) * 0.8 * zoomFactor.get();
    // see javadoc
    if (edgeWith > MAX_EDGE_WIDTH) {
      return MAX_EDGE_WIDTH;
    } else if (edgeWith < MIN_EDGE_WIDTH) {
      return MIN_EDGE_WIDTH;
    }
    return edgeWith;
  }

  /**
   * Add a label with the ID of the node to the circle.
   *
   * @param pane      the pane to add the label to.
   * @param graphNode the graph node object to which to add the label.
   * @param id        the id to write in the label.
   */
  private static void addLabel(Pane pane, IViewGraphNode graphNode, int id) {
    Label label = new Label(Integer.toString(id));
    label.setMouseTransparent(true);
    label.layoutXProperty().bind(graphNode.centerXProperty().add(-graphNode.getWidth() / 2.0));
    label.layoutYProperty().bind(graphNode.centerYProperty().add(-graphNode.getHeight() / 2.0));
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

  private static class ViewRange {

    private final double rangeStartY;
    private final double rangeHeight;

    private ViewRange(double rangeStartY, double rangeHeight) {
      this.rangeStartY = rangeStartY;
      this.rangeHeight = rangeHeight;
    }
  }

  /**
   * Used to updated the graph at most once every frame.
   */
  private static class GraphUpdater extends AnimationTimer {

    private static final boolean PRINT_FRAME_RATE = false;
    private static final boolean PRINT_MEMORY = false;
    private final DrawComparedGraphs graphComparer;
    private final AtomicBoolean updateGraph = new AtomicBoolean(false);
    private long time = System.nanoTime();
    private int count = 0;

    private GraphUpdater(DrawComparedGraphs graphComparer) {
      this.graphComparer = graphComparer;
      start();
    }

    @Override
    public void handle(long now) {
      if (PRINT_FRAME_RATE) {
        double fps = 1_000_000_000.0 / (now - time);
        System.out.println("FPS = " + fps);
      }
      if (PRINT_MEMORY && count++ % 60 == 0) {
        Runtime runtime = Runtime.getRuntime();
        long bytes = (runtime.totalMemory() - runtime.freeMemory());
        double megabytes = bytes / 1024.0 / 1024.0;
        System.out.println("memory = " + String.format(Locale.US, "%.3f", megabytes) + " MB");
      }
      time = now;
      if (updateGraph.get()) {
        updateGraph.set(false);
        graphComparer.updateGraph();
      }
    }

    public void update() {
      updateGraph.set(true);
    }
  }
}
