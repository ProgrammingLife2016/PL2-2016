package nl.tudelft.pl2016gr2.core.factories;

import net.sourceforge.olduvai.treejuxtaposer.drawer.Tree;

public interface TreeFactory {

  /**
   * Build the phylogenetic tree.
   *
   * @return the tree.
   */
  Tree getTree();
}
