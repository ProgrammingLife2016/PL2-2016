package nl.tudelft.pl2016gr2.gui.view.graph;

import com.sun.javafx.collections.ObservableSetWrapper;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.NumberBinding;
import javafx.beans.binding.When;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
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
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.GraphOrdererThread;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.OrderedGraph;
import nl.tudelft.pl2016gr2.core.algorithms.subgraph.SubgraphAlgorithmManager;
import nl.tudelft.pl2016gr2.gui.view.graph.heatmap.GraphBubbleDensity;
import nl.tudelft.pl2016gr2.gui.view.graph.heatmap.IHeatmapColorer;
import nl.tudelft.pl2016gr2.gui.view.graph.heatmap.IndelDensity;
import nl.tudelft.pl2016gr2.gui.view.graph.heatmap.MutationDensity;
import nl.tudelft.pl2016gr2.gui.view.graph.heatmap.OverlapHeatmap;
import nl.tudelft.pl2016gr2.gui.view.graph.heatmap.PhyloBubbleDensity;
import nl.tudelft.pl2016gr2.gui.view.graph.heatmap.PointMutationDensity;
import nl.tudelft.pl2016gr2.gui.view.graph.heatmap.StraightSequenceDensity;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.Settings;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.graph.data.GraphViewRange;
import nl.tudelft.pl2016gr2.model.graph.nodes.GraphNode;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;
import nl.tudelft.pl2016gr2.util.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Draws two compared graphs above each other in a pane.
 *
 * @author Faris
 */
public class GraphPaneController implements Initializable {

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
  @FXML
  private Canvas topEdgeCanvas;
  @FXML
  private Canvas bottomEdgeCanvas;
  @FXML
  private Label showingRange;
  @FXML
  private Label totalBases;
  @FXML
  private Canvas heatmap;
  @FXML
  private ComboBox<String> heatmapChooser;
  @FXML
  private Label heatmapLabel;

  public static final double NODE_MARGIN = 0.9;
  private static final double HALF_NODE_MARGIN = 1.0 - (1.0 - NODE_MARGIN) / 2.0;
  private static final double MIN_VISIBILITY_WIDTH = 5.0;
  public static final int MINUMUM_BASE_SIZE = 150;
  private static final double MAX_EDGE_WIDTH = 4.0;
  private static final double MIN_EDGE_WIDTH = 0.1;
  private static final double SCROLL_BAR_HEIGHT = 20.0;
  private static final double UNIT_INCREMENT_RATE = 100.0;
  private static final double BLOCK_INCREMENT_RATE = 1000.0;
  private static final double SCROLL_ZOOM_FACTOR = 0.0025;
  private static final double MAX_ZOOM_FACTOR = 180.0;
  private static final double BUBBLE_POP_SIZE = 150.0;
  private static final double GRAPH_HEATMAP_HEIGHT = 20.0;
  private static final double DECENT_NODE_WIDTH = 30.0;
  public static final double HALF_HEATMAP_HEIGHT = GRAPH_HEATMAP_HEIGHT / 2.0;

  private ContextMenu contextMenu;
  private IPhylogeneticTreeRoot<?> treeRoot;
  private final GraphUpdater graphUpdater = new GraphUpdater(this);

  private GraphOrdererThread mainGraphOrder;
  private SequenceGraph mainGraph;

  @TestId(id = "topGraph")
  private OrderedGraph topGraph;
  @TestId(id = "bottomGraph")
  private OrderedGraph bottomGraph;
  private SelectionManager selectionManager;
  @TestId(id = "amountOfLevels")
  private final IntegerProperty amountOfLevels = new SimpleIntegerProperty(0);
  private final DoubleProperty zoomFactor = new SimpleDoubleProperty(1.0);

  private final ObservableSet<Integer> topGraphGenomes
      = new ObservableSetWrapper<>(new HashSet<>());
  private final ObservableSet<Integer> bottomGraphGenomes
      = new ObservableSetWrapper<>(new HashSet<>());

  private IHeatmapColorer heatmapColorer = (node, start) -> {
  };

  private final Map<String, IHeatmapColorer> heatmapOptions = new LinkedHashMap<>();

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
    topPane.prefHeightProperty().bind(mainPane.prefHeightProperty().add(-SCROLL_BAR_HEIGHT
        - GRAPH_HEATMAP_HEIGHT));
    bottomPane.setVisible(false);
    deleteBottomGraphImage.setVisible(false);
    bottomPane.prefHeightProperty().bind(
        mainPane.prefHeightProperty().add(-SCROLL_BAR_HEIGHT - GRAPH_HEATMAP_HEIGHT).divide(2.0));

    initializeIndicatorLayout();
    initializeTopDragOverEvent();
    initializeTopDragDroppedEvent();
    initializeBottomDragEvent();

    initializeScrollbar();
    initializeScrollEvent();
    initializeOnMouseEventHandler();
    initializeBaseLabels();
    initializeHeatmap();
    Settings.getInstance().addListener(invalid -> redrawGraphs());
  }

  /**
   * Initialize the graph heatmap.
   */
  private void initializeHeatmap() {
    addHeatmapOptions();
    heatmap.setHeight(GRAPH_HEATMAP_HEIGHT);
    heatmap.widthProperty().bind(mainPane.widthProperty());
    heatmapOptions.forEach((String name, IHeatmapColorer heatmap) -> {
      heatmapChooser.getItems().add(name);
    });
    heatmapChooser.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
      if (newValue.length() > 30) {
        heatmapLabel.setText(newValue.substring(0, 30) + "...");
      } else {
        heatmapLabel.setText(newValue);
      }
      heatmapColorer = heatmapOptions.get(newValue);
      graphUpdater.update();
    });
  }

  /**
   * Add all of the possible heatmap options.
   */
  private void addHeatmapOptions() {
    GraphicsContext heatmapGraphics = heatmap.getGraphicsContext2D();
    heatmapOptions.put("Amount of mutations (parralel paths in the graph)",
        new MutationDensity(heatmapGraphics, zoomFactor));
    heatmapOptions.put("Point mutation bubbels",
        new PointMutationDensity(heatmapGraphics, zoomFactor));
    heatmapOptions.put("Indel bubbels", new IndelDensity(heatmapGraphics, zoomFactor));
    heatmapOptions.put("Straight sequences bubbels",
        new StraightSequenceDensity(heatmapGraphics, zoomFactor));
    heatmapOptions.put("Phylogenetic bubbles", new PhyloBubbleDensity(heatmapGraphics, zoomFactor));
    heatmapOptions.put("Graph based bubbles", new GraphBubbleDensity(heatmapGraphics, zoomFactor));
    heatmapOptions.put("Equalities between the graphs (dark = equal, light = not equal",
        new OverlapHeatmap(heatmapGraphics, zoomFactor));
  }

  /**
   * Initialize the position and content of the labels which show the range of bases which is
   * currently shown in the user interface.
   */
  private void initializeBaseLabels() {
    totalBases.textProperty().bind(amountOfLevels.asString(Locale.US, "%,d"));
    NumberBinding startLevel = amountOfLevels.multiply(scrollbar.valueProperty());

    NumberBinding endLevel = mainPane.widthProperty().divide(zoomFactor).add(startLevel);
    NumberBinding actualEndLevel
        = new When(endLevel.lessThan(amountOfLevels)).then(endLevel).otherwise(amountOfLevels);
    showingRange.textProperty().bind(startLevel.asString(Locale.US, "%,.0f")
        .concat(" - ").concat(actualEndLevel.asString(Locale.US, "%,.0f")));
    showingRange.translateXProperty().bind(
        mainPane.widthProperty().add(showingRange.widthProperty().negate()).divide(2.0));
  }

  /**
   * Initialize the mouse handlers.
   */
  private void initializeOnMouseEventHandler() {
    mainPane.setOnMouseClicked(event -> {
      if (contextMenu != null) {
        contextMenu.hide();
        contextMenu = null;
      }
    });
    initializeEdgeCanvas();
  }

  /**
   * Initialize the scrollbar and pane width listeners.
   */
  private void initializeScrollbar() {
    initializeScrollbarListeners();

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
  }

  /**
   * Initialize the scrollbar listeners.
   */
  private void initializeScrollbarListeners() {
    scrollbar.maxProperty().bind(new SimpleDoubleProperty(1.0).add(
        mainPane.widthProperty().negate().divide(zoomFactor).divide(amountOfLevels)));
    scrollbar.valueProperty().addListener(invalidate -> graphUpdater.update());
    scrollbar.unitIncrementProperty().bind(new SimpleDoubleProperty(UNIT_INCREMENT_RATE)
        .divide(amountOfLevels).divide(zoomFactor));
    scrollbar.blockIncrementProperty().bind(new SimpleDoubleProperty(BLOCK_INCREMENT_RATE)
        .divide(amountOfLevels).divide(zoomFactor));
    scrollbar.visibleAmountProperty().bind(scrollbar.maxProperty().multiply(
        mainPane.widthProperty().divide(zoomFactor)
        .divide(amountOfLevels)));
  }

  /**
   * Initialize the scroll event (zoom) handlers.
   */
  private void initializeScrollEvent() {
    mainPane.setOnScroll((ScrollEvent event) -> {
      double relativeXPos = event.getX() / mainPane.getWidth();
      if (event.getDeltaY() > 0) {
        zoomIn(event.getDeltaY(), relativeXPos);
      } else {
        zoomOut(-event.getDeltaY(), relativeXPos);
      }
    });
  }

  /**
   * Initialize the edge canvas'.
   */
  private void initializeEdgeCanvas() {
    topEdgeCanvas.widthProperty().bind(mainPane.widthProperty());
    topEdgeCanvas.heightProperty().bind(topPane.prefHeightProperty());
    bottomEdgeCanvas.widthProperty().bind(mainPane.widthProperty());
    bottomEdgeCanvas.heightProperty().bind(bottomPane.prefHeightProperty());
  }

  /**
   * Verify and update the scroll bar value.
   *
   * @param newValue the new value of the scrollbar.
   */
  private void updateScrollbarValue(double newValue) {
    if (Double.isNaN(newValue) || Double.isInfinite(newValue)) {
      return;
    }
    if (newValue < 0.0) {
      scrollbar.setValue(0.0);
    } else if (scrollbar.getMax() > 0.0 && newValue > scrollbar.getMax()) {
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

  /**
   * Calculate the minimum zoom factor, for which the whole graph is shown.
   *
   * @return the minimum zoom factor.
   */
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
    bottomGraphIndicator.heightProperty().bind(bottomPane.prefHeightProperty());
    bottomGraphIndicator.visibleProperty().bind(bottomPane.visibleProperty());
  }

  /**
   * Initialize the drag over event for the top pane.
   */
  private void initializeTopDragOverEvent() {
    topPane.setOnDragOver((DragEvent event) -> {
      if (event.getDragboard().hasString()) {
        event.acceptTransferModes(TransferMode.ANY);
      }
      event.consume();
    });
  }

  /**
   * Initialize the drag dropped event for the top pane.
   */
  private void initializeTopDragDroppedEvent() {
    topPane.setOnDragDropped((DragEvent event) -> {
      Dragboard dragboard = event.getDragboard();
      if (dragboard.hasString()) {
        Collection<String> genomes = Stream.of(dragboard.getString().split("\n"))
            .map(genome -> genome.replace("\r", ""))
            .collect(Collectors.toCollection(ArrayList::new));
        try {
          handleGenomesDropped(GenomeMap.getInstance().mapAll(genomes), event,
              getTopGraphGenomes(),
              getBottomGraphGenomes());
        } catch (Exception ex) {
          Logger.getLogger(GraphPaneController.class.getName()).log(Level.SEVERE, null, ex);
        }
        event.setDropCompleted(false);
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
        Collection<String> genomes = Stream.of(dragboard.getString().split("\n"))
            .map(genome -> genome.replace("\r", ""))
            .collect(Collectors.toCollection(ArrayList::new));
        handleGenomesDropped(GenomeMap.getInstance().mapAll(genomes), event,
            getBottomGraphGenomes(),
            getTopGraphGenomes());
        event.setDropCompleted(false);
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
        getTopGraphGenomes().addAll(genomes);
        redrawGraphs();
        break;
      case 1:
        contextMenu.getItems().add(createCreateNewGraphMenuItem(genomes, otherGenomeMap));
      /* intended fall through */
      case 2:
        contextMenu.getItems().add(createClearAndAddToGraphMenuItem(genomes, genomeMap));
        if (!genomeMap.containsAll(genomes)) {
          contextMenu.getItems().add(createAddToExistingGraphMenuItem(genomes, genomeMap));
        }
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
    if (!getTopGraphGenomes().isEmpty()) {
      drawnGraphs++;
      if (!getBottomGraphGenomes().isEmpty()) {
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
        drawOneGraph(getTopGraphGenomes());
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
    scrollbar.setValue(0.0);
    updateGraphSize();
    System.gc();
    graphUpdater.update();
  }

  /**
   * Draw and compare two subgraphs of genomes.
   *
   * @param topGenomes    the genomes of the top graph.
   * @param bottomGenomes the genomes of the bottom graph.
   */
  public void compareTwoGraphs(Collection<Integer> topGenomes, Collection<Integer> bottomGenomes) {
    getTopGraphGenomes().clear();
    getTopGraphGenomes().addAll(topGenomes);
    getBottomGraphGenomes().clear();
    getBottomGraphGenomes().addAll(bottomGenomes);

    compareTwoGraphs();
  }

  /**
   * Draw and compare two subgraphs of genomes.
   */
  private void compareTwoGraphs() {
    Pair<OrderedGraph, OrderedGraph> compareRes
        = SubgraphAlgorithmManager.compareTwoGraphs(
            getTopGraphGenomes(),
            getBottomGraphGenomes(),
            mainGraph,
            mainGraphOrder,
            treeRoot);
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
    zoomFactor.set(mainPane.getWidth() / amountOfLevels.get());
    scrollbar.setValue(0.0);
    updateGraphSize();
    System.gc();
    graphUpdater.update();
  }

  /**
   * Load a new main graph.
   *
   * @param graph       the graph.
   * @param root        the root of the phylogenetic tree.
   * @param annotations the list of annotations.
   */
  public void loadMainGraph(SequenceGraph graph, IPhylogeneticTreeRoot<?> root,
      List<Annotation> annotations) {
    clear();
    treeRoot = root;
    mainGraph = graph;
    mainGraphOrder = new GraphOrdererThread(mainGraph, annotations);
    mainGraphOrder.start();
  }

  /**
   * Clear the content of the class.
   */
  private void clear() {
    if (getDrawnGraphs() == 0) {
      return;
    }
    clearCanvas();
    getTopGraphGenomes().clear();
    getBottomGraphGenomes().clear();
    topPane.getChildren().clear();
    bottomPane.getChildren().clear();
    bottomGraph = null;
    topGraph = null;
    updateGraphSize();
    graphUpdater.update();
  }

  /**
   * Clear all of the canvas'.
   */
  private void clearCanvas() {
    topEdgeCanvas.getGraphicsContext2D().clearRect(0, 0, topEdgeCanvas.getWidth(),
        topEdgeCanvas.getWidth());
    bottomEdgeCanvas.getGraphicsContext2D().clearRect(0, 0, bottomEdgeCanvas.getWidth(),
        bottomEdgeCanvas.getWidth());
    heatmap.getGraphicsContext2D().clearRect(0, 0, heatmap.getWidth(),
        heatmap.getHeight());
  }

  /**
   * Update the size of the graphs. Show both graphs if 2 graphs are drawn, otherwise only show the
   * top graph and resize it to fit the whole screen.
   */
  private void updateGraphSize() {
    if (getDrawnGraphs() < 2) {
      topPane.prefHeightProperty().bind(mainPane.prefHeightProperty().add(-SCROLL_BAR_HEIGHT
          - GRAPH_HEATMAP_HEIGHT));
      bottomPane.setVisible(false);
      deleteBottomGraphImage.setVisible(false);
    } else {
      topPane.prefHeightProperty().bind(
          mainPane.prefHeightProperty().add(-SCROLL_BAR_HEIGHT - GRAPH_HEATMAP_HEIGHT).divide(2.0));
      bottomPane.setVisible(true);
      deleteBottomGraphImage.setVisible(true);
    }
  }

  /**
   * Update the graph by redrawing it.
   */
  private void updateGraph() {
    double graphWidth = mainPane.getWidth();
    int levelsToDraw = (int) (graphWidth / zoomFactor.get()) + 1;
    double startLevel = amountOfLevels.get() * scrollbar.getValue();
    if (startLevel < 0) {
      startLevel = 0;
    }
    clearCanvas();
    if (topGraph != null) {
      drawGraph(topPane, topEdgeCanvas, topGraph, topGraph.getSubgraph().getGenomes().size(),
          startLevel, startLevel + levelsToDraw);
    }
    if (bottomGraph != null) {
      drawGraph(bottomPane, bottomEdgeCanvas, bottomGraph,
          bottomGraph.getSubgraph().getGenomes().size(), startLevel, startLevel + levelsToDraw);
    }
  }

  /**
   * Draw the given graph in the given pane.
   *
   * @param pane       the pane to draw the graph in.
   * @param canvas     the canvas in which to draw the edges.
   * @param graphOrder the graph node order.
   * @param graph      the graph.
   * @param startLevel the level where to start drawing.
   * @param endLevel   the level where to stop drawing.
   */
  private void drawGraph(Pane pane, Canvas canvas, OrderedGraph orderedGraph, int genomeCount,
      double startLevel, double endLevel) {
    pane.getChildren().clear();
    HashSet<GraphNode> drawnGraphNodes = new HashSet<>();
    GraphViewRange fullRange = new GraphViewRange(0, pane.getPrefHeight());
    for (GraphNode node : orderedGraph.getGraphOrder()) {
      drawNode(pane, node, drawnGraphNodes, startLevel, endLevel, fullRange);
    }
    drawEdges(canvas, drawnGraphNodes, startLevel, genomeCount);
  }

  /**
   * Draw the given node in the given pane.
   *
   * @param pane            the pane to draw the node in.
   * @param node            the node to draw.
   * @param drawnGraphNodes the list of drawn nodes to which to add the node which is drawn and any
   *                        child nodes of the given node which are drawn.
   * @param startLevel      the start level: where to start drawing.
   * @param endLevel        the start level: where to stop drawing.
   * @param viewRange       the range of values which may be used as y coordinate to draw this node.
   */
  @SuppressWarnings("checkstyle:MethodLength") // either this or long lines.
  private void drawNode(Pane pane, GraphNode node, HashSet<GraphNode> drawnGraphNodes,
      double startLevel, double endLevel, GraphViewRange viewRange) {
    drawnGraphNodes.add(node);
    node.getGuiData().range = viewRange;
    double width = calculateNodeWidth(node, zoomFactor.get());
    if (!isInView(node, startLevel, endLevel)) {
      return;
    } else if (width < MIN_VISIBILITY_WIDTH) {
      heatmapColorer.drawHeatmap(node, startLevel);
      return;
    }
    double height = node.getGuiData().maxHeight * viewRange.rangeHeight;
    if (height > width) {
      height = width;
    }
    IViewGraphNode viewNode = ViewNodeBuilder.buildNode(node, width, height);

    double fade = calculateBubbleFadeFactor(node, width);
    if (fade > 0.0) {
      viewNode.get().setOnMouseClicked(mouseEvent -> {
        selectionManager.select(viewNode);
        mouseEvent.consume();
      });
      // select when previously selected node is equal to this new one
      selectionManager.checkSelected(viewNode);
      pane.getChildren().add(viewNode.get());
      viewNode.setOpacity(fade);
    }
    viewNode.centerXProperty().set(zoomFactor.get()
        * (node.getLevel() - startLevel - node.size() / 2.0));
    viewNode.centerYProperty().set(viewRange.rangeHeight * node.getGuiData().relativeYPos
        + viewRange.rangeStartY);
    if (node.hasChildren() && width > BUBBLE_POP_SIZE) {
      GraphViewRange bubbleViewRange = new GraphViewRange(viewNode.getLayoutY(), height);
      drawNestedNodes(pane, node, drawnGraphNodes, startLevel, endLevel, bubbleViewRange);
    } else {
      node.unpop();
    }
    if (node.hasAnnotation()) {
      Annotation annotation = node.getAnnotation();
      Label label = new Label(annotation.getAttribute("name"));
      label.setWrapText(true);
      label.setMaxWidth(viewNode.getWidth());
      label.setLayoutX(viewNode.centerXProperty().get() - viewNode.getWidth() / 2.0);
      label.setLayoutY(viewNode.centerYProperty().get() - viewNode.getHeight() / 2.0);
      pane.getChildren().add(label);
    }
    heatmapColorer.drawHeatmap(node, startLevel);
  }

  /**
   * Calculate the fade factor of the bubbles. If a bubble is twice the size of the screen it will
   * be fully faded out.
   *
   * @param bubble the bubble.
   * @param width  the width.
   * @return the fade factor.
   */
  private double calculateBubbleFadeFactor(GraphNode bubble, double width) {
    if (!bubble.hasChildren()) {
      return 1.0;
    }
    double fade = (mainPane.getWidth() / width - 1.0) * 2.0 + 1.0;
    if (fade >= 1.0) {
      return 1.0;
    } else {
      return fade;
    }
  }

  /**
   * Go to the given level.
   *
   * @param level the level to center at.
   */
  public void centerOnLevel(int level) {
    GraphNode nodeToCenter
        = mainGraphOrder.getGraphMapper().findBase(GenomeMap.getInstance().getReferenceId(), level);
    double levelsToDraw = mainPane.getWidth() / zoomFactor.get();
    updateScrollbarValue((nodeToCenter.getLevel() - levelsToDraw / 2.0) / amountOfLevels.get());
  }

  /**
   * Draw the nodes which are nested in the given bubble.
   *
   * @param pane            the pane in which to draw the nested nodes.
   * @param bubble          the bubble which contains the nested nodes.
   * @param drawnGraphNodes the list of drawn nodes to which to add all child nodes which are drawn.
   * @param startLevel      the start level: where to start drawing.
   * @param endLevel        the start level: where to stop drawing.
   * @param viewRange       the range of values which may be used as y coordinate to draw the
   *                        children of this bubble.
   */
  private void drawNestedNodes(Pane pane, GraphNode bubble, HashSet<GraphNode> drawnGraphNodes,
      double startLevel, double endLevel, GraphViewRange viewRange) {
    Collection<GraphNode> poppedNodes = bubble.pop();
    for (GraphNode poppedNode : poppedNodes) {
      drawNode(pane, poppedNode, drawnGraphNodes, startLevel, endLevel, viewRange);
    }
  }

  /**
   * Check if the given node is in the view.
   *
   * @param node       the node.
   * @param startLevel the start level of the current view.
   * @param endLevel   the end level of the current view.
   * @return if the node is inside of the view.
   */
  private boolean isInView(GraphNode node, double startLevel, double endLevel) {
    int nodeStart = node.getLevel() - node.size();
    int nodeEnd = node.getLevel();
    return nodeStart < endLevel && nodeEnd > startLevel;
  }

  /**
   * Calculate the radius of the node. The radius depends on the amount of bases inside the node.
   *
   * @param node       the node.
   * @param zoomfactor the current zoom factor.
   * @return the radius.
   */
  public static double calculateNodeWidth(GraphNode node, double zoomfactor) {
    int nodeSize = node.size();
    double width = nodeSize * zoomfactor;
    if (nodeSize < MINUMUM_BASE_SIZE && width < DECENT_NODE_WIDTH) {
      width = MINUMUM_BASE_SIZE * zoomfactor;
      if (width > DECENT_NODE_WIDTH) {
        return DECENT_NODE_WIDTH;
      }
    }
    return width;
  }

  /**
   * Draw the edges between all of the nodes which are drawn on the screen.
   *
   * @param canvas          the canvas in which to draw the edges.
   * @param drawnGraphNodes the set of nodes which are drawn on the screen.
   * @param startLevel      the start level: where to start drawing.
   * @param genomeCount     the amount of genomes which are present in the drawn graph.
   */
  private void drawEdges(Canvas canvas, HashSet<GraphNode> drawnGraphNodes, double startLevel,
      int genomeCount) {
    drawnGraphNodes.forEach((GraphNode node) -> {
      if (!node.isPopped()) {
        for (GraphNode outEdge : node.getOutEdges()) {
          double edgeWidth = calculateEdgeWidth(genomeCount, node, outEdge);
          drawEdge(canvas.getGraphicsContext2D(), node, outEdge, edgeWidth, startLevel);
        }
        for (GraphNode inEdge : node.getInEdges()) {
          if (!drawnGraphNodes.contains(inEdge)) {
            double edgeWidth = calculateEdgeWidth(genomeCount, inEdge, node);
            drawEdge(canvas.getGraphicsContext2D(), inEdge, node, edgeWidth, startLevel);
          }
        }
      }
    });
  }

  /**
   * Draw an edge from the fromNode to the toNode.
   *
   * @param graphicContext the graphicContext to which to add the edges.
   * @param fromNode       the node from which to draw the edge.
   * @param toNode         the node to draw the edge to.
   * @param edgeWidth      the width of the edge.
   * @param startLevel     the start level: where to start drawing.
   */
  @SuppressWarnings("checkstyle:MethodLength")
  private void drawEdge(GraphicsContext graphicContext, GraphNode fromNode, GraphNode toNode,
      double edgeWidth, double startLevel) {
    graphicContext.setLineWidth(edgeWidth * 2);
    double startX;
    if (fromNode.hasChildren()) {
      startX = zoomFactor.get() * (fromNode.getLevel() - startLevel);
    } else {
      startX = zoomFactor.get()
          * (fromNode.getLevel() - startLevel - fromNode.size() * (1.0 - HALF_NODE_MARGIN));
    }
    double endX;
    if (toNode.hasChildren()) {
      endX = zoomFactor.get() * (toNode.getLevel() - startLevel - toNode.size());
    } else {
      endX = zoomFactor.get() * (toNode.getLevel() - startLevel - toNode.size() * HALF_NODE_MARGIN);
    }
    GraphViewRange fromRange = fromNode.getGuiData().range;
    GraphViewRange toRange = toNode.getGuiData().range;
    double startY = fromNode.getGuiData().relativeYPos * fromRange.rangeHeight
        + fromRange.rangeStartY;
    double endY = toNode.getGuiData().relativeYPos * toRange.rangeHeight + toRange.rangeStartY;
    if (toNode.getGenomeSize() < fromNode.getGenomeSize()) {
      graphicContext.setStroke(toNode.getMostFrequentLineage().getColor());
    } else {
      graphicContext.setStroke(fromNode.getMostFrequentLineage().getColor());
    }
    graphicContext.strokeLine(startX, startY, endX, endY);
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
    int genomesOverEdge = from.approximateGenomesOverEdge(to);
    // see javadoc
    double edgeWith = Math.log(100.0 * genomesOverEdge / maxGenomes) * 0.8 * zoomFactor.get();
    if (edgeWith > MAX_EDGE_WIDTH) {
      return MAX_EDGE_WIDTH;
    } else if (edgeWith < MIN_EDGE_WIDTH) {
      return MIN_EDGE_WIDTH;
    }
    return edgeWith;
  }

  /**
   * Get the observable list of genomes which must be drawn in the top graph.
   *
   * @return the observable list of genomes which must be drawn in the top graph.
   */
  public ObservableSet<Integer> getTopGraphGenomes() {
    return topGraphGenomes;
  }

  /**
   * Get the observable list of genomes which must be drawn in the bottom graph.
   *
   * @return the observable list of genomes which must be drawn in the bottom graph.
   */
  public ObservableSet<Integer> getBottomGraphGenomes() {
    return bottomGraphGenomes;
  }

  /**
   * This method clears the bottom graph when the cross icon is clicked. It is linked by JavaFX via
   * the fxml file (using reflection), so it appears to be unused to code quality tools. For this
   * reason the suppress warning "unused" annotation is used.
   */
  @FXML
  @SuppressWarnings("unused")
  private void deleteBottomGraph() {
    getBottomGraphGenomes().clear();
    bottomGraph = null;
    redrawGraphs();
  }

  /**
   * Setup anything that can't be done or wired by JavaFX's magic box.
   *
   * @param selectionManager The selection manager used
   */
  public void setup(SelectionManager selectionManager) {
    this.selectionManager = selectionManager;
    selectionManager.addListener((observable, oldValue, newValue) -> {
      graphUpdater.update();
    });
  }

  /**
   * Used to updated the graph at most once every frame.
   */
  private static class GraphUpdater extends AnimationTimer {

    private static final boolean PRINT_FRAME_RATE = false;
    private static final boolean PRINT_MEMORY = false;
    private final GraphPaneController graphComparer;
    private final AtomicBoolean updateGraph = new AtomicBoolean(false);
    private long time = System.nanoTime();
    private int count = 0;

    /**
     * Construct a graph updater.
     *
     * @param graphComparer the graph to update.
     */
    private GraphUpdater(GraphPaneController graphComparer) {
      this.graphComparer = graphComparer;
      start();
    }

    /**
     * This method is automatically called by JavaFX each time when a new frame is being rendered.
     * This method updates the graph if the update method was called somewhere between the previous
     * and current rendered frame.
     *
     * @param now the current time in nanoseconds.
     */
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

    /**
     * Update the graph the next time the frame is rendered.
     */
    public void update() {
      updateGraph.set(true);
    }
  }
}
