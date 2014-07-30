package graphicObject;

import javafx.scene.shape.Arc;
import javafx.scene.shape.Line;
import xml.ShapeX;

public class Kettle extends AShape {

	private final double SH_WIDTH = rect.getWidth();
	private final double SH_HEIGHT = rect.getHeight();
	
	private final Arc a1 = new Arc(SH_WIDTH/2, SH_HEIGHT/2, SH_WIDTH/2, SH_WIDTH/2, 90, 270);
	private final Line l1 = new Line(SH_WIDTH/2, 0, SH_WIDTH/2, SH_HEIGHT/2 - SH_WIDTH/2);
	private final Line l2 = new Line(SH_WIDTH/2, SH_HEIGHT/2, SH_WIDTH/2, SH_HEIGHT);
	private final Line l3 = new Line(SH_WIDTH/2, SH_HEIGHT/2, SH_WIDTH, SH_HEIGHT/2);
	
	public Kettle(ShapeX sh) {
		super(sh);
		
		shapes.getChildren().addAll(a1, l1, l2, l3);
		getChildren().add(shapes);
		setStrokeAndColor();
		
		if (Math.abs(sh.getAngle()) == 90 || Math.abs(sh.getAngle()) == 270) {
			setLayoutY(getLayoutY() - 0.5*lineWidth);
		}
	}
}
