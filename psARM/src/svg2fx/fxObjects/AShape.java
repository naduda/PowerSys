package svg2fx.fxObjects;

import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;

import pr.log.LogFiles;
import single.ProgramProperty;
import single.SingleObject;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public abstract class AShape extends Group {
	
	private static final double RECT_LINE_WIDTH = 3;
	private static final double MOUSE_DURATION_MILLS = 250;
	private final Timeline timeline = new Timeline();
	
	public final Rectangle rect = new Rectangle();
	private boolean isUpdateSignal = true;
	private Paint oldRectColor; 
	public int updateInterval;
	private DoubleProperty valueProp = new SimpleDoubleProperty();
	private BooleanProperty tsignalProp = new SimpleBooleanProperty();
	private Date lastDataDate;
	private double deadZone;
	
	public AShape() {
		//Пунктирна лінія
		rect.getStrokeDashArray().addAll(2d, 5d);
		rect.setStrokeWidth(RECT_LINE_WIDTH);
		rect.setFill(Color.TRANSPARENT);

		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setAutoReverse(true);
		final KeyValue kv = new KeyValue(rect.strokeProperty(), Color.BLACK);
		final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
		timeline.getKeyFrames().add(kf);
		
		Duration maxTimeBetweenSequentialClicks = Duration.millis(MOUSE_DURATION_MILLS);
        PauseTransition clickTimer = new PauseTransition(maxTimeBetweenSequentialClicks);
        final IntegerProperty sequentialClickCount = new SimpleIntegerProperty(0);
        clickTimer.setOnFinished(event -> {
            int count = sequentialClickCount.get();
            if (count == 2) {
            	Platform.runLater(() -> onDoubleClick());
            }

            sequentialClickCount.set(0);
        });
        
	    setOnMouseClicked(event -> {
	    	setSelection(true);
	    	sequentialClickCount.set(sequentialClickCount.get() + 1);
            clickTimer.playFromStart();
		});
	    
	    valueProp.set(-888888.888888);
	    valueProp.addListener((observable, oldValue, newValue) -> {
	    	double deadZoneCur = (Double)oldValue == 0 ? 0 : 
	    		Math.abs(((Double)oldValue - (Double)newValue) * 100 / (Double)oldValue);
	    	
	    	if (deadZoneCur >= deadZone) Platform.runLater(() -> onValueChange((Double) newValue));
	    });
	    
	    tsignalProp.addListener((observable, oldValue, newValue) -> {
	    	if (newValue) Platform.runLater(() -> onSignalUpdate());
	    });
	}
	
	public AShape(Node g) {
		this();
		
		rect.setX(g.getBoundsInLocal().getMinX());
		rect.setY(g.getBoundsInLocal().getMinY());
		rect.setWidth(g.getBoundsInLocal().getWidth());
		rect.setHeight(g.getBoundsInLocal().getHeight());
		((Group) g).getChildren().add(rect);
			
		getChildren().add(g);
		setId(g.getId());
	}
	
	abstract void onDoubleClick();
	abstract void onValueChange(Double newValue);
	abstract void onSignalUpdate(); 
	
	public void setSelection(boolean isSelected) {
		boolean isChanged = !this.equals(SingleObject.selectedShape);
		if (isChanged) {
			ProgramProperty.selectedShapeChangeProperty.set(false);
		}
		if (isSelected) {
			if (SingleObject.selectedShape != null) {
				SingleObject.selectedShape.setSelection(false);
			}
			
			oldRectColor = rect.getStroke();
			rect.setStroke(Color.BLUE);
			rect.getStrokeDashArray().addAll(2d, 5d);
			
			SingleObject.selectedShape = (EShape) this;
			if (isChanged) ProgramProperty.selectedShapeChangeProperty.set(true);
		} else {
			if (oldRectColor != null) {
				rect.getStrokeDashArray().clear();
				rect.setStroke(oldRectColor);
				oldRectColor = null;
			} else {
				rect.setStroke(Color.TRANSPARENT);
			}
		}
	}
	
	public Node getNodeById(String path) {
		Node ret = getChildren().get(0);
		StringTokenizer st = new StringTokenizer(path, "/");
		while (st.hasMoreElements()) {
			int id = Integer.parseInt(st.nextElement().toString());
			ret = ((Group)ret).getChildren().get(id);
		}
		ret = ((Group)ret).getChildren().get(0);
		return ret;
	}
	
	public void getAllNodes(Group g, String path) {
		int i = 0;
		for(Node c : g.getChildren()) { 
			if (c instanceof Group) {
				getAllNodes((Group) c, path + "/" + i);
			} else {
				if (path.length() > 4) {
					path = path.substring(3, path.length());
					path = "[" + path + "]";
					LogFiles.log.log(Level.INFO, path + " - " + c);
				}
			}
			i++;
		}
	}
	
	public void rotate(Shape n, double deg) {
		while (Math.abs(deg) > 180) {
			deg = deg > 0 ? deg - 360 : deg + 360;
		}
		n.setRotate(-deg);

		double adeg = Math.abs(deg);
		double sWidth = n.getBoundsInLocal().getWidth() ;
		double sHeight = n.getBoundsInLocal().getHeight() ;
		
		double sinD = Math.abs(Math.sin(Math.toRadians(adeg)));
		double cosD = Math.abs(Math.cos(Math.toRadians(adeg)));
		
		double sX = deg > 0 ? 
				deg <= 90 ? 
						0.5 * (sinD * sHeight - cosD * sWidth + sWidth) : 0.5 * (-sinD * sHeight + cosD * sWidth + sWidth) : 
				deg >= -90 ? 
						0.5 * (cosD * sWidth + sinD * sHeight - sWidth) : 0.5 * (cosD * sWidth + sinD * sHeight + sWidth);
		n.setTranslateX(-sX);
		
		double sY = deg > 0 ? 0.5 * (sinD * sWidth + cosD * sHeight - sHeight) : 0.5 * (sinD * sWidth - cosD * sHeight + sHeight);
		n.setTranslateY(deg > 0 ? -sY : sY);
	}
	
	public void updateSignal(int sec) {
		if (updateInterval == 0) updateInterval = sec;
		if (lastDataDate == null || !isUpdateSignal) return;
		if ((System.currentTimeMillis() - lastDataDate.getTime()) < sec * 1000) {
			if ((Color.WHITE).equals(rect.getStroke())) {
				rect.setStroke(Color.TRANSPARENT);
			}
			timeline.stop();
		} else {
			rect.getStrokeDashArray().clear();
			rect.setStroke(Color.WHITE);
			//timeline.play();
		}
	}
	
	public void setNormalMode() {
		isUpdateSignal = false;
		rect.getStrokeDashArray().clear();
		rect.setStroke(Color.RED);
	}
	
	public void clearNormalMode() {
		isUpdateSignal = true;
		rect.setStroke(Color.TRANSPARENT);
		if (updateInterval > 0) updateSignal(updateInterval);
	}
	
	public void setNotConfirmed(boolean isConfirmed) {
		if (isConfirmed) {
			clearNormalMode();
			timeline.stop();
		} else {
			isUpdateSignal = false;
			rect.getStrokeDashArray().clear();
			rect.setStroke(Color.YELLOW);
			//timeline.play();
		}
	}
	
	public Paint getColorByName(String colorName) {
		return Color.valueOf(colorName.toUpperCase());
	}
	
	public LinearGradient getColorByName(String colorName1, String colorName2) {
		Stop[] stops = new Stop[] { 
				new Stop(0, Color.valueOf(colorName1.toUpperCase())), 
				new Stop(1, Color.valueOf(colorName2.toUpperCase()))
		};
		return new LinearGradient(0, 0.1, 0.1, 0, true, CycleMethod.NO_CYCLE, stops);
	}

	public DoubleProperty getValueProp() {
		return valueProp;
	}

	public BooleanProperty getTsignalProp() {
		return tsignalProp;
	}

	public void setLastDataDate(Date lastDataDate) {
		this.lastDataDate = lastDataDate;
	}

	public void setDeadZone(double deadZone) {
		this.deadZone = deadZone;
	}
}
