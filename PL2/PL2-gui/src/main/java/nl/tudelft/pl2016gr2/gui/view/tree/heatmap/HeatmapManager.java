package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.tudelft.pl2016gr2.gui.view.MetadataPropertyMap;
import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;
import nl.tudelft.pl2016gr2.model.MetaData;
import nl.tudelft.pl2016gr2.thirdparty.testing.utility.TestId;

import java.util.ArrayList;

/**
 * Manages the different kinds of heatmaps.
 *
 * @author faris
 */
public class HeatmapManager {

  private final Pane firstPane;
  private final Pane secondPane;
  private final MenuButton firstMenuButton;
  private final MenuButton secondMenuButton;
  @TestId(id = "firstHeatmap")
  private PropertyHeatmap firstHeatmap;
  @TestId(id = "secondHeatmap")
  private PropertyHeatmap secondHeatmap;

  /**
   * Create a heatmap manager.
   *
   * @param firstPane           the first heatmap pane.
   * @param secondPane          the second heatmap pane.
   * @param firstMenuButton     the first heatmap menu button.
   * @param secondMenuButton    the second heatmap menu button.
   * @param metadataPropertyMap the metadata property map.
   */
  @SuppressWarnings("unchecked")
  public HeatmapManager(Pane firstPane, Pane secondPane, MenuButton firstMenuButton,
      MenuButton secondMenuButton, ObservableValue<MetadataPropertyMap> metadataPropertyMap) {
    this.firstPane = firstPane;
    this.secondPane = secondPane;
    this.firstMenuButton = firstMenuButton;
    this.secondMenuButton = secondMenuButton;
    initHeatmaps();
    initComboboxes(metadataPropertyMap);
  }

  /**
   * Initialize the heatmaps with an empty list of leaves. The first heatmap will show the density
   * and the second heatmap will be empty by default.
   */
  private void initHeatmaps() {
    firstHeatmap = new PropertyHeatmap(firstPane, new DensityColorMapping());
    firstMenuButton.setText(DensityMenuItem.DENSITY_HEATMAP_TEXT);
    secondHeatmap = new PropertyHeatmap(secondPane, heatmapRectangles -> {
      heatmapRectangles.forEach((TreeNodeCircle node, Rectangle rectangle) -> {
        rectangle.setFill(Color.WHITE);
      });
    });
  }

  /**
   * Initialize the content of the combo boxes.
   */
  private void initComboboxes(ObservableValue<MetadataPropertyMap> metadataPropertyMap) {
    metadataPropertyMap.addListener((observable, old, newValue) -> {
      firstMenuButton.getItems().clear();
      secondMenuButton.getItems().clear();

      firstMenuButton.getItems().add(new DensityMenuItem(firstMenuButton, firstHeatmap));
      secondMenuButton.getItems().add(new DensityMenuItem(secondMenuButton, secondHeatmap));

      newValue.forEach((String column, ArrayList<String> values) -> {
        SelectableProperty property = new SelectableProperty(column, values);
        firstMenuButton.getItems().add(new MetadataMenu(firstMenuButton, firstHeatmap, property));
        secondMenuButton.getItems()
            .add(new MetadataMenu(secondMenuButton, secondHeatmap, property));
      });
    });
  }

  /**
   * Change the leaves of the phylogenetic tree. Here leaves mean the nodes which don't have any
   * child nodes in the user interface. This doesn't mean that these nodes are real leave nodes, as
   * there may just be too few space to display the child nodes of a node.
   *
   * @param currentLeaves the new leaves.
   */
  public void setLeaves(ArrayList<TreeNodeCircle> currentLeaves) {
    firstHeatmap.onChange(currentLeaves);
    secondHeatmap.onChange(currentLeaves);
  }

  /**
   * A metadata heatmap menu. In this menu one of the possible values of a selectable property can
   * be selected to display in the heatmap.
   */
  private static class MetadataMenu extends Menu {

    /**
     * Construct a metadata menu item.
     *
     * @param menuButton the menu button to which this menu is bound.
     * @param heatmap    the heatmap to which this menu is bound.
     * @param property   the property of which a value can be selected in this menu.
     */
    private MetadataMenu(MenuButton menuButton, PropertyHeatmap heatmap,
        SelectableProperty property) {
      setText(property.propertyName);
      property.propertyValues.forEach((String value) -> {
        MenuItem menuItem = new MenuItem(value);
        getItems().add(menuItem);
        menuItem.setOnAction((ActionEvent event) -> {
          menuButton.setText(property.propertyName + " : " + value);
          heatmap.setValueChecker(new MetadataColorMapping(property.getChecker(value)));
        });
      });
    }
  }

  /**
   * A density heatmap item. This is the only possible heatmap value which is independent from the
   * metadata file.
   */
  private static class DensityMenuItem extends MenuItem {

    private static final String DENSITY_HEATMAP_TEXT = "amount of hidden nodes";

    /**
     * Construct a density heatmap item.
     *
     * @param menuButton the menu button to which this menu item is bound.
     * @param heatmap    the heatmap to which this menu item is bound.
     */
    private DensityMenuItem(MenuButton menuButton, PropertyHeatmap heatmap) {
      setText(DENSITY_HEATMAP_TEXT);
      setOnAction((ActionEvent event) -> {
        menuButton.setText(DENSITY_HEATMAP_TEXT);
        heatmap.setValueChecker(new DensityColorMapping());
      });
    }
  }

  /**
   * This class stores a property name and the possible values which can be selected for the
   * property.
   */
  private static class SelectableProperty {

    private final String propertyName;
    private final ObservableList<String> propertyValues;

    /**
     * Construct a selectable property.
     *
     * @param propertyName   the name of the property.
     * @param propertyValues all of the possible values which the property can take.
     */
    private SelectableProperty(String propertyName, ArrayList<String> propertyValues) {
      this.propertyName = propertyName;
      this.propertyValues = new ObservableListWrapper<>(propertyValues);
    }

    /**
     * Get a checker for a given value of this property.
     *
     * @param value the selected value of this property.
     * @return a checker which returns true iff a metadata object contains the correct property.
     */
    private IMetadataChecker getChecker(String value) {
      assert propertyValues.contains(value);
      return (MetaData metadata) -> metadata.getValueForColumnName(propertyName).equals(value);
    }

    @Override
    public String toString() {
      return propertyName;
    }
  }
}
