package graphicObject;

import javafx.scene.shape.Line;
import xml.ShapeX;

public class VCar extends AShape {
	
	private final double SH_WIDTH = rect.getWidth();
	private final double SH_HEIGHT = rect.getHeight();
	private final double CAR_HEIGHT = 49.6762478463243;

	private Car c1;
	private Car c2;
	
	public VCar(ShapeX sh) {
		super(sh);
		
		double angle = sh.getAngle();
		sh.setHeight(CAR_HEIGHT);
		sh.setAngle(180);
		sh.setX(0);
		sh.setY(0);
		sh.setZoomX(1);
		sh.setZoomY(1);
		
		c1 = new Car(sh);
		sh.setAngle(0);
		sh.setY(SH_HEIGHT - CAR_HEIGHT);
		c2 = new Car(sh);
		
		sh.setAngle(angle);
		Line l1 = new Line(SH_WIDTH/2, CAR_HEIGHT, SH_WIDTH/2, SH_HEIGHT - CAR_HEIGHT);
		
		shapes.getChildren().addAll(l1);
		getChildren().addAll(c1, c2, shapes);
		setStrokeAndColor();
	}
}
