package dnav.model;

import java.util.Random;

/**
 * Dummy tree class.
 *
 * @author Faris
 */
public class TreeNode implements IPhylogeneticTreeNode {

    private final TreeNode parent;
    private final TreeNode[] children;
    private int childCount;

    /**
     * Create a tree node.
     *
     * @param parent the parent of this node (null if no parent).
     * @param amountOfChildren the amount of children of this node.
     */
    private TreeNode(TreeNode parent, int amountOfChildren) {
        this.parent = parent;
        this.children = new TreeNode[amountOfChildren];
        childCount += amountOfChildren;
    }

    /**
     * Create a random tree.
     *
     * @param treeDepth the depth of the tree after this node.
     * @param maxChildren the maximum amount of children per node (must be at least 2).
     * @return the root of the generated tree.
     */
    public static TreeNode createRandomGraph(int treeDepth, int maxChildren) {
        return createRandomGraph(null, treeDepth, maxChildren);
    }

    /**
     * Recursively create a random tree.
     *
     * @param parent the parent of the current level of the graph (null for level 0).
     * @param treeDepth the depth of the tree after this node.
     * @param maxChildren the maximum amount of children per node (must be at least 2).
     * @return the root of the generated (sub) tree.
     */
    private static TreeNode createRandomGraph(TreeNode parent, int treeDepth, int maxChildren) {
        if (treeDepth <= 1) {
            return new TreeNode(parent, 0);
        }
//        Random rand = new Random();
//        int children = rand.nextInt(maxChildren - 1) + 2; // between 2 and maxChildren
//        if (rand.nextDouble() * treeDepth < 0.5) {
//            children = 0;
//        }
        TreeNode res = new TreeNode(parent, 2);
        for (int i = 0; i < 2; i++) {
            res.children[i] = createRandomGraph(res, treeDepth - 1, maxChildren);
            res.childCount += res.children[i].childCount;
        }
        return res;
    }

    @Override
    public int getDirectChildCount() {
        return children.length;
    }

    @Override
    public int getChildCount() {
        return childCount;
    }

    @Override
    public TreeNode getChild(int i) {
        return children[i];
    }

    @Override
    public boolean hasParent() {
        return parent != null;
    }

    @Override
    public TreeNode getParent() {
        assert hasParent();
        return parent;
    }

    @Override
    public int getChildIndex(IPhylogeneticTreeNode child) {
        for (int i = 0; i < children.length; i++) {
            if (children[i] == child) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException();
    }
}
