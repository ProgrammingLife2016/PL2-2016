package nl.tudelft.pl2016gr2.gui.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.core.GraphFactory;
import nl.tudelft.pl2016gr2.core.InputStreamGraphFactory;
import nl.tudelft.pl2016gr2.core.InputStreamTreeFactory;
import nl.tudelft.pl2016gr2.core.TreeFactory;
import nl.tudelft.pl2016gr2.gui.view.graph.DrawComparedGraphs;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeManager;
import nl.tudelft.pl2016gr2.model.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.PhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.SequenceGraph;
import nl.tudelft.pl2016gr2.parser.controller.AnnotationReader;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
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
public class RootLayoutController implements
    Initializable,
    FileChooserController.InputFileConsumer {

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
    mainPane.getItems().add(treeManager.getTreePane());

    Region graphRegion = drawGraphs.getGraphPane();
    mainPane.getItems().add(graphRegion);
    graphRegion.prefHeightProperty().bind(mainPane.heightProperty());
    mainPane.setDividerPosition(0, 0.35);
  }

  /**
   * Load the data into the root layout.
   *
   * @param treeRoot the root of the loaded tree.
   */
  public void loadTree(IPhylogeneticTreeRoot treeRoot) {
    treeManager.loadTree(treeRoot);
  }

  /**
   * Load the data into the root layout.
   *
   * @param graph    the graph you want to load
   * @param treeRoot the root of the loaded tree.
   */
  public void loadGraph(SequenceGraph graph, IPhylogeneticTreeRoot treeRoot) {
    this.drawGraphs.loadMainGraph(graph, treeRoot);
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

  @Override
  public void filesLoaded(InputStream treeFile, InputStream graphFile, InputStream metadataFile) {
    try {
      // Note: not really enjoying pro's of factories with this structure.
      // consider to decouple a bit more, as the factories can't be mocked right now.
      GraphFactory graphFactory = new InputStreamGraphFactory(graphFile);
      TreeFactory treeFactory = new InputStreamTreeFactory(treeFile);

      SequenceGraph graph = graphFactory.getGraph();
      Tree tree = treeFactory.getTree();

      if (graph != null && tree != null) {
        IPhylogeneticTreeRoot treeRoot = new PhylogeneticTreeRoot(tree.getRoot(),
            new AnnotationReader(metadataFile).read());
        loadGraph(graph, treeRoot);
        loadTree(treeRoot);
      } else {
        Logger.getLogger(RootLayoutController.class.getName()).log(
            Level.SEVERE,
            "tree or graph was null");
      }
    } catch (IOException | InvalidFormatException ex) {
      Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Prompts the user for with the FileChooser.
   */
  public void promptFileChooser() {
    try {
      FileChooserController fileChooserController
          = FileChooserController.initialize(mainPane.getScene().getWindow());
      fileChooserController.setInputFileConsumer(this);
      fileChooserController.getStage().show();
    } catch (IOException e) {
      Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
    }
  }

  @FXML
  @SuppressWarnings("unused")
  private void openFileMenuItemClicked() {
    promptFileChooser();
  }
}
