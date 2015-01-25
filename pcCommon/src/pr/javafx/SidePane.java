package pr.javafx;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class SidePane extends Pane {
	
	public SidePane(boolean isResize, String topBottomLeftRight, SideBar sideBar, Node content) {
		Parent main = isResize ?  new SplitPane() : new BorderPane();
		if (isResize) {
			
			SplitPane sp = (SplitPane)main;
			if (topBottomLeftRight.toLowerCase().equals("top") || 
					topBottomLeftRight.toLowerCase().equals("bottom"))
				sp.setOrientation(Orientation.VERTICAL);
			
			switch (topBottomLeftRight.toLowerCase()) {
				case "top":
				case "left":
					sp.getItems().addAll(sideBar, content);
					sideBar.prefWidthProperty().addListener((observ, old, newValue) -> {
				    	sp.setDividerPosition(0, round((double)newValue / sp.getWidth()));
				    });
					break;
				case "bottom":
				case "right":
					sp.getItems().addAll(content, sideBar);
					sideBar.prefWidthProperty().addListener((observ, old, newValue) -> {
				    	sp.setDividerPosition(1, round(1 - (double)newValue / sp.getWidth()));
				    });
					break;
			}
		} else {
			switch (topBottomLeftRight.toLowerCase()) {
				case "top":
					((BorderPane)main).setTop(sideBar);
					((BorderPane)main).setCenter(content);
					break;
				case "bottom":
					((BorderPane)main).setBottom(sideBar);
					((BorderPane)main).setCenter(content);
					break;
				case "left":
					((BorderPane)main).setLeft(sideBar);
					((BorderPane)main).setCenter(content);
					break;
				case "right":
					((BorderPane)main).setRight(sideBar);
					((BorderPane)main).setCenter(content);
					break;
			}
		}
		getChildren().add(main);
	}
	
	private double round(double value) {
		value = Math.round(value * 100);
		return value/100;
	}
}
