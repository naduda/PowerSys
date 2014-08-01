package graphicObject;

import javafx.scene.shape.Line;
import xml.ShapeX;

public class DisConnectorGRND extends AShape {

	private final double SH_WIDTH = rect.getWidth();
	private final double SH_HEIGHT = rect.getHeight();
	private final double H = SH_HEIGHT * 0.15;
	
	private final Line l5 = new Line(0, SH_HEIGHT - H, SH_WIDTH, SH_HEIGHT - H);
	private final Line l6 = new Line(H/2, SH_HEIGHT - H/2, SH_WIDTH - H/2, SH_HEIGHT - H/2);
	private final Line l7 = new Line(H, SH_HEIGHT, SH_WIDTH - H, SH_HEIGHT);
	
	private Disconnector dc;
	
	public DisConnectorGRND(ShapeX sh) {
		super(sh);
		double angle = sh.getAngle();
		sh.setHeight(SH_HEIGHT * 0.85);
		sh.setAngle(0);
		sh.setX(0);
		sh.setY(0);
		sh.setZoomX(1);
		sh.setZoomY(1);
		
		dc = new Disconnector(sh);
		dc.setLayoutX(SH_WIDTH/2 - ONE_MM);
		
		shapes.getChildren().addAll(l5, l6, l7);
		getChildren().addAll(dc, shapes);
		setStrokeAndColor();
		
		if (Math.abs(angle) == 90 || Math.abs(angle) == 270) {
			setLayoutX(getLayoutX() - lineWidth);
			setLayoutY(getLayoutY() - 1.5*lineWidth);
		}
	}

	@Override
	public void changeTS(int val) {
		super.changeTS(val);
		dc.changeTS(val);
	}
	
}
