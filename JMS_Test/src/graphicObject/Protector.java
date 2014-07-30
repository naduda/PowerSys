package graphicObject;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import xml.ShapeX;

public class Protector extends AShape {

	private final double SH_WIDTH = rect.getWidth();
	private final double SH_HEIGHT = rect.getHeight();
	
	private final Line l1 = new Line(SH_WIDTH/2, 0, SH_WIDTH/2, SH_HEIGHT);
	private final Rectangle rectB = new Rectangle(0, 4 * ONE_MM, SH_WIDTH, SH_HEIGHT - 8 * ONE_MM);
	
	public Protector(ShapeX sh) {
		super(sh);
		
		rectB.setFill(Color.TRANSPARENT);
		
		shapes.getChildren().addAll(l1, rectB);
		getChildren().add(shapes);
		setStrokeAndColor();
		
		if (Math.abs(sh.getAngle()) == 90 || Math.abs(sh.getAngle()) == 270) {
			setLayoutY(getLayoutY() - 0.5*lineWidth);
		}
	}
}
