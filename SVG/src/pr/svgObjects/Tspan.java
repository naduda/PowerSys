package pr.svgObjects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="tspan", namespace=INamespaces.SVG)
public class Tspan {
	@XmlAttribute(name="x")
	private Double x;
	@XmlAttribute(name="y")
	private Double y;
	@XmlAttribute(name="dx")
	private String dx;
	@XmlAttribute(name="dy")
	private String dy;
	@XmlAttribute(name="style")
	private String styleName;
	@XmlValue
	private String value;
	
	public Double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public Double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public String getDx() {
		return dx;
	}
	
	public double getDxPix(double oldValue, double em) {
		if (dx == null) return oldValue;
		return Double.parseDouble(dx.substring(0, dx.indexOf("em"))) * em + oldValue;
	}
	
	public void setDx(String dx) {
		this.dx = dx;
	}
	
	public String getDy() {
		return dy;
	}
	
	public double getDyPix(double oldValue, double em) {
		if (dy == null) return oldValue;
		return Double.parseDouble(dy.substring(0, dy.indexOf("em"))) * em + oldValue;
	}
	
	public void setDy(String dy) {
		this.dy = dy;
	}
	
	public String getStyleName() {
		return styleName;
	}
	
	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
}