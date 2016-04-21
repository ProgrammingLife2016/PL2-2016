package dnav.view;

import dnav.model.TreeNode;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 *
 * @author Faris
 */
public class ViewNode extends Circle {

    private final TreeNode dataNode;

    public ViewNode(TreeNode dataNode) {
        super(10);
        this.dataNode = dataNode;
        
        Random rand = new Random();
        double red = (dataNode.getChildCount() * 13 % 200) / 255d;
        double green = (dataNode.getChildCount() * 34 % 200) / 255d;
        double blue = (dataNode.getChildCount() * 88 % 200) / 255d;
        this.setFill(new Color(red, green, blue, 1.0));
    }

    public TreeNode getDataNode() {
        return dataNode;
    }

}
