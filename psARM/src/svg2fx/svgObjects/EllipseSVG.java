package svg2fx.svgObjects;

import javafx.scene.Node;
import javafx.scene.shape.Ellipse;

import javax.xml.bind.annotation.XmlAttribute;

public class EllipseSVG extends AClassSVG {
	@XmlAttribute(name="cx")
	private Double cx;
	@XmlAttribute(name="cy")
	private Double cy;
	@XmlAttribute(name="rx")
	private Double rx;
	@XmlAttribute(name="ry")
	private Double ry;
	
	@Override
	public String toString() {
		return "cx = " + cx + "; cy = " + cy + "; " + "rx = " + rx + "; ry = " + ry;
	}
	
	public Double getCx() {
		return cx;
	}
	public void setCx(Double cx) {
		this.cx = cx;
	}
	public Double getCy() {
		return cy;
	}
	public void setCy(Double cy) {
		this.cy = cy;
	}
	public Double getRx() {
		return rx;
	}
	public void setRx(Double rx) {
		this.rx = rx;
	}
	public Double getRy() {
		return ry;
	}
	public void setRy(Double ry) {
		this.ry = ry;
	}

	@Override
	public Node getNode(SVG svg) {
		return setStyles(new Ellipse(cx, cy, rx, ry), svg.getStyleByName(getClazz()), svg);
	}
}
