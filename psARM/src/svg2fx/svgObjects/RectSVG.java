package svg2fx.svgObjects;

import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

import javax.xml.bind.annotation.XmlAttribute;

public class RectSVG extends AClassSVG {
	@XmlAttribute(name="x")
	private Double x;
	@XmlAttribute(name="y")
	private Double y;
	@XmlAttribute(name="width")
	private Double width;
	@XmlAttribute(name="height")
	private Double height;
	
	@Override
	public String toString() {
		return "x = " + x + "; y = " + y + "; " + "w = " + width + "; h = " + height;
	}

	public Double getX() {
		return x;
	}
	
	public void setX(Double x) {
		this.x = x;
	}
	
	public Double getY() {
		return y;
	}
	
	public void setY(Double y) {
		this.y = y;
	}
	
	public Double getWidth() {
		return width;
	}
	
	public void setWidth(Double width) {
		this.width = width;
	}
	
	public Double getHeight() {
		return height;
	}
	
	public void setHeight(Double height) {
		this.height = height;
	}

	@Override
	public Node getNode(SVG svg) {
		return setStyles(new Rectangle(x, y, width, height), svg.getStyleByName(getClazz()), svg);
	}
}
