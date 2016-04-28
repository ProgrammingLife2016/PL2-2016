package nl.tudelft.pl2016gr2.gui.model;


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
        // TODO Should work for now, but need to start working with ids asap
        return new PhylogeneticTreeNode(node.getChild(index));
    }

    @Override
    public int getChildIndex(IPhylogeneticTreeNode child) {
        // TODO can't directly reach without changing library
        return 0;
    }
}
