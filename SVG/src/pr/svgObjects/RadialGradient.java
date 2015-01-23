package pr.svgObjects;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class RadialGradient {
	@XmlAttribute(name="id")
	private String id;
	@XmlAttribute(name="fillPattern", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String fillPattern;
	@XmlAttribute(name="foreground", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String foreground;
	@XmlAttribute(name="background", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String background;
	@XmlAttribute(name="cx")
	private double cx;
	@XmlAttribute(name="cy")
	private double cy;
	@XmlAttribute(name="r")
	private double r;
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

	public double getCx() {
		return cx;
	}

	public void setCx(double cx) {
		this.cx = cx;
	}

	public double getCy() {
		return cy;
	}

	public void setCy(double cy) {
		this.cy = cy;
	}

	public double getR() {
		return r;
	}

	public void setR(double r) {
		this.r = r;
	}

	public List<Stop> getStops() {
		return stops;
	}

	public void setStops(List<Stop> stops) {
		this.stops = stops;
	}
}
