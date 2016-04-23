/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnav.view.graph;

/**
 *
 * @author faris
 */
public class GraphArea {

    protected final double startX, endX, startY, endY;

    public GraphArea(double startX, double endX, double startY, double endY) {
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

    public double getWidth() {
        return endX - startX;
    }

    public double getHeight() {
        return endY - startY;
    }

    public double getCenterX() {
        return (endX - startX) / 2.0 + startX;
    }

    public double getCenterY() {
        return (endY - startY) / 2.0 + startY;
    }

    public boolean contains(double xCoord, double yCoord) {
        return xCoord >= startX && xCoord <= endX
                && yCoord >= startY && yCoord <= endY;
    }
    
    public GraphArea rescaleWidth(double scale) {
        return new GraphArea(startX * scale, endX * scale, startY, endY);
    }
    
    public GraphArea rescaleHeight(double scale) {
        return new GraphArea(startX, endX, startY * scale, endY * scale);
    }

//    public Rectangle createHighlightBox() {
//        Rectangle res = new Rectangle();
//        res.setX(startX);
//        res.setY(startY);
//        res.setWidth(endX - startX);
//        res.setHeight(endY - startY);
//        res.getStrokeDashArray().addAll(2d, 4d);
//        res.setFill(null);
//        res.setStroke(Color.DARKRED);
//        res.setMouseTransparent(true);
//        res.setStrokeWidth(2.0);
//        return res;
//    }
}
