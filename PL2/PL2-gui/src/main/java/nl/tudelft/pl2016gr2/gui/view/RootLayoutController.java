package nl.tudelft.pl2016gr2.gui.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.core.algorithms.CompareSubgraphs;
import nl.tudelft.pl2016gr2.core.algorithms.GraphOrdererThread;
import nl.tudelft.pl2016gr2.core.algorithms.SplitGraphs;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.events.GraphicsChangedEvent;
import nl.tudelft.pl2016gr2.gui.view.graph.DrawComparedGraphs;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeManager;
import nl.tudelft.pl2016gr2.gui.view.tree.heatmap.HeatmapManager;
import nl.tudelft.pl2016gr2.model.NodePosition;
import nl.tudelft.pl2016gr2.model.OriginalGraph;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class.
 *
 * @author Faris
 */
public class RootLayoutController implements Initializable {

  @FXML
  private StackPane rootPane;
  @FXML
  private Pane treePane;
  @FXML
  private Pane heatmapPane;
  @FXML
  private Pane selectionDescriptionPane;
  @FXML
  private Button zoomOutButton;
  @FXML
  private StackPane locationIdentifierPane;
  @FXML
  private Rectangle locationIdentifierRectangle;
  @FXML
  private ImageView treeIcon;
  @FXML
  private ImageView graphIcon;
  @FXML
  private SplitPane mainPane;
  private final Pane graphPane = new Pane();

  private TreeManager treeManager;
  private HeatmapManager heatmapManager;
  private SelectionManager selectionManager;
  private boolean zoomOutButtonDisabled = true;
  private OriginalGraph graph;

  private GraphOrdererThread mainGraphOrder;
  private final DrawComparedGraphs drawGraphs = DrawComparedGraphs.loadView();

  /**
   * Initializes the controller class.
   *
   * @param location  unused variable.
   * @param resources unused variable.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    heatmapManager = new HeatmapManager(heatmapPane);
    initializeSelectionManager();
    initializeTreeIcon();
    initializeGraphIcon();
    graph = new GfaReader("TB328.gfa").read();
    mainGraphOrder = new GraphOrdererThread(graph);
    mainGraphOrder.start();
  }

  /**
   * Load this view.
   *
   * @return the controller of the loaded view.
   */
  public static RootLayoutController loadView() {
    FXMLLoader loader = new FXMLLoader();
    try {
      loader.setLocation(RootLayoutController.class.getClassLoader()
          .getResource("pages/RootLayout.fxml"));
      loader.load();
      return loader.<RootLayoutController>getController();
    } catch (IOException ex) {
      Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
    }
    throw new RuntimeException("failed to load the fxml file: " + loader.getLocation());
  }

  /**
   * Get the pane of this view.
   *
   * @return the pane of this view.
   */
  public Region getPane() {
    return rootPane;
  }

  /**
   * Draw two subgraphs.
   *
   * @param topGenomes    the genomes of the top graph.
   * @param bottomGenomes the genomes of the bottom graph.
   */
  public void drawGraph(ArrayList<String> topGenomes, ArrayList<String> bottomGenomes) {
    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(graph, 0),
        topGenomes);
    SplitGraphsThread bottomSubGraphThread = new SplitGraphsThread(new SplitGraphs(graph, 1),
        bottomGenomes);
    topSubGraphThread.start();
    bottomSubGraphThread.start();
    topSubGraphThread.getSubGraph();
    bottomSubGraphThread.getSubGraph();
    Pair<ArrayList<NodePosition>, ArrayList<NodePosition>> alignedGraphs
        = CompareSubgraphs.compareGraphs(mainGraphOrder.getOrderedGraph(),
            topSubGraphThread.getSubGraph(), bottomSubGraphThread.getSubGraph());
    drawGraphs.drawGraphs(alignedGraphs.left, alignedGraphs.right);
  }

  /**
   * Set the data which has to be visualized.
   *
   * @param root the root of the tree which has to be drawn.
   */
  public void setData(IPhylogeneticTreeNode root) {
    assert treeManager == null;
    treeManager = new TreeManager(treePane, root, zoomOutButton, selectionManager);
    heatmapManager.initLeaves(treeManager.getCurrentLeaves());
    treeManager.setOnLeavesChanged((Observable observable, Object arg) -> {
      heatmapManager.setLeaves(treeManager.getCurrentLeaves());
    });
  }

  /**
   * Initialize the selection manager (which manages showing the description of selected objects).
   */
  private void initializeSelectionManager() {
    selectionManager = new SelectionManager(this, selectionDescriptionPane, mainPane);
    mainPane.setOnMouseClicked((MouseEvent event) -> {
      if (!event.isConsumed()) {
        selectionManager.deselect();
        event.consume();
      }
    });
  }

  /**
   * Initialize the tree icon. When this icon is clicked, the tree will be shown.
   */
  private void initializeTreeIcon() {
    treeIcon.setOnMouseClicked((MouseEvent event) -> {
      if (mainPane.getItems().contains(treePane)) {
        return;
      }
      mainPane.getItems().clear();
      mainPane.getItems().addAll(treePane, heatmapPane);
      mainPane.setDividerPositions(0.8);
      zoomOutButton.setDisable(zoomOutButtonDisabled);
      mainPane.fireEvent(new GraphicsChangedEvent());
    });
  }

  /**
   * Initialize the tree icon. When this icon is clicked, the tree will be shown.
   */
  private void initializeGraphIcon() {
    graphIcon.setOnMouseClicked((MouseEvent event) -> {
      if (mainPane.getItems().contains(graphPane)) {
        return;
      }
      mainPane.getItems().clear();
      mainPane.getItems().add(drawGraphs.getGraphPane());
      zoomOutButtonDisabled = zoomOutButton.isDisabled();
      zoomOutButton.setDisable(true);
      mainPane.fireEvent(new GraphicsChangedEvent());
    });
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
