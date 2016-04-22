/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnav.view;

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
}
