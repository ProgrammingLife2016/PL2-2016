package nl.tudelft.pl2016gr2.gui.view.tree.heatmap;

import nl.tudelft.pl2016gr2.gui.view.tree.TreeNodeCircle;

import java.util.ArrayList;

/**
 * This interface must be implemented by all heatmaps.
 *
 * @author Faris
 */
public interface INodeHeatmap {

  /**
   * Notify a heatmap that the displayed leaves in the user interface have changed.
   *
   * @param newLeaves the new list of leave nodes.
   */
  void onChange(ArrayList<TreeNodeCircle> newLeaves);

}
