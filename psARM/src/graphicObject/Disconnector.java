package graphicObject;

import java.rmi.RemoteException;

import ui.Main;
import ui.MainStage;
import xml.ShapeX;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class Disconnector extends AShape {

	private final double CIRCLE_RADIUS = lineWidth;
	private final double CIRCLE_LINE_WIDTH = lineWidth/2;
	private final Paint CIRCLE_FILL_COLOR = Color.LIGHTGRAY;
	
	private final double SH_HEIGHT = rect.getHeight() - lineWidth;
	private final double oneMM = SH_HEIGHT / 15;
	
	private final Line l1 = new Line(oneMM, 0, oneMM, 5 * oneMM);
	private final Line l2 = new Line(oneMM, 10 * oneMM, oneMM, SH_HEIGHT);
	private final Line l3 = new Line(oneMM, 10 * oneMM, oneMM + 5 * oneMM / Math.sqrt(3), 5 * oneMM);
	private final Line l4 = new Line(0, 5 * oneMM, 2 * oneMM, 5 * oneMM);
	
	private final Line l5 = new Line(oneMM + 2.5 * oneMM / Math.sqrt(3), 7.5 * oneMM, 4 * oneMM, 8.5 * oneMM);
	private final Circle c = new Circle(oneMM, 10 * oneMM, CIRCLE_RADIUS, CIRCLE_FILL_COLOR);
	
	private boolean isOD = false;
	
	public Disconnector(ShapeX sh) {
		super(sh);

		c.setStrokeWidth(CIRCLE_LINE_WIDTH);
		c.setStroke(lineColor);
		
		shapes.getChildren().addAll(l1, l2, l3, l4);
		getChildren().addAll(shapes, c);
		setStrokeAndColor();
	}
	
	public Disconnector(ShapeX sh, boolean isOD) {
		this(sh);
		this.isOD = isOD;
		
		if (isOD) {
			l5.setStroke(lineColor);
			l5.setStrokeWidth(lineWidth);
			getChildren().addAll(l5);
		}
	}
	
	@Override
	public void changeTS(int val) {
		super.changeTS(val);
		
		l3.setEndX(isON ? l3.getStartX() : l3.getStartX() + 5 * oneMM / Math.sqrt(3));
		if (isOD) {
			l5.setStartX(isON ? oneMM : oneMM + 2.5 * oneMM / Math.sqrt(3));
//			l5.setLayoutY(isON ? 0 : 7.5 * oneMM);
			l5.setEndX(isON ? 3 * oneMM : 4 * oneMM);
			l5.setEndY(isON ? 7.5 * oneMM : 8.5 * oneMM);
		}
	}

	@Override
	public void setTS(int val) {
		super.setTS(val);

		try {
			MainStage.psClient.setTS(Integer.parseInt(getId()), val, Main.mainScheme.getIdScheme());
		} catch (RemoteException | NumberFormatException e) {
			e.printStackTrace();
		}
	}
}
