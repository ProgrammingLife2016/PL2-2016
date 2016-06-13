package nl.tudelft.pl2016gr2.gui.view;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;
import nl.tudelft.pl2016gr2.core.GraphFactory;
import nl.tudelft.pl2016gr2.core.InputStreamGraphFactory;
import nl.tudelft.pl2016gr2.core.InputStreamTreeFactory;
import nl.tudelft.pl2016gr2.core.TreeFactory;
import nl.tudelft.pl2016gr2.core.algorithms.BuildTree;
import nl.tudelft.pl2016gr2.gui.view.graph.GraphPaneController;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionPaneController;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;
import nl.tudelft.pl2016gr2.gui.view.tree.TreePaneController;
import nl.tudelft.pl2016gr2.model.Annotation;
import nl.tudelft.pl2016gr2.model.GenomeMap;
import nl.tudelft.pl2016gr2.model.MetaData;
import nl.tudelft.pl2016gr2.model.graph.SequenceGraph;
import nl.tudelft.pl2016gr2.model.metadata.LineageColor;
import nl.tudelft.pl2016gr2.model.phylogenetictree.IPhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.model.phylogenetictree.PhylogeneticTreeRoot;
import nl.tudelft.pl2016gr2.parser.controller.AnnotationReader;
import nl.tudelft.pl2016gr2.parser.controller.MetaDataReader;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
  // @FXML
  // private SelectionPaneController selectionPaneController;
  @FXML
  private SplitPane mainPane;
  @FXML
  private LegendController graphLegendController;
  @FXML
  private LegendController treeLegendController;

  @FXML
  private SearchPaneController searchPaneController;

  @TestId(id = "treeManager")
  @FXML
  private TreePaneController treePaneController;

  @TestId(id = "selectionManager")
  private SelectionManager selectionManager;

  @TestId(id = "drawGraphs")
  @FXML
  private GraphPaneController graphPaneController;

  @FXML
  private SelectionPaneController selectionPaneController;

  private final ObjectProperty<MetadataPropertyMap> metadataPropertyMap = 
      new SimpleObjectProperty<>(new MetadataPropertyMap(new ArrayList<>()));

  /**
   * Initializes the controller class.
   *
   * @param location  unused variable.
   * @param resources unused variable.
   */
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    initializeSubPanes();
    initializeLegend();
    Region graphRegion = graphPaneController.getGraphPane();
    graphRegion.prefHeightProperty().bind(mainPane.heightProperty());
    mainPane.setDividerPosition(0, 0.35);
    rootPane.sceneProperty().addListener(new ChangeListener<Scene>() {
      @Override
      public void changed(ObservableValue<? extends Scene> observable, Scene oldValue,
          Scene newValue) {
        if (newValue != null) {
          initializeSearchPaneController();
          // fire this only once when scene is set.
          rootPane.sceneProperty().removeListener(this);
        }
      }
    });
  }

  /**
   * Load the data into the root layout.
   *
   * @param treeRoot the root of the loaded tree.
   */
  public void loadTree(IPhylogeneticTreeRoot treeRoot) {
    treePaneController.loadTree(treeRoot);
  }

  /**
   * Load the data into the root layout.
   *
   * @param graph       the graph you want to load
   * @param treeRoot    the root of the loaded tree.
   * @param annotations the list of annotations.
   */
  public void loadGraph(SequenceGraph graph, IPhylogeneticTreeRoot treeRoot,
      List<Annotation> annotations) {
    this.graphPaneController.loadMainGraph(graph, treeRoot, annotations);
  }

  /**
   * Load this view.
   *
   * @return the controller of the loaded view.
   */
  public static RootLayoutController loadView() {
    try {
      FXMLLoader loader = new FXMLLoader(
          RootLayoutController.class.getClassLoader().getResource("pages/RootLayout.fxml"));
      loader.load();
      return loader.getController();
    } catch (IOException ex) {
      Logger.getLogger(RootLayoutController.class.getName()).log(Level.SEVERE, null, ex);
    }
    throw new RuntimeException("failed to load the RootLayout fxml file.");
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
   * Initialize the sub panes.
   */
  private void initializeSubPanes() {
    selectionManager = new SelectionManager();
    treePaneController.setup(selectionManager, graphPaneController);
    treePaneController.initializeHeatmaps(metadataPropertyMap);
    graphPaneController.setup(selectionManager);
    selectionPaneController.setup(selectionManager);
    mainPane.setOnMouseClicked((MouseEvent event) -> {
      if (!event.isConsumed()) {
        selectionManager.deselect();
        event.consume();
      }
    });
  }

  /**
   * Initialize the legend.
   */
  @SuppressWarnings("checkstyle:methodlength")
  private void initializeLegend() {
    graphLegendController.initializeData(
        "Legend",
        -5.0, 5.0,
        new LegendController.LegendItem(
            "This node contains several other nodes based on phylogeny.",
            "Phylogenetic bubble",
            new Rectangle(20, 20, Color.ALICEBLUE)),
        new LegendController.LegendItem(
            "This node contains several sequences without mutations.",
            "Straight sequence bubble",
            new Rectangle(20, 20, Color.LIGHTCORAL)),
        new LegendController.LegendItem(
            "This node contains a sequence that is not present in other genomes (InDel).",
            "InDel bubble",
            new Rectangle(20, 20, Color.LIGHTSKYBLUE)),
        new LegendController.LegendItem(
            "This node contains a point mutation.",
            "Point Mutation bubble",
            new Rectangle(20, 20, Color.PLUM)),
        new LegendController.LegendItem(
            "This node represents a straight sequence of multiple nodes.",
            "Straight sequence.",
            new Circle(10, graphPaneController.NO_OVERLAP_COLOR)),
        new LegendController.LegendItem(
            "This node has overlap with other (different) nodes.",
            "Overlapping sequence.",
            new Circle(10, graphPaneController.OVERLAP_COLOR))
    );

    List<LegendController.LegendItem> treeLegendItems = new ArrayList<>();
    treeLegendItems.add(new LegendController.LegendItem(
        "Leaf node of the graph. This node has no children",
        "Leaf node",
        new Circle(10, Color.BLACK)
    ));
    treeLegendItems.add(new LegendController.LegendItem(
        "Node of the graph. This node has children",
        "Node",
        new Circle(10, Color.ALICEBLUE)
    ));
    Circle tempCircle;
    tempCircle = new Circle(TreeNodeCircle.NODE_RADIUS, Color.ALICEBLUE);
    tempCircle.setStrokeWidth(TreeNodeCircle.NODE_BORDER_WIDTH);
    tempCircle.setStroke(graphPaneController.TOP_GRAPH_COLOR);
    treeLegendItems.add(new LegendController.LegendItem(
        "Node in orange section of the graph (top)",
        "Top node",
        tempCircle
    ));
    tempCircle = new Circle(TreeNodeCircle.NODE_RADIUS, Color.ALICEBLUE);
    tempCircle.setStrokeWidth(TreeNodeCircle.NODE_BORDER_WIDTH);
    tempCircle.setStroke(TreeNodeCircle.MULTI_GRAPH_GRADIENT);
    treeLegendItems.add(new LegendController.LegendItem(
        "Node both sections of the graph",
        "Shared node",
        tempCircle
    ));
    tempCircle = new Circle(TreeNodeCircle.NODE_RADIUS, Color.ALICEBLUE);
    tempCircle.setStrokeWidth(TreeNodeCircle.NODE_BORDER_WIDTH);
    tempCircle.setStroke(graphPaneController.BOTTOM_GRAPH_COLOR);
    treeLegendItems.add(new LegendController.LegendItem(
        "Node in blue section of the graph (bottom)",
        "Bottom node",
        tempCircle
    ));
    for (LineageColor color : LineageColor.values()) {
      treeLegendItems.add(new LegendController.LegendItem(
          String.format("Lineage color %s", color.name()),
          color.name(),
          new Rectangle(20, 5, color.getColor())
      ));
    }
    treeLegendController.initializeData(
        "Legend",
        10.0, 5.0,
        treeLegendItems.toArray(new LegendController.LegendItem[treeLegendItems.size()]));
  }

  /**
   * Initialize the controller of the search pane.
   */
  private void initializeSearchPaneController() {
    String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    boolean isOSx = os.contains("mac") || os.contains("darwin");

    rootPane.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
      switch (keyEvent.getCode()) {
        case F:
          if (keyEvent.isControlDown() || isOSx && keyEvent.isMetaDown()) {
            searchPaneController.requestSearchFieldFocus();
            keyEvent.consume();
          }
          break;
        case ESCAPE:
          rootPane.requestFocus();
          break;
        default:
      }
    });
    searchPaneController.setup(selectionManager, graphPaneController);
  }

  @Override
  public void filesLoaded(InputStream treeFile, InputStream graphFile,
      InputStream metadataFile, InputStream annotationFile) {
    try {
      GraphFactory graphFactory = new InputStreamGraphFactory(graphFile);
      TreeFactory treeFactory = new InputStreamTreeFactory(treeFile);

      SequenceGraph graph = graphFactory.getGraph();
      Tree tree = treeFactory.getTree();
      List<MetaData> metaDatas = new MetaDataReader(metadataFile).read();
      List<Annotation> annotations = new AnnotationReader(annotationFile).read();
      metadataPropertyMap.set(new MetadataPropertyMap(metaDatas));
      if (graph != null && tree != null) {
        IPhylogeneticTreeRoot treeRoot = new PhylogeneticTreeRoot(tree.getRoot(), metaDatas);
        treeRoot = new BuildTree(treeRoot, GenomeMap.getInstance().copyAllGenomes()).getTree();
        loadGraph(graph, treeRoot, annotations);
        loadTree(treeRoot);
        searchPaneController.setData(metaDatas);
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

  /**
   * Handle the event which occurs when the "open file" button is clicked in the menu. This method
   * is linked by reflection (see the FXML file), thus it is needed to suppress the "unused"
   * warning.
   */
  @FXML
  @SuppressWarnings("unused")
  private void openFileMenuItemClicked() {
    promptFileChooser();
  }
}
