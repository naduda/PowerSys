package graphicObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Tsignal;
import ui.Main;
import ui.Scheme;
import xml.ShapeX;
import javafx.animation.PauseTransition;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public abstract class AShape extends Group {
	
	public enum typeSignalRef {
		 TI(1), TS(2), TU(3);
		 
		 private int code;
		 
		 private typeSignalRef(int c) {
		   code = c;
		 }
		 
		 public int getCode() {
		   return code;
		 }
	}
	private static final double RECT_LINE_WIDTH = 3;
	private static final double MOUSE_DURATION_MILLS = 250;	
	
	public static final int ONE_MM = 4;
	public final Map<Integer, Tsignal> signals = new HashMap<Integer, Tsignal>();
	public final Rectangle rect = new Rectangle();
	public final Group shapes = new Group();
	
	public Color lineColor;
	public Color fillColor;
	public double lineWidth;
	public boolean isON = false;	
	
	private Date lastDataDate;
	
	public AShape() {
		//Пунктирна лінія
		rect.getStrokeDashArray().addAll(2d, 5d);
		rect.setStrokeWidth(RECT_LINE_WIDTH);
		rect.setFill(Color.TRANSPARENT);
	}
	
	public AShape(ShapeX sh) {
		this();
		lineWidth = sh.getLineWeight();
		lineColor = Scheme.getColor(sh.getLineColor());
		fillColor = Scheme.getColor(sh.getFillColor());
		
		rect.setWidth(sh.getWidth());
		rect.setHeight(sh.getHeight());
		rect.setX(-lineWidth/2);

		setSignals(sh);
		setId(signals.size() == 0 ? "" : signals.get(typeSignalRef.TS.getCode()) != null ? 
				"" + signals.get(typeSignalRef.TS.getCode()).getIdsignal() : "" + signals.get(typeSignalRef.TI.getCode()).getIdsignal());

	    Duration maxTimeBetweenSequentialClicks = Duration.millis(MOUSE_DURATION_MILLS);
        PauseTransition clickTimer = new PauseTransition(maxTimeBetweenSequentialClicks);
        final IntegerProperty sequentialClickCount = new SimpleIntegerProperty(0);
        clickTimer.setOnFinished(event -> {
            int count = sequentialClickCount.get();
            if (count == 2) {
            	setTS(isON ? 0 : 1);
            }

            sequentialClickCount.set(0);
        });
        
	    setOnMouseClicked(event -> {
	    	setSelection(true);
	    	sequentialClickCount.set(sequentialClickCount.get() + 1);
            clickTimer.playFromStart();
		});
	    
	    getChildren().add(rect);
	    
	    setRotate(sh.getAngle());
	    setLayoutX(sh.getX());
	    setLayoutY(sh.getY());	    
	    setScaleX(sh.getZoomX());
	    setScaleY(sh.getZoomY());
	}
	
	public void setStrokeAndColor() {
		shapes.getChildren().forEach(sh -> {
			((Shape)sh).setStroke(lineColor);
			((Shape)sh).setStrokeWidth(lineWidth);
		});
	}
	
	public void setSelection(boolean isSelected) {
		if (this.equals(Scheme.selectedShape) && isSelected) return;
		if (isSelected) {
			rect.setStroke(Color.BLUE);
			rect.getStrokeDashArray().addAll(2d, 5d);
			if (Scheme.selectedShape != null) {
				Scheme.selectedShape.setSelection(false);
			}
			Scheme.selectedShape = this;
		} else {
			rect.setStroke(Color.TRANSPARENT);
		}
	}
	
	public void updateSignal(int sec) {
		if (lastDataDate == null) return;
		if ((System.currentTimeMillis() - lastDataDate.getTime()) < sec * 1000) {
			rect.setStroke(Color.TRANSPARENT);	
		} else {
			rect.getStrokeDashArray().clear();
			rect.setStroke(Color.WHITE);
		}
	}
	
	private void setSignals(ShapeX sh) {
		if (sh.getSignal() != null) {
			String[] sSign = sh.getSignal().split("\\|");
			for (String st : sSign) {
				int idSignal = Integer.parseInt(st);
				Tsignal tSignal = Main.signals.get(idSignal);
				if (tSignal != null) {
					signals.put(tSignal.getTypesignalref(), tSignal);
				}
			}
		}
	}
	
	public void changeTS(int val) {
		isON = val == 0 ? false : true;
	}
	
	public void setTS(int val) {}
	
	public Date getLastDataDate() {
		return lastDataDate;
	}

	public void setLastDataDate(Date lastDataDate) {
		this.lastDataDate = lastDataDate;
	}
}
