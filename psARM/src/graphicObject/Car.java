package graphicObject;

import javafx.scene.shape.Line;
import xml.ShapeX;

public class Car extends AShape {
	
	private final double SH_WIDTH = rect.getWidth();
	private final double SH_HEIGHT = rect.getHeight();
	private final double oneMM = SH_HEIGHT / 13;

	private final Line l1 = new Line(SH_WIDTH/2, 0, SH_WIDTH/2, 5 * oneMM);
	private final Line l2 = new Line(SH_WIDTH/2, 8 * oneMM, SH_WIDTH/2, SH_HEIGHT);
	private final Line l3 = new Line(0, 5 * oneMM - SH_WIDTH/2, SH_WIDTH/2, 5 * oneMM);
	private final Line l4 = new Line(SH_WIDTH, 5 * oneMM - SH_WIDTH/2, SH_WIDTH/2, 5 * oneMM);
	private final Line l5 = new Line(0, 8 * oneMM - SH_WIDTH/2, SH_WIDTH/2, 8 * oneMM);
	private final Line l6 = new Line(SH_WIDTH, 8 * oneMM - SH_WIDTH/2, SH_WIDTH/2, 8 * oneMM);
	
	public Car(ShapeX sh) {
		super(sh);
		
		getChildren().addAll(l1, l2, l3, l4, l5, l6);
		
		shapes.getChildren().addAll(l1, l2, l3, l4, l5, l6);
		getChildren().add(shapes);
		setStrokeAndColor();
	}
}
