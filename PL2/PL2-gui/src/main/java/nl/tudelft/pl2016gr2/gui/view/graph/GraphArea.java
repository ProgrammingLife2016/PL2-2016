/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tudelft.pl2016gr2.gui.view.graph;

/**
 *
 * @author faris
 */
public class GraphArea {

    protected final double startX, endX, startY, endY;

    /**
     * Create a graph area.
     *
     * @param startX the x coordinate of the left border of the graph area.
     * @param endX the x coordinate of the right border of the graph area.
     * @param startY the y coordinate of the top border of the graph area.
     * @param endY the y coordinate of the bottom border of the graph area.
     */
    public GraphArea(double startX, double endX, double startY, double endY) {
        this.startX = startX;
        this.endX = endX;
        this.startY = startY;
        this.endY = endY;
    }

    /**
     * Get the width of the graph area.
     * @return the width of the graph area.
     */
    public double getWidth() {
        return endX - startX;
    }

    /**
     * Get the height of the graph area.
     * @return the height of the graph area.
     */
    public double getHeight() {
        return endY - startY;
    }

    /**
     * Get the center x coordinate of the graph area.
     * @return the center x coordinate of the graph area.
     */
    public double getCenterX() {
        return (endX - startX) / 2.0 + startX;
    }

    /**
     * Get the center y coordinate of the graph area.
     * @return the center y coordinate of the graph area.
     */
    public double getCenterY() {
        return (endY - startY) / 2.0 + startY;
    }

    /**
     * Check if the given coordinates are within the graph area.
     * @param xCoord the x coordinate.
     * @param yCoord the y coordinate.
     * @return if the given coordinates are within the graph area.
     */
    public boolean contains(double xCoord, double yCoord) {
        return xCoord >= startX && xCoord <= endX
                && yCoord >= startY && yCoord <= endY;
    }
}
