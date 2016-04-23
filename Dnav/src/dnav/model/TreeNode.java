package dnav.model;

import java.util.Random;

/**
 * Dummy tree class.
 * @author Faris
 */
public class TreeNode {

    private final TreeNode parent;
    private final TreeNode[] children;
    private int childCount;

    private TreeNode(TreeNode parent, int amountOfChildren) {
        this.parent = parent;
        this.children = new TreeNode[amountOfChildren];
        childCount += amountOfChildren;
    }

    public static TreeNode createRandomGraph(int treeDepth, int maxChildren) {
        return createRandomGraph(null, treeDepth, maxChildren);
    }

    private static TreeNode createRandomGraph(TreeNode parent, int treeDepth, int maxChildren) {
        if (treeDepth <= 1) {
            return new TreeNode(parent, 0);
        }
        Random rand = new Random();
        int children = rand.nextInt(maxChildren - 1) + 2; // between 2 and maxChildren
        if (rand.nextDouble() * treeDepth < 0.5) {
            children = 0;
        }
        TreeNode res = new TreeNode(parent, children);
        for (int i = 0; i < children; i++) {
            res.children[i] = createRandomGraph(res, treeDepth - 1, maxChildren);
            res.childCount += res.children[i].childCount;
        }
        return res;
    }

    public int getDirectChildCount() {
        return children.length;
    }

    public int getChildCount() {
        return childCount;
    }

    public TreeNode getChild(int i) {
        return children[i];
    }

    public boolean hasParent() {
        return parent != null;
    }

    public TreeNode getParent() {
        return parent;
    }

    public String recusiveToString(int level) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < level; i++) {
            res.append("  ");
        }
        res.append("> node");
        res.append('\n');
        for (TreeNode child : children) {
            res.append(child.recusiveToString(level + 1));
        }
        return res.toString();
    }

}
