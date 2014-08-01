package graphicObject;

import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import xml.ShapeX;

public class TC extends AShape {
	
	private final double SH_WIDTH = rect.getWidth();
	private final double SH_HEIGHT = rect.getHeight();
	
	private final double rArc = 1.5 * ONE_MM;
	
	private final Circle c1 = new Circle(lineWidth, rArc, 0.7 * ONE_MM);
	private final Circle c2 = new Circle(SH_WIDTH - lineWidth, rArc, 0.7 * ONE_MM);
	private final Line l1 = new Line(lineWidth, rArc, SH_WIDTH - lineWidth, rArc);
	private final Arc a1 = new Arc(SH_WIDTH/2 - rArc, rArc, rArc, rArc, 0, 180);
	private final Arc a2 = new Arc(SH_WIDTH/2 + rArc, rArc, rArc, rArc, 0, 180);
	private final Line l2 = new Line(SH_WIDTH/2 - 2 * rArc, rArc, SH_WIDTH/2 - 2 * rArc, SH_HEIGHT);
	private final Line l3 = new Line(SH_WIDTH/2 + 2 * rArc, rArc, SH_WIDTH/2 + 2 * rArc, SH_HEIGHT);
	
	public TC(ShapeX sh) {
		super(sh);

		c1.setFill(lineColor);
		c2.setFill(lineColor);
		
		shapes.getChildren().addAll(c1, c2, l1, a1, a2, l2, l3);
		getChildren().add(shapes);
		setStrokeAndColor();
	}

}
