package graphicObject;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import xml.ShapeX;

public class Breaker extends AShape {

	private final double SH_WIDTH = rect.getWidth();
	private final double SH_HEIGHT = rect.getHeight();
	
	private final Rectangle rectB = new Rectangle(0, 5 * ONE_MM, SH_WIDTH, SH_WIDTH);
	private final Line l1 = new Line(SH_WIDTH/2, 0, SH_WIDTH/2, 5 * ONE_MM);
	private final Line l2 = new Line(SH_WIDTH/2, 5 * ONE_MM + SH_WIDTH, SH_WIDTH/2, SH_HEIGHT);
	
	public Breaker(ShapeX sh) {
		super(sh);
		rectB.setFill(Color.GREEN);

		shapes.getChildren().addAll(rectB, l1, l2);
		getChildren().add(shapes);
		setStrokeAndColor();
	}

	@Override
	public void changeTS(int ts) {
		super.changeTS(ts);
		rectB.setFill(ts == 0 ? Color.GREEN : Color.RED);
	}
	
	
}
