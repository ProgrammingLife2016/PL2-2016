package nl.tudelft.pl2016gr2.gui.view.tree;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import nl.tudelft.pl2016gr2.gui.model.IPhylogeneticTreeNode;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectable;
import nl.tudelft.pl2016gr2.gui.view.selection.ISelectionInfo;
import nl.tudelft.pl2016gr2.gui.view.selection.SelectionManager;
import nl.tudelft.pl2016gr2.gui.view.selection.TextDescription;

import java.util.ArrayList;

/**
 * This class represent a node of the phylogenetic tree which can be drawn in the user interface (it
 * extends Circle).
 *
 * @author Faris
 */
public class ViewNode extends Circle implements ISelectable {

  private static final double NODE_RADIUS = 10.0;
  private static final double NODE_DIAMETER = NODE_RADIUS * 2.0;
  private static final Duration ANIMATION_DURATION = Duration.millis(750.0);

  private final IPhylogeneticTreeNode dataNode;
  private final ArrayList<ViewNode> children = new ArrayList<>();
  private final Area area;
  private final SelectionManager selectionManager;
  private boolean isLeaf = true;

  /**
   * Create a nl.tudelft.pl2016gr2.gui.view node.
   *
   * @param dataNode   the data of the node.
   * @param parent     the parent nl.tudelft.pl2016gr2.gui.view node.
   * @param graphArea  the graph area in which the node has to be drawn.
   * @param controller the controller of the tree.
   */
  private ViewNode(IPhylogeneticTreeNode dataNode, Area graphArea,
          SelectionManager selectionManager) {
    super(NODE_RADIUS);
    this.dataNode = dataNode;
    this.area = graphArea;
    this.selectionManager = selectionManager;

    double red = Math.abs(((dataNode.getChildCount() * 13) % 200) / 255d);
    double green = Math.abs(((dataNode.getChildCount() * 34) % 200) / 255d);
    double blue = Math.abs(((dataNode.getChildCount() * 88) % 200) / 255d);
    this.setFill(new Color(red, green, blue, 1.0));

    this.setCenterX(this.getRadius() + graphArea.getStartX());
    this.setCenterY(graphArea.getCenterY());
    initializeClickedEvent();
  }

  private void initializeClickedEvent() {
    setOnMouseClicked((MouseEvent event) -> {
      System.out.println("clicked");
      selectionManager.select(this);
      event.consume();
    });
  }

  /**
   * Get the data of this node.
   *
   * @return the data of this node.
   */
  public IPhylogeneticTreeNode getDataNode() {
    return dataNode;
  }

  /**
   * Draw a root node and all of its children which fit on the screen.
   *
   * @param root             the root node.
   * @param graphPane        the pane in which to draw the node.
   * @param selectionManager the selection manager.
   * @return the nl.tudelft.pl2016gr2.gui.view node of the root.
   */
  protected static ViewNode drawRootNode(IPhylogeneticTreeNode root, Pane graphPane,
          SelectionManager selectionManager) {
    double startX = TreeManager.GRAPH_BORDER_OFFSET;
    double endX = graphPane.getWidth() - TreeManager.GRAPH_BORDER_OFFSET;
    double startY = TreeManager.GRAPH_BORDER_OFFSET;
    double endY = graphPane.getHeight() - TreeManager.GRAPH_BORDER_OFFSET;
    Area gbox = new Area(startX, endX, startY, endY);
    return drawNode(root, gbox, graphPane, selectionManager);
  }

  /**
   * Recursively draw the node and all of its children.
   *
   * @param dataNode         the data of the node to draw.
   * @param parent           the parent of the node to draw.
   * @param graphArea        the area in which the node should be drawn.
   * @param graphPane        the pane in which to draw the node.
   * @param selectionManager the selection manager.
   * @return the drawn nl.tudelft.pl2016gr2.gui.view node.
   */
  private static ViewNode drawNode(IPhylogeneticTreeNode dataNode, Area graphArea,
          Pane graphPane, SelectionManager selectionManager) {
    if (graphArea.getWidth() < NODE_DIAMETER || graphArea.getHeight() < NODE_DIAMETER
            || dataNode == null) {
      return null; // box too small to draw node.
    }
    ViewNode node = new ViewNode(dataNode, graphArea, selectionManager);
    graphPane.getChildren().add(node);
    double nextStartX = graphArea.getCenterX();
    double ySize = graphArea.getHeight() / dataNode.getDirectChildCount();
    for (int i = 0; i < dataNode.getDirectChildCount(); i++) {
      IPhylogeneticTreeNode childDataNode = dataNode.getChild(i);
      double nextStartY = ySize * i + graphArea.getStartY();
      double nextEndY = nextStartY + ySize;
      Area childArea = new Area(nextStartX, graphArea.getEndX(), nextStartY, nextEndY);
      ViewNode child = drawNode(childDataNode, childArea, graphPane, selectionManager);
      if (child == null) {
        drawElipsis(node, graphPane);
        node.isLeaf = true;
        break;
      }
      node.isLeaf = false;
      node.children.add(child);
      drawEdge(node, child, graphPane);
    }
    return node;
  }

  /**
   * Draw the edge between a parent and a child node.
   *
   * @param parent    the parent node.
   * @param child     the child node.
   * @param graphPane the pane in which the edge should be drawn.
   */
  private static void drawEdge(ViewNode parent, ViewNode child, Pane graphPane) {
    Line edge = new Line();
    edge.startXProperty().bind(parent.centerXProperty());
    edge.startYProperty().bind(parent.centerYProperty());
    edge.endXProperty().bind(child.centerXProperty());
    edge.endYProperty().bind(child.centerYProperty());
    graphPane.getChildren().add(edge);
    edge.toBack();
  }

  /**
   * Draw an elipsis after a parent node to indicate that the parent has children, but there is not
   * enough space to draw the children.
   *
   * @param node      the parent node.
   * @param graphPane the pane in which to draw the elipsis.
   */
  private static void drawElipsis(ViewNode node, Pane graphPane) {
    Line elipsis = new Line();
    elipsis.startXProperty().bind(node.centerXProperty().add(node.getRadius() * 2));
    elipsis.endXProperty().bind(node.centerXProperty().add(node.getRadius() * 4));
    elipsis.startYProperty().bind(node.centerYProperty());
    elipsis.endYProperty().bind(node.centerYProperty());
    elipsis.getStrokeDashArray().addAll(2d, 5d);
    graphPane.getChildren().add(elipsis);
  }

  /**
   * Animate the zoom in on a node.
   *
   * @param newRoot  the node which should be the root after zooming in.
   * @param timeline the timeline which is used for the animation.
   */
  public void zoomIn(ViewNode newRoot, Timeline timeline) {
    zoomIn(this.area, newRoot.area, timeline);
  }

  /**
   * Zoom in on a node and animate the zooming proces.
   *
   * @param originalArea the area of the currently drawn tree.
   * @param zoomArea     the area of the tree to zoom in on.
   * @param timeline     the timeline which is used for the animation.
   */
  private void zoomIn(Area originalArea, Area zoomArea, Timeline timeline) {
    double newX = getCenterX() - zoomArea.getStartX() - NODE_RADIUS;
    newX = newX / zoomArea.getWidth() * originalArea.getWidth();
    newX += NODE_RADIUS + TreeManager.GRAPH_BORDER_OFFSET;
    double newY = getCenterY() - zoomArea.getStartY();
    newY = newY / zoomArea.getHeight() * originalArea.getHeight();
    newY += TreeManager.GRAPH_BORDER_OFFSET;

    KeyValue kvX = new KeyValue(this.centerXProperty(), newX, Interpolator.EASE_BOTH);
    KeyValue kvY = new KeyValue(this.centerYProperty(), newY, Interpolator.EASE_BOTH);
    timeline.getKeyFrames().add(new KeyFrame(ANIMATION_DURATION, kvX, kvY));
    fireEvent(new AnimationEvent(getCenterX(), getCenterY(), newX, newY,
            originalArea.getHeight() / zoomArea.getHeight(), timeline, ANIMATION_DURATION));

    for (ViewNode child : children) {
      child.zoomIn(originalArea, zoomArea, timeline);
    }
  }

  /**
   * Animate the zoom out of 1 level (to the direct parent of the current root).
   *
   * @param timeline the timeline which is used for the animation.
   */
  public void zoomOut(Timeline timeline) {
    IPhylogeneticTreeNode newRoot = dataNode.getParent();
    double nextStartX = area.getCenterX();
    double ySize = area.getHeight() / newRoot.getDirectChildCount();
    double nextStartY = ySize * newRoot.getChildIndex(this.dataNode) + area.getStartY();
    double nextEndY = nextStartY + ySize;
    Area newArea = new Area(nextStartX, this.area.getEndX(), nextStartY, nextEndY);
    zoomOut(this.area, newArea, timeline);
  }

  /**
   * Zoom out and animate the zooming proces.
   *
   * @param originalArea the area of the currently drawn tree.
   * @param zoomArea     the area of the tree where the current tree will be drawn after zooming
   *                     out.
   * @param timeline     the timeline which is used for the animation.
   */
  private void zoomOut(Area originalArea, Area zoomArea, Timeline timeline) {
    double newX = getCenterX() - originalArea.getStartX() - NODE_RADIUS;
    newX = newX * zoomArea.getWidth() / originalArea.getWidth() + zoomArea.getStartX();
    newX += NODE_RADIUS;
    double newY = getCenterY() - originalArea.getStartY();
    newY = newY * zoomArea.getHeight() / originalArea.getHeight() + zoomArea.getStartY();

    KeyValue kvX = new KeyValue(this.centerXProperty(), newX, Interpolator.EASE_BOTH);
    KeyValue kvY = new KeyValue(this.centerYProperty(), newY, Interpolator.EASE_BOTH);
    timeline.getKeyFrames().add(new KeyFrame(ANIMATION_DURATION, kvX, kvY));
    fireEvent(new AnimationEvent(getCenterX(), getCenterY(), newX, newY, 0.5, timeline,
            ANIMATION_DURATION));

    for (ViewNode child : children) {
      child.zoomOut(originalArea, zoomArea, timeline);
    }
  }

  /**
   * Get the area of this node.
   *
   * @return the area of this node.
   */
  public Area getGraphArea() {
    return area;
  }

  /**
   * Get the nodes which are currently being displayed as leaf nodes.
   *
   * @return the nodes which are currently being displayed as leaf nodes.
   */
  public ArrayList<ViewNode> getCurrentLeaves() {
    ArrayList<ViewNode> res = new ArrayList<>();
    if (!isLeaf) {
      for (ViewNode child : children) {
        res.addAll(child.getCurrentLeaves());
      }
    } else {
      res.add(this);
    }
    return res;
  }

  @Override
  public void select() {
    System.out.println("selected: " + this);
  }

  @Override
  public void deselect() {
    System.out.println("deselected: " + this);
  }

  @Override
  public ISelectionInfo getSelectionInfo() {
    return new TextDescription(this + "\n"
            + "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quia nec honesto quic quam "
        + "honestius nec turpi turpius. Serpere anguiculos, nare anaticulas, evolare merulas, "
        + "cornibus uti videmus boves, nepas aculeis. Duo Reges: constructio interrete. Haec bene "
        + "dicuntur, nec ego repugno, sed inter sese ipsa pugnant. Itaque in rebus minime obscuris "
        + "non multus est apud eos disserendi labor. Quod cum accidisset ut alter alterum "
        + "necopinato videremus, surrexit statim.\n"
            + "\n"
            + "Sed vos squalidius, illorum vides quam niteat oratio. Et harum quidem rerum facilis "
        + "est et expedita distinctio. Sine ea igitur iucunde negat posse se vivere? Varietates "
        + "autem iniurasque fortunae facile veteres philosophorum praeceptis instituta vita "
        + "superabat. Eorum enim est haec querela, qui sibi cari sunt seseque diligunt. Ut non "
        + "sine causa ex iis memoriae ducta sit disciplina. Quorum altera prosunt, nocent altera. "
        + "Piso igitur hoc modo, vir optimus tuique, ut scis, amantissimus.\n"
            + "\n"
            + "Neque enim disputari sine reprehensione nec cum iracundia aut pertinacia recte "
        + "disputari potest. Habent enim et bene longam et satis litigiosam disputationem. Qua "
        + "ex cognitione facilior facta est investigatio rerum occultissimarum. Nam memini etiam "
        + "quae nolo, oblivisci non possum quae volo. Quid enim mihi potest esse optatius quam cum "
        + "Catone, omnium virtutum auctore, de virtutibus disputare? Si longus, levis;\n"
            + "\n"
            + "At multis se probavit. Tibi hoc incredibile, quod beatissimum. Num igitur utiliorem "
        + "tibi hunc Triarium putas esse posse, quam si tua sint Puteolis granaria? Immo alio "
        + "genere; Tollitur beneficium, tollitur gratia, quae sunt vincla concordiae. An me, "
        + "inquam, nisi te audire vellem, censes haec dicturum fuisse? Item de contrariis, a "
        + "quibus ad genera formasque generum venerunt. De illis, cum volemus.\n"
            + "\n"
            + "Nunc omni virtuti vitium contrario nomine opponitur. Vide, quaeso, rectumne sit. At "
        + "ille pellit, qui permulcet sensum voluptate. Experiamur igitur, inquit, etsi habet haec "
        + "Stoicorum ratio difficilius quiddam et obscurius. Cur deinde Metrodori liberos "
        + "commendas? Dic in quovis conventu te omnia facere, ne doleas. Traditur, inquit, ab "
        + "Epicuro ratio neglegendi doloris. Sit enim idem caecus, debilis. Dolere malum est: in "
        + "crucem qui agitur, beatus esse non potest. Sin te auctoritas commovebat, nobisne "
        + "omnibus et Platoni ipsi nescio quem illum anteponebas?");
  }
}
