/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dnav.view;

import javafx.scene.layout.Pane;

/**
 *
 * @author faris
 */
public interface ZoomHandler {

	public void handleZoom(GraphArea zoomedArea, Pane graphPane);
}
