package pr.javafx;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SideBar extends VBox {
	private final Button controlButton;
	private double expandedWidth;
	private double duration = 250;
	private boolean isButton = false;
	
	public SideBar(double prefWidth, String collapse, String show, Node... nodes) {
		expandedWidth = prefWidth;
		this.setPrefWidth(expandedWidth);
		this.setMinWidth(0);
		
		widthProperty().addListener((observ, old, newValue) -> {
			setPrefWidth((double)newValue);
			if (!isButton && (double)newValue != 0) {
				expandedWidth = (double)newValue;
			}
		});
		
		setAlignment(Pos.CENTER);
		getChildren().addAll(nodes);
		
		controlButton = new Button(collapse);
		controlButton.getStyleClass().add("hide-left");
		
		controlButton.setOnAction(e -> {
			isButton = true;
			controlButton.setDisable(true);
			final Animation hideSidebar = new Transition() {{
					setCycleDuration(Duration.millis(duration));
				}
				@Override
	            protected void interpolate(double frac) {
					final double curWidth = expandedWidth * (1.0 - frac);
					setPrefWidth(curWidth);
					setTranslateX(-expandedWidth + curWidth);
				}
			};
		
			hideSidebar.onFinishedProperty().set(eah -> {
				setVisible(false);
				controlButton.setText(show);
				controlButton.getStyleClass().remove("hide-left");
				controlButton.getStyleClass().add("show-right");
				isButton = false;
				controlButton.setDisable(false);
			});
		
			final Animation showSidebar = new Transition() {{
					setCycleDuration(Duration.millis(duration));
				}
				@Override
				protected void interpolate(double frac) {
					final double curWidth = expandedWidth * frac;
					setPrefWidth(curWidth);
					setTranslateX(-expandedWidth + curWidth);
				}
			};
			
			showSidebar.onFinishedProperty().set(eaf -> {
				controlButton.setText(collapse);
				controlButton.getStyleClass().add("hide-left");
				controlButton.getStyleClass().remove("show-right");
				isButton = false;
				controlButton.setDisable(false);
			});
			
			if (showSidebar.statusProperty().get() == Animation.Status.STOPPED && 
					hideSidebar.statusProperty().get() == Animation.Status.STOPPED) {
				if (isVisible()) {
					hideSidebar.play();
				} else {
					setVisible(true);
					showSidebar.play();
				}
			}
		});
		
	}
	
	public Button getControlButton() { 
		return controlButton; 
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}
}
