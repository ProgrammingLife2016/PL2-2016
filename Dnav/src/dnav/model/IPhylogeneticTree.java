package dnav.model;

/**
 * This is an interface which contains all of the methods which must be implemented by a container
 * class of a phylogenetic tree.
 *
 * @author Faris
 */
public interface IPhylogeneticTree {

    /**
     * Check if this node has a parent.
     *
     * @return if this node has a parent.
     */
    public boolean hasParent();

    /**
     * Get the parent of this node.
     *
     * @return the parent of this node.
     */
    public IPhylogeneticTree getParent();

    /**
     * Get the amount of direct child nodes (i.e. child nodes with an edge to this node).
     *
     * @return theamount of direct child nodes.
     */
    public int getDirectChildCount();

    /**
     * Get the total amount of child nodes. This includes the direct child nodes and the indirect
     * child nodes.
     *
     * @return the total amount of child nodes.
     */
    public int getChildCount();

    /**
     * Get the child at the given index.
     *
     * @param index the index of the child node.
     * @return the child at the given index.
     */
    public IPhylogeneticTree getChild(int index);

    /**
     * Get the index of a direct child node.
     *
     * @param child a direct child node.
     * @return the index of the child ndoe.
     */
    public int getChildIndex(IPhylogeneticTree child);
}
