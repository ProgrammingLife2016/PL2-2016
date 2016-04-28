package nl.tudelft.pl2016gr2.gui.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhylogeneticTreeNode implements IPhylogeneticTreeNode {

    private final net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode node;

    public PhylogeneticTreeNode(net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode node) {
        this.node = node;
    }

    @Override
    public boolean hasParent() {
        return !node.isRoot();
    }

    @Override
    public IPhylogeneticTreeNode getParent() {
        return new PhylogeneticTreeNode(node.parent());
    }

    @Override
    public int getDirectChildCount() {
        return node.numberChildren();
    }

    @Override
    public int getChildCount() {
        int total = node.numberChildren();

        for (int i = 0; i < getDirectChildCount(); i++) {
            total += new PhylogeneticTreeNode(node.getChild(i)).getChildCount();
        }

        return total;
    }

    @Override
    public IPhylogeneticTreeNode getChild(int index) {
        return new PhylogeneticTreeNode(node.getChild(index));
    }

    @Override
    public int getChildIndex(IPhylogeneticTreeNode child) {
        try {
            Field children = net.sourceforge.olduvai.treejuxtaposer.drawer.TreeNode.class.
                    getDeclaredField("children");
            children.setAccessible(true);
            return ((ArrayList) children.get(node)).indexOf(((PhylogeneticTreeNode) child).node);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException |
                IllegalAccessException ex) {
            Logger.getLogger(PhylogeneticTreeNode.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}
