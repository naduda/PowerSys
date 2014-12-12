package pr.svgObjects;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LinearGradient {
	@XmlAttribute(name="id")
	private String id;
	@XmlAttribute(name="fillPattern", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String fillPattern;
	@XmlAttribute(name="foreground", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String foreground;
	@XmlAttribute(name="background", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String background;
	@XmlAttribute(name="x1")
	private double x1;
	@XmlAttribute(name="y1")
	private double y1;
	@XmlAttribute(name="x2")
	private double x2;
	@XmlAttribute(name="y2")
	private double y2;
	@XmlElement(name="stop")
	List<Stop> stops;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFillPattern() {
		return fillPattern;
	}

	public void setFillPattern(String fillPattern) {
		this.fillPattern = fillPattern;
	}

	public String getForeground() {
		return foreground;
	}

	public void setForeground(String foreground) {
		this.foreground = foreground;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public double getX1() {
		return x1;
	}

	public void setX1(double x1) {
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

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}
}
