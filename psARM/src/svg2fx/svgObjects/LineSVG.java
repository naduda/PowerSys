package svg2fx.svgObjects;

import javafx.scene.Node;
import javafx.scene.shape.Line;

import javax.xml.bind.annotation.XmlAttribute;

public class LineSVG extends AClassSVG {
	@XmlAttribute(name="x1")
	private double x1;
	@XmlAttribute(name="y1")
	private double y1;
	@XmlAttribute(name="x2")
	private double x2;
	@XmlAttribute(name="y2")
	private double y2;
	@XmlAttribute(name="stroke")
	private double stroke;
	@XmlAttribute(name="stroke-width")
	private double strokeWidth;
	
	@Override
	public String toString() {
		return "x1 = " + x1 + "; y1 = " + y1 + "; " + "x2 = " + x2 + "; y2 = " + y2;
	}
	
	public double getX1() {
		return x1;
	}
	public void setX1(Double x1) {
		this.x1 = x1;
	}
	public double getY1() {
		return y1;
	}
	public void setY1(double y1) {
		this.y1 = y1;
	}
	public double getX2() {
		return x2;
	}
	public void setX2(double x2) {
		this.x2 = x2;
	}
	public double getY2() {
		return y2;
	}
	public void setY2(double y2) {
		this.y2 = y2;
	}
	public double getStroke() {
		return stroke;
	}
	public void setStroke(double stroke) {
		this.stroke = stroke;
	}
	public double getStrokeWidth() {
		return strokeWidth;
	}
	public void setStrokeWidth(double strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	@Override
	public Node getNode(SVG svg) {
		return setStyles(new Line(x1, y1, x2, y2), svg.getStyleByName(getClazz()), svg);
	}
}
