package pr.javafx;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class SidePane extends Pane {
	private final BooleanProperty isShowingProperty = new SimpleBooleanProperty(true);
	private final Button controlButton = new Button();
	private boolean isButton = false;
	private double duration = 250;
	private String collapse;
	private String show;
	private double expandedSize;
	private double expandedWidthOld;
	private boolean isHorizontal;
	private SideBar sideBar;
	private Pos aligment;
	private String position;
	private Node content;
	private Node sideBarContent;
	private boolean isResize;
	private boolean isNotInitYet = false;
	
	public SidePane() {
		super();
	}
	
	public SidePane(boolean isResize, String position) {
		this();
		this.isResize = isResize;
		this.position = position;
	}
	
	public SidePane(boolean isResize, String position, SideBar sideBar, Node content) {
		this(isResize, position);
		this.sideBar = sideBar;
		this.content = content;
		init();
	}
	
	private void init() {
		if (isNotInitYet) return;
		isNotInitYet = true;
		
		this.position = position.toLowerCase();
		isHorizontal = position.toLowerCase().equals("left") || position.toLowerCase().equals("right");

		if (expandedSize == 0 && sideBar.getExpandedSize() != 0) expandedSize = sideBar.getExpandedSize();
		
		Parent main = isResize ?  new SplitPane() : new BorderPane();
		
		if (isResize) {
			SplitPane sp = (SplitPane)main;
			SplitPane.setResizableWithParent(sideBar, Boolean.FALSE);
			sp.setStyle("-fx-box-border: transparent;");
			sp.prefWidthProperty().bind(widthProperty());
			sp.prefHeightProperty().bind(heightProperty());
			
			sp.setOnMouseClicked(e -> {
				if (sideBar.getMinHeight() != 0) {
					sideBar.setMinHeight(-1);
					sideBar.setMaxHeight(-1);
				}
				if (sideBar.getMinWidth() != 0) {
					sideBar.setMinWidth(-1);
					sideBar.setMaxWidth(-1);
				}
			});
			
			if (!isHorizontal) sp.setOrientation(Orientation.VERTICAL);
			
			sideBar.visibleProperty().addListener((observ, old, newValue) -> disableEnableDividers(sp, !newValue));
			
			if (isHorizontal) {
				sideBar.widthProperty().addListener((observ, old, newValue) -> {
					if (isButton || (double)newValue == 0) return;
					if (sideBar.getContent() instanceof ScrollPane) {
						ScrollPane spC = (ScrollPane)sideBar.getContent();
						
						if ((double)newValue < spC.getMinWidth()) sideBar.setMinWidth(spC.getMinWidth());
						if ((double)newValue > spC.getMaxWidth()) sideBar.setMaxWidth(spC.getMaxWidth());
					}
					if (sideBar.getContent() instanceof Pane) {
						Pane spC = (Pane)sideBar.getContent();
						
						if ((double)newValue < spC.getMinWidth()) sideBar.setMinWidth(spC.getMinWidth());
						if ((double)newValue > spC.getMaxWidth()) sideBar.setMaxWidth(spC.getMaxWidth());
					}
					if (!isButton && (double)old != 0) expandedSize = (double)newValue;
				});
			} else {
				sideBar.heightProperty().addListener((observ, old, newValue) -> {
					if (isButton) return;
					if (sideBar.getContent() instanceof ScrollPane) {
						ScrollPane spC = (ScrollPane)sideBar.getContent();
						
						if ((double)newValue < spC.getMinHeight()) sideBar.setMinHeight(spC.getMinHeight());
						if ((double)newValue > spC.getMaxHeight()) sideBar.setMaxHeight(spC.getMaxHeight());
					}
					if (sideBar.getContent() instanceof Pane) {
						Pane spC = (Pane)sideBar.getContent();
						
						if ((double)newValue < spC.getMinHeight()) sideBar.setMinHeight(spC.getMinHeight());
						if ((double)newValue > spC.getMaxHeight()) sideBar.setMaxHeight(spC.getMaxHeight());
					}
					if (!isButton && (double)newValue != 0 && (double)old != 0) expandedSize = (double)newValue;
				});
			}
			
			switch (position.toLowerCase()) {
				case "top":
					sp.getItems().addAll(sideBar, content);
					sideBar.prefHeightProperty().addListener((observ, old, newValue) -> {
						sp.setDividerPosition(0, (double)newValue / sp.getHeight());
					});
					break;
				case "left":
					sp.getItems().addAll(sideBar, content);
					sideBar.prefWidthProperty().addListener((observ, old, newValue) -> {
						sp.setDividerPosition(0, (double)newValue / sp.getWidth());
					});
					break;
				case "bottom":
					sp.getItems().addAll(content, sideBar);
					sideBar.prefHeightProperty().addListener((observ, old, newValue) -> {
						sp.setDividerPosition(0, 1 - (double)newValue / sp.getHeight());
					});
					break;
				case "right":
					sp.getItems().addAll(content, sideBar);
					sideBar.prefWidthProperty().addListener((observ, old, newValue) -> {
						sp.setDividerPosition(0, 1 - (double)newValue / sp.getWidth());
					});
					break;
			}
		} else {
			BorderPane bp = (BorderPane)main;
			bp.prefWidthProperty().bind(widthProperty());
			bp.prefHeightProperty().bind(heightProperty());
			
			switch (position.toLowerCase()) {
				case "top":
					bp.setTop(sideBar);
					break;
				case "bottom":
					bp.setBottom(sideBar);
					break;
				case "left":
					bp.setLeft(sideBar);
					break;
				case "right":
					bp.setRight(sideBar);
					break;
			}
			bp.setCenter(content);
		}
		
		getChildren().add(main);
		
		isShowingProperty.bind(sideBar.visibleProperty());
		isShowingProperty.addListener((observ, old, newValue) -> {
			if (newValue) {
				if (!sideBar.isVisible()) showSide();
			} else {
				if (sideBar.isVisible()) {
					hideSide();
				}
			}
		});
		
		sideBar.visibleProperty().addListener((observ, old, newValue) -> {
			if (!newValue) {
				expandedWidthOld = expandedSize;
			}
		});
	}
	
	private void disableEnableDividers(SplitPane split, boolean isDisable) {
		split.lookupAll(".split-pane-divider").forEach(divider -> divider.setMouseTransparent(isDisable));
	}

	public void showHideSide() {
		if (controlButton.isDisable()) return;
		
		if (!sideBar.isVisible()) {
			showSide();
		} else {
			hideSide();
		}
	}
	
	public void showSide() {
		controlButton.setDisable(true);
		sideBar.setVisible(true);
		if (expandedWidthOld < 10) expandedWidthOld = 10;
		
		final Animation showSidebar = new Transition() {{
				setCycleDuration(Duration.millis(duration));
			}
		
			@Override
			protected void interpolate(double frac) {
				final double curWidth = expandedWidthOld * frac;
				animate(curWidth);
			}
		};
		
		showSidebar.onFinishedProperty().set(e -> {
			controlButton.setText(collapse);
			isButton = false;
			controlButton.setDisable(false);
		});
		
		Platform.runLater(() -> showSidebar.play());
	}
	
	public void hideSide() {
		if (expandedSize == 0) {
			if (isHorizontal && sideBar.getWidth() != 0) expandedSize = sideBar.getWidth();
			if (!isHorizontal && sideBar.getHeight() != 0) expandedSize = sideBar.getHeight();
		}
		expandedWidthOld = expandedSize;
		
		isButton = true;
		controlButton.setDisable(true);
		
		final Animation hideSidebar = new Transition() {{
				setCycleDuration(Duration.millis(duration));
			}
		
			@Override
			protected void interpolate(double frac) {
				final double curWidth = expandedWidthOld * (1.0 - frac);
				animate(curWidth);
			}
		};
		
		hideSidebar.onFinishedProperty().set(e -> {
			sideBar.setVisible(false);
			controlButton.setText(show);
			isButton = false;
			controlButton.setDisable(false);
		});
		
		Platform.runLater(() -> hideSidebar.play());
	}
	
	private void animate(double curWidth) {
		if (isHorizontal) {
			sideBar.setMinWidth(curWidth);
			sideBar.setPrefWidth(curWidth);
		} else {
			sideBar.setMinHeight(curWidth);
			sideBar.setPrefHeight(curWidth);
		}
	}

	public Button getControlButton(String collapse, String show) {
		this.collapse = collapse;
		this.show = show;
		controlButton.setText(collapse);
		controlButton.setOnAction(e -> showHideSide());
		return controlButton;
	}

	public boolean isButton() {
		return isButton;
	}

	public SideBar getSideBar() {
		return sideBar;
	}

	public void setSideBar(SideBar sideBar) {
		this.sideBar = sideBar;
		if (sideBar != null && content != null && position != null) init();
	}
	
	public Node getSideBarContent() {
		return sideBarContent;
	}
	
	public void setSideBarContent(Node content) {
		this.sideBarContent = content;
		this.sideBar = new SideBar(expandedSize, content);
		if (sideBar != null && content != null && position != null) init();
	}

	public Node getContent() {
		return content;
	}

	public void setContent(Node content) {
		this.content = content;
		if (sideBar != null && content != null && position != null) init();
	}

	public boolean isResize() {
		return isResize;
	}

	public void setResize(boolean isResize) {
		this.isResize = isResize;
		if (sideBar != null && content != null && position != null) init();
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
		if (sideBar != null && content != null && position != null) init();
	}

	public double getExpandedSize() {
		return expandedSize;
	}

	public void setExpandedSize(double expandedWidth) {
		Platform.runLater(() -> {
			this.expandedWidthOld = expandedWidth;
			if (isHorizontal) {
				sideBar.setMinWidth(expandedWidth);
				sideBar.setPrefWidth(expandedWidth);
				sideBar.setMaxWidth(expandedWidth);
			} else {
				sideBar.setMinHeight(expandedWidth);
				sideBar.setPrefHeight(expandedWidth);
				sideBar.setMaxHeight(expandedWidth);
			}
			this.expandedSize = expandedWidth;
		});
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration == 0 ? 10 : duration;
	}

	public Pos getAligment() {
		return aligment;
	}

	public void setAligment(Pos aligment) {
		this.aligment = aligment;
		sideBar.setAlignment(aligment);
	}

	public BooleanProperty isShowingProperty() {
		return isShowingProperty;
	}
}
