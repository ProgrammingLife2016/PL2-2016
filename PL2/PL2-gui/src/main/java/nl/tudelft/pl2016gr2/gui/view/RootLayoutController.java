package nl.tudelft.pl2016gr2.gui.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import net.sourceforge.olduvai.treejuxtaposer.TreeParser;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.gui.model.PhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.gui.view.graph.DrawComparedGraphs;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeManager;
import nl.tudelft.pl2016gr2.parser.controller.GfaReader;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
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
  private AnchorPane rootPane;
  @FXML
  private Pane selectionDescriptionPane;
  @FXML
  private SplitPane mainPane;

  @TestId(id = "treeManager")
  private TreeManager treeManager;
  @TestId(id = "selectionManager")
  private SelectionManager selectionManager;

  @TestId(id = "drawGraphs")
  private DrawComparedGraphs drawGraphs;

  /**
   * Initializes the controller class.
   *
   * @param location  unused variable.
   * @param resources unused variable.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeSelectionManager();
    treeManager = TreeManager.loadView(selectionManager);
    drawGraphs = DrawComparedGraphs.loadView(selectionManager);
    drawGraphs.loadMainGraph("TB10.gfa");
    mainPane.getItems().add(treeManager.getTreePane());

    Region graphRegion = drawGraphs.getGraphPane();
    mainPane.getItems().add(graphRegion);
    graphRegion.prefHeightProperty().bind(mainPane.heightProperty());
    mainPane.setDividerPosition(0, 0.35);
    loadTree();
  }

  /**
   * Load the data into the root layout.
   */
  private void loadTree() {
    Reader reader = new InputStreamReader(
        GfaReader.class.getClassLoader().getResourceAsStream("10tree_custom.rooted.TKK.nwk"));
    BufferedReader br = new BufferedReader(reader);
    TreeParser tp = new TreeParser(br);

    Tree tree = tp.tokenize("10tree_custom.rooted.TKK.nwk");
    treeManager.loadTree(new PhylogeneticTreeRoot(tree.getRoot()));
    try {
      reader.close();
    } catch (IOException ex) {
      Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
    }
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
//<<<<<<< HEAD
//    SplitGraphsThread topSubGraphThread = new SplitGraphsThread(new SplitGraphs(graph),
//        topGenomes);
//    SplitGraphsThread bottomSubGraphThread = new SplitGraphsThread(new SplitGraphs(graph),
//        bottomGenomes);
//    topSubGraphThread.start();
//    bottomSubGraphThread.start();
//    OriginalGraph topSubgraph = topSubGraphThread.getSubGraph();
//    OriginalGraph bottomSubgraph = bottomSubGraphThread.getSubGraph();
//    Pair<ArrayList<NodePosition>, ArrayList<NodePosition>> alignedGraphs
//        = CompareSubgraphs.compareGraphs(mainGraphOrder.getOrderedGraph(), topSubgraph,
//            bottomSubgraph);
//    drawGraphs.drawGraphs(alignedGraphs.left, alignedGraphs.right);
//=======
    drawGraphs.compareTwoGraphs(topGenomes, bottomGenomes);
  }

  /**
   * Initialize the selection manager (which manages showing the description of selected objects).
   */
  private void initializeSelectionManager() {
    selectionManager = new SelectionManager(this, selectionDescriptionPane);
    mainPane.setOnMouseClicked((MouseEvent event) -> {
      if (!event.isConsumed()) {
        selectionManager.deselect();
        event.consume();
      }
    });
  }
}
